package fish.focus.uvms.rest.mobileterminal.rest.service;

import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.model.dto.VmsBillingDto;
import fish.focus.uvms.rest.asset.AbstractAssetRestTest;
import fish.focus.uvms.rest.asset.AssetHelper;
import fish.focus.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class VmsBillingTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void getVmsResponseListTest() throws Exception {
        Asset createdAsset = createAsset("vmsBillingTestAsset");
        MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, false, false);

        assertEquals(1, created.getChannels().size());

        List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
        assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getVesselId().equals(createdAsset.getNationalId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getVmsResponseListRemovedChannelTest() throws Exception {
        Asset createdAsset = createAsset("RemovedChannelTest");
        MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, false, false);
        assertEquals(1, created.getChannels().size());
        created.getChannels().clear();
        MobileTerminal updated = updateMobileTerminal(created);
        assertEquals(0, updated.getChannels().size());

        List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
        assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getVesselId().equals(createdAsset.getNationalId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getVmsResponseListIncludeNonDefaultChannelIfNotPollOrConfigTest() throws Exception {
        Asset createdAsset = createAsset("NotPollOrConfigTest");
        MobileTerminal created = createMobileTerminalWithChannel(createdAsset, false, false, false);
        assertEquals(1, created.getChannels().size());
        List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
        assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getName().equals(createdAsset.getName())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getVmsResponseListIncludePollChannelTest() throws Exception {
        Asset createdAsset = createAsset("PollChannelTest");
        MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, true, false);
        assertEquals(1, created.getChannels().size());

        List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
        assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getName().equals(createdAsset.getName())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getVmsResponseListIncludeConfigChannelTest() throws Exception {
        Asset createdAsset = createAsset("ConfigChannelTest");
        MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, false, true);
        assertEquals(1, created.getChannels().size());

        List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
        assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getName().equals(createdAsset.getName())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getVmsResponseListIncludePollAndConfigChannelTest() throws Exception {
        Asset createdAsset = createAsset("PollAndConfigChannelTest");
        MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, true, true);
        assertEquals(1, created.getChannels().size());

        List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
        assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getName().equals(createdAsset.getName())));
    }


    /*
     *
     * Helper methods below
     *
     */

    private List<VmsBillingDto> getVmsBillingResultList() throws Exception {
        Response response = getWebTargetInternal()
                .path("internal")
                .path("/vmsBilling")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();
        assertEquals(200, response.getStatus());

        String result = response.readEntity(String.class);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            return jsonb.fromJson(result,
                    new ArrayList<VmsBillingDto>() {
                        private static final long serialVersionUID = 1L;
                    }.getClass().getGenericSuperclass());
        }
    }

    private Asset createAsset(String name) {
        Asset asset = AssetHelper.createBasicAsset();
        long nationalId = ThreadLocalRandom.current().nextLong(100_000,  (1_000_000 - 100_000));
        asset.setNationalId(nationalId);
        asset.setName(name);

        return getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
    }

    private MobileTerminal createMobileTerminalWithChannel(Asset asset,
                                                           boolean defaultChannel, boolean pollChannel, boolean configChannel) {
        Integer memberNr = ThreadLocalRandom.current().nextInt(10_000,  (100_000 - 10_000));
        Integer dnid = ThreadLocalRandom.current().nextInt(100,  (1_000 - 100));

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);
        mobileTerminal.setAssetUUID(asset.getId().toString());

        Channel channel = MobileTerminalTestHelper.createBasicChannel();
        channel.setDnid(dnid);
        channel.setMemberNumber(memberNr);
        channel.setMobileTerminal(mobileTerminal);
        channel.setConfigChannel(configChannel);
        channel.setPollChannel(pollChannel);
        channel.setDefaultChannel(defaultChannel);
        channel.setName(asset.getName());
        channel.setMobileTerminal(mobileTerminal);

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);

        mobileTerminal.getChannels().clear();
        mobileTerminal.setChannels(channels);

        return getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);
    }

    private MobileTerminal updateMobileTerminal(MobileTerminal mobileTerminal) {
        return getWebTargetInternal()
                .path("mobileterminal")
                .queryParam("comment", "UPDATE_MT_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .put(Entity.json(mobileTerminal), MobileTerminal.class);
    }
}
