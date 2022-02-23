package fish.focus.uvms.asset.remote.dto;

import java.time.Instant;
import java.util.*;

public class ChangeHistoryRow {

    private String updatedBy;
    private Instant updateTime;
    private UUID id;
    private UUID historyId;
    private ChangeType changeType;
    private String assetName;

    private Map<String, ChannelChangeHistory> channelChanges = new HashMap<>();

    private List<ChangeHistoryItem> changes = new ArrayList<>();

    private Object snapshot;

    public ChangeHistoryRow(String updatedBy, Instant updateTime) {
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
    }

    public ChangeHistoryRow() {
    }

    public void addNewItem(String field, Object oldValue, Object newValue){
        changes.add(new ChangeHistoryItem(field, oldValue, newValue));
    }


    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public List<ChangeHistoryItem> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeHistoryItem> changes) {
        this.changes = changes;
    }

    public Map<String, ChannelChangeHistory> getChannelChanges() {
        return channelChanges;
    }

    public void setChannelChanges(Map<String, ChannelChangeHistory> channelChanges) {
        this.channelChanges = channelChanges;
    }

    public Object getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Object snapshot) {
        this.snapshot = snapshot;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }
    
    public String getAssetName() {
        return assetName;
    }
    
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
