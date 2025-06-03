package fish.focus.uvms.tests.asset.service.arquillian.arquillian;

import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.domain.entity.AssetRemapMapping;
import fish.focus.uvms.mobileterminal.timer.AssetRemapTask;
import fish.focus.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetRemapTest extends TransactionalTests {

    @Inject
    AssetRemapTask assetRemapTask;

    @Inject
    AssetDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void assetRemapTest() {
        Asset oldAsset = AssetTestsHelper.createBasicAsset();
        oldAsset = assetDao.createAsset(oldAsset);
        Asset newAsset = AssetTestsHelper.createBasicAsset();
        newAsset = assetDao.createAsset(newAsset);
        AssetRemapMapping assetRemapMapping = new AssetRemapMapping();
        assetRemapMapping.setOldAssetId(oldAsset.getId());
        assetRemapMapping.setNewAssetId(newAsset.getId());
        assetRemapMapping.setCreatedDate(Instant.now());

        assetDao.createAssetRemapMapping(assetRemapMapping);

        System.setProperty("MovementsRemapped", "0");
        assetRemapTask.remap();
        System.clearProperty("MovementsRemapped");

        assertFalse(assetDao.getAllAssetRemappings().isEmpty());

        oldAsset = assetDao.getAssetById(oldAsset.getId());
        assertNotNull(oldAsset);
        newAsset = assetDao.getAssetById(newAsset.getId());
        assertNotNull(newAsset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void assetRemapWithOldAssetMappingTest() {
        Asset oldAsset = AssetTestsHelper.createBasicAsset();
        oldAsset = assetDao.createAsset(oldAsset);
        Asset newAsset = AssetTestsHelper.createBasicAsset();
        newAsset = assetDao.createAsset(newAsset);
        AssetRemapMapping assetRemapMapping = new AssetRemapMapping();
        assetRemapMapping.setOldAssetId(oldAsset.getId());
        assetRemapMapping.setNewAssetId(newAsset.getId());
        assetRemapMapping.setCreatedDate(Instant.now().minus(4, ChronoUnit.HOURS));

        assetRemapMapping = assetDao.createAssetRemapMapping(assetRemapMapping);

        String id = assetRemapMapping.getId().toString();

        System.setProperty("MovementsRemapped", "0");
        assetRemapTask.remap();
        System.clearProperty("MovementsRemapped");

        List<AssetRemapMapping> mappingList = assetDao.getAllAssetRemappings();
        assertFalse(mappingList.stream().anyMatch(mapping -> mapping.getId().toString().equals(id)));

        oldAsset = assetDao.getAssetById(oldAsset.getId());
        assertNull(oldAsset);
        newAsset = assetDao.getAssetById(newAsset.getId());
        assertNotNull(newAsset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void assetRemapWithOldAssetThatStillHasMovementsMappingTest() {
        Asset oldAsset = AssetTestsHelper.createBasicAsset();
        oldAsset = assetDao.createAsset(oldAsset);
        Asset newAsset = AssetTestsHelper.createBasicAsset();
        newAsset = assetDao.createAsset(newAsset);
        AssetRemapMapping assetRemapMapping = new AssetRemapMapping();
        assetRemapMapping.setOldAssetId(oldAsset.getId());
        assetRemapMapping.setNewAssetId(newAsset.getId());
        assetRemapMapping.setCreatedDate(Instant.now().minus(4, ChronoUnit.HOURS));

        assetRemapMapping = assetDao.createAssetRemapMapping(assetRemapMapping);

        String id = assetRemapMapping.getId().toString();

        System.setProperty("MovementsRemapped", "777");
        assetRemapTask.remap();
        System.clearProperty("MovementsRemapped");

        List<AssetRemapMapping> mappingList = assetDao.getAllAssetRemappings();
        assertTrue(mappingList.stream().anyMatch(mapping -> mapping.getId().toString().equals(id)));

        oldAsset = assetDao.getAssetById(oldAsset.getId());
        assertNotNull(oldAsset);
        newAsset = assetDao.getAssetById(newAsset.getId());
        assertNotNull(newAsset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void assetRemapOldAssetIsRandomTest() {
        Asset newAsset = AssetTestsHelper.createBasicAsset();
        newAsset = assetDao.createAsset(newAsset);
        AssetRemapMapping assetRemapMapping = new AssetRemapMapping();
        assetRemapMapping.setOldAssetId(UUID.randomUUID());
        assetRemapMapping.setNewAssetId(newAsset.getId());
        assetRemapMapping.setCreatedDate(Instant.now().minus(4, ChronoUnit.HOURS));

        assetRemapMapping = assetDao.createAssetRemapMapping(assetRemapMapping);

        String id = assetRemapMapping.getId().toString();

        System.setProperty("MovementsRemapped", "0");
        assetRemapTask.remap();
        System.clearProperty("MovementsRemapped");

        List<AssetRemapMapping> mappingList = assetDao.getAllAssetRemappings();
        assertFalse(mappingList.stream().anyMatch(mapping -> mapping.getId().toString().equals(id)));

        Asset oldAsset = assetDao.getAssetById(assetRemapMapping.getOldAssetId());
        assertNull(oldAsset);
        newAsset = assetDao.getAssetById(newAsset.getId());
        assertNotNull(newAsset);
    }
}
