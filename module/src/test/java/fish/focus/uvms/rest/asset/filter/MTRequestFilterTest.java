package fish.focus.uvms.rest.asset.filter;

import fish.focus.uvms.rest.asset.AbstractAssetRestTest;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Arquillian.class)
@RunAsClient
public class MTRequestFilterTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void checkMDCNoHeaderTest() {
        Response response = getWebTargetExternal()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        String requestId = response.getHeaderString("requestId");
        assertThat(requestId, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkMDCHeaderSetTest() {
        String requestId = UUID.randomUUID().toString();
        Response response = getWebTargetExternal()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        String returnedRequestId = response.getHeaderString("requestId");
        assertThat(returnedRequestId, CoreMatchers.is(requestId));
    }
}
