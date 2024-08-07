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
package fish.focus.uvms.mobileterminal.bean;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.schema.mobileterminal.types.v1.MobileTerminalFault;
import fish.focus.uvms.asset.message.event.AssetMessageErrorEvent;
import fish.focus.uvms.asset.message.event.EventMessage;
import fish.focus.uvms.asset.model.mapper.JAXBMarshaller;

@Stateless
@LocalBean
public class MobileTerminalEventServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalEventServiceBean.class);

    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    @AssetMessageErrorEvent
    private Event<EventMessage> errorEvent;

    public void returnError(@Observes @AssetMessageErrorEvent EventMessage message) {
        try (Connection connection = connectionFactory.createConnection()) {
            LOG.debug("Sending error message back from Mobile Terminal module to recipient om JMS Queue with correlationID: {} ",
                    message.getJmsMessage().getJMSMessageID());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MobileTerminalFault request = new MobileTerminalFault();
            request.setMessage(message.getErrorMessage());
            String data = JAXBMarshaller.marshallJaxBObjectToString(request);

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSCorrelationID());
            MessageProducer producer = session.createProducer(message.getJmsMessage().getJMSReplyTo());
            producer.send(response);
        } catch (Exception ex) {
            LOG.error("Error when returning Error message to recipient", ex);
        }
    }
}
