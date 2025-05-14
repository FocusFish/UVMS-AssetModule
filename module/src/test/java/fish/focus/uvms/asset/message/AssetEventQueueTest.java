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
package fish.focus.uvms.asset.message;

import fish.focus.uvms.asset.bean.AssetServiceBean;
import fish.focus.uvms.asset.dto.AssetMTEnrichmentRequest;
import fish.focus.uvms.asset.message.event.AssetModelMapper;
import fish.focus.uvms.asset.model.mapper.JAXBMarshaller;
import fish.focus.uvms.rest.asset.AbstractAssetRestTest;
import fish.focus.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import fish.focus.wsdl.asset.module.AssetModuleMethod;
import fish.focus.wsdl.asset.module.PingRequest;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.wsdl.asset.types.AssetIdType;
import fish.focus.wsdl.asset.types.CarrierSource;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.jms.Message;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static fish.focus.uvms.asset.domain.constant.AssetIdentifier.*;
import static fish.focus.uvms.asset.message.AisAssetTestHelper.getNationalSourceAssetForUpsertUsingMethod;
import static fish.focus.uvms.asset.message.AssetTestHelper.getRandomIntegers;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetEventQueueTest extends AbstractAssetRestTest {

    private final JMSHelper jmsHelper = new JMSHelper();

    @Inject
    AssetModelMapper assetModelMapper;

    @Inject
    AssetServiceBean assetServiceBean;

    @Test
    @OperateOnDeployment("normal")
    public void pingTest() throws Exception {
        PingRequest request = new PingRequest();
        request.setMethod(AssetModuleMethod.PING);
        String requestString = JAXBMarshaller.marshallJaxBObjectToString(request);
        String correlationId = jmsHelper.sendAssetMessage(requestString);
        Message response = jmsHelper.listenForResponse();
        assertThat(response, is(notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCFRTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);

                    assertThat(assetById, is(notNullValue()));
                    assertThat(assetById.getCfr(), is(asset.getCfr()));
                    assertThat(assetById.getName(), is(asset.getName()));
                    assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
                    assertThat(assetById.getIrcs(), is(asset.getIrcs()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIRCSTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);

                    assertThat(assetById, is(notNullValue()));
                    assertThat(assetById.getCfr(), is(asset.getCfr()));
                    assertThat(assetById.getName(), is(asset.getName()));
                    assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
                    assertThat(assetById.getIrcs(), is(asset.getIrcs()));

                    assertEquals(AssetIdType.GUID, assetById.getAssetId().getType());
                    assertEquals(assetById.getAssetId().getGuid(), assetById.getAssetId().getValue()); //since guid and value are supposed t obe the same
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMMSITest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);

                    assertThat(assetById, is(notNullValue()));
                    assertThat(assetById.getCfr(), is(asset.getCfr()));
                    assertThat(assetById.getName(), is(asset.getName()));
                    assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
                    assertThat(assetById.getIrcs(), is(asset.getIrcs()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);

                    assertThat(assetById, is(notNullValue()));
                    assertThat(assetById.getCfr(), is(asset.getCfr()));
                });

        String newName = "Name upserted";
        asset.setName(newName);
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);

                    assertThat(assetById, is(notNullValue()));
                    assertThat(assetById.getCfr(), is(asset.getCfr()));
                    assertThat(assetById.getName(), is(newName));
                    assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
                    assertThat(assetById.getIrcs(), is(asset.getIrcs()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void assetSourceTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        asset.setSource(CarrierSource.INTERNAL);
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);
                    assertThat(fetchedAsset.getSource(), is(asset.getSource()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest1() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        asset.setName(null);
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
                    assertThat("The asset should exist after creation", assetById, is(notNullValue()));
                    assertNull(assetById.getName());
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(asset.getMmsiNo());
        newAsset.setName("namebyassetinfo");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(assetById.getName());
                    assertEquals("namebyassetinfo", assetById.getName());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest2() throws Exception {
        Asset assetWithsMMSI = AssetTestHelper.createBasicAsset();
        assetWithsMMSI.setIrcs(null);
        assetWithsMMSI.setName("ShouldNotBeThis");
        jmsHelper.upsertAsset(assetWithsMMSI);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });


        Asset assetWithsIRCS = AssetTestHelper.createBasicAsset();
        assetWithsIRCS.setMmsiNo(null);
        assetWithsIRCS.setName("namnetestfall2");
        assetWithsIRCS.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsIRCS);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                });


        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(assetWithsMMSI.getMmsiNo());
        newAsset.setIrcs(assetWithsIRCS.getIrcs());
        newAsset.setName("ShouldNotBeThis");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
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
    }

    @Test
    @OperateOnDeployment("normal")
    public void nationalAssetWithCorrectIrcsAndMmsiAndInternalAssetWithCorrectButFaultyFormatedIrcs() throws Exception {
        String correctIrcs = "SFC-" + AssetTestsHelper.getRandomIntegers(4);
        String correctMmsi = AssetTestsHelper.getRandomIntegers(9);

        Asset internalAsset = AssetTestHelper.createBasicAsset();
        internalAsset.setIrcs(correctIrcs.replace("-", ""));
        internalAsset.setMmsiNo(null);
        internalAsset.setName("ShouldNotBeThis");
        jmsHelper.upsertAsset(internalAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(internalAsset.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                });

        Asset nationalAsset = AssetTestHelper.createBasicAsset();
        nationalAsset.setMmsiNo(correctMmsi);
        nationalAsset.setIrcs(correctIrcs);
        nationalAsset.setName("namnetestfall2");
        nationalAsset.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(nationalAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(nationalAsset.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(correctMmsi);
        newAsset.setIrcs(internalAsset.getIrcs());
        newAsset.setName("ShouldNotBeThis");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(nationalAsset.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                    assertNotNull(fetchedAsset.getName());
                    assertThat(fetchedAsset.getName(), is(equalTo(nationalAsset.getName())));
                    assertNotNull(fetchedAsset.getMmsiNo());
                    assertEquals(fetchedAsset.getMmsiNo(), correctMmsi);
                    assertNotNull(fetchedAsset.getIrcs());
                    assertEquals(fetchedAsset.getIrcs(), correctIrcs);
                });
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest3() throws Exception {
        Asset assetWithsIRCS = AssetTestHelper.createBasicAsset();
        assetWithsIRCS.setMmsiNo(null);
        assetWithsIRCS.setName(null);
        assetWithsIRCS.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsIRCS);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                });

        // an ais with a "random" mmsi
        String mmsi = UUID.randomUUID().toString().substring(0, 10);

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(mmsi);
        newAsset.setName("namnetestfall3");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(mmsi, AssetIdType.MMSI);
                    assertNull(fetchedAsset);
                });
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest4() throws Exception {
        Asset assetWithsIRCS = AssetTestHelper.createBasicAsset();
        assetWithsIRCS.setMmsiNo(null);
        assetWithsIRCS.setName("namnetestfall4");
        assetWithsIRCS.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsIRCS);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setIrcs(assetWithsIRCS.getIrcs());
        newAsset.setName("ShouldNotBeThis");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                    assertNotNull(fetchedAsset.getName());
                    assertEquals(fetchedAsset.getName(), assetWithsIRCS.getName());
                    assertNotNull(fetchedAsset.getIrcs());
                    assertEquals(fetchedAsset.getIrcs(), assetWithsIRCS.getIrcs());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest5() throws Exception {
        Asset assetWithsMMSI = AssetTestHelper.createBasicAsset();
        assetWithsMMSI.setIrcs(null);
        assetWithsMMSI.setName(null);
        assetWithsMMSI.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsMMSI);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });

        // an ais with a "random" ircs
        String ircs = UUID.randomUUID().toString().substring(0, 9);

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setIrcs(ircs);
        newAsset.setName("namnetestfall5");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(ircs, AssetIdType.IRCS);
                    assertNull(fetchedAsset);
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest6() throws Exception {
        Asset assetWithsMMSI = AssetTestHelper.createBasicAsset();
        assetWithsMMSI.setIrcs(null);
        assetWithsMMSI.setName(null);
        assetWithsMMSI.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsMMSI);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });


        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(assetWithsMMSI.getMmsiNo());
        newAsset.setName("namnetestfall6");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                    assertNull(fetchedAsset.getName());
                    assertNotNull(fetchedAsset.getMmsiNo());
                    assertEquals(fetchedAsset.getMmsiNo(), assetWithsMMSI.getMmsiNo());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest6XEUSource() throws Exception {
        Asset assetWithsMMSI = AssetTestHelper.createBasicAsset();
        assetWithsMMSI.setIrcs(null);
        assetWithsMMSI.setName(null);
        assetWithsMMSI.setSource(CarrierSource.XEU);
        jmsHelper.upsertAsset(assetWithsMMSI);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(assetWithsMMSI.getMmsiNo());
        newAsset.setName("namnetestfall6");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                    assertNotNull(fetchedAsset.getName());
                    assertEquals(fetchedAsset.getName(), newAsset.getName());
                    assertNotNull(fetchedAsset.getMmsiNo());
                    assertEquals(fetchedAsset.getMmsiNo(), assetWithsMMSI.getMmsiNo());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest7() throws Exception {
        Asset assetWithsIRCS = AssetTestHelper.createBasicAsset();
        assetWithsIRCS.setMmsiNo(null);
        assetWithsIRCS.setName(null);
        assetWithsIRCS.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsIRCS);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                });


        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setIrcs(assetWithsIRCS.getIrcs());
        newAsset.setName("namnetestfall7");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                    assertNull(fetchedAsset.getName());
                    assertNotNull(fetchedAsset.getIrcs());
                    assertEquals(fetchedAsset.getIrcs(), assetWithsIRCS.getIrcs());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationTest7ThirdCountrySource() throws Exception {
        Asset assetWithsIRCS = AssetTestHelper.createBasicAsset();
        assetWithsIRCS.setMmsiNo(null);
        assetWithsIRCS.setName(null);
        assetWithsIRCS.setSource(CarrierSource.THIRD_COUNTRY);
        jmsHelper.upsertAsset(assetWithsIRCS);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                });


        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setIrcs(assetWithsIRCS.getIrcs());
        newAsset.setName("namnetestfall7");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(assetWithsIRCS.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                    assertNotNull(fetchedAsset.getName());
                    assertEquals(fetchedAsset.getName(), newAsset.getName());
                    assertNotNull(fetchedAsset.getIrcs());
                    assertEquals(fetchedAsset.getIrcs(), assetWithsIRCS.getIrcs());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationIrcsWithSpaceTest() throws Exception {
        String randomSuffix = AssetTestsHelper.getRandomIntegers(6);
        String ircs = "I" + randomSuffix;
        String testIrcs = "I " + randomSuffix;
        Asset asset = AssetTestHelper.createBasicAsset();
        asset.setMmsiNo(null);
        asset.setIrcs(ircs);
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);
                    assertNull(assetById.getMmsiNo());
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(AssetTestsHelper.getRandomIntegers(9));
        newAsset.setIrcs(testIrcs);
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(assetById.getMmsiNo());
                    assertEquals(assetById.getMmsiNo(), newAsset.getMmsi());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetInformationMergeIrcsWithSpaceTest() throws Exception {
        String randomSuffix = AssetTestsHelper.getRandomIntegers(6);
        String ircs = "I" + randomSuffix;
        String testIrcs = "I " + randomSuffix;

        Asset asset = AssetTestHelper.createBasicAsset();
        asset.setSource(CarrierSource.NATIONAL);
        asset.setMmsiNo(null);
        asset.setIrcs(ircs);
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(fetchedAsset);
                });

        Asset asset2 = AssetTestHelper.createBasicAsset();
        String mmsi = AssetTestsHelper.getRandomIntegers(9);
        asset2.setMmsiNo(mmsi);
        asset2.setIrcs(null);
        jmsHelper.upsertAsset(asset2);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(asset2.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(mmsi);
        newAsset.setIrcs(testIrcs);
        jmsHelper.updateAssetInfo(List.of(newAsset));
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset assetById = jmsHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);
                    assertNotNull(assetById.getMmsiNo());
                    assertEquals(assetById.getMmsiNo(), newAsset.getMmsi());
                    Asset assetByMmsi = jmsHelper.getAssetById(mmsi, AssetIdType.MMSI);
                    assertEquals(assetByMmsi.getAssetId().getGuid(), assetById.getAssetId().getGuid());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetWithEmptyStringIRCS() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        asset.setIrcs("");
        asset.setName("updateAssetWithEmptyStringIRCS");
        jmsHelper.upsertAsset(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();

        newAsset.setMmsi(getRandomIntegers(9));
        newAsset.setIrcs(asset.getIrcs());
        newAsset.setName("shouldNotBeThis");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                    assertNotNull(fetchedAsset.getName());
                    assertThat(fetchedAsset.getName(), is(equalTo(asset.getName())));
                    assertNotNull(fetchedAsset.getMmsiNo());
                    assertNull(fetchedAsset.getIrcs());
                });
    }


    @Test
    @OperateOnDeployment("normal")
    public void createSeveralAssetsWithEmptyStringIRCSAndUpdateOneOfThemTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        asset.setIrcs("");
        asset.setName("createAssetWithEmptyStringIRCS");
        jmsHelper.upsertAsset(asset);

        Asset asset2 = AssetTestHelper.createBasicAsset();
        asset2.setIrcs("");
        asset2.setName("createAssetWithEmptyStringIRCS2");
        jmsHelper.upsertAsset(asset2);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAsset = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAsset);
                });

        fish.focus.uvms.asset.domain.entity.Asset newAsset = new fish.focus.uvms.asset.domain.entity.Asset();

        newAsset.setMmsi(asset2.getMmsiNo());
        newAsset.setIrcs(asset2.getIrcs());
        newAsset.setName("createAssetWithEmptyStringIRCS2NewName");
        List<fish.focus.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.updateAssetInfo(assetList);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    Asset fetchedAssetNotUpdated = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAssetNotUpdated);
                    assertNotNull(fetchedAssetNotUpdated.getName());
                    assertThat(fetchedAssetNotUpdated.getName(), is(equalTo(asset.getName())));
                    assertNotNull(fetchedAssetNotUpdated.getMmsiNo());
                    assertNull(fetchedAssetNotUpdated.getIrcs());

                    Asset fetchedAssetUpdated = jmsHelper.getAssetById(asset2.getMmsiNo(), AssetIdType.MMSI);
                    assertNotNull(fetchedAssetUpdated);
                    assertNotNull(fetchedAssetUpdated.getName());
                    assertThat(fetchedAssetUpdated.getName(), is(equalTo(newAsset.getName())));
                    assertNotNull(fetchedAssetUpdated.getMmsiNo());
                    assertNull(fetchedAssetUpdated.getIrcs());
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetWithNationalId() throws Exception {
        Asset assetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset asset = assetModelMapper.toAssetEntity(assetType);
        asset.setName("Create with national id");
        Long nationalId = ThreadLocalRandom.current().nextLong(10_000_000);
        asset.setNationalId(nationalId);

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(NATIONAL, nationalId.toString());
                    assertThat("Asset should exist after creation", fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getNationalId(), is(nationalId));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetWithOnlyNationalIdToNewFlagState() throws Exception {
        Asset assetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset asset = assetModelMapper.toAssetEntity(assetType);
        asset.setName("Create with national id");
        Long nationalId = ThreadLocalRandom.current().nextLong(10_000_000);
        asset.setNationalId(nationalId);
        asset.setFlagStateCode("SWE");

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(NATIONAL, nationalId.toString());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getNationalId(), is(nationalId));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat(fetchedAsset.getFlagStateCode(), is(asset.getFlagStateCode()));
                });

        asset.setFlagStateCode("NOR");

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(NATIONAL, nationalId.toString());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getNationalId(), is(nationalId));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat(fetchedAsset.getFlagStateCode(), is(asset.getFlagStateCode()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNewAssetWithSameNationalIdButDifferingFlagStateTest() throws Exception {
        Asset assetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset asset = assetModelMapper.toAssetEntity(assetType);
        asset.setName("Create with national id");
        Long nationalId = ThreadLocalRandom.current().nextLong(10_000_000);
        asset.setNationalId(nationalId);
        asset.setFlagStateCode("SWE");

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(CFR, asset.getCfr());
                    assertThat("Asset should exist after creation", fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getNationalId(), is(nationalId));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat(fetchedAsset.getFlagStateCode(), is(asset.getFlagStateCode()));
                });

        Asset secondBasicAsset = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset secondAsset = assetModelMapper.toAssetEntity(secondBasicAsset);
        secondAsset.setName("Second with national id");
        secondAsset.setNationalId(nationalId);
        secondAsset.setFlagStateCode("NOR");

        jmsHelper.upsertAssetUsingMethod(secondAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(CFR, secondAsset.getCfr());
                    assertThat("The second asset with the same national ID should exist", fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getNationalId(), is(nationalId));
                    assertThat(fetchedAsset.getName(), is(secondAsset.getName()));
                    assertThat(fetchedAsset.getFlagStateCode(), is(secondAsset.getFlagStateCode()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void creatingNewActiveAssetInactivatingAndActivatingShouldWorkTest() throws Exception {
        Asset assetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset asset = assetModelMapper.toAssetEntity(assetType);

        asset.setActive(true);

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(MMSI, asset.getMmsi());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be active after creation", fetchedAsset.getActive(), is(true));
                });

        asset.setActive(false);

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(MMSI, asset.getMmsi());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be inactive after update", fetchedAsset.getActive(), is(false));
                });

        asset.setActive(true);

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(MMSI, asset.getMmsi());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be active after update", fetchedAsset.getActive(), is(true));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void creatingNewActiveAssetWithSameMmsiFromOtherActiveAssetShouldFailTest() throws Exception {
        Asset assetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset asset = assetModelMapper.toAssetEntity(assetType);
        asset.setName("First MMSI vessel");
        asset.setActive(true);

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(MMSI, asset.getMmsi());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be active after creation", fetchedAsset.getActive(), is(true));
                });

        Asset otherBasicAsset = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset otherAsset = assetModelMapper.toAssetEntity(otherBasicAsset);
        otherAsset.setName("Second MMSI vessel");
        otherAsset.setActive(true);
        otherAsset.setMmsi(asset.getMmsi());

        jmsHelper.upsertAssetUsingMethod(otherAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(CFR, asset.getCfr());
                    assertThat("First active vessel should still exist after trying to add second active vessel with same MMSI",
                            firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(firstFetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat(firstFetchedAsset.getName(), is(asset.getName()));
                    assertThat("First asset should still be active after trying to create a second active asset",
                            firstFetchedAsset.getActive(), is(true));

                    fish.focus.uvms.asset.domain.entity.Asset secondFetchedAsset = assetServiceBean.getAssetById(CFR, otherAsset.getCfr());
                    assertThat(secondFetchedAsset, is(nullValue()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNewActiveAssetWithMmsiFromInactiveAssetTest() throws Exception {
        Asset assetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset asset = assetModelMapper.toAssetEntity(assetType);
        asset.setName("First MMSI vessel");
        asset.setActive(true);

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(MMSI, asset.getMmsi());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be active after creation", fetchedAsset.getActive(), is(true));
                });

        asset.setActive(false);
        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(MMSI, asset.getMmsi());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be inactive after update", fetchedAsset.getActive(), is(false));
                });

        Asset otherBasicAsset = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset otherAsset = assetModelMapper.toAssetEntity(otherBasicAsset);
        otherAsset.setName("Second MMSI vessel");
        otherAsset.setActive(true);
        otherAsset.setMmsi(asset.getMmsi());

        jmsHelper.upsertAssetUsingMethod(otherAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(CFR, asset.getCfr());
                    assertThat("The first vessel should still exist", firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getMmsi(), is(asset.getMmsi()));
                    assertThat("The first vessel should still be inactive", firstFetchedAsset.getActive(), is(false));

                    fish.focus.uvms.asset.domain.entity.Asset otherFetchedAsset = assetServiceBean.getAssetById(CFR, otherAsset.getCfr());
                    assertThat("A new active vessel should be able to reuse MMSI from inactive vessel", otherFetchedAsset, is(notNullValue()));
                    assertThat(otherFetchedAsset.getCfr(), is(not(asset.getCfr())));
                    assertThat(otherFetchedAsset.getCfr(), is(otherAsset.getCfr()));

                    assertThat(otherFetchedAsset.getMmsi(), is(otherAsset.getMmsi()));

                    assertThat(otherFetchedAsset.getName(), is(otherAsset.getName()));
                    assertThat("The second vessel should be active", otherFetchedAsset.getActive(), is(true));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void createOneActiveAssetAndTwoInactiveAssetsWithSameMmsiTest() throws Exception {
        // Assets are hardcoded to be active upon creation
        Asset firstAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset firstInactiveAsset = assetModelMapper.toAssetEntity(firstAssetType);
        firstInactiveAsset.setName("First MMSI vessel");
        firstInactiveAsset.setActive(true);

        String mmsi = firstInactiveAsset.getMmsi();

        jmsHelper.upsertAssetUsingMethod(firstInactiveAsset);
        verifyAssetMmsiNameAndActive(firstInactiveAsset, mmsi, "First asset should be active after creation", true);

        firstInactiveAsset.setActive(false);
        jmsHelper.upsertAssetUsingMethod(firstInactiveAsset);
        verifyAssetMmsiNameAndActive(firstInactiveAsset, mmsi, "First asset should be inactive after update", false);

        Asset secondInactiveAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset secondInactiveAsset = assetModelMapper.toAssetEntity(secondInactiveAssetType);
        secondInactiveAsset.setName("Second MMSI vessel");
        secondInactiveAsset.setActive(true);
        secondInactiveAsset.setMmsi(mmsi);

        jmsHelper.upsertAssetUsingMethod(secondInactiveAsset);
        verifyAssetMmsiNameAndActive(secondInactiveAsset, mmsi, "Second asset should be active after creation", true);

        secondInactiveAsset.setActive(false);
        jmsHelper.upsertAssetUsingMethod(secondInactiveAsset);
        verifyAssetMmsiNameAndActive(secondInactiveAsset, mmsi, "Second asset should be inactive after update", false);

        Asset activeAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset activeAsset = assetModelMapper.toAssetEntity(activeAssetType);
        activeAsset.setName("Active MMSI vessel");
        activeAsset.setActive(true);
        activeAsset.setMmsi(mmsi);

        jmsHelper.upsertAssetUsingMethod(activeAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstInactiveFetchedAsset = assetServiceBean.getAssetById(CFR, firstInactiveAsset.getCfr());
                    assertThat("The first vessel should still exist", firstInactiveFetchedAsset, is(notNullValue()));
                    assertThat(firstInactiveFetchedAsset.getMmsi(), is(mmsi));
                    assertThat("The first vessel should still be inactive", firstInactiveFetchedAsset.getActive(), is(false));

                    fish.focus.uvms.asset.domain.entity.Asset secondInactiveFetchedAsset = assetServiceBean.getAssetById(CFR, secondInactiveAsset.getCfr());
                    assertThat("The second vessel should still exist", secondInactiveFetchedAsset, is(notNullValue()));
                    assertThat(secondInactiveFetchedAsset.getMmsi(), is(mmsi));
                    assertThat("The second vessel should still be inactive", secondInactiveFetchedAsset.getActive(), is(false));

                    fish.focus.uvms.asset.domain.entity.Asset fetchedActiveAsset = assetServiceBean.getAssetById(CFR, activeAsset.getCfr());
                    assertThat("A new active vessel should be able to reuse MMSI from inactive vessel", fetchedActiveAsset, is(notNullValue()));
                    assertThat(fetchedActiveAsset.getCfr(), is(not(firstInactiveAsset.getCfr())));
                    assertThat(fetchedActiveAsset.getCfr(), is(not(secondInactiveAsset.getCfr())));
                    assertThat(fetchedActiveAsset.getCfr(), is(activeAsset.getCfr()));

                    assertThat(fetchedActiveAsset.getMmsi(), is(mmsi));

                    assertThat(fetchedActiveAsset.getName(), is(activeAsset.getName()));
                    assertThat("The last created vessel should be active", fetchedActiveAsset.getActive(), is(true));
                });
    }

    private void verifyAssetMmsiNameAndActive(fish.focus.uvms.asset.domain.entity.Asset firstInactiveAsset, String mmsi, String reason, boolean value) {
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(CFR, firstInactiveAsset.getCfr());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getMmsi(), is(mmsi));
                    assertThat(fetchedAsset.getName(), is(firstInactiveAsset.getName()));
                    assertThat(reason, fetchedAsset.getActive(), is(value));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNewActiveAssetWithIrcsFromInactiveAssetTest() throws Exception {
        Asset assetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset asset = assetModelMapper.toAssetEntity(assetType);
        asset.setName("First IRCS vessel");
        asset.setActive(true);

        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(IRCS, asset.getIrcs());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getIrcs(), is(asset.getIrcs()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be active after creation", fetchedAsset.getActive(), is(true));
                });

        asset.setActive(false);
        jmsHelper.upsertAssetUsingMethod(asset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(IRCS, asset.getIrcs());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
                    assertThat(fetchedAsset.getIrcs(), is(asset.getIrcs()));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat("Asset should be inactive after update", fetchedAsset.getActive(), is(false));
                });

        Asset otherBasicAsset = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset otherAsset = assetModelMapper.toAssetEntity(otherBasicAsset);
        otherAsset.setName("Second MMSI vessel");
        otherAsset.setActive(true);
        otherAsset.setIrcs(asset.getIrcs());

        jmsHelper.upsertAssetUsingMethod(otherAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(CFR, asset.getCfr());
                    assertThat("The first vessel should still exist", firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getIrcs(), is(asset.getIrcs()));
                    assertThat("The first vessel should still be inactive", firstFetchedAsset.getActive(), is(false));

                    fish.focus.uvms.asset.domain.entity.Asset otherFetchedAsset = assetServiceBean.getAssetById(CFR, otherAsset.getCfr());
                    assertThat("A new active vessel should be able to reuse IRCS from inactive vessel", otherFetchedAsset, is(notNullValue()));
                    assertThat(otherFetchedAsset.getCfr(), is(not(asset.getCfr())));
                    assertThat(otherFetchedAsset.getCfr(), is(otherAsset.getCfr()));

                    assertThat(otherFetchedAsset.getIrcs(), is(otherAsset.getIrcs()));

                    assertThat(otherFetchedAsset.getName(), is(otherAsset.getName()));
                    assertThat("The second vessel should be active", otherFetchedAsset.getActive(), is(true));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void creatingTwoAssetsWithNullIrcsShouldWorkTest() throws Exception {
        Asset firstAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset firstAsset = assetModelMapper.toAssetEntity(firstAssetType);
        firstAsset.setName("First IRCS vessel");
        firstAsset.setIrcs(null);

        jmsHelper.upsertAssetUsingMethod(firstAsset);
        verifyAssetIrcsNameAndActive(firstAsset, null, "First asset should exist after creation", true);

        Asset secondAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset secondAsset = assetModelMapper.toAssetEntity(secondAssetType);
        secondAsset.setName("Second IRCS vessel");
        secondAsset.setIrcs(null);

        jmsHelper.upsertAssetUsingMethod(secondAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(CFR, firstAsset.getCfr());
                    assertThat("The first asset should exist", firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getIrcs(), is(nullValue()));

                    fish.focus.uvms.asset.domain.entity.Asset secondFetchedAsset = assetServiceBean.getAssetById(CFR, secondAsset.getCfr());
                    assertThat("The second asset should exist", secondFetchedAsset, is(notNullValue()));
                    assertThat(secondFetchedAsset.getIrcs(), is(nullValue()));

                    assertThat(firstFetchedAsset.getCfr(), is(not(secondFetchedAsset.getCfr())));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void createOneActiveAssetAndTwoInactiveAssetsWithSameIrcsTest() throws Exception {
        // Assets are hardcoded to be active upon creation
        Asset firstAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset firstInactiveAsset = assetModelMapper.toAssetEntity(firstAssetType);
        firstInactiveAsset.setName("First IRCS vessel");
        firstInactiveAsset.setActive(true);

        String ircs = firstInactiveAsset.getIrcs();

        jmsHelper.upsertAssetUsingMethod(firstInactiveAsset);
        verifyAssetIrcsNameAndActive(firstInactiveAsset, ircs, "First asset should be active after creation", true);

        firstInactiveAsset.setActive(false);
        jmsHelper.upsertAssetUsingMethod(firstInactiveAsset);
        verifyAssetIrcsNameAndActive(firstInactiveAsset, ircs, "First asset should be inactive after update", false);

        Asset secondInactiveAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset secondInactiveAsset = assetModelMapper.toAssetEntity(secondInactiveAssetType);
        secondInactiveAsset.setName("Second IRCS vessel");
        secondInactiveAsset.setActive(true);
        secondInactiveAsset.setIrcs(ircs);

        jmsHelper.upsertAssetUsingMethod(secondInactiveAsset);
        verifyAssetIrcsNameAndActive(secondInactiveAsset, ircs, "Second asset should be active after creation", true);

        secondInactiveAsset.setActive(false);
        jmsHelper.upsertAssetUsingMethod(secondInactiveAsset);
        verifyAssetIrcsNameAndActive(secondInactiveAsset, ircs, "Second asset should be inactive after update", false);

        Asset activeAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset activeAsset = assetModelMapper.toAssetEntity(activeAssetType);
        activeAsset.setName("Active IRCS vessel");
        activeAsset.setActive(true);
        activeAsset.setIrcs(ircs);

        jmsHelper.upsertAssetUsingMethod(activeAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstInactiveFetchedAsset = assetServiceBean.getAssetById(CFR, firstInactiveAsset.getCfr());
                    assertThat("The first vessel should still exist", firstInactiveFetchedAsset, is(notNullValue()));
                    assertThat(firstInactiveFetchedAsset.getIrcs(), is(ircs));
                    assertThat("The first vessel should still be inactive", firstInactiveFetchedAsset.getActive(), is(false));

                    fish.focus.uvms.asset.domain.entity.Asset secondInactiveFetchedAsset = assetServiceBean.getAssetById(CFR, secondInactiveAsset.getCfr());
                    assertThat("The second vessel should still exist", secondInactiveFetchedAsset, is(notNullValue()));
                    assertThat(secondInactiveFetchedAsset.getIrcs(), is(ircs));
                    assertThat("The second vessel should still be inactive", secondInactiveFetchedAsset.getActive(), is(false));

                    fish.focus.uvms.asset.domain.entity.Asset fetchedActiveAsset = assetServiceBean.getAssetById(CFR, activeAsset.getCfr());
                    assertThat("A new active vessel should be able to reuse IRCS from inactive vessel", fetchedActiveAsset, is(notNullValue()));
                    assertThat(fetchedActiveAsset.getCfr(), is(not(firstInactiveAsset.getCfr())));
                    assertThat(fetchedActiveAsset.getCfr(), is(not(secondInactiveAsset.getCfr())));
                    assertThat(fetchedActiveAsset.getCfr(), is(activeAsset.getCfr()));

                    assertThat(fetchedActiveAsset.getIrcs(), is(ircs));

                    assertThat(fetchedActiveAsset.getName(), is(activeAsset.getName()));
                    assertThat("The last created vessel should be active", fetchedActiveAsset.getActive(), is(true));
                });
    }

    private void verifyAssetIrcsNameAndActive(fish.focus.uvms.asset.domain.entity.Asset asset, String ircs, String reason, boolean value) {
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAsset = assetServiceBean.getAssetById(CFR, asset.getCfr());
                    assertThat(fetchedAsset, is(notNullValue()));
                    assertThat(fetchedAsset.getIrcs(), is(ircs));
                    assertThat(fetchedAsset.getName(), is(asset.getName()));
                    assertThat(reason, fetchedAsset.getActive(), is(value));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAisStaticReportEntryWithNationalDataTest() throws Exception {
        fish.focus.uvms.asset.domain.entity.Asset aisAsset = AisAssetTestHelper.createAisStaticReport();
        String flagState = "LTU";
        aisAsset.setFlagStateCode(flagState);

        String ircs = aisAsset.getIrcs();
        String mmsi = aisAsset.getMmsi();
        String name = aisAsset.getName();

        createAssetThroughAisMovementFlow(name, flagState, ircs, mmsi);

        jmsHelper.updateAssetInfo(List.of(aisAsset));
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset fetchedAssetByIrcs = assetServiceBean.getAssetById(IRCS, ircs);
                    assertThat("The vessel should have been created", fetchedAssetByIrcs, is(notNullValue()));
                    assertThat(fetchedAssetByIrcs.getIrcs(), is(ircs));
                    assertThat(fetchedAssetByIrcs.getMmsi(), is(mmsi));
                    assertThat(fetchedAssetByIrcs.getCfr(), is(nullValue()));
                    assertThat(fetchedAssetByIrcs.getImo(), is(nullValue()));
                    assertThat(fetchedAssetByIrcs.getNationalId(), is(nullValue()));
                    assertThat(fetchedAssetByIrcs.getActive(), is(true));
                    assertThat(fetchedAssetByIrcs.getVesselType(), is("Fishing"));
                });

        fish.focus.uvms.asset.domain.entity.Asset nationalSourceAsset = getNationalSourceAssetForUpsertUsingMethod();
        nationalSourceAsset.setIrcs(ircs);
        nationalSourceAsset.setMmsi(mmsi);
        nationalSourceAsset.setFlagStateCode(flagState);

        jmsHelper.upsertAssetUsingMethod(nationalSourceAsset);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(CFR, nationalSourceAsset.getCfr());
                    assertThat(firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getIrcs(), is(ircs));
                    assertThat(firstFetchedAsset.getActive(), is(true));
                });
    }

    private void createAssetThroughAisMovementFlow(String name, String flagState, String ircs, String mmsi) {
        createAssetThroughAisMovementFlow(name, flagState, ircs, mmsi, null);
    }

    private void createAssetThroughAisMovementFlow(String name, String flagState, String ircs, String mmsi, String cfr) {
        // AIS static reports can not be created, only updated
        // A real version of this would have MovementModule creating the new asset when fetching MT
        // AIS position -> movement module -> get MT -> create new unknown asset
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        request.setAssetName(name);
        request.setFlagState(flagState);
        request.setIrcsValue(ircs);
        request.setMmsiValue(mmsi);
        request.setCfrValue(cfr);

        Response response = getWebTargetInternal()
                .path("internal")
                .path("collectassetmt")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(request), Response.class);

        assertThat(response.getStatus(), is(200));
    }

    @Test
    @OperateOnDeployment("normal")
    public void entryFromAisMovementFlowShouldBeMergedAndRemappedUponNationalSourceUpdate() throws Exception {
        // Scenario: Asset exists in DB
        // new MMSI, starts sending AIS positions
        // AIS position -> movement module -> get MT -> create new unknown asset since mmsi doesn't exist in database
        // (sync from national source hasn't happened yet)
        // Later sync from national source happens with updated mmsi, finds match on e.g. cfr
        // tries to update, old version fails due to mmsi collision
        // What should happen is the Unknown AIS entry with same mmsi is nulled out and any positions are remapped
        // i.e. there should only be one "real" entry in the db for the asset, not several half entries

        Asset firstAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset assetFromNationalSource = assetModelMapper.toAssetEntity(firstAssetType);
        assetFromNationalSource.setName("Asset from national source");
        assetFromNationalSource.setSource(CarrierSource.NATIONAL.value());

        String oldMmsi = assetFromNationalSource.getMmsi();
        String cfr = assetFromNationalSource.getCfr();
        String ircs = assetFromNationalSource.getIrcs();

        jmsHelper.upsertAssetUsingMethod(assetFromNationalSource);
        verifyAssetMmsiNameAndActive(assetFromNationalSource, oldMmsi, "First asset should be active after creation", true);

        // Asset from national source should now exist
        // introduce new mmsi and fake the AIS movement flow

        String flagState = assetFromNationalSource.getFlagStateCode();
        String newMmsi = getRandomIntegers(9);

        createAssetThroughAisMovementFlow("Unknown: 4328", flagState, null, newMmsi);

        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(MMSI, newMmsi);
                    assertThat("Asset should exist after creation using the AIS movement flow", firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getIrcs(), is(nullValue()));
                    assertThat(firstFetchedAsset.getCfr(), is(nullValue()));
                });

        // Send national source update with the updated mmsi
        assetFromNationalSource.setMmsi(newMmsi);

        jmsHelper.upsertAssetUsingMethod(assetFromNationalSource);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(MMSI, newMmsi);
                    assertThat("Should only be one entry per mmsi", firstFetchedAsset, is(notNullValue()));

                    fish.focus.uvms.asset.domain.entity.Asset secondFetchedAsset = assetServiceBean.getAssetById(CFR, cfr);
                    assertThat(secondFetchedAsset, is(notNullValue()));
                    assertThat(secondFetchedAsset.getIrcs(), is(ircs));

                    assertThat("The assets should be the same after the update", firstFetchedAsset.getCfr(), is(secondFetchedAsset.getCfr()));
                });
    }

    @Test
    @OperateOnDeployment("normal")
    public void twoEntriesFromAisMovementFlowShouldBeMergedAndRemappedUponNationalSourceUpdate() throws Exception {
        // Scenario: National sync has not happened yet
        // Vessel is configuring sending equipment but not all values are set and starts sending
        // e.g. first mmsi is configured and a position is sent
        // next only ircs is configured and position is sent
        // AIS position -> movement module -> get MT -> create new unknown asset since the values doesn't exist in database
        // Later sync from national source happens, finds match on e.g. cfr
        // tries to update, due to ID collision
        // What should happen is the Unknown AIS entries with same mmsi/ircs is nulled out, merged and any positions are remapped
        // i.e. there should only be one "real" entry in the db for the asset, not several half entries

        String flagState = "SWE";
        String mmsi = getRandomIntegers(9);
        String ircs = "I" + getRandomIntegers(7);
        String cfr = "CFR" + getRandomIntegers(7);

        createAssetThroughAisMovementFlow("Unknown: 9934", flagState, ircs, null);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(IRCS, ircs);
                    assertThat("Asset with IRCS only should exist after creation using the AIS movement flow", firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getMmsi(), is(nullValue()));
                    assertThat(firstFetchedAsset.getCfr(), is(nullValue()));
                });

        createAssetThroughAisMovementFlow("Unknown: 8047", flagState, null, mmsi, cfr);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset firstFetchedAsset = assetServiceBean.getAssetById(CFR, cfr);
                    assertThat("Asset with MMSI and CFR should exist after creation using the AIS movement flow", firstFetchedAsset, is(notNullValue()));
                    assertThat(firstFetchedAsset.getMmsi(), is(mmsi));
                    assertThat(firstFetchedAsset.getIrcs(), is(nullValue()));
                });

        // Two internal entries now exists
        // Fake sync from national source
        // One of the two internal entries is kept and then updated with information from the national source asset

        Asset firstAssetType = AssetTestHelper.createBasicAsset();
        fish.focus.uvms.asset.domain.entity.Asset assetFromNationalSource = assetModelMapper.toAssetEntity(firstAssetType);
        assetFromNationalSource.setName("Asset from national source");
        assetFromNationalSource.setSource(CarrierSource.NATIONAL.value());
        assetFromNationalSource.setCfr(cfr);
        assetFromNationalSource.setIrcs(ircs);
        assetFromNationalSource.setMmsi(mmsi);

        jmsHelper.upsertAssetUsingMethod(assetFromNationalSource);
        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> {
                    fish.focus.uvms.asset.domain.entity.Asset assetByMmsi = assetServiceBean.getAssetById(MMSI, mmsi);
                    assertThat("Should only be one entry per mmsi", assetByMmsi, is(notNullValue()));

                    fish.focus.uvms.asset.domain.entity.Asset assetByIrcs = assetServiceBean.getAssetById(IRCS, ircs);
                    assertThat("Should only be one entry per ircs", assetByIrcs, is(notNullValue()));

                    fish.focus.uvms.asset.domain.entity.Asset assetByCfr = assetServiceBean.getAssetById(CFR, cfr);
                    assertThat("Asset by cfr should have IRCS set after update", assetByCfr.getIrcs(), is(ircs));

                    assertThat("The assets should be the same after the update", assetByMmsi.getCfr(), is(assetByCfr.getCfr()));
                    assertThat("The assets should be the same after the update", assetByIrcs.getCfr(), is(assetByCfr.getCfr()));
                    assertThat("The asset entry should've been update with national ID", assetByCfr.getNationalId(), is(assetFromNationalSource.getNationalId()));
                });
    }
}
