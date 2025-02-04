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

import fish.focus.uvms.asset.dto.AssetBO;
import fish.focus.uvms.asset.model.mapper.AssetModuleRequestMapper;
import fish.focus.uvms.asset.model.mapper.JAXBMarshaller;
import fish.focus.uvms.asset.util.JsonBConfiguratorAsset;
import fish.focus.uvms.commons.message.api.MessageConstants;
import fish.focus.wsdl.asset.module.GetAssetModuleResponse;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.wsdl.asset.types.AssetIdType;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;

import javax.jms.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JMSHelper {

    private static final long TIMEOUT = 20000;
    private static final String ASSET_QUEUE = "UVMSAssetEvent";
    private static final String RESPONSE_QUEUE = "IntegrationTestsResponseQueue";

    private static final Jsonb JSONB = JsonbBuilder.create();

    public Asset upsertAsset(Asset asset) throws Exception {
        String request = AssetModuleRequestMapper.createUpsertAssetModuleRequest(asset, "Test user");
        sendAssetMessage(request);
        return asset;
    }

    public Asset getAssetById(String value, AssetIdType type) throws Exception {
        String msg = AssetModuleRequestMapper.createGetAssetModuleRequest(value, type);
        String correlationId = sendAssetMessage(msg);
        Message response = listenForResponse(correlationId);
        GetAssetModuleResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage((TextMessage) response, GetAssetModuleResponse.class);
        return assetModuleResponse.getAsset();
    }

    public String sendAssetMessage(String text) throws Exception {
        try (Connection connection = getConnectionFactory().createConnection("test", "test")) {
            connection.setClientID(UUID.randomUUID().toString());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
            Queue assetQueue = session.createQueue(ASSET_QUEUE);

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            session.createProducer(assetQueue).send(message);

            return message.getJMSMessageID();
        }
    }

    public String sendAssetMessageWithFunction(String text, String function) throws Exception {
        return sendAssetMessage(text, MessageConstants.JMS_FUNCTION_PROPERTY, function);
    }

    public String sendAssetMessage(String text, String jmsStringProperty, String jmsStringPropertyValue) throws Exception {
        try (Connection connection = getConnectionFactory().createConnection("test", "test")) {
            connection.setClientID(UUID.randomUUID().toString());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
            Queue assetQueue = session.createQueue(ASSET_QUEUE);

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setStringProperty(jmsStringProperty, jmsStringPropertyValue);
            message.setText(text);

            session.createProducer(assetQueue).send(message);

            return message.getJMSMessageID();
        }
    }

    public Message listenForResponse(String correlationId) throws Exception {
        try (Connection connection = getConnectionFactory().createConnection("test", "test")) {
            connection.setClientID(UUID.randomUUID().toString());
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);

            return session.createConsumer(responseQueue).receive(TIMEOUT);
        }
    }

    private ConnectionFactory getConnectionFactory() {
        Map<String, Object> params = new HashMap<>();
        params.put("host", "localhost");
        params.put("port", 5445);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        return ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
    }

    public void assetInfo(List<fish.focus.uvms.asset.domain.entity.Asset> asset) throws Exception {
        String json = JSONB.toJson(asset);
        sendAssetMessageWithFunction(json, "ASSET_INFORMATION");
    }

    public void upsertAssetUsingMethod(fish.focus.uvms.asset.domain.entity.Asset asset) throws Exception {
        AssetBO abo = new AssetBO();
        abo.setAsset(asset);

        Jsonb jsonb = new JsonBConfiguratorAsset().getContext(null);
        String json = jsonb.toJson(abo);

        sendAssetMessage(json, "METHOD", "UPSERT_ASSET");
    }
}
