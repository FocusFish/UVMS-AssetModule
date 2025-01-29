package fish.focus.uvms.rest.mobileterminal.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import fish.focus.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/plugin")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PluginRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(PluginRestResource.class);

    @Inject
    private ConfigServiceBeanMT configServiceMT;

    @Context
    private HttpServletRequest request;

    @GET
    @Path("/plugins")
    public Response getPlugins() throws Exception {
        try {
            List<MobileTerminalPlugin> list = configServiceMT.getMobileTerminalPlugins();
            return Response.ok(list).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting plugins ] {}", ex);
            throw ex;
        }
    }
}
