/*
 ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package fish.focus.uvms.asset.message;

import fish.focus.uvms.asset.message.event.AssetMessageErrorEvent;
import fish.focus.uvms.asset.message.event.AssetMessageEvent;
import fish.focus.uvms.asset.message.event.AssetMessageEventBean;
import fish.focus.uvms.asset.message.event.AssetMessageJSONBean;
import fish.focus.uvms.asset.model.exception.AssetException;
import fish.focus.uvms.asset.model.mapper.AssetModuleResponseMapper;
import fish.focus.uvms.asset.model.mapper.JAXBMarshaller;
import fish.focus.uvms.commons.message.api.MessageConstants;
import fish.focus.wsdl.asset.module.AssetModuleMethod;
import fish.focus.wsdl.asset.module.AssetModuleRequest;
import fish.focus.wsdl.asset.module.GetAssetModuleRequest;
import fish.focus.wsdl.asset.module.UpsertAssetModuleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.PrintWriter;
import java.io.StringWriter;

import static fish.focus.uvms.asset.model.constants.FaultCode.ASSET_MESSAGE;

@MessageDriven(mappedName = MessageConstants.QUEUE_ASSET_EVENT, activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = MessageConstants.QUEUE_ASSET_EVENT),
})
public class MessageConsumerBean implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @Inject
    private AssetMessageEventBean messageEventBean;

    @Inject
    private AssetMessageJSONBean assetJsonBean;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;

        try {
            String propertyMethod = textMessage.getStringProperty("METHOD");
            if (propertyMethod != null && propertyMethod.equals("UPSERT_ASSET")) {
                LOG.info("Message received in AssetModule with METHOD = {}", propertyMethod);
                assetJsonBean.upsertAsset(textMessage);
                return;
            }
            String propertyFunction = textMessage.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY);
            if (propertyFunction != null && propertyFunction.equals("ASSET_INFORMATION")) {
                LOG.info("Message received in AssetModule with FUNCTION = {}", propertyFunction);
                assetJsonBean.updateAssetInformation(textMessage);
                return;
            }

            handleXmlFormattedMessage(textMessage);
        } catch (Exception e) {
            LOG.error("[ Error when receiving message in AssetModule. ] {}", findLineInStackTrace(e, "duplicate key value violates unique constraint"));
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(ASSET_MESSAGE, "Error processing message in Asset Module: " + e.getMessage())));
        }
    }

    private void handleXmlFormattedMessage(TextMessage textMessage) throws AssetException {
        AssetModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetModuleRequest.class);
        AssetModuleMethod method = request.getMethod();
        LOG.info("Message received in AssetModule with unmarshalled method = {}", method);

        switch (method) {
            case GET_ASSET:
                GetAssetModuleRequest getRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetModuleRequest.class);
                messageEventBean.getAsset(textMessage, getRequest.getId());
                break;
            case PING:
                messageEventBean.ping(new AssetMessageEvent(textMessage));
                break;
            case UPSERT_ASSET:
                UpsertAssetModuleRequest upsertRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, UpsertAssetModuleRequest.class);
                AssetMessageEvent upsertAssetMessageEvent = new AssetMessageEvent(textMessage, upsertRequest.getAsset(), upsertRequest.getUserName());
                messageEventBean.upsertAsset(upsertAssetMessageEvent);
                break;
            default:
                LOG.error("[ Not implemented method consumed: {} ]", method);
                assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(ASSET_MESSAGE, "Method not implemented")));
        }
    }

    private String findLineInStackTrace(Exception e, String searchPhrase) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        String[] lines = stackTrace.split("\n");
        for (String line : lines) {
            if (line.contains(searchPhrase)) {
                return line.trim();
            }
        }
        return stackTrace;
    }
}
