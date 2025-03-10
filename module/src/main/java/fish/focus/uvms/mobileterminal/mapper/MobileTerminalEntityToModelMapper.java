package fish.focus.uvms.mobileterminal.mapper;

import fish.focus.schema.mobileterminal.types.v1.*;
import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.asset.mapper.PollToCommandRequestMapper.PollReceiverInmarsatC;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class MobileTerminalEntityToModelMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalEntityToModelMapper.class);

    private MobileTerminalEntityToModelMapper() {
        // to hide default public constructor
    }

    public static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity) {
        if (entity == null) {
            throw new NullPointerException("No mobile terminal entity to map");
        }

        MobileTerminalType model = new MobileTerminalType();
        model.setMobileTerminalId(mapToMobileTerminalId(entity.getId().toString()));

        Plugin plugin = PluginMapper.mapEntityToModel(entity.getPlugin());
        model.setPlugin(plugin);

        try {
            String value = entity.getSource().value();
            model.setSource(MobileTerminalSource.valueOf(value));
        } catch (RuntimeException e) {
            LOG.error("[ Error when setting mobile terminal source. ] {}", e);
            throw new RuntimeException(e);
        }

        if (entity.getAsset() != null) {
            model.setConnectId(entity.getAsset().getId().toString());
        }

        model.setType(entity.getMobileTerminalType().name());
        model.setInactive(!entity.getActive());
        model.setArchived(entity.getArchived());
        model.setId((int) entity.getCreateTime().getEpochSecond());

        model.getChannels().addAll(ChannelMapper.mapChannels(entity));

        MobileTerminalAttribute serialNumber = new MobileTerminalAttribute();
        serialNumber.setType(PollReceiverInmarsatC.SERIAL_NUMBER.toString());
        serialNumber.setValue(entity.getSerialNo());
        model.getAttributes().add(serialNumber);

        MobileTerminalAttribute satelliteNumber = new MobileTerminalAttribute();
        serialNumber.setType(PollReceiverInmarsatC.SATELLITE_NUMBER.toString());
        serialNumber.setValue(entity.getSatelliteNumber());
        model.getAttributes().add(satelliteNumber);

        model.setInstalledOn(DateUtils.dateToEpochMilliseconds(entity.getInstallDate()));
        model.setUninstalledOn(DateUtils.dateToEpochMilliseconds(entity.getUninstallDate()));
        model.setInstalledBy(entity.getInstalledBy());

        return model;
    }

    private static MobileTerminalId mapToMobileTerminalId(String mobTermGuid) {
        if (mobTermGuid == null || mobTermGuid.isEmpty()) {
            throw new NullPointerException("No GUID found");
        }
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(mobTermGuid);
        return terminalId;
    }
}
