/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.arquillian;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetSEDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;

@RunWith(Arquillian.class)
public class AssetSEDaoTest extends TransactionalTests {

    @Inject
    AssetSEDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void createAssetTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        assertThat(asset.getId(), is(notNullValue()));
        
        AssetSE fetchedAsset = assetDao.getAssetById(asset.getId());
        
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test(expected = AssetDaoException.class)
    @OperateOnDeployment("normal")
    public void createAssetNullInputShouldThrowExceptionTest() throws AssetDaoException {
        assetDao.createAsset(null);
    }
    
    // TODO should test history GUID
    @Ignore
    @Test
    @OperateOnDeployment("normal")
    public void createAssetCheckHistoryGuid() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
       
//        assertThat(asset.getHistoryGuid()), is(notNullValue()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCfrTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByCfr(asset.getCfr());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCfrTestNonExistingCrf() throws AssetDaoException {
        String randomCrf = UUID.randomUUID().toString();
        AssetSE fetchedAsset = assetDao.getAssetByCfr(randomCrf);
        assertThat(fetchedAsset, is(nullValue()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIrcsTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByIrcs(asset.getIrcs());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIrcs(), is(asset.getIrcs()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByImoTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByImo(asset.getImo());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getImo(), is(asset.getImo()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMmsiTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByMmsi(asset.getMmsi());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIccatTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByIccat(asset.getIccat());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIccat(), is(asset.getIccat()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByUviTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByUvi(asset.getUvi());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getUvi(), is(asset.getUvi()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByGfcmTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByGfcm(asset.getGfcm());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getGfcm(), is(asset.getGfcm()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void updateAssetTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        String newName = "UpdatedName";
        asset.setName(newName);
        asset = assetDao.updateAsset(asset);
        assertThat(asset.getName(), is(newName));
        
        AssetSE updatedAsset = assetDao.getAssetById(asset.getId());
        assertThat(updatedAsset.getId(), is(asset.getId()));
        assertThat(updatedAsset.getName(), is(newName));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetSingleRevisionTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        List<AssetSE> assetRevisions = assetDao.getRevisionsForAsset(asset);
        
        assertEquals(1, assetRevisions.size());
        
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetCheckSizeTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        String newName1 = "NewName1";
        asset.setName(newName1);
        asset = assetDao.updateAsset(asset);
       
        List<AssetSE> assetRevisions = assetDao.getRevisionsForAsset(asset);
        
        assertEquals(2, assetRevisions.size());
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetCompareRevisionsTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        AssetSE assetVersion1 = assetDao.createAsset(asset);
        
        String newName1 = "NewName1";
        assetVersion1.setName(newName1);
        AssetSE assetVersion2 = assetDao.updateAsset(assetVersion1);
        
        String newName2 = "NewName2";
        assetVersion2.setName(newName2);
        AssetSE assetVersion3 = assetDao.updateAsset(assetVersion2);
        
        assertThat(assetVersion3.getId(), is(notNullValue()));
        List<AssetSE> assetRevisions = assetDao.getRevisionsForAsset(assetVersion3);
        
        assertEquals(3, assetRevisions.size());
     
        //TODO compare revisions with versions
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtDateSingleAssetTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE assetAtDate = assetDao.getAssetAtDate(asset, LocalDateTime.now(ZoneOffset.UTC));

        assertThat(assetAtDate.getId(), is(notNullValue()));
        
        assertThat(assetAtDate.getName(), is(asset.getName()));
        assertThat(assetAtDate.getCfr(), is(asset.getCfr()));
        assertThat(assetAtDate.getActive(), is(asset.getActive()));
    }
}