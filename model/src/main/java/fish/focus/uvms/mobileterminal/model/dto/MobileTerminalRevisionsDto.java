package fish.focus.uvms.mobileterminal.model.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import fish.focus.uvms.asset.remote.dto.ChangeHistoryRow;

public class MobileTerminalRevisionsDto {

    List<MobileTerminalDto> mobileTerminalVersions;

    Map<UUID, ChangeHistoryRow> changes;

    public List<MobileTerminalDto> getMobileTerminalVersions() {
        return mobileTerminalVersions;
    }

    public void setMobileTerminalVersions(List<MobileTerminalDto> mobileTerminalVersions) {
        this.mobileTerminalVersions = mobileTerminalVersions;
    }

    public Map<UUID, ChangeHistoryRow> getChanges() {
        return changes;
    }

    public void setChanges(Map<UUID, ChangeHistoryRow> changes) {
        this.changes = changes;
    }
}
