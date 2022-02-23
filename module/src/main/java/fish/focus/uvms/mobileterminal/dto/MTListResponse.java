package fish.focus.uvms.mobileterminal.dto;

import java.util.ArrayList;
import java.util.List;
import fish.focus.uvms.mobileterminal.model.dto.MobileTerminalDto;

public class MTListResponse {

    private List<MobileTerminalDto> mobileTerminalList = new ArrayList<>();
    private Integer totalNumberOfPages = 0;
    private Integer currentPage = 0;

    public List<MobileTerminalDto> getMobileTerminalList() {
        return mobileTerminalList;
    }

    public void setMobileTerminalList(List<MobileTerminalDto> mobileTerminalList) {
        this.mobileTerminalList = mobileTerminalList;
    }

    public Integer getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public void setTotalNumberOfPages(Integer totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
