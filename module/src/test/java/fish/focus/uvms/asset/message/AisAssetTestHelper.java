package fish.focus.uvms.asset.message;

import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.wsdl.asset.types.CarrierSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static fish.focus.uvms.asset.message.AssetTestHelper.getRandomIntegers;

public class AisAssetTestHelper {

    private AisAssetTestHelper() {
        // hide default public constructor
    }

    public static Asset createAisStaticReport() {
        Asset asset = new Asset();

        asset.setName("Ship" + getRandomIntegers(10));
        asset.setActive(true);
        asset.setIrcs("I" + getRandomIntegers(7));
        asset.setEventCode("MOD");
        asset.setFlagStateCode("SWE");
        asset.setMmsi(getRandomIntegers(9));
        asset.setSource(CarrierSource.INTERNAL.value());
        asset.setUpdatedBy("AIS Message Type 5");
        asset.setVesselType("Fishing");

        return asset;
    }

    public static Asset getNationalSourceAssetForUpsertUsingMethod() {
        Asset nationalSourceAsset = new Asset();

        nationalSourceAsset.setActive(true);
        nationalSourceAsset.setAisIndicator(true);
        nationalSourceAsset.setCfr("LTU" + getRandomIntegers(7));
        nationalSourceAsset.setConstructionYear("2022");
        nationalSourceAsset.setErsIndicator(true);
        nationalSourceAsset.setEventCode("MOD");
        nationalSourceAsset.setExternalMarking("EXT" + getRandomIntegers(3));
        nationalSourceAsset.setFlagStateCode("SWE");
        nationalSourceAsset.setGrossTonnage(1197.0);
        nationalSourceAsset.setHasLicence(true);
        nationalSourceAsset.setHasVms(true);
        nationalSourceAsset.setHullMaterial("2");
        nationalSourceAsset.setImo("0" + getRandomIntegers(6));
        nationalSourceAsset.setIrcs("I" + getRandomIntegers(7));
        nationalSourceAsset.setIrcsIndicator(true);
        nationalSourceAsset.setLengthBetweenPerpendiculars(44.5);
        nationalSourceAsset.setLengthOverAll(49.9);
        nationalSourceAsset.setMainFishingGearCode("OTM");
        nationalSourceAsset.setMmsi(getRandomIntegers(9));
        nationalSourceAsset.setName("Ship" + getRandomIntegers(10));
        nationalSourceAsset.setNationalId(ThreadLocalRandom.current().nextLong(10_000_000));
        nationalSourceAsset.setPortOfRegistration("LTKLJ Klaipeda");
        nationalSourceAsset.setPowerOfAuxEngine(718.0);
        nationalSourceAsset.setPowerOfMainEngine(2666.0);
        nationalSourceAsset.setSegment("MFL");
        nationalSourceAsset.setSource(CarrierSource.NATIONAL.value());
        nationalSourceAsset.setSubFishingGearCode("OTM");
        nationalSourceAsset.setUpdatedBy("Testing code");
        nationalSourceAsset.setVesselDateOfEntry(ZonedDateTime.now(ZoneOffset.UTC).minusYears(1).toInstant());
        nationalSourceAsset.setVesselType("Fishing");
        nationalSourceAsset.setVmsIndicator(true);

        return nationalSourceAsset;
    }
}
