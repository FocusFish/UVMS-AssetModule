package fish.focus.uvms.asset.client;

import fish.focus.uvms.asset.client.model.*;
import fish.focus.uvms.asset.client.model.mt.MobileTerminal;
import fish.focus.uvms.asset.client.model.search.SearchBranch;
import fish.focus.uvms.asset.client.model.search.SearchFields;
import fish.focus.uvms.asset.dto.AssetProjection;
import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.commons.date.JsonBConfigurator;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetClientTest extends AbstractClientTest {

    @Inject
    AssetClient assetClient;

    @Before
    public void before() throws NamingException {
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/asset_endpoint", "http://localhost:8080/asset/rest");
    }

    @Test
    @OperateOnDeployment("normal")
    public void pingTest() {
        String response = assetClient.ping();
        assertThat(response, is("pong"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByGuidTest() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());

        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);

        AssetDTO asset = assetClient.getAssetById(AssetIdentifier.GUID, upsertAssetBo.getAsset().getId().toString());
        assertThat(asset, is(notNullValue()));
        assertThat(asset.getId(), is(upsertAssetBo.getAsset().getId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getNonExistentAssetTest() {
        AssetDTO asset = assetClient.getAssetById(AssetIdentifier.GUID, UUID.randomUUID().toString());
        assertNull(asset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetTest() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        AssetBO upsertAsset = assetClient.upsertAsset(assetBo);

        assertThat(upsertAsset.getAsset().getId(), is(notNullValue()));
        assertThat(upsertAsset.getAsset().getHistoryId(), is(notNullValue()));
        assertThat(upsertAsset, is(notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssertUpdateNameShouldCreateHistory() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);
        assetBo.getAsset().setName("New" + UUID.randomUUID());
        AssetBO upsertAssetBo2 = assetClient.upsertAsset(assetBo);

        assertThat(upsertAssetBo.getAsset().getHistoryId(), is(not(upsertAssetBo2.getAsset().getHistoryId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssertTwiceShouldNotCreateNewHistory() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);
        AssetDTO created = assetClient.getAssetById(AssetIdentifier.GUID, upsertAssetBo.getAsset().getId().toString());
        AssetBO upsertAssetBo2 = assetClient.upsertAsset(assetBo);
        AssetDTO updated = assetClient.getAssetById(AssetIdentifier.GUID, upsertAssetBo2.getAsset().getId().toString());

        assertEquals(created.getHistoryId(), updated.getHistoryId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void queryAssetsTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);

        SearchBranch trunk = new SearchBranch(true);
        trunk.addNewSearchLeaf(SearchFields.FLAG_STATE, asset.getFlagStateCode());

        List<AssetDTO> assets = assetClient.getAssetList(trunk, 1, 1000);
        assertEquals(1, assets.stream()
                .filter(a -> a.getId().equals(upsertAssetBo.getAsset().getId()))
                .count());
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetJMSTest() throws Exception {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        assetClient.upsertAssetAsync(assetBo);
        Thread.sleep(5000); // Needed due to async call
        AssetDTO fetchedAsset = assetClient.getAssetById(AssetIdentifier.CFR, asset.getCfr());
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistoryListByAssetIdTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        firstAssetBo.getAsset().setName(UUID.randomUUID().toString());
        AssetBO secondAssetBo = assetClient.upsertAsset(firstAssetBo);

        List<AssetDTO> histories = assetClient.getAssetHistoryListByAssetId(firstAssetBo.getAsset().getId());
        assertThat(histories.size(), is(2));
        assertEquals(1, histories.stream()
                .filter(a -> a.getHistoryId().equals(firstAssetBo.getAsset().getHistoryId()))
                .count());
        assertEquals(1, histories.stream()
                .filter(a -> a.getHistoryId().equals(secondAssetBo.getAsset().getHistoryId()))
                .count());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFromAssetIdAndDateTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        firstAssetBo.getAsset().setName(UUID.randomUUID().toString());
        Instant timestamp = Instant.now();
        assetClient.upsertAsset(firstAssetBo);

        AssetDTO assetHistory = assetClient.getAssetFromAssetIdAndDate(AssetIdentifier.CFR, firstAssetBo.getAsset().getCfr(), timestamp);
        assertThat(assetHistory.getId(), is(firstAssetBo.getAsset().getId()));
        assertThat(assetHistory.getHistoryId(), is(firstAssetBo.getAsset().getHistoryId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetsAtDateTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);

        List<AssetDTO> assets = assetClient.getAssetsAtDate(List.of(firstAssetBo.getAsset().getId()), Instant.now());
        assertThat(assets.size(), is(1));
        AssetDTO assetDto = assets.get(0);
        assertThat(assetDto.getId(), is(firstAssetBo.getAsset().getId()));
        assertThat(assetDto.getHistoryId(), is(firstAssetBo.getAsset().getHistoryId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetsAtDateWithTwoRevisionsTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        firstAssetBo.getAsset().setName(UUID.randomUUID().toString());
        Instant timestamp = Instant.now();
        assetClient.upsertAsset(firstAssetBo);

        List<AssetDTO> assets = assetClient.getAssetsAtDate(List.of(firstAssetBo.getAsset().getId()), timestamp);
        assertThat(assets.size(), is(1));
        AssetDTO assetDto = assets.get(0);
        assertThat(assetDto.getId(), is(firstAssetBo.getAsset().getId()));
        assertThat(assetDto.getHistoryId(), is(firstAssetBo.getAsset().getHistoryId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetsAtCurrentDateWithTwoRevisionsTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        firstAssetBo.getAsset().setName(UUID.randomUUID().toString());
        assetClient.upsertAsset(firstAssetBo);

        List<AssetDTO> assets = assetClient.getAssetsAtDate(List.of(firstAssetBo.getAsset().getId()), Instant.now());
        assertThat(assets.size(), is(1));
        AssetDTO assetDto = assets.get(0);
        assertThat(assetDto.getId(), is(firstAssetBo.getAsset().getId()));
        assertThat(assetDto.getHistoryId(), is(CoreMatchers.not(firstAssetBo.getAsset().getHistoryId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetsAtPastDateTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);

        List<AssetDTO> assets = assetClient.getAssetsAtDate(List.of(firstAssetBo.getAsset().getId()), Instant.now().minus(10, ChronoUnit.HOURS));
        assertThat(assets.size(), is(1));
        AssetDTO assetDto = assets.get(0);
        assertThat(assetDto.getId(), is(firstAssetBo.getAsset().getId()));
        assertThat(assetDto.getHistoryId(), is(firstAssetBo.getAsset().getHistoryId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetsAtPastDateWithTwoRevisionsTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        firstAssetBo.getAsset().setName(UUID.randomUUID().toString());
        assetClient.upsertAsset(firstAssetBo);

        List<AssetDTO> assets = assetClient.getAssetsAtDate(List.of(firstAssetBo.getAsset().getId()), Instant.now().minus(10, ChronoUnit.HOURS));
        assertThat(assets.size(), is(1));
        AssetDTO assetDto = assets.get(0);
        assertThat(assetDto.getId(), is(firstAssetBo.getAsset().getId()));
        assertThat(assetDto.getHistoryId(), is(firstAssetBo.getAsset().getHistoryId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistoryByAssetHistGuidTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        firstAssetBo.getAsset().setName(UUID.randomUUID().toString());
        assetClient.upsertAsset(firstAssetBo);

        AssetDTO assetHistory = assetClient.getAssetHistoryByAssetHistGuid(firstAssetBo.getAsset().getHistoryId());
        assertThat(assetHistory.getId(), is(firstAssetBo.getAsset().getId()));
        assertThat(assetHistory.getHistoryId(), is(firstAssetBo.getAsset().getHistoryId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistoryByDateQuery() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        Instant timestamp = Instant.now();
        firstAssetBo.getAsset().setName(UUID.randomUUID().toString());
        assetClient.upsertAsset(firstAssetBo);

        SearchBranch trunk = new SearchBranch(true);
        trunk.addNewSearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk.addNewSearchLeaf(SearchFields.NAME, asset.getName());
        trunk.addNewSearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(timestamp));

        List<AssetDTO> assetList = assetClient.getAssetList(trunk);
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getHistoryId(), is(firstAssetBo.getAsset().getHistoryId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetIdByQuery() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);

        SearchBranch trunk = new SearchBranch(true);
        trunk.addNewSearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk.addNewSearchLeaf(SearchFields.NAME, asset.getName());

        List<String> assetList = assetClient.getAssetIdList(trunk, 1, 100000, false);
        assertThat(assetList.size(), is(1));
        assertEquals(assetList.get(0), firstAssetBo.getAsset().getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getConstantsTest() {
        CustomCode customCode = AssetHelper.createCustomCode("TEST_Constant");
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);
        List<String> rs = assetClient.getConstants();
        assertThat(rs, is(not(empty())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getCodesForConstantsTest() {
        String constant = "Test_Constant" + UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            CustomCode customCode = AssetHelper.createCustomCode(constant);
            CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
            assertNotNull(createdCustomCode);
        }
        List<CustomCode> rs = assetClient.getCodesForConstant(constant);
        assertEquals(5, rs.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void isCodeValidTest() {
        String constant = "Test_Constant" + UUID.randomUUID();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        Instant validFromDate = createdCustomCode.getPrimaryKey().getValidFromDate();

        Boolean ok = assetClient.isCodeValid(cst, code, validFromDate.plus(5, ChronoUnit.DAYS));
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void isCodeValidNegativeTest() {
        String constant = "Test_Constant" + UUID.randomUUID();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        Instant validToDate = createdCustomCode.getPrimaryKey().getValidToDate();

        Boolean ok = assetClient.isCodeValid(cst, code, validToDate.plus(5, ChronoUnit.DAYS));
        Assert.assertFalse(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getCodeForDateTest() {
        String constant = "Test_Constant" + UUID.randomUUID();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        Instant validFromDate = createdCustomCode.getPrimaryKey().getValidFromDate();
        Instant validToDate = createdCustomCode.getPrimaryKey().getValidToDate();

        List<CustomCode> retrievedCustomCode = assetClient.getCodeForDate(cst, code, validToDate);
        assertNotNull(retrievedCustomCode);
        assertThat(retrievedCustomCode, is(not(empty())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getCodeForDateNegativeTest() {
        String constant = "Test_Constant" + UUID.randomUUID();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        Instant validFromDate = createdCustomCode.getPrimaryKey().getValidFromDate();
        Instant validToDate = createdCustomCode.getPrimaryKey().getValidToDate();

        List<CustomCode> retrievedCustomCode = assetClient.getCodeForDate(cst, code, validToDate.plus(5, ChronoUnit.DAYS));
        assertNotNull(retrievedCustomCode);
        assertEquals(0, retrievedCustomCode.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void customCodesReplaceTest() {
        String constant = "Test_Constant" + UUID.randomUUID();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        assetClient.replace(customCode);
        customCode.setDescription("replaced");
        assetClient.replace(customCode);
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTTest() {
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);  // proofs we reach the endpoint  . . .
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNewAssetOnUnknown() {
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setMmsiValue("123456789");
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);
        assertNotNull(response.getAssetHistoryId());
        assertNotNull(response.getAssetUUID());
        assertTrue(response.getAssetName().contains("Unknown"));
        assertEquals("UNK", response.getFlagstate());
        assertEquals("123456789", response.getMmsi());
    }

    @Test
    @OperateOnDeployment("normal")
    public void mmsiLongerThen9CharactersTest() {
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setMmsiValue("1024307102");
        request.setFlagState("ERR");
        request.setTranspondertypeValue("AIS");
        request.setPluginType("OTHER");
        request.setUser("OTHER");
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);
        assertNull(response.getAssetHistoryId());
        assertNull(response.getAssetUUID());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNewAssetWoMmsiFromFlux() {
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setFlagState("SWE");
        request.setTranspondertypeValue("FLUX");
        request.setPluginType("FLUX");
        request.setUser("FLUX");
        request.setCfrValue("CFR756051784");
        request.setIrcsValue("F1234591");
        request.setExternalMarking("EXT3");
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);
        assertNotNull(response.getAssetHistoryId());
        assertNotNull(response.getAssetUUID());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNewAssetOnUnknownWithVeryLongIRCS() {
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setMmsiValue("123456789");
        request.setIrcsValue("An IRCS value that is longer then 8 and should thus be set to null");
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);
        assertNotNull(response.getAssetHistoryId());
        assertNotNull(response.getAssetUUID());
        assertTrue(response.getAssetName().contains("Unknown"));
        assertEquals("UNK", response.getFlagstate());
        assertEquals("123456789", response.getMmsi());
        assertNull(response.getIrcs());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNewAssetOnUnknownUseNameAndFSFromRequest() {
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setMmsiValue("987654321");
        request.setAssetName("Named Ship");
        request.setFlagState("SWE");
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);
        assertNotNull(response.getAssetHistoryId());
        assertNotNull(response.getAssetUUID());
        assertEquals("Named Ship", response.getAssetName());
        assertEquals("SWE", response.getFlagstate());
        assertEquals("987654321", response.getMmsi());
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMtTest() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        assetBo.getAsset().setParked(true);

        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);

        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setMmsiValue(upsertAssetBo.getAsset().getMmsi());
        request.setAssetName(upsertAssetBo.getAsset().getName());
        request.setFlagState(upsertAssetBo.getAsset().getFlagStateCode());
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);
        assertNotNull(response.getAssetHistoryId());
        assertNotNull(response.getAssetUUID());
        assertEquals(upsertAssetBo.getAsset().getName(), response.getAssetName());
        assertEquals(upsertAssetBo.getAsset().getFlagStateCode(), response.getFlagstate());
        assertEquals(upsertAssetBo.getAsset().getMmsi(), response.getMmsi());

        assertTrue(response.getParked());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetInformation() {
        List<String> assetIdList = new ArrayList<>();

        for (int i = 0; i < 41; i++) {
            assetIdList.add(createAsset());
        }
        String output = assetClient.getAssetList(assetIdList);

        var jsonb = new JsonBConfigurator().getContext(null);
        List<AssetProjection> assets = jsonb.fromJson(output, new ArrayList<AssetProjection>() {
        }.getClass().getGenericSuperclass());

        assertThat("should return the same number of assets as was created", assets.size(), is(41));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetInformationEmptyInputList() {
        List<String> assetIdList = new ArrayList<>();

        String output = assetClient.getAssetList(assetIdList);

        assertEquals("[]", output);
    }

    @Test
    @OperateOnDeployment("normal")
    public void pollsForAssetInTheLastDay() {
        List<SanePollDto> output = assetClient.getPollsForAssetInTheLastDay(UUID.randomUUID());

        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void pollsInfo() {
        SanePollDto output = assetClient.getPollInfo(UUID.randomUUID());

        assertNull(output);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsTest() {
        List<MobileTerminal> mobileTerminals = assetClient.getMobileTerminals(false, false);
        assertNotNull(mobileTerminals);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMtAtDateRandomId() {

        MobileTerminal output = assetClient.getMtAtDate(UUID.randomUUID(), Instant.now());

        assertNull(output);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalAtDateWithMemberNumberAndDnidTest() {
        Integer memberNr = 564;
        Integer dnid = 15365;
        Instant instant = Instant.now().plusSeconds(1);// Instant.parse("2020-12-10T12:56:50Z");
        MobileTerminal mobileTerminal = assetClient.getMtFromMemberNumberAndDnidAtDate(memberNr, dnid, instant);

        assertNull(mobileTerminal);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getVmsBillingTest() {
        List<fish.focus.uvms.asset.client.model.mt.VmsBillingDto> vmsBillingList = assetClient.getVmsBillingList();
        assertNotNull(vmsBillingList);
    }

    private String createAsset() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        AssetBO upsertAsset = assetClient.upsertAsset(assetBo);
        return upsertAsset.getAsset().getId().toString();
    }
}
