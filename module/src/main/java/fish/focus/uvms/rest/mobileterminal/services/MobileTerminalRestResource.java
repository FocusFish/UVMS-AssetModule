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
package fish.focus.uvms.rest.mobileterminal.services;

import fish.focus.uvms.commons.date.JsonBConfigurator;
import fish.focus.uvms.rest.security.RequiresFeature;
import fish.focus.uvms.rest.security.UnionVMSFeature;
import fish.focus.uvms.asset.mapper.HistoryMapper;
import fish.focus.uvms.asset.remote.dto.AssetDto;
import fish.focus.uvms.asset.remote.dto.ChangeHistoryRow;
import fish.focus.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import fish.focus.uvms.mobileterminal.dao.ChannelDaoBean;
import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.dto.MTListResponse;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.entity.types.MobileTerminalStatus;
import fish.focus.uvms.mobileterminal.mapper.MobileTerminalDtoMapper;
import fish.focus.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import fish.focus.uvms.mobileterminal.search.MTSearchKeyValue;
import fish.focus.uvms.rest.asset.mapper.SearchFieldMapper;
import fish.focus.uvms.rest.mobileterminal.dto.MTQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/mobileterminal")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class MobileTerminalRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalRestResource.class);
    @Inject
    ChannelDaoBean channelDao;
    @EJB
    private MobileTerminalServiceBean mobileTerminalService;
    @Inject
    private MobileTerminalPluginDaoBean pluginDao;
    @Context
    private HttpServletRequest request;

    private Jsonb jsonb;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    @PostConstruct
    public void init() {
        jsonb = new JsonBConfigurator().getContext(null);
    }

    @POST
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response createMobileTerminal(MobileTerminal terminal) {
        LOG.info("Create mobile terminal invoked in rest layer.");
        LOG.info("MobileTerminalType: SHORT_PREFIX_STYLE {}", terminal.toString());
        try {
            if (terminal.getId() != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Given MobileTerminal is already persisted in DB.").build();
            }

            terminal.setSource(TerminalSourceEnum.INTERNAL);

            MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(terminal.getPlugin().getPluginServiceName());
            terminal.setPlugin(plugin);
            terminal = mobileTerminalService.populateAssetInMT(terminal);

            MobileTerminal mobileTerminal = mobileTerminalService.createMobileTerminal(terminal, request.getRemoteUser());
            String returnString = jsonb.toJson(mobileTerminal);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating mobile terminal ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalById(@PathParam("id") UUID guid) {
        LOG.info("Get mobile terminal by id invoked in rest layer.");
        try {
            MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(guid);
            String returnString = jsonb.toJson(mobileTerminal);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when fetching mobile terminal ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("/notConnectedToAssetList")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getNotConnectedToAssetList() {
        LOG.info("Get mobile terminal list of not connected to an Asset");
        try {
            List<MobileTerminal> mtList = mobileTerminalService.getMobileTerminalListNotConnectedToAsset();
            String returnString = jsonb.toJson(mtList);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when fetching mobile terminal list] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PUT
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response updateMobileTerminal(MobileTerminal terminal) {
        LOG.info("Update mobile terminal by id invoked in rest layer.");
        try {
            terminal.setSource(TerminalSourceEnum.INTERNAL);
            mobileTerminalService.assertTerminalHasSerialNumber(terminal);
            MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(terminal.getPlugin().getPluginServiceName());
            terminal = mobileTerminalService.populateAssetInMT(terminal);

            MobileTerminal mobileTerminal = mobileTerminalService.updateMobileTerminal(terminal, terminal.getComment(), request.getRemoteUser());
            String returnString = jsonb.toJson(mobileTerminal);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when updating mobile terminal ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @POST
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalList(MTQuery query,
                                          @DefaultValue("1") @QueryParam("page") int page,
                                          @DefaultValue("1000000") @QueryParam("size") int size,
                                          @DefaultValue("true") @QueryParam("dynamic") boolean dynamic,
                                          @DefaultValue("false") @QueryParam("includeArchived") boolean includeArchived) {
        LOG.info("Get mobile terminal list invoked in rest layer.");
        try {
            List<MTSearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query);
            MTListResponse mobileTerminalList = mobileTerminalService.getMobileTerminalList(searchFields, page, size, dynamic, includeArchived);

            String returnJson = jsonb.toJson(mobileTerminalList);
            LOG.debug(returnJson);
            return Response.ok(returnJson).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal list ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("checkIfExists/serialNr/{serialNr}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response checkIfSerialNumberExistsInDB(@PathParam("serialNr") String serialNbr,
                                                  @DefaultValue("false") @QueryParam("returnWholeObject") Boolean returnWholeObject) {
        try {
            MTQuery query = new MTQuery();
            query.setSerialNumbers(Arrays.asList(serialNbr));
            List<MTSearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query);
            MTListResponse mobileTerminalList = mobileTerminalService.getMobileTerminalList(searchFields, 1, 10, true, true);
            String returnString = jsonb.toJson(returnWholeObject && !mobileTerminalList.getMobileTerminalList().isEmpty() ?
                    mobileTerminalList.getMobileTerminalList().get(0) : !mobileTerminalList.getMobileTerminalList().isEmpty());
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when checking if serial number already exists ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("checkIfExists/memberNbr/dnid/{memberNbr}/{dnid}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response checkIfMemberNumberDnidComboExistsInDB(@PathParam("memberNbr") Integer memberNbr,
                                                           @PathParam("dnid") Integer dnid,
                                                           @DefaultValue("false") @QueryParam("returnWholeObject") Boolean returnWholeObject) {
        try {
            MTQuery query = new MTQuery();
            query.setMemberNumbers(Arrays.asList(memberNbr));
            query.setDnids(Arrays.asList(dnid));
            List<MTSearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query);
            MTListResponse mobileTerminalList = mobileTerminalService.getMobileTerminalList(searchFields, 1, 10, true, true);
            String returnString = jsonb.toJson(returnWholeObject && !mobileTerminalList.getMobileTerminalList().isEmpty() ?
                    mobileTerminalList.getMobileTerminalList().get(0) : !mobileTerminalList.getMobileTerminalList().isEmpty());
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when if a member number dnid combo already exists ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PUT
    @Path("/{mtId}/assign/{assetId}")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response assignMobileTerminal(@QueryParam("comment") String comment,
                                         @PathParam("assetId") UUID assetId,
                                         @PathParam("mtId") UUID mobileTerminalId) {
        LOG.info("Assign mobile terminal invoked in rest layer.");
        try {
            MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(assetId, mobileTerminalId, comment, request.getRemoteUser());
            String returnString = jsonb.toJson(mobileTerminal);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when assigning mobile terminal ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PUT
    @Path("/{mtId}/unassign/{assetId}")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response unAssignMobileTerminal(@QueryParam("comment") String comment,
                                           @PathParam("assetId") UUID assetId,
                                           @PathParam("mtId") UUID mtId) {
        LOG.info("Unassign mobile terminal invoked in rest layer.");
        try {
            MobileTerminal mobileTerminal = mobileTerminalService.unAssignMobileTerminal(assetId, mtId, comment, request.getRemoteUser());
            String returnString = jsonb.toJson(mobileTerminal);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when unassigning mobile terminal ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PUT
    @Path("/{mtId}/status")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response setStatus(@QueryParam("comment") String comment, @PathParam("mtId") UUID mtId, MobileTerminalStatus status) {
        LOG.info("Set mobile terminal status active invoked in rest layer.");
        try {
            MobileTerminal mobileTerminal = mobileTerminalService.setStatusMobileTerminal(mtId, comment, status, request.getRemoteUser());
            String returnString = jsonb.toJson(mobileTerminal);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when activating mobile terminal ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("/{mtId}/history/")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalHistoryListByMobileTerminalId(@PathParam("mtId") UUID id,
                                                                   @DefaultValue("100")
                                                                   @QueryParam("maxNbr") Integer maxNbr) {
        LOG.info("Get mobile terminal history by mobile terminal id invoked in rest layer.");
        try {
            List<MobileTerminal> mobileTerminalRevisions = mobileTerminalService.getMobileTerminalRevisions(id, maxNbr);
            String returnString = jsonb.toJson(MobileTerminalDtoMapper.mapToMobileTerminalDtos(mobileTerminalRevisions));
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal history by terminalId ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("/history/getMtHistoryForAsset/{assetId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalHistoryByAssetId(@PathParam("assetId") UUID assetId,
                                                      @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr) {
        try {
            Map<String, ChangeHistoryRow> mobileTerminalRevisionMap =
                    mobileTerminalService.getMobileTerminalRevisionsByAssetId(assetId, maxNbr);
            String returnString = jsonb.toJson(mobileTerminalRevisionMap);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal history by assetId ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("history/getAssetHistoryForMT/{mobileTerminalId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetRevisionByMobileTerminalId(@PathParam("mobileTerminalId") UUID mobileTerminalId,
                                                       @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr) {
        try {
            List<AssetDto> assetRevisions = mobileTerminalService.getAssetRevisionsByMobileTerminalId(mobileTerminalId);
            String returnString = jsonb.toJson(assetRevisions);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting Asset history by mobileTerminalId ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("/{mtId}/changeHistory/")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalHistoryChangesListByMobileTerminalId(@PathParam("mtId") UUID id,
                                                                          @DefaultValue("100")
                                                                          @QueryParam("maxNbr") Integer maxNbr) {
        LOG.info("Get mobile terminal history by mobile terminal id invoked in rest layer.");
        try {
            List<MobileTerminal> mobileTerminalRevisions = mobileTerminalService.getMobileTerminalRevisions(id, maxNbr);
            Map<String, ChangeHistoryRow> changeHistory = HistoryMapper.mobileTerminalChangeHistory(mobileTerminalRevisions);
            String returnString = jsonb.toJson(changeHistory);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal history by terminalId ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GET
    @Path("/lowestFreeMemberNumberForDnid/{dnid}/")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getLowestFreeMemberNumberForDnid(@PathParam("dnid") Integer dnid) {
        try {
            Integer lowestFreeMemberNumberForDnid = channelDao.getLowestFreeMemberNumberForDnid(dnid);
            return Response.ok(lowestFreeMemberNumberForDnid).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error while searching for the lowest unused member number for a dnid ] {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
