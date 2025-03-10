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
package fish.focus.uvms.rest.asset.service;

import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.dto.AssetListResponse;
import fish.focus.uvms.asset.remote.dto.search.SearchBranch;
import fish.focus.uvms.asset.remote.dto.search.SearchFields;
import fish.focus.uvms.asset.remote.dto.search.SearchLeaf;
import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.rest.asset.AbstractAssetRestTest;
import fish.focus.uvms.rest.asset.AssetHelper;
import fish.focus.uvms.rest.asset.AssetMatcher;
import fish.focus.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class AssetRestResourceQueryTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void newAssetListQueryTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, createdAsset.getCfr());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.IRCS, createdAsset.getIrcs());
        trunk.getFields().add(leaf);

        SearchBranch branch = new SearchBranch(false);
        SearchLeaf subLeaf = new SearchLeaf(SearchFields.FLAG_STATE, "SWE");
        branch.getFields().add(subLeaf);
        subLeaf = new SearchLeaf(SearchFields.FLAG_STATE, "DNK");
        branch.getFields().add(subLeaf);

        trunk.getFields().add(branch);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void assetListQueryWithMappedSearchFieldsTest() {
        String jsonTest = "{\"fields\":[{\"searchField\":\"flagStateCode\",\"searchValue\":\"SWE\"},{\"fields\":[{\"searchField\":\"name\",\"searchValue\":\"*\"},{\"searchField\":\"externalMarking\",\"searchValue\":\"*\"},{\"searchField\":\"CFR\",\"searchValue\":\"*\"},{\"searchField\":\"ircs\",\"searchValue\":\"*\"}],\"logicalAnd\":false}],\"logicalAnd\":true}";

        String listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(jsonTest), String.class);

        assertNotNull(listResponse);
        assertTrue(listResponse, listResponse.length() > 1);
        assertTrue(listResponse.contains("assetList"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryWithGreaterOperatorTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset.setLengthOverAll(220d);
        asset.setFlagStateCode("SWE");
        sendAssetToCreation(asset);
        String jsonTest = "{\"fields\":[{\"searchField\":\"flagStateCode\",\"searchValue\":\"SWE\"},{\"fields\":[{\"searchField\":\"lengthOverAll\",\"searchValue\":200,\"operator\":\">=\"}],\"logicalAnd\":true}],\"logicalAnd\":true}";

        String listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(jsonTest), String.class);

        assertNotNull(listResponse);
        assertTrue(listResponse.length() > 1);
        assertTrue(listResponse.contains("220"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryWithLessOperatorTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset.setLengthOverAll(190d);
        asset.setFlagStateCode("SWE");
        sendAssetToCreation(asset);
        String jsonTest = "{\"fields\":[{\"searchField\":\"flagStateCode\",\"searchValue\":\"SWE\"},{\"fields\":[{\"searchField\":\"lengthOverAll\",\"searchValue\":1,\"operator\":\"<=\"}],\"logicalAnd\":true}],\"logicalAnd\":true}";

        String listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(jsonTest), String.class);

        assertNotNull(listResponse);
        assertTrue(listResponse.length() > 1);
        assertFalse(listResponse.contains("190"));
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, createdAsset.getCfr());
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllAssetsThatAreLongTermParkedTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset.setParked(true);
        Asset createdParkedAsset = sendAssetToCreation(asset);

        asset = AssetHelper.createBasicAsset();
        Asset createdActiveAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.LONG_TERM_PARKED, "true");
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertFalse(listResponse.getAssetList().isEmpty());
        assertTrue(listResponse.getAssetList().stream().anyMatch(a -> a.getId().equals(createdParkedAsset.getId())));
        assertFalse(listResponse.getAssetList().stream().anyMatch(a -> a.getId().equals(createdActiveAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListQueryTestEmptyResult() {
        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, "APA");
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(0));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void testCaseSensitiveness() {
        String cfrValue = UUID.randomUUID().toString().substring(0, 11).toUpperCase();
        Asset asset = AssetHelper.createBasicAsset();
        asset.setCfr(cfrValue);
        sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, cfrValue);
        trunk.getFields().add(leaf);

        SearchBranch trunk2 = new SearchBranch(true);
        SearchLeaf leaf2 = new SearchLeaf(SearchFields.CFR, cfrValue.toLowerCase());
        trunk2.getFields().add(leaf2);

        AssetListResponse listResponse1 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk2), AssetListResponse.class);

        assertNotNull(listResponse1);
        assertNotNull(listResponse2);
        assertThat(listResponse1.getAssetList(), is(not(empty())));
        assertThat(listResponse2.getAssetList(), is(not(empty())));

        Asset asset1 = listResponse1.getAssetList().get(0);
        Asset asset2 = listResponse2.getAssetList().get(0);

        assertEquals(asset1.getCfr(), asset2.getCfr());
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void testCaseSensitivenessTwoEntities() {
        String cfrValue = UUID.randomUUID().toString().substring(0, 11).toUpperCase();

        String cfrValue2 = UUID.randomUUID().toString().substring(0, 11).toUpperCase();

        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setCfr(cfrValue);
        sendAssetToCreation(asset1);

        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setCfr(cfrValue2);
        sendAssetToCreation(asset2);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, cfrValue);
        trunk.getFields().add(leaf);

        AssetListResponse listResponse1 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse1);
        assertEquals(1, listResponse1.getAssetList().size());

        SearchBranch trunk2 = new SearchBranch(false);
        SearchLeaf leaf2 = new SearchLeaf(SearchFields.CFR, cfrValue.toLowerCase());
        trunk2.getFields().add(leaf2);
        leaf2 = new SearchLeaf(SearchFields.CFR, cfrValue2.toLowerCase());
        trunk2.getFields().add(leaf2);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk2), AssetListResponse.class);

        assertNotNull(listResponse2);
        assertEquals(2, listResponse2.getAssetList().size());

        Asset fetchedAsset1 = listResponse1.getAssetList().get(0);
        Asset fetchedAsset2 = listResponse2.getAssetList().get(0);
        String cfrToCompare = fetchedAsset1.getId().equals(fetchedAsset2.getId()) ?
                listResponse2.getAssetList().get(0).getCfr() :
                listResponse2.getAssetList().get(1).getCfr();

        assertEquals(fetchedAsset1.getCfr(), cfrToCompare);
        assertEquals((fetchedAsset2.getCfr().equals(fetchedAsset1.getCfr()) ?
                        listResponse2.getAssetList().get(1).getCfr() :
                        listResponse2.getAssetList().get(0).getCfr())
                , cfrValue2);
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void testCaseIncompleteCFR() {
        String cfrValue = UUID.randomUUID().toString().substring(0, 11).toUpperCase();
        String cfrValue2 = UUID.randomUUID().toString().substring(0, 11).toUpperCase();

        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setCfr(cfrValue);
        sendAssetToCreation(asset1);

        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setCfr(cfrValue2);
        sendAssetToCreation(asset2);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, cfrValue);
        trunk.getFields().add(leaf);

        SearchBranch trunk2 = new SearchBranch(false);
        SearchLeaf leaf2 = new SearchLeaf(SearchFields.CFR, cfrValue.toLowerCase().substring(5));
        trunk2.getFields().add(leaf2);
        leaf2 = new SearchLeaf(SearchFields.CFR, cfrValue2.toLowerCase().substring(3, 8));
        trunk2.getFields().add(leaf2);

        AssetListResponse listResponse1 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk2), AssetListResponse.class);

        assertNotNull(listResponse1);
        assertNotNull(listResponse2);
        assertThat(listResponse1.getAssetList(), is(not(empty())));
        assertTrue(listResponse2.getAssetList().size() >= 2);

        boolean found = false;
        for (Asset asset : listResponse2.getAssetList()) {
            if (asset.getCfr().equals(cfrValue)) {
                found = true;
                break;
            }
        }

        assertTrue(found);

        found = false;
        for (Asset asset : listResponse2.getAssetList()) {
            if (asset.getCfr().equals(cfrValue2)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListEmptyCriteriaShouldReturnAllAssets() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListEmptyCriteriaShouldReturnAllAssetsOR() {
        // create an asset
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        // empty query
        SearchBranch trunk = new SearchBranch(false);

        // ask for everything since query is empty
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListEmptyCriteriaShouldNotReturnInactivatedAssets() {
        Asset asset = AssetHelper.createBasicAsset();
        // create an Asset
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        int sizeBefore = listResponse.getAssetList().size();

        // Archive the asset
        Asset archived = getWebTargetExternal()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("archive")
                .queryParam("comment", "The best test comment")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), Asset.class);

        assertFalse(archived.getActive());

        // ask for it
        AssetListResponse listResponseAfter = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertEquals(sizeBefore - 1, listResponseAfter.getAssetList().size());
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListPaginationTest() {
        String customFlagState = AssetHelper.getRandomIntegers(3);

        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setFlagStateCode(customFlagState);
        asset1.setName("Test asset 1");
        sendAssetToCreation(asset1);

        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setFlagStateCode(customFlagState);
        asset2.setName("Test asset 2");
        sendAssetToCreation(asset2);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.FLAG_STATE, customFlagState);
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("page", 1)
                .queryParam("size", 1)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertThat(listResponse.getCurrentPage(), is(1));
        assertThat(listResponse.getTotalNumberOfPages(), is(2));
        assertThat(listResponse.getAssetList().size(), is(1));

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("page", 2)
                .queryParam("size", 1)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertThat(listResponse2.getCurrentPage(), is(2));
        assertThat(listResponse2.getTotalNumberOfPages(), is(2));
        assertThat(listResponse2.getAssetList().size(), is(1));

        assertThat(listResponse.getAssetList().get(0).getId(), is(not(listResponse2.getAssetList().get(0).getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListWildcardSearchCaseInsensitive() {
        Asset asset = AssetHelper.createBasicAsset();
        String randomNumbers = AssetHelper.getRandomIntegers(10);
        asset.setName("ShipName" + randomNumbers);
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.NAME, "shipn*me" + randomNumbers);
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListFromShipType() {
        Asset asset = AssetHelper.createBasicAsset();
        String testVesselType = "TestVesselType";
        asset.setVesselType(testVesselType);
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.VESSEL_TYPE, testVesselType);
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertTrue(assetList.stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetWithDateSearch() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        Instant timestamp = Instant.now();

        createdAsset.setName(createdAsset.getName() + "UPDATE");
        Asset updatedAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);


        // Get history
        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(timestamp));
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
        assertThat(assetList.get(0).getHistoryId(), is(createdAsset.getHistoryId()));
        assertThat(assetList.get(0).getName(), is(asset.getName()));

        // Get current
        SearchBranch trunk2 = new SearchBranch(true);
        SearchLeaf leaf2 = new SearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk2.getFields().add(leaf2);
        leaf2 = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(Instant.now()));
        trunk2.getFields().add(leaf2);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk2), AssetListResponse.class);

        List<Asset> assetList2 = listResponse2.getAssetList();
        assertThat(assetList2.size(), is(1));
        assertThat(assetList2.get(0).getId(), is(updatedAsset.getId()));
        assertThat(assetList2.get(0).getHistoryId(), is(updatedAsset.getHistoryId()));
        assertThat(assetList2.get(0).getName(), is(updatedAsset.getName()));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetWithDateSearchThreeRevisions() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        Instant timestamp = Instant.now();

        String updatedName1 = createdAsset.getName() + "UPDATE";
        createdAsset.setName(updatedName1);
        Asset updatedAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);

        Instant timestamp2 = Instant.now();

        String updatedName2 = createdAsset.getName() + "UPDATE2";
        updatedAsset.setName(updatedName2);
        getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);

        // Get history1
        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(timestamp));
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
        assertThat(assetList.get(0).getHistoryId(), is(createdAsset.getHistoryId()));
        assertThat(assetList.get(0).getName(), is(asset.getName()));

        // Get history2
        SearchBranch trunk2 = new SearchBranch(true);
        SearchLeaf leaf2 = new SearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk2.getFields().add(leaf2);
        leaf2 = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(timestamp2));
        trunk2.getFields().add(leaf2);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk2), AssetListResponse.class);

        List<Asset> assetList2 = listResponse2.getAssetList();
        assertThat(assetList2.size(), is(1));
        assertThat(assetList2.get(0).getId(), is(updatedAsset.getId()));
        assertThat(assetList2.get(0).getHistoryId(), is(updatedAsset.getHistoryId()));
        assertThat(assetList2.get(0).getName(), is(updatedName1));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetsAtDate() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        Asset asset2 = AssetHelper.createBasicAsset();
        Asset createdAsset2 = sendAssetToCreation(asset2);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(Instant.now()));
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertTrue(assetList.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
        assertTrue(assetList.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset2.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetsAtDateAndIrcsAndFs() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        Asset asset2 = AssetHelper.createBasicAsset();
        sendAssetToCreation(asset2);

        Instant timestamp = Instant.now();

        String newIrcs = "F" + AssetHelper.getRandomIntegers(7);
        createdAsset.setIrcs(newIrcs);
        getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.IRCS, asset.getIrcs());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.FLAG_STATE, asset.getFlagStateCode());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(timestamp));
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
        assertThat(assetList.get(0).getHistoryId(), is(createdAsset.getHistoryId()));
        assertThat(assetList.get(0).getIrcs(), is(asset.getIrcs()));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetAtInvalidDate() {
        Asset asset = AssetHelper.createBasicAsset();
        sendAssetToCreation(asset);

        Instant pastDate = ZonedDateTime.now().minusYears(10).toInstant();

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.IRCS, asset.getIrcs());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.FLAG_STATE, asset.getFlagStateCode());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(pastDate));
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(0));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryGearTypeTest() {
        String gearType = "tempGearType" + AssetHelper.getRandomIntegers(10);
        Asset asset = AssetHelper.createBasicAsset();
        asset.setGearFishingType(gearType);
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.GEAR_TYPE, gearType);
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryGearTypeNoWildcardSearch() {
        String gearType1 = "tempGearType" + AssetHelper.getRandomIntegers(10);
        Asset asset = AssetHelper.createBasicAsset();
        asset.setGearFishingType(gearType1);
        Asset createdAsset = sendAssetToCreation(asset);

        String gearType2 = gearType1 + AssetHelper.getRandomIntegers(5);
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setGearFishingType(gearType2);
        getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset2), Asset.class);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.GEAR_TYPE, gearType1);
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListByOneFlagStateTest() {
        Asset assetSwe = AssetHelper.createBasicAsset();
        assetSwe.setFlagStateCode("SWE");
        Asset createdAssetSwe = sendAssetToCreation(assetSwe);

        Asset assetDnk = AssetHelper.createBasicAsset();
        assetDnk.setFlagStateCode("DNK");
        Asset createdAssetDnk = sendAssetToCreation(assetDnk);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.FLAG_STATE, assetSwe.getFlagStateCode());
        trunk.getFields().add(leaf);

        AssetListResponse output = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", 10000)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(output);
        assertFalse(output.getAssetList().isEmpty());
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetSwe.getId())));
        assertFalse(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetDnk.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListByTwoFlagStateTest() {
        Asset assetSwe = AssetHelper.createBasicAsset();
        assetSwe.setFlagStateCode("SWE");
        Asset createdAssetSwe = sendAssetToCreation(assetSwe);

        Asset assetDnk = AssetHelper.createBasicAsset();
        assetDnk.setFlagStateCode("DNK");
        Asset createdAssetDnk = sendAssetToCreation(assetDnk);

        SearchBranch trunk = new SearchBranch(false);
        SearchLeaf leaf = new SearchLeaf(SearchFields.FLAG_STATE, assetSwe.getFlagStateCode());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.FLAG_STATE, assetDnk.getFlagStateCode());
        trunk.getFields().add(leaf);

        AssetListResponse output = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", 1000)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(output);
        assertFalse(output.getAssetList().isEmpty());
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetSwe.getId())));
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetDnk.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListByTwoFlagStateAndAIRCSTest() {
        Asset assetSwe = AssetHelper.createBasicAsset();
        assetSwe.setFlagStateCode("SWE");
        Asset createdAssetSwe = sendAssetToCreation(assetSwe);

        Asset assetDnk = AssetHelper.createBasicAsset();
        assetDnk.setFlagStateCode("DNK");
        Asset createdAssetDnk = sendAssetToCreation(assetDnk);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.IRCS, assetSwe.getIrcs());
        trunk.getFields().add(leaf);

        SearchBranch branch = new SearchBranch(false);
        leaf = new SearchLeaf(SearchFields.FLAG_STATE, assetSwe.getFlagStateCode());
        branch.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.FLAG_STATE, assetDnk.getFlagStateCode());
        branch.getFields().add(leaf);

        trunk.getFields().add(branch);

        AssetListResponse output = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(output);
        assertFalse(output.getAssetList().isEmpty());
        assertEquals(1, output.getAssetList().size());
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetSwe.getId())));
        assertFalse(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetDnk.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListByTwoFlagStateAndTwoIRCSTest() {
        Asset assetSwe = AssetHelper.createBasicAsset();
        assetSwe.setFlagStateCode("SWE");
        Asset createdAssetSwe = sendAssetToCreation(assetSwe);

        Asset assetDnk = AssetHelper.createBasicAsset();
        assetDnk.setFlagStateCode("DNK");
        Asset createdAssetDnk = sendAssetToCreation(assetDnk);

        SearchBranch branchIrcs = new SearchBranch(false);
        SearchLeaf leaf = new SearchLeaf(SearchFields.IRCS, assetSwe.getIrcs());
        branchIrcs.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.IRCS, assetDnk.getIrcs());
        branchIrcs.getFields().add(leaf);

        SearchBranch branchFlag = new SearchBranch(false);
        leaf = new SearchLeaf(SearchFields.FLAG_STATE, assetSwe.getFlagStateCode());
        branchFlag.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.FLAG_STATE, assetDnk.getFlagStateCode());
        branchFlag.getFields().add(leaf);

        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(branchIrcs);
        trunk.getFields().add(branchFlag);

        AssetListResponse output = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(output);
        assertFalse(output.getAssetList().isEmpty());
        assertEquals(2, output.getAssetList().size());
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetSwe.getId())));
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAssetDnk.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getTwoAssetsWithAMTEachMakeSureTheyOnlyHaveOneMTEachTest() {
        String name = UUID.randomUUID().toString();
        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setName(name);
        Asset createdAsset1 = sendAssetToCreation(asset1);
        MobileTerminal mt1 = MobileTerminalTestHelper.createRestMobileTerminal(getWebTargetExternal(), createdAsset1, getTokenExternal());


        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setName(name);
        Asset createdAsset2 = sendAssetToCreation(asset2);
        MobileTerminal mt2 = MobileTerminalTestHelper.createRestMobileTerminal(getWebTargetExternal(), createdAsset2, getTokenExternal());


        SearchBranch trunk = new SearchBranch(true);
        trunk.addNewSearchLeaf(SearchFields.NAME, name);

        AssetListResponse output = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(output);
        assertFalse(output.getAssetList().isEmpty());
        assertEquals(2, output.getAssetList().size());
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAsset1.getId())));
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAsset2.getId())));

        assertEquals(1, output.getAssetList().get(0).getMobileTerminalUUIDList().size());

        UUID assetFromOutputId1 = output.getAssetList().get(0).getId();

        assertEquals(mt1.getId(), createdAsset1.getId().equals(assetFromOutputId1) ?
                UUID.fromString(output.getAssetList().get(0).getMobileTerminalUUIDList().get(0)) :
                UUID.fromString(output.getAssetList().get(1).getMobileTerminalUUIDList().get(0)));

        assertEquals(1, output.getAssetList().get(1).getMobileTerminalUUIDList().size());

        UUID assetFromOutputId2 = output.getAssetList().get(0).getId();
        assertEquals(mt2.getId(), createdAsset2.getId().equals(assetFromOutputId2) ?
                UUID.fromString(output.getAssetList().get(0).getMobileTerminalUUIDList().get(0)) :
                UUID.fromString(output.getAssetList().get(1).getMobileTerminalUUIDList().get(0)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAfterUpdatingMTMakeSureWeGotTheLatestMTTest() {
        String name = UUID.randomUUID().toString();
        Asset asset = AssetHelper.createBasicAsset();
        asset.setName(name);
        Asset createdAsset = sendAssetToCreation(asset);
        MobileTerminal mt = MobileTerminalTestHelper.createRestMobileTerminal(getWebTargetExternal(), createdAsset, getTokenExternal());
        assertEquals(createdAsset.getId(), UUID.fromString(mt.getAssetUUID()));
        mt.setAsset(createdAsset);  //this bc of how the serialization works ie it will only send the id of the connected asset in the asset id field
        mt.setComment("Updated comment");
        mt = MobileTerminalTestHelper.restMobileTerminalUpdate(getWebTargetExternal(), mt, getTokenExternal());

        SearchBranch trunk = new SearchBranch(true);
        trunk.addNewSearchLeaf(SearchFields.NAME, name);

        AssetListResponse output = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(output);
        assertFalse(output.getAssetList().isEmpty());
        assertEquals(1, output.getAssetList().size());
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));

        assertEquals(1, output.getAssetList().get(0).getMobileTerminalUUIDList().size());
        assertEquals(mt.getId(), UUID.fromString(output.getAssetList().get(0).getMobileTerminalUUIDList().get(0)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAfterAttachingUpdatingAndThenDetachingAMTTest() {
        String name = UUID.randomUUID().toString();
        Asset asset = AssetHelper.createBasicAsset();
        asset.setName(name);
        Asset createdAsset = sendAssetToCreation(asset);
        MobileTerminal mt = MobileTerminalTestHelper.createRestMobileTerminal(getWebTargetExternal(), createdAsset, getTokenExternal());
        assertEquals(createdAsset.getId(), UUID.fromString(mt.getAssetUUID()));
        mt.setComment("Updated comment");
        mt.setAsset(createdAsset);
        mt = MobileTerminalTestHelper.restMobileTerminalUpdate(getWebTargetExternal(), mt, getTokenExternal());

        getWebTargetExternal()
                .path("/mobileterminal")
                .path(mt.getId().toString())
                .path("unassign")
                .path(createdAsset.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);


        SearchBranch trunk = new SearchBranch(true);
        trunk.addNewSearchLeaf(SearchFields.NAME, name);

        AssetListResponse output = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        assertNotNull(output);
        assertFalse(output.getAssetList().isEmpty());
        assertEquals(1, output.getAssetList().size());
        assertTrue(output.getAssetList().stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));

        assertEquals(0, output.getAssetList().get(0).getMobileTerminalUUIDList().size());
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListBySource() {
        Asset asset = AssetHelper.createBasicAsset();
        String testVesselSource = "NATIONAL";
        asset.setSource(testVesselSource);
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.SOURCE, testVesselSource);
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertTrue(assetList.stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetWithMobileTerminal() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(createdAsset);
        createMobileTerminal(mobileTerminal);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.HAS_MOBILETERMINAL, "true");
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertTrue(assetList.stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetWithoutMobileTerminalNoMatch() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.HAS_MOBILETERMINAL, "true");
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertTrue(assetList.stream().noneMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetWithMobileTerminalNoMatch() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(createdAsset);
        createMobileTerminal(mobileTerminal);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.HAS_MOBILETERMINAL, "false");
        trunk.getFields().add(leaf);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertTrue(assetList.stream().noneMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getHistoricalAssetWithMobileTerminalNoMatch() {
        // Historical search and HAS_MOBILETERMINAL is not supported yet
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = sendAssetToCreation(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setActive(true);
        mobileTerminal.setAsset(createdAsset);
        createMobileTerminal(mobileTerminal);

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.HAS_MOBILETERMINAL, "true");
        trunk.getFields().add(leaf);
        SearchLeaf leaf2 = new SearchLeaf(SearchFields.DATE, DateUtils.dateToEpochMilliseconds(Instant.now()));
        trunk.getFields().add(leaf2);

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(trunk), AssetListResponse.class);

        List<Asset> assetList = listResponse.getAssetList();
        assertTrue(assetList.stream().noneMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    private Asset sendAssetToCreation(Asset asset) {
        return getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
    }

    private void createMobileTerminal(MobileTerminal mt) {
        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);
        assertNotNull(created);
    }
}
