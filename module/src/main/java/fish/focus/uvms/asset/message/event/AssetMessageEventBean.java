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
package fish.focus.uvms.asset.message.event;

import fish.focus.uvms.asset.bean.AssetServiceBean;
import fish.focus.uvms.asset.domain.constant.AssetIdentifier;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.dto.AssetBO;
import fish.focus.uvms.asset.message.AssetProducer;
import fish.focus.uvms.asset.model.constants.FaultCode;
import fish.focus.uvms.asset.model.exception.AssetException;
import fish.focus.uvms.asset.model.mapper.AssetModuleResponseMapper;
import fish.focus.uvms.asset.model.mapper.JAXBMarshaller;
import fish.focus.wsdl.asset.module.PingResponse;
import fish.focus.wsdl.asset.types.AssetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

@Stateless
public class AssetMessageEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMessageEventBean.class);

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @Inject
    private AssetServiceBean assetService;

    @Inject
    private AssetProducer assetMessageProducer;

    @Inject
    private AssetModelMapper assetMapper;

    public void getAsset(TextMessage textMessage, AssetId assetId) {
        Asset asset;
        boolean messageSent = false;

        try {
            AssetIdentifier assetIdentity = assetMapper.mapToAssetIdentity(assetId.getType());
            asset = assetService.getAssetById(assetIdentity, assetId.getValue());
        } catch (Exception e) {
            LOG.error("Error when getting asset by id {}", assetId.getValue(), e);
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting asset by id : " + assetId.getValue() + " Error message: " + e)));
            messageSent = true;
            asset = null;
        }

        if (!messageSent) {
            try {
                String response = AssetModuleResponseMapper.mapAssetModuleResponse(assetMapper.toAssetModel(asset));
                assetMessageProducer.sendResponseMessageToSender(textMessage, response);
            } catch (AssetException | JMSException e) {
                LOG.error("[ Error when mapping asset ] ");
                assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when mapping asset" + e)));
            }
        }
    }

    public void ping(AssetMessageEvent message) {
        try {
            PingResponse pingResponse = new PingResponse();
            pingResponse.setResponse("pong");
            assetMessageProducer.sendResponseMessageToSender(message.getMessage(), JAXBMarshaller.marshallJaxBObjectToString(pingResponse));
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when marshalling ping response ]");
        }
    }

    public void upsertAsset(AssetMessageEvent message) {
        try {
            fish.focus.wsdl.asset.types.Asset assetModel = message.getAsset();
            AssetBO assetBo = assetMapper.toAssetBO(assetModel);
            assetService.upsertAssetBO(assetBo, message.getUsername());
        } catch (Exception e) {
            LOG.error("Could not update asset in the local database");
        }
    }
}
