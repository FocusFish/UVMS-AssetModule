package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;

import java.time.Instant;
import java.util.UUID;

public class SanePollDto {

    private UUID id;

    private String comment;

    private String creator;

    private UUID channelId;

    private Instant createTime;

    private UUID assetId;

    private UUID mobileterminalId;

    private String updatedBy;

    private PollTypeEnum pollTypeEnum;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public UUID getMobileterminalId() {
        return mobileterminalId;
    }

    public void setMobileterminalId(UUID mobileterminalId) {
        this.mobileterminalId = mobileterminalId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public PollTypeEnum getPollTypeEnum() {
        return pollTypeEnum;
    }

    public void setPollTypeEnum(PollTypeEnum pollTypeEnum) {
        this.pollTypeEnum = pollTypeEnum;
    }
}
