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
package fish.focus.uvms.rest.asset.service;

import fish.focus.schema.mobileterminal.config.v1.ConfigList;
import fish.focus.schema.mobileterminal.config.v1.TerminalSystemType;
import fish.focus.schema.mobileterminal.types.v1.SearchKey;
import fish.focus.uvms.asset.bean.ConfigServiceBean;
import fish.focus.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import fish.focus.uvms.rest.mobileterminal.dto.MTMobileTerminalConfig;
import fish.focus.uvms.rest.security.RequiresFeature;
import fish.focus.uvms.rest.security.UnionVMSFeature;
import fish.focus.wsdl.asset.types.ConfigSearchField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/config")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class AssetConfigRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetConfigRestResource.class);

    @Inject
    ConfigServiceBeanMT configServiceMT;

    @Inject
    private ConfigServiceBean configService;

    @GET
    @Path("/searchfields")
    public Response getConfigSearchFields() throws Exception {
        try {
            return Response.ok(ConfigSearchField.values()).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @Path(value = "/parameters")
    public Response getParameters() throws Exception {
        try {
            Map<String, String> parameters = configService.getParameters();
            return Response.ok(parameters).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    //Stuff copied from MT
    @GET
    @Path("/MT/transponders")
    public Response getConfigTransponders() throws Exception {
        try {
            LOG.info("Get config transponders invoked in rest layer.");
            List<TerminalSystemType> list = configServiceMT.getTerminalSystems();
            return Response.ok(MTMobileTerminalConfig.mapConfigTransponders(list)).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting configTransponders {} ] {}", ex.getMessage(), ex.getStackTrace());
            throw ex;
        }
    }

    @GET
    @Path("/MT/searchfields")
    public Response getMTConfigSearchFields() throws Exception {
        LOG.info("Get config search fields invoked in rest layer.");
        try {
            return Response.ok(SearchKey.values()).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting config search fields ]", ex);
            throw ex;
        }
    }

    @GET
    @Path("/MT/")
    public Response getMTConfiguration() throws Exception {
        try {
            List<ConfigList> config = configServiceMT.getConfigValues();
            return Response.ok(MTMobileTerminalConfig.mapConfigList(config)).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            throw ex;
        }
    }
}