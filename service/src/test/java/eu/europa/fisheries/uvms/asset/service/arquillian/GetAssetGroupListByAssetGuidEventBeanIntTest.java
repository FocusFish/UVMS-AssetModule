package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.TextMessage;
import java.util.List;

/**
 * Created by thofan on 2017-06-16.
 */

@RunWith(Arquillian.class)
public class GetAssetGroupListByAssetGuidEventBeanIntTest extends TransactionalTests {




    @EJB
    private AssetGroupService assetGroupService;

    @EJB
    private AssetService assetService;

    @Inject
    InterceptorForTest interceptorForTest;

    @After
    public void teardown() {
        interceptorForTest.recycle();
    }




    @Ignore
    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupListByAssetEvent() throws AssetException {

        /*

        // create 2 asset with homeport   TEST_GOT
        Asset a1 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID, "1"), "test1");
        Asset a2 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"2"), "test2");


        // create 1 asset with homeport   STHLM
        Asset wa3 = AssetHelper.helper_createAsset(AssetIdType.GUID,"3");
        wa3.setHomePort("STHLM");
        Asset a3 = assetService.createAsset(wa3, "test3");


        // create n assetgroup with searchfield GUID value an Assets GUID
        // result should contain the group id for given asset


        AssetGroupWSDL ag = AssetHelper.create_asset_group();
        List<AssetGroupSearchField> searchFields =  ag.getSearchFields();
        AssetGroupSearchField assetGroupSearchField = new AssetGroupSearchField();
        assetGroupSearchField.setKey(ConfigSearchField.GUID);
        assetGroupSearchField.setValue(a1.getAssetId().getGuid());
        searchFields.add(assetGroupSearchField);
        AssetGroupWSDL  assetGroup = assetGroupService.createAssetGroup(ag, "TEST");
        String assetGroupGUID = assetGroup.getGuid();
        em.flush();

        TextMessage textMessage = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(textMessage);

        assetMessageEvent.setAssetGuid(a1.getAssetId().getGuid());
        getAssetGroupListByAssetGuidEventBean.getAssetGroupListByAssetEvent(assetMessageEvent);

        SuccessfulTestEvent successfulTestEvent = interceptorForTest.getSuccessfulTestEvent();
        String result = successfulTestEvent.getMessage();

        Assert.assertTrue(result.contains(assetGroupGUID));

        */

    }








}
