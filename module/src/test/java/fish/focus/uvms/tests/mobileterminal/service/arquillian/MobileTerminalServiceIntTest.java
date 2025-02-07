package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.uvms.asset.bean.AssetServiceBean;
import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import fish.focus.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import fish.focus.uvms.tests.TransactionalTests;
import fish.focus.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import fish.focus.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-05-29.
 */
@RunWith(Arquillian.class)
public class MobileTerminalServiceIntTest extends TransactionalTests {

    // TODO we do test on those transactions that are wrong in construction
    private static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";
    private static final String USERNAME = "TEST_USERNAME";
    private static final String NEW_MOBILETERMINAL_TYPE = "IRIDIUM";
    private static final String TEST_COMMENT = "TEST_COMMENT";

    @EJB
    private TestPollHelper testPollHelper;

    @EJB
    private MobileTerminalServiceBean mobileTerminalService;

    @Inject
    private MobileTerminalPluginDaoBean pluginDao;

    @Inject
    private AssetDao assetDao;

    @Inject
    private AssetServiceBean assetService;

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalById() {
        UUID createdMobileTerminalId;
        UUID fetchedMobileTerminalGuid;

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        MobileTerminal createdMobileTerminal = testPollHelper.createAndPersistMobileTerminal(null);
        createdMobileTerminalId = createdMobileTerminal.getId();

        MobileTerminal fetchedMobileTerminal = mobileTerminalService.getMobileTerminalEntityById(createdMobileTerminalId);
        assertNotNull(fetchedMobileTerminal);

        fetchedMobileTerminalGuid = fetchedMobileTerminal.getId();
        assertEquals(fetchedMobileTerminalGuid, createdMobileTerminalId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalWithMultipleChannel_ThenVerifyChannelOrder() {
        MobileTerminal terminal = testPollHelper.createBasicMobileTerminal();
        Channel c2 = testPollHelper.createChannel("VMS2", false, false, false);
        Channel c3 = testPollHelper.createChannel("VMS3", false, false, false);
        Channel c4 = testPollHelper.createChannel("VMS4", false, false, false);
        terminal.getChannels().addAll(Arrays.asList(c2, c3, c4));

        MobileTerminal created = mobileTerminalService.createMobileTerminal(terminal, "Test_User");
        assertNotNull(created);

        for (int i = 0; i < 10; i++) {
            MobileTerminal fetched = mobileTerminalService.getMobileTerminalEntityById(created.getId());
            List<String> nameList1 = new ArrayList<>();
            for (Channel c : fetched.getChannels()) {
                nameList1.add(c.getName());
            }

            MobileTerminal fetched2 = mobileTerminalService.getMobileTerminalEntityById(created.getId());
            List<String> nameList2 = new ArrayList<>();
            for (Channel c : fetched2.getChannels()) {
                nameList2.add(c.getName());
            }

            assertEquals(nameList1, nameList2);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal() {

        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);

        MobileTerminal updated = upsertMobileTerminalEntity(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().name());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalByUpsert() {
        MobileTerminal created = testPollHelper.createBasicMobileTerminal();
        MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(created.getPlugin().getPluginServiceName());
        if (plugin == null) {
            plugin = pluginDao.createMobileTerminalPlugin(created.getPlugin());
        }
        created.setPlugin(plugin);
        assertNotNull(created);

        created.setId(null);
        MobileTerminal updated = upsertMobileTerminalEntity(created);

        assertNotNull(updated);
        assertNotNull(updated.getId());
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().name());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);

        MobileTerminal updated = updateMobileTerminal(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().toString());
        assertEquals(TerminalSourceEnum.INTERNAL, updated.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignMobileTerminal() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        Asset asset = createAndPersistAsset();
        assertNotNull(created);

        UUID guid = created.getId();

        MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(asset.getId(), guid, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);
    }

    @Test
    @OperateOnDeployment("normal")
    public void unAssignMobileTerminalFromCarrier() {
        MobileTerminal persistMobileTerminal = testPollHelper.createAndPersistMobileTerminal(null);
        Asset persistAsset = createAndPersistAsset();
        assertNotNull(persistMobileTerminal.getId());
        assertNotNull(persistAsset.getId());

        UUID mobileTerminalId = persistMobileTerminal.getId();
        UUID assetId = persistAsset.getId();

        MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(assetId, mobileTerminalId, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);
        assertNotNull(mobileTerminal.getAsset());
        assertEquals(1, persistAsset.getMobileTerminals().size());

        mobileTerminal = mobileTerminalService.unAssignMobileTerminal(assetId, mobileTerminalId, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);
        assertNull(mobileTerminal.getAsset());
        assertEquals(0, persistAsset.getMobileTerminals().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAndUnarchiveMobileTerminal() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertFalse(created.getArchived());

        created.setArchived(true);
        MobileTerminal archived = updateMobileTerminal(created);
        assertTrue(archived.getArchived());

        archived.setArchived(false);
        MobileTerminal unarchived = updateMobileTerminal(created);
        assertFalse(unarchived.getArchived());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_Plugin() {
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        mobileTerminal.setPlugin(null);
        assertThrows(EJBTransactionRolledbackException.class, () -> createMobileTerminalAndFlush(mobileTerminal));
    }

    private void createMobileTerminalAndFlush(MobileTerminal mobileTerminal) {
        mobileTerminalService.createMobileTerminal(mobileTerminal, USERNAME);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Empty_Channel() {
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        Channel emptyChannel = new Channel();
        mobileTerminal.getChannels().add(emptyChannel);
        assertThrows(EJBTransactionRolledbackException.class, () -> createMobileTerminalAndFlush(mobileTerminal));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_SerialNumber() {
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        mobileTerminal.setSerialNo(null);
        assertThrows(ConstraintViolationException.class, () -> createMobileTerminalAndFlush(mobileTerminal));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal_WillFail_Null_TerminalId() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);
        created.setId(null);
        assertThrows(Exception.class, () -> upsertMobileTerminalEntity(created));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFail_Null_TerminalId() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);

        created.setId(null);

        assertThrows(Exception.class, () -> updateMobileTerminal(created));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalOceanRegion_all_false() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminalOceanRegionSupport(null, false, false, false, false);
        assertNotNull(created);
        UUID guid = created.getId();
        MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(guid);
        assertNotNull(mobileTerminal);
        assertFalse(mobileTerminal.getEastAtlanticOceanRegion());
        assertFalse(mobileTerminal.getWestAtlanticOceanRegion());
        assertFalse(mobileTerminal.getPacificOceanRegion());
        assertFalse(mobileTerminal.getIndianOceanRegion());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalOceanRegion_eastAtlantic() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminalOceanRegionSupport(null, true, false, false, false);
        assertNotNull(created);
        UUID guid = created.getId();
        MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(guid);
        assertNotNull(mobileTerminal);
        assertTrue(mobileTerminal.getEastAtlanticOceanRegion());
        assertFalse(mobileTerminal.getWestAtlanticOceanRegion());
        assertFalse(mobileTerminal.getPacificOceanRegion());
        assertFalse(mobileTerminal.getIndianOceanRegion());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalOceanRegion_westAtlantic() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminalOceanRegionSupport(null, false, true, false, false);
        assertNotNull(created);
        UUID guid = created.getId();
        MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(guid);
        assertNotNull(mobileTerminal);
        assertFalse(mobileTerminal.getEastAtlanticOceanRegion());
        assertTrue(mobileTerminal.getWestAtlanticOceanRegion());
        assertFalse(mobileTerminal.getPacificOceanRegion());
        assertFalse(mobileTerminal.getIndianOceanRegion());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalOceanRegion_PacificOcean() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminalOceanRegionSupport(null, false, false, true, false);
        assertNotNull(created);
        UUID guid = created.getId();
        MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(guid);
        assertNotNull(mobileTerminal);
        assertFalse(mobileTerminal.getEastAtlanticOceanRegion());
        assertFalse(mobileTerminal.getWestAtlanticOceanRegion());
        assertTrue(mobileTerminal.getPacificOceanRegion());
        assertFalse(mobileTerminal.getIndianOceanRegion());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalOceanRegion_IndianOcean() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminalOceanRegionSupport(null, false, false, false, true);
        assertNotNull(created);
        UUID guid = created.getId();
        MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(guid);
        assertNotNull(mobileTerminal);
        assertFalse(mobileTerminal.getEastAtlanticOceanRegion());
        assertFalse(mobileTerminal.getWestAtlanticOceanRegion());
        assertFalse(mobileTerminal.getPacificOceanRegion());
        assertTrue(mobileTerminal.getIndianOceanRegion());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignMobileTerminalOceanRegionIOR() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminalOceanRegionSupport(null, false, false, false, true);
        Asset createdAsset = createAndPersistAsset();
        assertNotNull(created);
        UUID guid = created.getId();
        MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(createdAsset.getId(), guid, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);

        Asset fetchedAsset = assetService.getAssetById(createdAsset.getId());
        assertNotNull(fetchedAsset);
        assertTrue(fetchedAsset.getMobileTerminals().iterator().next().getIndianOceanRegion());
        assertFalse(fetchedAsset.getMobileTerminals().iterator().next().getWestAtlanticOceanRegion());
        assertFalse(fetchedAsset.getMobileTerminals().iterator().next().getEastAtlanticOceanRegion());
        assertFalse(fetchedAsset.getMobileTerminals().iterator().next().getPacificOceanRegion());
    }

    private Asset createAndPersistAsset() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        return assetDao.createAsset(asset);
    }

    private MobileTerminal updateMobileTerminal(MobileTerminal created) {
        created.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        created.setSource(TerminalSourceEnum.INTERNAL);
        return mobileTerminalService.updateMobileTerminal(created, TEST_COMMENT, USERNAME);
    }

    private MobileTerminal upsertMobileTerminalEntity(MobileTerminal created) {
        created.setMobileTerminalType(MobileTerminalTypeEnum.getType(NEW_MOBILETERMINAL_TYPE));
        return mobileTerminalService.upsertMobileTerminal(created, TerminalSourceEnum.INTERNAL, USERNAME);
    }
}
