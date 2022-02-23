package fish.focus.uvms.mobileterminal.mapper;

import fish.focus.uvms.asset.mapper.HistoryMapper;
import fish.focus.uvms.asset.remote.dto.ChangeHistoryRow;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPluginCapability;
import fish.focus.uvms.mobileterminal.model.dto.*;
import java.util.*;

public class MobileTerminalDtoMapper {


    public static Map<String, ChangeHistoryRow> mapToMobileTerminalRevisionsMap(List<MobileTerminal> mts) {
        return HistoryMapper.mobileTerminalChangeHistory(mts);
    }

    public static List<MobileTerminalDto> mapToMobileTerminalDtos(List<MobileTerminal> mts) {
        List<MobileTerminalDto> dtos = new ArrayList<>(mts.size());
        for (MobileTerminal mt : mts) {
            dtos.add(mapToMobileTerminalDto(mt));
        }
        return dtos;
    }

    public static MobileTerminalDto mapToMobileTerminalDto(MobileTerminal mt){
        if(mt == null) {
            return null;
        }
        MobileTerminalDto dto = new MobileTerminalDto();
        dto.setId(mt.getId());
        dto.setActive(mt.getActive());
        dto.setAntenna(mt.getAntenna());
        dto.setArchived(mt.getArchived());

        dto.setAssetId(mt.getAsset() == null ? null : mt.getAsset().getId().toString());

        dto.setChannels(mt.getChannels() == null ? null : mapToChannelDtos(mt.getChannels()));

        dto.setComment(mt.getComment());
        dto.setCreateTime(mt.getCreateTime());
        dto.setEastAtlanticOceanRegion(mt.getEastAtlanticOceanRegion());
        dto.setHistoryId(mt.getHistoryId());
        dto.setIndianOceanRegion(mt.getIndianOceanRegion());
        dto.setInstallDate(mt.getInstallDate());
        dto.setInstalledBy(mt.getInstalledBy());
        dto.setMobileTerminalType(mt.getMobileTerminalType());
        dto.setPacificOceanRegion(mt.getPacificOceanRegion());

        dto.setPlugin(mt.getPlugin() == null ? null : mapToMobileTerminalPluginDto(mt.getPlugin()));

        dto.setSatelliteNumber(mt.getSatelliteNumber());
        dto.setSerialNo(mt.getSerialNo());
        dto.setSoftwareVersion(mt.getSoftwareVersion());
        dto.setSource(mt.getSource());
        dto.setTransceiverType(mt.getTransceiverType());
        dto.setUninstallDate(mt.getUninstallDate());
        dto.setUpdatetime(mt.getUpdatetime());
        dto.setUpdateuser(mt.getUpdateuser());
        dto.setWestAtlanticOceanRegion(mt.getWestAtlanticOceanRegion());

        return dto;
    }

    public static Set<ChannelDto> mapToChannelDtos(Set<Channel> channels){
        Set<ChannelDto> dtoSet = new LinkedHashSet<>(channels.size());
        for (Channel channel : channels) {
            dtoSet.add(mapToChannelDto(channel));
        }
        return dtoSet;
    }

    public static ChannelDto mapToChannelDto(Channel channel){
        ChannelDto dto = new ChannelDto();
        dto.setActive(channel.isActive());
        dto.setArchived(channel.getArchived());
        dto.setConfigChannel(channel.isConfigChannel());
        dto.setDefaultChannel(channel.isDefaultChannel());
        dto.setDnid(channel.getDnid());
        dto.setEndDate(channel.getEndDate());
        dto.setExpectedFrequency(channel.getExpectedFrequency());
        dto.setExpectedFrequencyInPort(channel.getExpectedFrequencyInPort());
        dto.setFrequencyGracePeriod(channel.getFrequencyGracePeriod());
        dto.setHistoryId(channel.getHistoryId());
        dto.setId(channel.getId());
        dto.setLesDescription(channel.getLesDescription());
        dto.setMemberNumber(channel.getMemberNumber());

        //dto.setMobileTerminal();

        dto.setName(channel.getName());
        dto.setPollChannel(channel.isPollChannel());
        dto.setStartDate(channel.getStartDate());
        dto.setUpdateTime(channel.getUpdateTime());
        dto.setUpdateUser(channel.getUpdateUser());

        return dto;
    }

    public static MobileTerminalPluginDto mapToMobileTerminalPluginDto(MobileTerminalPlugin plugin){
        MobileTerminalPluginDto dto = new MobileTerminalPluginDto();

        dto.setCapabilities(plugin.getCapabilities() == null ? null : mapToMobileTerminalPluginCapabilityDtos(plugin.getCapabilities()));

        dto.setDescription(plugin.getDescription());
        dto.setId(plugin.getId());
        dto.setName(plugin.getName());
        dto.setPluginInactive(plugin.getPluginInactive());
        dto.setPluginSatelliteType(plugin.getPluginSatelliteType());
        dto.setPluginServiceName(plugin.getPluginServiceName());
        dto.setUpdatedBy(plugin.getUpdatedBy());
        dto.setUpdateTime(plugin.getUpdateTime());

        return dto;
    }

    public static Set<MobileTerminalPluginCapabilityDto> mapToMobileTerminalPluginCapabilityDtos(Set<MobileTerminalPluginCapability> capabilities){
        Set<MobileTerminalPluginCapabilityDto> dtos = new HashSet<>(capabilities.size());
        for (MobileTerminalPluginCapability capability : capabilities) {
            dtos.add(mapToMobileTerminalPluginCapabilityDto(capability));
        }
        return dtos;
    }

    public static MobileTerminalPluginCapabilityDto mapToMobileTerminalPluginCapabilityDto(MobileTerminalPluginCapability capability){
        MobileTerminalPluginCapabilityDto dto = new MobileTerminalPluginCapabilityDto();
        dto.setId(capability.getId());
        dto.setName(capability.getName());
        dto.setPlugin(capability.getPlugin());
        dto.setUpdatedBy(capability.getUpdatedBy());
        dto.setUpdateTime(capability.getUpdateTime());
        dto.setValue(capability.getValue());

        return dto;
    }
}
