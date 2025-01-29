package fish.focus.uvms.asset.util;

import fish.focus.wsdl.asset.types.CarrierSource;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.dto.AssetMTEnrichmentRequest;

import java.time.Instant;


public class AssetUtil {

    public static Asset createNewAssetFromRequest(AssetMTEnrichmentRequest request, int shipNumber) {
        Asset asset = new Asset();
        asset.setName((request.getAssetName() == null) ? ("Unknown: " + shipNumber) : request.getAssetName());
        asset.setUpdateTime(Instant.now());
        asset.setSource(CarrierSource.INTERNAL.toString());
        asset.setUpdatedBy("Movement from source: " + request.getPluginType());
        asset.setFlagStateCode((request.getFlagState() == null) ? ("UNK") : request.getFlagState());
        asset.setActive(true);
        asset.setCfr(request.getCfrValue());
        asset.setImo(request.getImoValue());
        asset.setIrcs(((request.getIrcsValue() == null || request.getIrcsValue().length() > 8) ? null : request.getIrcsValue()));
        asset.setGfcm(request.getGfcmValue());
        asset.setIccat(request.getIccatValue());
        asset.setMmsi(request.getMmsiValue());
        asset.setUvi(request.getUviValue());
        asset.setExternalMarking(request.getExternalMarking());
        return asset;
    }
}
