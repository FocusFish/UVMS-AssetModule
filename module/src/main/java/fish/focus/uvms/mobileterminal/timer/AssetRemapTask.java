package fish.focus.uvms.mobileterminal.timer;

import fish.focus.uvms.asset.bean.AssetServiceBean;
import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.domain.entity.AssetRemapMapping;
import fish.focus.uvms.asset.message.event.UpdatedAssetEvent;
import fish.focus.uvms.asset.remote.dto.AssetMergeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetRemapTask {

    private static final Logger LOG = LoggerFactory.getLogger(AssetRemapTask.class);

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

        LOG.info("Checking {} asset re-mappings", mappings.size());

        Instant threeHoursAgo = Instant.now().minus(3, ChronoUnit.HOURS);

        for (AssetRemapMapping mapping : mappings) {
            int remappedMovements = assetService.remapAssetsInMovement(mapping.getOldAssetId().toString(), mapping.getNewAssetId().toString());
            Instant createdDate = mapping.getCreatedDate();

            if (remappedMovements == 0 && createdDate.isBefore(threeHoursAgo)) {
                deleteMappings.add(mapping);
            }
        }

        LOG.info("{} mappings should be deleted", deleteMappings.size());

        for (AssetRemapMapping mappingToBeDeleted : deleteMappings) {
            assetService.removeMovementConnectInMovement(mappingToBeDeleted.getOldAssetId().toString());
            assetDao.deleteAssetMapping(mappingToBeDeleted);
            Asset asset = assetDao.getAssetById(mappingToBeDeleted.getOldAssetId());
            if (asset != null) {
                LOG.info("Asset {} has been fully remapped. Deleting from DB", asset);
                assetDao.deleteAsset(asset);
                updatedAssetEvent.fire(new AssetMergeInfo(mappingToBeDeleted.getOldAssetId().toString(), mappingToBeDeleted.getNewAssetId().toString()));
            }
        }
    }
}
