package fish.focus.uvms.tests.asset.service.arquillian.arquillian;

import fish.focus.schema.exchange.plugin.types.v1.PluginType;
import fish.focus.uvms.asset.bean.AssetFilterServiceBean;
import fish.focus.uvms.asset.bean.AssetServiceBean;
import fish.focus.uvms.asset.domain.constant.AssetFilterValueType;
import fish.focus.uvms.asset.domain.constant.AssetIdentifier;
import fish.focus.uvms.asset.domain.entity.*;
import fish.focus.uvms.asset.dto.AssetBO;
import fish.focus.uvms.asset.dto.AssetMTEnrichmentRequest;
import fish.focus.uvms.asset.dto.AssetMTEnrichmentResponse;
import fish.focus.uvms.asset.remote.dto.search.SearchBranch;
import fish.focus.uvms.asset.remote.dto.search.SearchFields;
import fish.focus.uvms.asset.remote.dto.search.SearchLeaf;
import fish.focus.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import fish.focus.uvms.tests.BuildAssetServiceDeployment;
import fish.focus.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends BuildAssetServiceDeployment {

    @Inject
    private AssetServiceBean assetService;

    @Inject
    private TestPollHelper testPollHelper;

    @Inject
    private AssetFilterServiceBean assetFilterServiceBean;

    @Inject
    private MobileTerminalServiceBean mobileTerminalService;

    @Test
    @OperateOnDeployment("normal")
    public void createAssert() {
        // this test is to ensure that create actually works
        // create an Asset
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        assertNotNull(createdAsset);
        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssert() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Long nationalId = ThreadLocalRandom.current().nextLong(10_000_000);
        asset.setNationalId(nationalId);
        AssetBO abo = new AssetBO();
        abo.setAsset(asset);

        AssetBO createdAssetBo = assetService.upsertAssetBO(abo, "national id test");
        assertNotNull(createdAssetBo);
        assertNotNull(createdAssetBo.getAsset());

        Asset assetById = assetService.getAssetById(AssetIdentifier.NATIONAL, nationalId.toString());
        assertNotNull(assetById);
        assertEquals(nationalId, assetById.getNationalId());
        assertEquals("national id test", assetById.getUpdatedBy());
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssertUsingCfrId() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        String cfr = asset.getCfr();
        AssetBO abo = new AssetBO();
        abo.setAsset(asset);

        AssetBO createdAssetBo = assetService.upsertAssetBO(abo, "upsert asset test");
        assertNotNull(createdAssetBo);
        assertNotNull(createdAssetBo.getAsset());
        String oldIrcs = createdAssetBo.getAsset().getIrcs();

        Asset asset2 = AssetTestsHelper.createBiggerAsset();
        asset2.setCfr(cfr);
        abo.setAsset(asset2);
        createdAssetBo = assetService.upsertAssetBO(abo, "upsert asset test update");
        assertNotNull(createdAssetBo);
        assertNotNull(createdAssetBo.getAsset());

        Asset assetByIrcs = assetService.getAssetById(AssetIdentifier.IRCS, oldIrcs);
        assertNull(assetByIrcs);

        Asset assetById = assetService.getAssetById(AssetIdentifier.CFR, cfr);
        assertEquals(createdAssetBo.getAsset().getId(), assetById.getId());
        assertEquals(asset2.getName(), assetById.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetFishingLicence() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        AssetBO abo = new AssetBO();
        abo.setAsset(asset);
        FishingLicence licence = AssetTestsHelper.createFishingLicence();
        abo.setFishingLicence(licence);

        AssetBO createdAssetBo = assetService.upsertAssetBO(abo, "upsert asset test");
        assertNotNull(createdAssetBo);
        assertNotNull(createdAssetBo.getAsset());

        FishingLicence createdFishingLicence = assetService.getFishingLicenceByAssetId(createdAssetBo.getAsset().getId());

        assertThat(createdFishingLicence.getLicenceNumber(), is(licence.getLicenceNumber()));
        assertThat(createdFishingLicence.getName(), is(licence.getName()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetFishingLicenceTwice() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        AssetBO abo = new AssetBO();
        abo.setAsset(asset);
        FishingLicence licence = AssetTestsHelper.createFishingLicence();
        abo.setFishingLicence(licence);

        AssetBO createdAssetBo = assetService.upsertAssetBO(abo, "upsert asset test");
        assertNotNull(createdAssetBo);
        assertNotNull(createdAssetBo.getAsset());
        FishingLicence createdFishingLicence = assetService.getFishingLicenceByAssetId(createdAssetBo.getAsset().getId());
        UUID firstId = createdFishingLicence.getId();
        AssetBO createdAssetBo2 = assetService.upsertAssetBO(abo, "upsert asset test");
        assertNotNull(createdAssetBo2);
        assertNotNull(createdAssetBo2.getAsset());

        FishingLicence createdFishingLicence2 = assetService.getFishingLicenceByAssetId(createdAssetBo2.getAsset().getId());

        assertThat(createdFishingLicence2.getId(), is(firstId));
        assertThat(createdFishingLicence2.getLicenceNumber(), is(licence.getLicenceNumber()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetFishingLicenceNewLicence() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        AssetBO abo = new AssetBO();
        abo.setAsset(asset);
        FishingLicence licence = AssetTestsHelper.createFishingLicence();
        abo.setFishingLicence(licence);

        AssetBO createdAssetBo = assetService.upsertAssetBO(abo, "upsert asset test");
        assertNotNull(createdAssetBo);
        assertNotNull(createdAssetBo.getAsset());

        FishingLicence newLicence = AssetTestsHelper.createFishingLicence();
        abo.setFishingLicence(newLicence);

        AssetBO createdAssetBo2 = assetService.upsertAssetBO(abo, "upsert asset test");

        FishingLicence createdFishingLicence = assetService.getFishingLicenceByAssetId(createdAssetBo2.getAsset().getId());

        assertThat(createdFishingLicence.getLicenceNumber(), is(newLicence.getLicenceNumber()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetFishingLicenceRemoveLicence() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        AssetBO abo = new AssetBO();
        abo.setAsset(asset);
        FishingLicence licence = AssetTestsHelper.createFishingLicence();
        abo.setFishingLicence(licence);

        AssetBO createdAssetBo = assetService.upsertAssetBO(abo, "upsert asset test");
        assertNotNull(createdAssetBo);
        assertNotNull(createdAssetBo.getAsset());

        FishingLicence createdFishingLicence = assetService.getFishingLicenceByAssetId(createdAssetBo.getAsset().getId());
        assertThat(createdFishingLicence.getLicenceNumber(), is(licence.getLicenceNumber()));

        abo.setFishingLicence(null);

        AssetBO createdAssetBo2 = assetService.upsertAssetBO(abo, "upsert asset test");
        FishingLicence createdFishingLicence2 = assetService.getFishingLicenceByAssetId(createdAssetBo2.getAsset().getId());

        assertThat(createdFishingLicence2, is(CoreMatchers.nullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAsset() {
        // create an asset
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        // change it and store it
        createdAsset.setName("ÄNDRAD");
        assetService.updateAsset(createdAsset, "CHG_USER", "En changekommentar");

        // fetch it and check name
        Asset fetchedAsset = assetService.getAssetById(createdAsset.getId());
        assertEquals(createdAsset.getName(), fetchedAsset.getName());
        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAsset() {
        // create an asset
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");

        // change it to get an audit
        createdAsset.setName("ÄNDRAD_1");
        assetService.updateAsset(createdAsset, "CHG_USER_1", "En changekommentar");

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());

        // fetch it and it should be null
        Asset fetchedAsset = assetService.getAssetById(createdAsset.getId());
        assertNull(fetchedAsset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetThreeTimesAndCheckRevisionsAndValues() {
        // create an asset
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        // change it and store it
        createdAsset.setName("ÄNDRAD_1");
        assetService.updateAsset(createdAsset, "CHG_USER_1", "En changekommentar");

        // change it and store it
        createdAsset.setName("ÄNDRAD_2");
        Asset changedAsset2 = assetService.updateAsset(createdAsset, "CHG_USER_2", "En changekommentar");
        UUID historyId2 = changedAsset2.getHistoryId();

        // change it and store it
        createdAsset.setName("ÄNDRAD_3");
        assetService.updateAsset(createdAsset, "CHG_USER_3", "En changekommentar");

        List<Asset> assetVersions = assetService.getRevisionsForAsset(asset.getId());
        assertEquals(4, assetVersions.size());

        Asset fetchedAssetAtRevision = assetService.getAssetRevisionForRevisionId(historyId2);
        assertEquals(historyId2, fetchedAssetAtRevision.getHistoryId());

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetBOWithIdentifierCFR() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "Test");

        Asset assetUpdates = AssetTestsHelper.createBasicAsset();
        assetUpdates.setCfr(createdAsset.getCfr());

        AssetBO assetBO = new AssetBO();
        assetBO.setAsset(assetUpdates);
        assetBO.setDefaultIdentifier(AssetIdentifier.CFR);

        assetService.upsertAssetBO(assetBO, "Test");

        Asset updatedAsset = assetService.getAssetById(createdAsset.getId());

        assertThat(updatedAsset.getCfr(), is(createdAsset.getCfr()));
        assertThat(updatedAsset.getIrcs(), is(assetUpdates.getIrcs()));
        assertThat(updatedAsset.getMmsi(), is(assetUpdates.getMmsi()));
        assertThat(updatedAsset.getName(), is(assetUpdates.getName()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetBOWithIdentifierIRCS() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "Test");

        Asset assetUpdates = AssetTestsHelper.createBasicAsset();
        assetUpdates.setIrcs(createdAsset.getIrcs());

        AssetBO assetBO = new AssetBO();
        assetBO.setAsset(assetUpdates);
        assetBO.setDefaultIdentifier(AssetIdentifier.IRCS);

        assetService.upsertAssetBO(assetBO, "Test");

        Asset updatedAsset = assetService.getAssetById(createdAsset.getId());

        assertThat(updatedAsset.getIrcs(), is(createdAsset.getIrcs()));
        assertThat(updatedAsset.getCfr(), is(assetUpdates.getCfr()));
        assertThat(updatedAsset.getMmsi(), is(assetUpdates.getMmsi()));
        assertThat(updatedAsset.getName(), is(assetUpdates.getName()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetBOWithIdentifierNullCFR() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "Test");

        Asset assetUpdates = AssetTestsHelper.createBasicAsset();
        assetUpdates.setCfr(null);
        assetUpdates.setIrcs(createdAsset.getIrcs());

        AssetBO assetBO = new AssetBO();
        assetBO.setAsset(assetUpdates);
        assetBO.setDefaultIdentifier(AssetIdentifier.CFR);

        assetService.upsertAssetBO(assetBO, "Test");

        Asset updatedAsset = assetService.getAssetById(createdAsset.getId());

        assertThat(updatedAsset.getIrcs(), is(createdAsset.getIrcs()));
        assertThat(updatedAsset.getCfr(), is(assetUpdates.getCfr()));
        assertThat(updatedAsset.getMmsi(), is(assetUpdates.getMmsi()));
        assertThat(updatedAsset.getName(), is(assetUpdates.getName()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetLimitedTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");

        createdAsset.setName("NewName");
        assetService.updateAsset(asset, "test", "comment");

        List<Asset> revisions = assetService.getRevisionsForAsset(createdAsset.getId());
        assertEquals(2, revisions.size());

        List<Asset> revisions2 = assetService.getRevisionsForAssetLimited(createdAsset.getId(), 10);
        assertEquals(2, revisions2.size());

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetLimitedMaxNumberTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");

        createdAsset.setName("NewName");
        assetService.updateAsset(asset, "test", "comment");

        List<Asset> revisions = assetService.getRevisionsForAsset(createdAsset.getId());
        assertEquals(2, revisions.size());

        List<Asset> revisions2 = assetService.getRevisionsForAssetLimited(createdAsset.getId(), 1);
        assertEquals(1, revisions2.size());

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAssetTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        assetService.archiveAsset(createdAsset, "test", "archived");

        Asset assetByCfr = assetService.getAssetById(AssetIdentifier.CFR, createdAsset.getCfr());

        assertFalse(assetByCfr.getActive());
    }

    @Test
    @OperateOnDeployment("normal")
    public void unarchiveAssetTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");

        Asset archived = assetService.archiveAsset(createdAsset, "test", "archived");
        assertFalse(archived.getActive());
        Asset assetByCfr = assetService.getAssetById(AssetIdentifier.CFR, createdAsset.getCfr());
        assertFalse(assetByCfr.getActive());

        Asset unarchived = assetService.unarchiveAsset(archived.getId(), "test", "archived");
        assertTrue(unarchived.getActive());
        Asset assetByCfr2 = assetService.getAssetById(AssetIdentifier.CFR, createdAsset.getCfr());
        assertNotNull(assetByCfr2);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListTestIdQuery() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetService.createAsset(asset, "test");

        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.GUID, asset.getId().toString()));

        List<Asset> assets = assetService.getAssetList(trunk, 1, 100, false).getAssetList();

        assertEquals(1, assets.size());
        assertEquals(asset.getCfr(), assets.get(0).getCfr());
        assetService.deleteAsset(AssetIdentifier.GUID, asset.getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListTestNameQuery() {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset.setName("getAssetListTestNameQueryTest");
        asset = assetService.createAsset(asset, "test");

        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.NAME, asset.getName()));

        List<Asset> assets = assetService.getAssetList(trunk, 1, 100, false).getAssetList();

        assertFalse(assets.isEmpty());
        assertEquals(asset.getCfr(), assets.get(0).getCfr());
        assetService.deleteAsset(AssetIdentifier.GUID, asset.getId().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNotesTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        Note note = AssetTestsHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note, "test");

        Map<String, Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void addNoteTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        Note note = AssetTestsHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note, "test");

        Note note2 = AssetTestsHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note2, "test");

        Map<String, Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(2, notes.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteNoteTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createNoteForAsset(asset.getId(), AssetTestsHelper.createBasicNote(), "test");
        assetService.createNoteForAsset(asset.getId(), AssetTestsHelper.createBasicNote(), "test");

        Map<String, Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(2, notes.size());

        Note toBeDeleted = notes.values().iterator().next();
        assetService.deleteNote(toBeDeleted.getId(), toBeDeleted.getCreatedBy());

        notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteSomeoneElsesNoteTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createNoteForAsset(asset.getId(), AssetTestsHelper.createBasicNote(), "test");

        Map<String, Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());

        Note toBeDeleted = notes.values().iterator().next();
        Exception exception = assertThrows(Exception.class, () -> assetService.deleteNote(toBeDeleted.getId(), "Someone else"));
        assertThat(exception.getMessage(), containsString("Can only delete notes created by the same user"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateSomeoneElsesNoteTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createNoteForAsset(asset.getId(), AssetTestsHelper.createBasicNote(), "test");

        Map<String, Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());

        Note toBeUpdated = notes.values().iterator().next();
        toBeUpdated.setNote("Something completely different");
        Exception exception = assertThrows(Exception.class, () -> assetService.updateNote(toBeUpdated, "Someone else"));
        assertThat(exception.getMessage(), containsString("Can only change notes created by the same user"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createContactInfoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        ContactInfo contactInfo = AssetTestsHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo, "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(1, contacts.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void addContactInfoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        ContactInfo contactInfo = AssetTestsHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo, "test");

        ContactInfo contactInfo2 = AssetTestsHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo2, "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(2, contacts.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteContactInfoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createContactInfoForAsset(asset.getId(), AssetTestsHelper.createBasicContactInfo(), "test");
        assetService.createContactInfoForAsset(asset.getId(), AssetTestsHelper.createBasicContactInfo(), "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(2, contacts.size());

        assetService.deleteContactInfo(contacts.get(0).getId());

        contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(1, contacts.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRequiredEnrichment() {
        // create stuff so we can create a valid rawMovement
        Asset asset = createAsset();
        AssetFilter createdAssetFilter = createAssetGroup(asset);
        UUID createdAssetGroupId = createdAssetFilter.getId();
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminalWithAsset(asset);

        mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");

        AssetMTEnrichmentRequest request = createRequest(asset, mobileTerminal);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertNotNull(response.getMobileTerminalType());
        String assetUUID = response.getAssetUUID();
        assertEquals(asset.getId(), UUID.fromString(assetUUID));

        List<String> fetchedAssetGroups = response.getAssetFilterList();
        assertThat(fetchedAssetGroups, is(notNullValue()));
        assertThat(fetchedAssetGroups, is(not(empty())));
        assertTrue(fetchedAssetGroups.contains(createdAssetGroupId.toString()));
        assertEquals(request.getSerialNumberValue(), response.getSerialNumber());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRequiredEnrichmentOnlyMT_InmarsatSpecific() {
        // create stuff so we can create a valid rawMovement
        Asset asset = createAsset();
        AssetFilter createdAssetFilter = createAssetGroup(asset);
        UUID createdAssetGroupId = createdAssetFilter.getId();
        MobileTerminal mobileTerminal = createMobileTerminal(asset);
        mobileTerminal.setArchived(false);

        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();

        // put membernumber into request
        // put dnid into request
        // shall give Asset and Mobileterminal in response

        Set<Channel> channels = mobileTerminal.getChannels();
        Channel channel = channels.iterator().next();
        String dnid = String.valueOf(channel.getDnid());
        String memberNumber = String.valueOf(channel.getMemberNumber());

        request.setMemberNumberValue(memberNumber);
        request.setDnidValue(dnid);
        request.setIdValue(asset.getId());
        request.setTranspondertypeValue(mobileTerminal.getMobileTerminalType().toString());

        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertNotNull(response.getMobileTerminalType());
        String assetUUID = response.getAssetUUID();
        assertEquals(asset.getId(), UUID.fromString(assetUUID));

        List<String> fetchedAssetGroups = response.getAssetFilterList();
        assertThat(fetchedAssetGroups, is(notNullValue()));
        assertThat(fetchedAssetGroups, is(not(empty())));
        assertTrue(fetchedAssetGroups.contains(createdAssetGroupId.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRequiredEnrichment_NO_MOBILETERMINAL() {
        // create stuff so we can create a valid rawMovement
        Asset asset = createAsset();
        AssetFilter createdAssetFilter = createAssetGroup(asset);
        UUID createdAssetGroupId = createdAssetFilter.getId();

        AssetMTEnrichmentRequest request = createRequest(asset, null);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        String assetUUID = response.getAssetUUID();
        assertEquals(asset.getId(), UUID.fromString(assetUUID));

        List<String> fetchedAssetGroups = response.getAssetFilterList();
        assertThat(fetchedAssetGroups, is(notNullValue()));
        assertThat(fetchedAssetGroups, is(not(empty())));
        assertTrue(fetchedAssetGroups.contains(createdAssetGroupId.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTAssingCorrectAssetFromMTInformation() {
        String dnid = "1234";

        Asset asset1 = createAsset();
        MobileTerminal mobileTerminal1 = testPollHelper.createBasicMobileTerminal();
        mobileTerminal1.getChannels().iterator().next().setDnid(Integer.parseInt(dnid));
        mobileTerminal1.setAsset(asset1);
        mobileTerminal1 = mobileTerminalService.createMobileTerminal(mobileTerminal1, "TEST");
        MobileTerminalPlugin plugin = mobileTerminal1.getPlugin();

        Asset asset2 = createAsset();
        MobileTerminal mobileTerminal2 = testPollHelper.createBasicMobileTerminal();
        mobileTerminal2.setPlugin(plugin);
        mobileTerminal2.getChannels().iterator().next().setDnid(Integer.parseInt(dnid));
        mobileTerminal2.setAsset(asset2);
        mobileTerminal2 = mobileTerminalService.createMobileTerminal(mobileTerminal2, "TEST");

        Asset asset3 = createAsset();
        MobileTerminal mobileTerminal3 = testPollHelper.createBasicMobileTerminal();
        mobileTerminal3.setPlugin(plugin);
        mobileTerminal3.getChannels().iterator().next().setDnid(Integer.parseInt(dnid));
        mobileTerminal3.setAsset(asset3);
        mobileTerminal3 = mobileTerminalService.createMobileTerminal(mobileTerminal3, "TEST");

        AssetMTEnrichmentRequest request = createRequest(mobileTerminal1);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertThat(response.getAssetUUID(), is(asset1.getId().toString()));

        AssetMTEnrichmentRequest request2 = createRequest(mobileTerminal2);
        AssetMTEnrichmentResponse response2 = assetService.collectAssetMT(request2);
        assertThat(response2.getAssetUUID(), is(asset2.getId().toString()));

        AssetMTEnrichmentRequest request3 = createRequest(mobileTerminal3);
        AssetMTEnrichmentResponse response3 = assetService.collectAssetMT(request3);
        assertThat(response3.getAssetUUID(), is(asset3.getId().toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTCreateUnknownAsset() {
        Asset nonExisting = AssetTestsHelper.createBasicAsset();
        nonExisting.setName(null);

        AssetMTEnrichmentRequest request = createRequest(nonExisting, null);
        request.setDnidValue(null);
        request.setMemberNumberValue(null);
        request.setSerialNumberValue(null);
        request.setTranspondertypeValue(null);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertTrue(response.getAssetName().startsWith("Unknown"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTDontCreateUnknownAssetForInmarsat() {
        MobileTerminal mobileTerminalNonExisting = testPollHelper.createBasicMobileTerminal();

        AssetMTEnrichmentRequest request = createRequest(mobileTerminalNonExisting);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertThat(response.getAssetName(), is(CoreMatchers.nullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTRequestOnUnlinkedMTInmarsat() {
        MobileTerminal mobileTerminalUnlinked = testPollHelper.createBasicMobileTerminal();
        mobileTerminalUnlinked = mobileTerminalService.createMobileTerminal(mobileTerminalUnlinked, "TEST");

        AssetMTEnrichmentRequest request = createRequest(mobileTerminalUnlinked);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertThat(response.getAssetName(), is(CoreMatchers.nullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTRequestIridiumTransponder() {
        Asset asset = createAsset();
        MobileTerminal iridiumTerminal = testPollHelper.createBasicMobileTerminal();
        iridiumTerminal.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        iridiumTerminal.setAsset(asset);
        iridiumTerminal = mobileTerminalService.createMobileTerminal(iridiumTerminal, "TEST");

        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setTranspondertypeValue(iridiumTerminal.getMobileTerminalType().toString());
        request.setSerialNumberValue(iridiumTerminal.getSerialNo());
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertThat(response.getAssetUUID(), is(asset.getId().toString()));
    }

    private AssetMTEnrichmentRequest createRequest(MobileTerminal mobileTerminal) {

        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();

        Channel channel = mobileTerminal.getChannels().iterator().next();

        // for mobileTerminal
        request.setMemberNumberValue(String.valueOf(channel.getMemberNumber()));
        request.setSerialNumberValue(mobileTerminal.getSerialNo());
        request.setDnidValue(String.valueOf(channel.getDnid()));
        request.setLesValue(channel.getLesDescription());
        request.setTranspondertypeValue(mobileTerminal.getMobileTerminalType().toString());

        return request;
    }

    private AssetMTEnrichmentRequest createRequest(Asset asset, MobileTerminal mobileTerminal) {
        AssetMTEnrichmentRequest request;
        if (mobileTerminal == null) {
            request = new AssetMTEnrichmentRequest();
        } else {
            request = createRequest(mobileTerminal);
        }

        // for asset
        if (asset != null) {
            request.setIdValue(asset.getId());
            request.setCfrValue(asset.getCfr());
            request.setImoValue(asset.getImo());
            request.setIrcsValue(asset.getIrcs());
            request.setMmsiValue(asset.getMmsi());
            request.setGfcmValue(asset.getGfcm());
            request.setUviValue(asset.getUvi());
            request.setIccatValue(asset.getIccat());
        }
        request.setPluginType(PluginType.NAF.value());

        request.setTranspondertypeValue(MobileTerminalTypeEnum.INMARSAT_C.name());
        return request;
    }

    private AssetFilter createAssetGroup(Asset asset) {
        AssetFilter filter = new AssetFilter();
        filter.setUpdatedBy("test");
        filter.setUpdateTime(Instant.now(Clock.systemUTC()));
        filter.setName("The Name");
        filter.setOwner("test");

        AssetFilter createdAssetGroup = assetFilterServiceBean.createAssetFilter(filter, "test");
        AssetFilterQuery assetFilterQuery = new AssetFilterQuery();
        assetFilterQuery.setAssetFilter(createdAssetGroup);

        AssetFilterValue assetFilterValue = new AssetFilterValue();
        assetFilterValue.setAssetFilterQuery(assetFilterQuery);
        assetFilterValue.setValueString(asset.getId().toString());

        assetFilterQuery.setType("GUID");
        assetFilterQuery.setValueType(AssetFilterValueType.STRING);
        assetFilterQuery.setValues(new HashSet<>(List.of(assetFilterValue)));
        assetFilterServiceBean.createAssetFilterQuery(filter.getId(), assetFilterQuery);

        return createdAssetGroup;
    }

    private Asset createAsset() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        return assetService.createAsset(asset, "TEST");
    }

    private MobileTerminal createMobileTerminal(Asset asset) {
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);
        return mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");
    }
}
