package fish.focus.uvms.rest.asset.service;

import fish.focus.uvms.asset.domain.constant.AssetFilterValueType;
import fish.focus.uvms.asset.domain.entity.AssetFilter;
import fish.focus.uvms.asset.domain.entity.AssetFilterQuery;
import fish.focus.uvms.asset.domain.entity.AssetFilterValue;
import fish.focus.uvms.rest.asset.AbstractAssetRestTest;
import fish.focus.uvms.rest.asset.AssetHelper;
import fish.focus.uvms.rest.asset.util.AssetFilterRestResponseAdapter;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class AssetFilterRestResourceTest extends AbstractAssetRestTest {

    private AssetFilter assetFilter;
    private Jsonb jsonb;

    @Before
    public void setup() {
        JsonbConfig config = new JsonbConfig().withAdapters(new AssetFilterRestResponseAdapter());
        jsonb = JsonbBuilder.create(config);
        assetFilter = createAssetFilter();
        AssetFilterQuery assetFilterQuery = AssetHelper.createBasicAssetFilterQuery(assetFilter);
        AssetHelper.createBasicAssetFilterValue(assetFilterQuery);
        assetFilterQuery = createAssetFilterQuery(assetFilter);
        createAssetFilterValue(assetFilterQuery);
    }

    @After
    public void tearDown() {
        deleteAssetFilter(assetFilter);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetFilterFromJsonTest() {
        String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";

        String assetFilterCreateResp = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(afjson), String.class);

        AssetFilter assetFilter2 = jsonb.fromJson(assetFilterCreateResp, AssetFilter.class);

        assertNotNull(assetFilter2.getId().toString());

        try (Response deleteResp = getWebTargetExternal()
                .path("filter")
                .path(assetFilter2.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .delete()) {

            assertEquals(deleteResp.getStatus(), Status.OK.getStatusCode());
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetFilterFromJsonMoreComplexQuery() {
        String afjson = "{\"name\":\"VMS båtar\",\"filter\":[{\"type\":\"flagstate\",\"values\":[\"SWE\"],\"inverse\":false,\"valueType\":\"STRING\"},{\"type\":\"vesselType\",\"values\":[\"Fishing\"],\"inverse\":false,\"valueType\":\"STRING\"},{\"type\":\"lengthOverAll\",\"values\":[{\"operator\":\"greater than or equal\",\"value\":12}],\"inverse\":false,\"valueType\":\"NUMBER\"},{\"type\":\"hasLicence\",\"values\":[true],\"inverse\":false,\"valueType\":\"BOOLEAN\"}]}";

        String assetFilterCreateResp = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(afjson), String.class);

        AssetFilter assetFilter2 = jsonb.fromJson(assetFilterCreateResp, AssetFilter.class);

        assertNotNull(assetFilter2.getId().toString());

        try (Response deleteResp = getWebTargetExternal()
                .path("filter")
                .path(assetFilter2.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .delete()) {

            assertEquals(deleteResp.getStatus(), Status.OK.getStatusCode());
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void createAssetFilterTest() {
        AssetFilter testAssetFilter = createAssetFilter();
        assertNotNull(testAssetFilter.getId());

        try (Response deleteResp = getWebTargetExternal()
                .path("filter")
                .path(testAssetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .delete()) {
            assertEquals(deleteResp.getStatus(), Status.OK.getStatusCode());
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterTest() {
        String fetchedAssetFilter = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        AssetFilter fetchedAssetFilterJsonAdapter = jsonb.fromJson(fetchedAssetFilter, AssetFilter.class);
        assertEquals(fetchedAssetFilterJsonAdapter.getName(), assetFilter.getName());
        assertNotNull(fetchedAssetFilter);
        assertEquals(fetchedAssetFilterJsonAdapter.getId(), assetFilter.getId());
        assertEquals(fetchedAssetFilterJsonAdapter.getName(), assetFilter.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterListByUserNoUserParamTest() {
        Response response = getWebTargetExternal()
                .path("filter")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(response.getStatus(), Status.OK.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterListByUserTest() {
        Response response = getWebTargetExternal()
                .path("filter")
                .path("listAssetFiltersByUser")
                .queryParam("user", assetFilter.getOwner())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertNotNull(response);
        assertEquals(response.getStatus(), Status.OK.getStatusCode());
        assertTrue(response.getEntity().toString().length() > 1);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetFilterQueryTest() {
        AssetFilterQuery assetQuery = new AssetFilterQuery();
        assetQuery.setType("GUID");
        assetQuery.setValueType(AssetFilterValueType.STRING);
        assetQuery.setAssetFilter(assetFilter);

        assetQuery = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetQuery), AssetFilterQuery.class);

        assertNotNull(assetQuery.getId());

        getWebTargetExternal()
                .path("filter")
                .path(assetQuery.getId().toString())
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .delete();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByIdTest() {
        String fetchedAssetFilterJsonString = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        AssetFilter fetchedAssetFilter = jsonb.fromJson(fetchedAssetFilterJsonString, AssetFilter.class);

        assertEquals(assetFilter.getName(), fetchedAssetFilter.getName());
        assertEquals(assetFilter.getId(), fetchedAssetFilter.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetFilterFromJson() {
        String afId = assetFilter.getId().toString();
        String afjson = "{\"id\":\"" + afId + "\",\"name\":\"Nya Båtar och Update Test\", \"filter\": [{\"values\":[{\"value\":23, \"operator\":\"Not an Operator\"}, {\"value\":10100, \"operator\":\"bla bla bla\"}],\"type\": \"lapad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";

        String assetFilterResp = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(afjson), String.class);

        AssetFilter assetFilterRespString = getWebTargetExternal()
                .path("filter")
                .path(afId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(AssetFilter.class);

        assetFilter = jsonb.fromJson(assetFilterResp, AssetFilter.class);
        assertNotNull(assetFilter.getId());
        assertEquals(2, new ArrayList<>(assetFilter.getQueries()).get(0).getValues().size());
        assertTrue(assetFilterResp.contains(afId));
        assertEquals(assetFilterRespString.getName(), assetFilter.getName());
        assertEquals(assetFilterRespString.getOwner(), assetFilter.getOwner());

        String assetFilterList = getWebTargetExternal()
                .path("filter")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        assertTrue(assetFilterList.contains(assetFilter.getId().toString()));
        assertTrue(assetFilterList.contains("Not an Operator"));
    }


    private AssetFilter createAssetFilter() {
        String assetFilterString = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";
        String assetFilterJson = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilterString), String.class);
        return jsonb.fromJson(assetFilterJson, AssetFilter.class);
    }

    private AssetFilterQuery createAssetFilterQuery(AssetFilter assetFilterForQuery) {
        AssetFilterQuery assetFilterQuery = AssetHelper.createBasicAssetFilterQuery(assetFilterForQuery);
        return getWebTargetExternal()
                .path("filter")
                .path(assetFilterForQuery.getId().toString())
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilterQuery), AssetFilterQuery.class);
    }

    private void createAssetFilterValue(AssetFilterQuery assetFilterQueryForValue) {
        AssetFilterValue assetFilterValue = AssetHelper.createBasicAssetFilterValue(assetFilterQueryForValue);
        getWebTargetExternal()
                .path("filter")
                .path(assetFilterQueryForValue.getId().toString())
                .path("value")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilterValue), AssetFilterValue.class);
    }

    private void deleteAssetFilter(AssetFilter assetFilter) {
        getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .delete();
    }
}
