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

import java.time.Instant;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.schema.exchange.common.v1.AcknowledgeTypeType;
import fish.focus.schema.exchange.common.v1.CommandType;
import fish.focus.schema.exchange.common.v1.CommandTypeType;
import fish.focus.schema.exchange.module.v1.ExchangeModuleMethod;
import fish.focus.schema.exchange.module.v1.SetCommandRequest;
import fish.focus.schema.exchange.plugin.types.v1.PollType;
import fish.focus.schema.mobileterminal.polltypes.v1.PollResponseType;
import fish.focus.uvms.asset.mapper.PollToCommandRequestMapper;
import fish.focus.uvms.exchange.client.ExchangeRestClient;

@Stateless
@LocalBean
public class PluginServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(PluginServiceBean.class);

    @Inject
    private PollToCommandRequestMapper pollToCommandRequestMapper;

    @Inject
    private ExchangeRestClient exchangeClient;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public AcknowledgeTypeType sendPoll(PollResponseType poll) {
        try {
            String username = poll.getUserName();
            PollType pollType = pollToCommandRequestMapper.mapToPollType(poll);
            String pluginServiceName = poll.getMobileTerminal().getPlugin().getServiceName();
            SetCommandRequest request = createSetCommandRequest(pluginServiceName, CommandTypeType.POLL, username, null);
            request.getCommand().setPoll(pollType);

            exchangeClient.sendCommandToPlugin(request);

            LOG.debug("Poll: {} sent to exchange. Response: {}", poll.getPollId().getGuid(), AcknowledgeTypeType.OK);
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            LOG.error("Failed to send poll command! Poll with guid {} was created but not sent. Error: {}", poll.getPollId().getGuid(), e);
            return AcknowledgeTypeType.NOK;
        }
    }

    private SetCommandRequest createSetCommandRequest(String pluginName, CommandTypeType type, String username, String fwdRule) {
        SetCommandRequest request = new SetCommandRequest();
        request.setMethod(ExchangeModuleMethod.SET_COMMAND);
        CommandType commandType = new CommandType();
        commandType.setTimestamp(Date.from(Instant.now()));
        commandType.setCommand(type);
        commandType.setPluginName(pluginName);
        commandType.setFwdRule(fwdRule);
        request.setUsername(username);
        request.setCommand(commandType);
        return request;
    }
}
