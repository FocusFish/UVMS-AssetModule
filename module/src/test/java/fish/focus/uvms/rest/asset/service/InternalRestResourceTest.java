package fish.focus.uvms.rest.asset.service;

import fish.focus.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import fish.focus.schema.mobileterminal.polltypes.v1.PollRequestType;
import fish.focus.schema.mobileterminal.polltypes.v1.PollType;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.dto.AssetBO;
import fish.focus.uvms.commons.date.JsonBConfigurator;
import fish.focus.uvms.mobileterminal.dto.SanePollDto;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.types.PollTypeEnum;
import fish.focus.uvms.mobileterminal.model.dto.CreatePollResultDto;
import fish.focus.uvms.mobileterminal.model.dto.SimpleCreatePoll;
import fish.focus.uvms.rest.asset.AbstractAssetRestTest;
import fish.focus.uvms.rest.asset.AssetHelper;
import fish.focus.uvms.rest.asset.AssetMatcher;
import fish.focus.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.bind.Jsonb;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class InternalRestResourceTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdNonValidIdentifierTest() {
        Response response = getWebTargetInternal()
                .path("/internal/asset/apa/" + UUID.randomUUID())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();

        assertThat(response.getStatus(), is(Status.OK.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdGUIDTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/guid/" + createdAsset.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdCfrTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/cfr/" + createdAsset.getCfr())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdIrcsTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/ircs/" + createdAsset.getIrcs())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdImoTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/imo/" + createdAsset.getImo())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdMmsiTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/mmsi/" + createdAsset.getMmsi())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdIccatTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/iccat/" + createdAsset.getIccat())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdUviTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/uvi/" + createdAsset.getUvi())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdGfcmTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/gfcm/" + createdAsset.getGfcm())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        Asset upsertedAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), Asset.class);

        assertThat(upsertedAsset, is(CoreMatchers.notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetRetainMMSIAndCommentTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset.setComment("Update Asset Retain Comment");
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO createdAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(createdAsset, is(CoreMatchers.notNullValue()));

        createdAsset.getAsset().setMmsi(null);
        createdAsset.getAsset().setComment(null);
        assetBo.setAsset(createdAsset.getAsset());

        AssetBO updatedAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(updatedAsset, is(CoreMatchers.notNullValue()));
        assertEquals(asset.getComment(), updatedAsset.getAsset().getComment());
        assertEquals(asset.getMmsi(), updatedAsset.getAsset().getMmsi());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetOverwriteMMSIAndCommentTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset.setComment("Update Asset Discard This Comment");
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO createdAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(createdAsset, is(CoreMatchers.notNullValue()));

        createdAsset.getAsset().setMmsi("MMSI" + AssetHelper.getRandomIntegers(5));
        createdAsset.getAsset().setComment("It Should Be This Comment");
        assetBo.setAsset(createdAsset.getAsset());

        AssetBO updatedAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(updatedAsset, is(CoreMatchers.notNullValue()));
        assertEquals(createdAsset.getAsset().getComment(), updatedAsset.getAsset().getComment());
        assertEquals(createdAsset.getAsset().getMmsi(), updatedAsset.getAsset().getMmsi());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollTest() {      //just checking that the endpoint exists, there are better tests for the logic in pollRestResources
        PollRequestType input = new PollRequestType();

        PollMobileTerminal pmt = new PollMobileTerminal();
        input.getMobileTerminals().add(pmt);

        input.setPollType(PollType.MANUAL_POLL);
        input.setComment("Test Comment");
        input.setUserName("Test User");

        Response response = getWebTargetInternal()
                .path("/internal/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(input), Response.class);

        assertNotNull(response);
        assertEquals(500, response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollUsingOnlyAssetTest() {      //just checking that the endpoint exists, there are better tests for the logic in pollRestResources
        Asset asset = createTestAssetWithAttachedMobileTerminal();

        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setComment("test comment");

        CreatePollResultDto response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("username", "Test User")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(createPoll), CreatePollResultDto.class);

        assertNotNull(response);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollWithInactiveAssetTest() {
        Asset asset = createTestAssetWithAttachedMobileTerminal();

        // deactivate the asset to test that an asset can be e.g. in port for a while without UVMS sending polls
        asset.setActive(false);
        var updatedAsset = updateAsset(asset);
        assertThat("Asset should be inactive", updatedAsset.getActive(), is(false));

        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setComment("test comment");

        try (var response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(updatedAsset.getId().toString())
                .queryParam("username", "Test User")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(createPoll), Response.class)) {

            assertThat("http response status code", response.getStatus(), is(500));
            String responseException = response.readEntity(String.class);
            assertThat(responseException, containsString("is inactive"));
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollWithParkedAssetTest() {
        Asset asset = createTestAssetWithAttachedMobileTerminal();

        // park the asset and verify that UVMS is not sending polls
        // parked assets should be inactive but this test checks that a parked active asset still doesn't get polled
        asset.setParked(true);
        asset = updateAsset(asset);
        assertThat("Asset should be parked", asset.getParked(), is(true));

        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setComment("test comment");

        try (var response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("username", "Test User")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(createPoll), Response.class)) {

            assertThat("http response status code", response.getStatus(), is(500));
            String responseException = response.readEntity(String.class);
            assertThat(responseException, containsString("is parked"));
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollNonexistentAssetTest() {
        UUID assetId = UUID.randomUUID();

        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setComment("Error test comment");

        try (Response response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(assetId.toString())
                .queryParam("username", "Test User")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(createPoll), Response.class)) {

            assertThat("http response status code", response.getStatus(), is(500));
            String responseException = response.readEntity(String.class);
            assertThat(responseException, containsString("No asset with id: " + assetId));
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllPollsForAnAsset() {
        Asset asset = createTestAssetWithAttachedMobileTerminal();

        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setComment("Test comment");

        CreatePollResultDto response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("username", "Test User")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(createPoll), CreatePollResultDto.class);

        assertNotNull(response);

        List<SanePollDto> pollList = getWebTargetInternal()
                .path("/internal")
                .path("pollListForAsset")
                .path(asset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(new GenericType<>() {
                });

        assertEquals(1, pollList.size());
        assertEquals(response.getSentPolls().get(0), pollList.get(0).getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollForAnAsset() {
        Asset asset = createTestAssetWithAttachedMobileTerminal();

        Integer frequency = 100;
        Instant startDate = Instant.now().minus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);
        Instant endDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setPollType(PollType.PROGRAM_POLL);
        createPoll.setFrequency(frequency);
        createPoll.setStartDate(startDate);
        createPoll.setEndDate(endDate);
        createPoll.setComment("Test comment");

        CreatePollResultDto response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("username", "Test User")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(createPoll), CreatePollResultDto.class);

        assertNotNull(response);

        List<SanePollDto> pollList = getWebTargetInternal()
                .path("/internal")
                .path("pollListForAsset")
                .path(asset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(new GenericType<>() {
                });

        assertEquals(1, pollList.size());
        assertEquals(response.getUnsentPolls().get(0), pollList.get(0).getId().toString());
        assertEquals(PollTypeEnum.PROGRAM_POLL, pollList.get(0).getPollTypeEnum());
        assertEquals(frequency, pollList.get(0).getFrequency());
        assertEquals(startDate, pollList.get(0).getStartDate());
        assertEquals(endDate, pollList.get(0).getEndDate());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollInfo() {
        Asset asset = createTestAssetWithAttachedMobileTerminal();

        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setComment("Test comment");

        CreatePollResultDto response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("username", "Test User")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(createPoll), CreatePollResultDto.class);

        assertNotNull(response);
        String pollId = response.getSentPolls().get(0);

        SanePollDto poll = getWebTargetInternal()
                .path("/internal")
                .path("pollInfo")
                .path(pollId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(SanePollDto.class);

        assertEquals(pollId, poll.getId().toString());
        assertEquals(asset.getId(), poll.getAssetId());
        assertEquals(createPoll.getComment(), poll.getComment());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListTest() {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal createdMobileTerminal = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(mt), MobileTerminal.class);

        List<MobileTerminal> mobileTerminals = getWebTargetInternal()
                .path("/internal")
                .path("mobileterminals")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(new GenericType<>() {
                });

        assertTrue(mobileTerminals.stream().anyMatch(m -> m.getId().equals(createdMobileTerminal.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithHistoryTest() {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal createdMobileTerminal = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(mt), MobileTerminal.class);

        createdMobileTerminal.setAntenna("Different antenna");

        MobileTerminal updatedMobileTerminal = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .put(Entity.json(createdMobileTerminal), MobileTerminal.class);

        List<MobileTerminal> mobileTerminals = getWebTargetInternal()
                .path("/internal")
                .path("mobileterminals")
                .queryParam("includeHistory", true)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(new GenericType<>() {
                });

        assertTrue(mobileTerminals.stream().anyMatch(m -> m.getHistoryId().equals(createdMobileTerminal.getHistoryId())));
        assertTrue(mobileTerminals.stream().anyMatch(m -> m.getHistoryId().equals(updatedMobileTerminal.getHistoryId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMtAtDate() {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();

        Jsonb jsonb = new JsonBConfigurator().getContext(null); //for some reason serializing the mt gives a stack overflow error while serializing using the client, so we do it manually b4 instead
        String json = jsonb.toJson(mt);

        Response mtResponse = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(json), Response.class);
        assertEquals(200, mtResponse.getStatus());

        Instant timestamp = Instant.now();

        MobileTerminal createdTerminal = mtResponse.readEntity(MobileTerminal.class);
        createdTerminal.setAntenna("Different antenna");

        json = jsonb.toJson(createdTerminal);
        mtResponse = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .put(Entity.json(json), Response.class);
        assertEquals(200, mtResponse.getStatus());

        Response historicalResponse = getWebTargetInternal()
                .path("/internal")
                .path("mobileTerminalAtDate")
                .path(createdTerminal.getId().toString())
                .queryParam("date", "" + timestamp.toEpochMilli())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Response.class);
        assertEquals(200, historicalResponse.getStatus());

        MobileTerminal historicalTerminal = historicalResponse.readEntity(MobileTerminal.class);
        assertEquals(createdTerminal.getId(), historicalTerminal.getId());
        assertEquals(mt.getAntenna(), historicalTerminal.getAntenna());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalAtDateWithMemberNumberAndDnidTest() {
        Integer memberNr = ThreadLocalRandom.current().nextInt(10000, (100000 - 10000));
        Integer dnid = ThreadLocalRandom.current().nextInt(100, (1000 - 100));

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        Channel channel = MobileTerminalTestHelper.createBasicChannel();
        channel.setDnid(dnid);
        channel.setMemberNumber(memberNr);
        channel.setMobileTerminal(mobileTerminal);
        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        mobileTerminal.getChannels().clear();
        mobileTerminal.setChannels(channels);

        MobileTerminal created = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Integer newdnid = 54321;
        created.getChannels().iterator().next().setDnid(newdnid);
        Instant createdTimestamp = Instant.now();

        getWebTargetInternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .put(Entity.json(created), MobileTerminal.class);

        Response response = getWebTargetInternal()
                .path("internal")
                .path("revision")
                .queryParam("memberNumber", "" + memberNr)
                .queryParam("dnid", "" + dnid)
                .queryParam("date", "" + createdTimestamp.toEpochMilli())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();

        assertEquals(200, response.getStatus());
        MobileTerminal mt = response.readEntity(MobileTerminal.class);
        assertNotNull(mt);
        Channel respChannel = mt.getChannels().iterator().next();
        assertEquals(memberNr, respChannel.getMemberNumber());
        assertEquals(dnid, respChannel.getDnid());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalWithMemberNumberAndDnidAtDateTest() {
        Integer memberNr = ThreadLocalRandom.current().nextInt(10000, (100000 - 10000));
        Integer dnid = ThreadLocalRandom.current().nextInt(100, (1000 - 100));

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        Channel channel = MobileTerminalTestHelper.createBasicChannel();
        channel.setDnid(dnid);
        channel.setMemberNumber(memberNr);
        channel.setMobileTerminal(mobileTerminal);
        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        mobileTerminal.getChannels().clear();
        mobileTerminal.setChannels(channels);

        MobileTerminal created = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Integer newMemberNumber = 11111 != memberNr ? 11111 : 11112;
        created.getChannels().iterator().next().setMemberNumber(newMemberNumber);

        Instant createdTimestamp = Instant.now().minusSeconds(20);

        getWebTargetInternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);

        Response response = getWebTargetInternal()
                .path("internal")
                .path("revision")
                .queryParam("memberNumber", "" + newMemberNumber)
                .queryParam("dnid", "" + dnid)
                .queryParam("date", "" + createdTimestamp.toEpochMilli())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();

        assertEquals(200, response.getStatus());
        assertNotNull(response);
        // Should not find Updated mt with new MemberNumber
        MobileTerminal mt = response.readEntity(MobileTerminal.class);
        assertNull(mt);

        response = getWebTargetInternal()
                .path("internal")
                .path("revision")
                .queryParam("memberNumber", "" + newMemberNumber)
                .queryParam("dnid", "" + dnid)
                .queryParam("date", "" + Instant.now().toEpochMilli())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();

        // Should find Updated mt with new MemberNumber
        MobileTerminal mt2 = response.readEntity(MobileTerminal.class);
        assertNotNull(mt2);
        Channel respChannel = mt2.getChannels().iterator().next();
        assertEquals(newMemberNumber, respChannel.getMemberNumber());
        assertEquals(dnid, respChannel.getDnid());
    }

    private Asset createTestAssetWithAttachedMobileTerminal() {
        var basicAsset = AssetHelper.createBasicAsset();
        var asset = createAsset(basicAsset);

        var mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(asset);

        try (var mtResponse = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(mt), Response.class)) {
            assertEquals(200, mtResponse.getStatus());
        }
        return asset;
    }

    private Asset createAsset(Asset asset) {
        return getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
    }

    private Asset updateAsset(Asset asset) {
        return getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .put(Entity.json(asset), Asset.class);
    }
}
