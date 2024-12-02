package fish.focus.uvms.asset.message;

import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.AssetRemapMapping;
import fish.focus.uvms.tests.TransactionalTests;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.wsdl.asset.types.AssetIdType;
import fish.focus.wsdl.asset.types.CarrierSource;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetReMappingTest extends TransactionalTests {

    private final JMSHelper jmsHelper = new JMSHelper();

    @Inject
    private AssetDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void assetInformationTest2CheckIfAssetMappingsContainsInfo() throws Exception {
        Asset assetWithsMMSI = AssetTestHelper.createBasicAsset();
        assetWithsMMSI.setIrcs(null);
        assetWithsMMSI.setName("ShouldNotBeThis");
        jmsHelper.upsertAsset(assetWithsMMSI);

        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);

                    assertThat(assetById, is(notNullValue()));
                });

        fish.focus.uvms.asset.domain.entity.Asset mmsiEntity = assetDao.getAssetByMmsi(assetWithsMMSI.getMmsiNo());
        mmsiEntity.setComment("Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!");
        UUID mmsiEntityId = mmsiEntity.getId();

        Asset assetWithsIRCS = AssetTestHelper.createBasicAsset();
        assetWithsIRCS.setMmsiNo(null);
        assetWithsIRCS.setName("namnetestfall2");
        assetWithsIRCS.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsIRCS);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);

                    assertThat(assetById, is(notNullValue()));
                });

        fish.focus.uvms.asset.domain.entity.Asset ircsEntity = assetDao.getAssetByIrcs(assetWithsIRCS.getIrcs());

        userTransaction.commit();
        userTransaction.begin();

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(assetWithsMMSI.getMmsiNo());
        newAsset.setIrcs(assetWithsIRCS.getIrcs());
        newAsset.setName("ShouldNotBeThis2");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.assetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                    assertNotNull(fetchedAsset.getName());
                    assertThat(fetchedAsset.getName(), is(equalTo(assetWithsIRCS.getName())));
                    assertNotNull(fetchedAsset.getMmsiNo());
                    assertNotNull(fetchedAsset.getIrcs());
                });

        List<AssetRemapMapping> mappingList = assetDao.getAllAssetRemappings();
        assertTrue(mappingList.stream().anyMatch(mapping -> (mapping.getOldAssetId().equals(mmsiEntityId) && mapping.getNewAssetId().equals(ircsEntity.getId()))));

        mmsiEntity = assetDao.getAssetById(mmsiEntityId);
        assertEquals(255, mmsiEntity.getComment().length());
        assertFalse(mmsiEntity.getActive());

        assetDao.deleteAsset(mmsiEntity);
        assetDao.deleteAsset(ircsEntity);

        userTransaction.commit();
        userTransaction.begin();
    }
}
