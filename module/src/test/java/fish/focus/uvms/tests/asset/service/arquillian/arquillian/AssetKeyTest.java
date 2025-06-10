package fish.focus.uvms.tests.asset.service.arquillian.arquillian;

import fish.focus.uvms.asset.domain.constant.AssetIdentifier;
import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.message.AssetTestHelper;
import fish.focus.uvms.tests.BuildAssetServiceDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

/**
 * Main focus for this testclass is to verify that the keyhandling is ok
 * since it is divided with different columns for different keytypes
 */

@RunWith(Arquillian.class)
public class AssetKeyTest extends BuildAssetServiceDeployment {

    private final AssetTestsHelper assetTestsHelper = new AssetTestsHelper();

    @Inject
    private AssetDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IRCS() {
        create(AssetIdentifier.IRCS, "IRCSVAL");
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_MMSI() {
        create(AssetIdentifier.MMSI, AssetTestHelper.getRandomIntegers(9)); // MUST be 9 in length
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_CFR() {
        create(AssetIdentifier.CFR, "CFR_VAL" + UUID.randomUUID());
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IMO() {
        create(AssetIdentifier.IMO, "IMO_VAL");
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_UVI() {
        String val = UUID.randomUUID().toString();
        create(AssetIdentifier.UVI, val);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_ICCAT() {
        String val = UUID.randomUUID().toString();
        create(AssetIdentifier.ICCAT, val);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_GFCM() {
        String val = UUID.randomUUID().toString();
        create(AssetIdentifier.GFCM, val);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IRCS() {
        AssetIdentifier keyType = AssetIdentifier.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val);
        String createdIRCS = theCreatedAsset.getIrcs();

        Asset fetchedEntity = get(keyType, createdIRCS);
        String fetchedIRCS = fetchedEntity.getIrcs();
        assertEquals(createdIRCS, fetchedIRCS);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_MMSI() {
        AssetIdentifier keyType = AssetIdentifier.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val);
        String createdMMSI = theCreatedAsset.getMmsi();

        Asset fetchedEntity = get(keyType, createdMMSI);
        String fetchedMMSI = fetchedEntity.getMmsi();
        assertEquals(createdMMSI, fetchedMMSI);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_CFR() {
        AssetIdentifier keyType = AssetIdentifier.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val);
        String createdCFR = theCreatedAsset.getCfr();

        Asset fetchedEntity = get(keyType, createdCFR);
        String fetchedCFR = fetchedEntity.getCfr();
        assertEquals(createdCFR, fetchedCFR);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IMO() {
        AssetIdentifier keyType = AssetIdentifier.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val);
        String createdIMO = theCreatedAsset.getImo();

        Asset fetchedEntity = get(keyType, createdIMO);
        String fetchedIMO = fetchedEntity.getImo();
        assertEquals(createdIMO, fetchedIMO);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_ICCAT() {
        AssetIdentifier keyType = AssetIdentifier.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdICCAT = theCreatedAsset.getIccat();

        Asset fetchedEntity = get(keyType, createdICCAT);
        String fetchedUUID = fetchedEntity.getIccat();
        assertEquals(createdICCAT, fetchedUUID);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_UVI() {
        AssetIdentifier keyType = AssetIdentifier.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdUvi = theCreatedAsset.getUvi();

        Asset fetchedEntity = get(keyType, createdUvi);
        String fetchedUvi = fetchedEntity.getUvi();
        assertEquals(createdUvi, fetchedUvi);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_GFCM() {
        AssetIdentifier keyType = AssetIdentifier.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdGfcm = theCreatedAsset.getGfcm();

        Asset fetchedEntity = get(keyType, createdGfcm);
        String fetchedGfcm = fetchedEntity.getGfcm();
        assertEquals(fetchedGfcm, createdGfcm);
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IRCS() {
        AssetIdentifier keyType = AssetIdentifier.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val);
        String createdIRCS = theCreatedAsset.getIrcs();
        deleteAssetAndValidateNull(keyType, theCreatedAsset, createdIRCS);
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_MMSI() {
        AssetIdentifier keyType = AssetIdentifier.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val);
        String createdMMSI = theCreatedAsset.getMmsi();
        deleteAssetAndValidateNull(keyType, theCreatedAsset, createdMMSI);
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_CFR() {
        AssetIdentifier keyType = AssetIdentifier.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val);
        String createdCFR = theCreatedAsset.getCfr();
        deleteAssetAndValidateNull(keyType, theCreatedAsset, createdCFR);
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IMO() {
        AssetIdentifier keyType = AssetIdentifier.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val);
        String createdIMO = theCreatedAsset.getImo();
        deleteAssetAndValidateNull(keyType, theCreatedAsset, createdIMO);
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_ICCAT() {
        AssetIdentifier keyType = AssetIdentifier.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdIccat = theCreatedAsset.getIccat();
        deleteAssetAndValidateNull(keyType, theCreatedAsset, createdIccat);
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_UVI() {
        AssetIdentifier keyType = AssetIdentifier.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdUvi = theCreatedAsset.getUvi();
        deleteAssetAndValidateNull(keyType, theCreatedAsset, createdUvi);
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_GFCM() {
        AssetIdentifier keyType = AssetIdentifier.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdGFCM = theCreatedAsset.getGfcm();
        deleteAssetAndValidateNull(keyType, theCreatedAsset, createdGFCM);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_IRCS() {
        AssetIdentifier keyType = AssetIdentifier.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val);
        String createdIRCS = theCreatedAsset.getIrcs();
        updateAssetAndValidate(keyType, theCreatedAsset, createdIRCS);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_MMSI() {
        AssetIdentifier keyType = AssetIdentifier.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val);
        String createdMMSI = theCreatedAsset.getMmsi();
        updateAssetAndValidate(keyType, theCreatedAsset, createdMMSI);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_CFR() {
        AssetIdentifier keyType = AssetIdentifier.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val);
        String createdCFR = theCreatedAsset.getCfr();
        updateAssetAndValidate(keyType, theCreatedAsset, createdCFR);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_IMO() {
        AssetIdentifier keyType = AssetIdentifier.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val);
        String createdIMO = theCreatedAsset.getImo();
        updateAssetAndValidate(keyType, theCreatedAsset, createdIMO);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_ICCAT() {
        AssetIdentifier keyType = AssetIdentifier.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdICCAT = theCreatedAsset.getIccat();
        updateAssetAndValidate(keyType, theCreatedAsset, createdICCAT);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_UVI() {
        AssetIdentifier keyType = AssetIdentifier.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdUVI = theCreatedAsset.getUvi();
        updateAssetAndValidate(keyType, theCreatedAsset, createdUVI);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_GFCM() {
        AssetIdentifier keyType = AssetIdentifier.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdGFCM = theCreatedAsset.getGfcm();
        updateAssetAndValidate(keyType, theCreatedAsset, createdGFCM);
    }

    private Asset get(AssetIdentifier assetIdType, String value) {
        return getAssetHelper(assetIdType, value);
    }

    private Asset getAssetHelper(AssetIdentifier assetIdType, String value) {
        Asset fetchedAsset;
        switch (assetIdType) {
            case CFR:
                fetchedAsset = assetDao.getAssetByCfr(value);
                break;
            case IMO:
                fetchedAsset = assetDao.getAssetByImo(value);
                break;
            case IRCS:
                fetchedAsset = assetDao.getAssetByIrcs(value);
                break;
            case MMSI:
                fetchedAsset = assetDao.getAssetByMmsi(value);
                break;
            case ICCAT:
                fetchedAsset = assetDao.getAssetByIccat(value);
                break;
            case UVI:
                fetchedAsset = assetDao.getAssetByUvi(value);
                break;
            case GFCM:
                fetchedAsset = assetDao.getAssetByGfcm(value);
                break;
            default:
                fetchedAsset = null;
        }
        return fetchedAsset;
    }

    private Asset create(AssetIdentifier key, String value) {
        Asset assetEntity = assetTestsHelper.createAssetHelper(key, value);
        Asset createdAsset = assetDao.createAsset(assetEntity);
        UUID guid = createdAsset.getId();
        Asset fetchedAsset = assetDao.getAssetById(guid);
        assertEquals(createdAsset.getId(), fetchedAsset.getId());

        return fetchedAsset;
    }

    private void updateAssetAndValidate(AssetIdentifier keyType, Asset theCreatedAsset, String createdIRCS) {
        theCreatedAsset.setUpdatedBy("CHANGED");
        assetDao.updateAsset(theCreatedAsset);
        Asset fetchedAsset = get(keyType, createdIRCS);
        assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
    }

    private void deleteAssetAndValidateNull(AssetIdentifier keyType, Asset theCreatedAsset, String createdIRCS) {
        assetDao.deleteAsset(theCreatedAsset);
        Asset fetchedAsset = get(keyType, createdIRCS);
        assertThat(fetchedAsset, is(nullValue()));
    }
}
