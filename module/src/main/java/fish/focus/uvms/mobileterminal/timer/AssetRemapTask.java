package fish.focus.uvms.mobileterminal.timer;

import fish.focus.uvms.asset.bean.AssetServiceBean;
import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.domain.entity.AssetRemapMapping;
import fish.focus.uvms.asset.message.event.UpdatedAssetEvent;
import fish.focus.uvms.asset.remote.dto.AssetMergeInfo;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetRemapTask {

    @Inject
    private AssetDao assetDao;

    @Inject
    private AssetServiceBean assetService;

    @Inject
    @UpdatedAssetEvent
    private Event<AssetMergeInfo> updatedAssetEvent;

    public void remap() {
        List<AssetRemapMapping> mappings = assetDao.getAllAssetRemappings();
        List<AssetRemapMapping> deleteMappings = new ArrayList<>();

        for (AssetRemapMapping mapping : mappings) {
            int remappedMovements = assetService.remapAssetsInMovement(mapping.getOldAssetId().toString(), mapping.getNewAssetId().toString());
            if (remappedMovements == 0 && Instant.now().isAfter(mapping.getCreatedDate().plus(3, ChronoUnit.HOURS))) {
                deleteMappings.add(mapping);
            }
        }
        for (AssetRemapMapping mappingToBeDeleted : deleteMappings) {
            assetService.removeMovementConnectInMovement(mappingToBeDeleted.getOldAssetId().toString());
            assetDao.deleteAssetMapping(mappingToBeDeleted);
            Asset asset = assetDao.getAssetById(mappingToBeDeleted.getOldAssetId());
            if (asset != null) {
                assetDao.deleteAsset(asset);
                updatedAssetEvent.fire(new AssetMergeInfo(mappingToBeDeleted.getOldAssetId().toString(), mappingToBeDeleted.getNewAssetId().toString()));
            }
        }
    }
}
