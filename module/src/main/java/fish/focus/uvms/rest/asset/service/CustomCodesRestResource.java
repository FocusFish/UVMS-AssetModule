package fish.focus.uvms.rest.asset.service;

import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.rest.security.RequiresFeature;
import fish.focus.uvms.rest.security.UnionVMSFeature;
import fish.focus.uvms.asset.bean.CustomCodesServiceBean;
import fish.focus.uvms.asset.domain.entity.CustomCode;
import fish.focus.uvms.asset.domain.entity.CustomCodesPK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

@Path("/customcodes")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class CustomCodesRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomCodesRestResource.class);

    @Inject
    private CustomCodesServiceBean customCodesSvc;

    @POST
    public Response createCustomCode(CustomCode customCode) {
        try {
            CustomCode createdCustomCode = customCodesSvc.create(customCode);
            return Response.ok(createdCustomCode).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @PUT
    @Path("replace")
    public Response replace(CustomCode customCode) {
        try {
            CustomCode replacedCustomCode = customCodesSvc.replace(customCode);
            return Response.ok(replacedCustomCode).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GET
    @Path("/{constant}/{code}")
    public Response retrieveCustomCode(
            @PathParam("constant") String constant,
            @PathParam("code") String code,
            @QueryParam(value = "validFromDate") String validFromDate,
            @QueryParam(value = "validToDate") String validToDate) {
        try {
            Instant fromDate = (validFromDate == null ? CustomCodesPK.STANDARD_START_DATE : DateUtils.stringToDate(validFromDate));
            Instant toDate = (validToDate == null ? CustomCodesPK.STANDARD_END_DATE : DateUtils.stringToDate(validToDate));
            CustomCode customCode = customCodesSvc.get(constant, code, fromDate, toDate);
            return Response.ok(customCode).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when fetching CustomCode. " + validFromDate + " " + validToDate);
            throw e;
        }
    }

    @GET
    @Path("/{constant}/{code}/exists")
    public Response exists(@PathParam("constant") String constant,
                           @PathParam("code") String code,
                           @QueryParam(value = "validFromDate") String validFromDate,
                           @QueryParam(value = "validToDate") String validToDate) {
        try {

            Instant fromDate = (validFromDate == null ? CustomCodesPK.STANDARD_START_DATE : DateUtils.stringToDate(validFromDate));
            Instant toDate = (validToDate == null ? CustomCodesPK.STANDARD_END_DATE : DateUtils.stringToDate(validToDate));
            CustomCodesPK pk = new CustomCodesPK();
            pk.setConstant(constant);
            pk.setCode(code);
            pk.setValidFromDate(fromDate);
            pk.setValidToDate(toDate);
            Boolean exists = customCodesSvc.exists(constant, code, fromDate, toDate);

            return Response.status(200).entity(exists).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @Path("/{constant}/{code}/getfordate")
    public Response getForDate(@PathParam("constant") String constant,
                               @PathParam("code") String code,
                               @QueryParam(value = "date") String date) {
        try {

            Instant aDate = (date == null ? Instant.now() : DateUtils.stringToDate(date));
            List<CustomCode> customCodes = customCodesSvc.getForDate(constant, code, aDate);

            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @Path("/{constant}/{code}/verify")
    public Response verify(@PathParam("constant") String constant,
                           @PathParam("code") String code,
                           @QueryParam(value = "date") String date) {
        try {

            Instant aDate = (date == null ? Instant.now() : DateUtils.stringToDate(date));
            Boolean exists = customCodesSvc.verify(constant, code, aDate);

            return Response.ok(exists).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @Path("/listconstants")
    public Response getAllConstants() {
        try {
            List<String> constants = customCodesSvc.getAllConstants();
            return Response.ok(constants).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @Path("/listcodesforconstant/{constant}")
    public Response getCodesForConstant(@PathParam("constant") String constant) {
        try {
            List<CustomCode> customCodes = customCodesSvc.getAllFor(constant);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @DELETE
    @Path("/{constant}/{code}")
    public Response deleteCustomCode(@PathParam("constant") String constant,
                                     @PathParam("code") String code,
                                     @QueryParam(value = "validFromDate") String validFromDate,
                                     @QueryParam(value = "validToDate") String validToDate) {
        try {

            Instant fromDate = (validFromDate == null ? CustomCodesPK.STANDARD_START_DATE : DateUtils.stringToDate(validFromDate));
            Instant toDate = (validToDate == null ? CustomCodesPK.STANDARD_END_DATE : DateUtils.stringToDate(validToDate));
            customCodesSvc.delete(constant, code, fromDate, toDate);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }
}
