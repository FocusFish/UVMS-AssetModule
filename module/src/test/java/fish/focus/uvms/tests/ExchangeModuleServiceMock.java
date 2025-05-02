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
package fish.focus.uvms.tests;

import fish.focus.schema.exchange.plugin.types.v1.PluginType;
import fish.focus.schema.exchange.service.v1.CapabilityListType;
import fish.focus.schema.exchange.service.v1.CapabilityType;
import fish.focus.schema.exchange.service.v1.CapabilityTypeType;
import fish.focus.schema.exchange.service.v1.ServiceResponseType;
import fish.focus.uvms.commons.date.JsonBConfigurator;
import fish.focus.uvms.commons.message.api.MessageConstants;
import fish.focus.uvms.commons.message.context.MappedDiagnosticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.*;
import javax.json.bind.Jsonb;

@Startup
@Singleton
public class ExchangeModuleServiceMock {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeModuleServiceMock.class);

    @Inject
    @JMSConnectionFactory("java:/ConnectionFactory")
    JMSContext context;

    @Resource(mappedName = "java:/" + MessageConstants.EVENT_STREAM_TOPIC)
    private Topic destination;

    private Jsonb jsonb = new JsonBConfigurator().getContext(null);

    @PostConstruct
    public void initPlugins() {
        try {
            LOG.info("Sending plugin information to event topic");
            ServiceResponseType serviceResponseType = new ServiceResponseType();
            serviceResponseType.setServiceClassName("fish.focus.uvms.plugins.inmarsat");
            serviceResponseType.setName("Thrane&Thrane");
            serviceResponseType.setSatelliteType("INMARSAT_C");
            serviceResponseType.setPluginType(PluginType.SATELLITE_RECEIVER);
            serviceResponseType.setActive(true);
            CapabilityListType capabilityList = new CapabilityListType();
            CapabilityType capabilityType = new CapabilityType();
            capabilityType.setType(CapabilityTypeType.POLLABLE);
            capabilityType.setValue("TRUE");
            capabilityList.getCapability().add(capabilityType);
            CapabilityType configurable = new CapabilityType();
            configurable.setType(CapabilityTypeType.CONFIGURABLE);
            configurable.setValue("TRUE");
            capabilityList.getCapability().add(configurable);
            serviceResponseType.setCapabilityList(capabilityList);

            String message = jsonb.toJson(serviceResponseType);
            TextMessage textMessage = this.context.createTextMessage(message);
            textMessage.setStringProperty(MessageConstants.EVENT_STREAM_EVENT, "Service Registered");
            textMessage.setStringProperty(MessageConstants.EVENT_STREAM_SUBSCRIBER_LIST, null);
            MappedDiagnosticContext.addThreadMappedDiagnosticContextToMessageProperties(textMessage);

            context.createProducer()
                    .setDeliveryMode(DeliveryMode.PERSISTENT)
                    .send(destination, textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}