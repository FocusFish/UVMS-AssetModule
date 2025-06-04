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
package fish.focus.uvms.rest.mobileterminal.rest;

import fish.focus.schema.exchange.module.v1.GetServiceListRequest;
import fish.focus.schema.exchange.module.v1.GetServiceListResponse;
import fish.focus.schema.exchange.module.v1.SetCommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("exchange/rest/unsecured/api")
@Stateless
public class ExchangeModuleRestMock {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeModuleRestMock.class);

    private static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";

    @POST
    @Path("serviceList")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public GetServiceListResponse getServiceList(GetServiceListRequest request) {
        try {
            return new GetServiceListResponse();
        } catch (Exception e) {
            LOG.error("Mock error", e);
            return null;
        }
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/pluginCommand")
    public Response sendCommandToPlugin(SetCommandRequest request) {
        boolean shouldFail = Boolean.parseBoolean(System.getProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false"));
        if (shouldFail) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

}
