package fish.focus.uvms.rest.mobileterminal.rest.service;

import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.mobileterminal.dto.MTListResponse;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.types.MobileTerminalStatus;
import fish.focus.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import fish.focus.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import fish.focus.uvms.rest.asset.AbstractAssetRestTest;
import fish.focus.uvms.rest.asset.AssetHelper;
import fish.focus.uvms.rest.mobileterminal.dto.MTQuery;
import fish.focus.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import fish.focus.uvms.rest.mobileterminal.rest.dto.TestMTListResponse;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class MobileTerminalListQueryTest extends AbstractAssetRestTest {


    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithExistingButEmptyInput() {
        //First, to make sure that we have something in DB, create one MT
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        createMobileTerminal(mobileTerminal);

        Response response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json("{\"mobileterminalIds\":[]}"), Response.class);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        createMobileTerminal(mobileTerminal);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(List.of(MobileTerminalTestHelper.getSerialNumber()));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
        assertNotNull(terminal.getPlugin());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithWildCardsInSerialNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        createMobileTerminal(mobileTerminal);

        String serialNumber = MobileTerminalTestHelper.getSerialNumber();
        // Wildcard in front of serialNumber
        String wildCardInFront = "*" + serialNumber.substring(3);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(List.of(wildCardInFront));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());

        // Wildcard in back of serial
        String wildCardInBack = serialNumber.substring(0, serialNumber.length() - 3) + "*";

        mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(List.of(wildCardInBack));

        response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);

        terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());

        // Wildcard at both ends
        String wildCardAtBothEnds = "*" + serialNumber.substring(3, serialNumber.length() - 3) + "*";

        mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(List.of(wildCardAtBothEnds));

        response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);

        terminal = response.getMobileTerminalList().get(0);
        List<String> terminalSerialNumberList = new ArrayList<>();
        response.getMobileTerminalList().forEach(mt -> terminalSerialNumberList.add(mt.getSerialNo()));
        assertTrue(terminalSerialNumberList.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertThat(response.getMobileTerminalList(), is(not(empty())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithSatelliteNrTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        createMobileTerminal(mobileTerminal);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSateliteNumbers(List.of(mobileTerminal.getSatelliteNumber()));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithDNIDTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        createMobileTerminal(mobileTerminal);

        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());

        MTQuery mtQuery = new MTQuery();
        mtQuery.setDnids(List.of(channelList.get(0).getDnid()));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);

        assertEquals(1, response.getMobileTerminalList().size());
        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(MobileTerminalTestHelper.getSerialNumber(), terminal.getSerialNo());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithMemberNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        createMobileTerminal(mobileTerminal);

        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMemberNumbers(List.of(channelList.get(0).getMemberNumber()));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);
        assertThat(response.getMobileTerminalList(), is(not(empty())));
        assertThat(response.getMobileTerminalList(), hasSize(1));

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(MobileTerminalTestHelper.getSerialNumber(), terminal.getSerialNo());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithSatelliteAndDNIDTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        createMobileTerminal(mobileTerminal);

        // One thing from channel
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSateliteNumbers(List.of(mobileTerminal.getSatelliteNumber()));
        mtQuery.setDnids(List.of(channelList.get(0).getDnid()));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithConnectIDTest() {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);

        createMobileTerminal(mobileTerminal);

        MTQuery mtQuery = new MTQuery();
        List<String> inputList = new ArrayList<>();
        inputList.add(asset.getId().toString());
        mtQuery.setAssetIds(inputList);


        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .queryParam("includeArchived", false)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
        assertEquals(asset.getId().toString(), terminal.getAssetUUID());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithMtIdTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = createMobileTerminal(mobileTerminal);

        MTQuery mtQuery = new MTQuery();
        List<String> inputList = new ArrayList<>();
        inputList.add(created.getId().toString());
        mtQuery.setMobileterminalIds(inputList);

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithTwoChannelsTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());
        channelList.get(0).setConfigChannel(false);

        Channel channel = MobileTerminalTestHelper.createBasicChannel();
        channel.setConfigChannel(true);
        channel.setPollChannel(false);
        channel.setDefaultChannel(false);
        mobileTerminal.getChannels().add(channel);

        MobileTerminal created = createMobileTerminal(mobileTerminal);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMobileterminalIds(List.of(created.getId().toString()));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithTwoChannelsAndAnUpdateTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());
        channelList.get(0).setConfigChannel(false);

        Channel channel = MobileTerminalTestHelper.createBasicChannel();
        channel.setConfigChannel(true);
        channel.setPollChannel(false);
        channel.setDefaultChannel(false);
        mobileTerminal.getChannels().add(channel);

        MobileTerminal created = createMobileTerminal(mobileTerminal);

        created.setSoftwareVersion("B");

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);
        assertNotNull(updated);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMobileterminalIds(List.of(created.getId().toString()));

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(updated.getSoftwareVersion(), terminal.getSoftwareVersion());
        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getHistoricalMobileTerminalListTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = createMobileTerminal(mobileTerminal);

        created.setSoftwareVersion("B");
        Instant now = Instant.now();

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);
        assertNotNull(updated);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMobileterminalIds(List.of(created.getId().toString()));
        mtQuery.setDate(now);

        TestMTListResponse response = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), TestMTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(mobileTerminal.getSoftwareVersion(), terminal.getSoftwareVersion());
        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalList_ArchivedMobileTerminalsIncluded() {
        MobileTerminal prePersist = MobileTerminalTestHelper.createBasicMobileTerminal();
        prePersist.setAsset(null);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(List.of(MobileTerminalTestHelper.getSerialNumber()));

        MTListResponse mtListResponse = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        int sizeBefore = mtListResponse.getMobileTerminalList().size();

        MobileTerminal created = createMobileTerminal(prePersist);

        assertFalse(created.getArchived());

        MobileTerminal response = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Remove")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.ARCHIVE))
                .readEntity(MobileTerminal.class);

        assertTrue(response.getArchived());

        MTListResponse responseWithoutArchived = getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        int sizeAfter = responseWithoutArchived.getMobileTerminalList().size();

        assertEquals(sizeBefore, sizeAfter);

        MTListResponse responseWithArchived = getWebTargetExternal()
                .path("/mobileterminal/list")
                .queryParam("includeArchived", true)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        int sizeAfterWithArchived = responseWithArchived.getMobileTerminalList().size();

        assertEquals(sizeBefore + 1, sizeAfterWithArchived);
    }

    @Test
    @OperateOnDeployment("normal")
    public void searchForSerialNumberAfterCreatingNewEvents() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        Asset asset = createAndRestBasicAsset();

        MobileTerminal created = createMobileTerminal(mobileTerminal);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(List.of(MobileTerminalTestHelper.getSerialNumber()));

        // Check the search result
        MTListResponse returnList = sendMTListQuery(mtQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());

        MobileTerminal response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(created.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        assertNotNull(response);

        // Check the search result
        returnList = sendMTListQuery(mtQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());

        // Unassign
        MobileTerminal responseUnAssign = getWebTargetExternal()
                .path("/mobileterminal")
                .path(created.getId().toString())
                .path("unassign")
                .path(asset.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        assertNotNull(responseUnAssign);

        //check the search result
        returnList = sendMTListQuery(mtQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());

        //And inactivate
        response = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Inactivate")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.INACTIVE), MobileTerminal.class);

        assertNotNull(response);

        //check the search result
        returnList = sendMTListQuery(mtQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());
    }

    private MobileTerminal createMobileTerminal(MobileTerminal mt) {
        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);
        assertNotNull(created);

        return created;
    }

    private Asset createAndRestBasicAsset() {
        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);

        return createdAsset;
    }

    private MTListResponse sendMTListQuery(MTQuery mobileTerminalListQuery) {
        return getWebTargetExternal()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);
    }
}
