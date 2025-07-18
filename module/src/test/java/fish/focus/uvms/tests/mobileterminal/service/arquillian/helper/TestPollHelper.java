package fish.focus.uvms.tests.mobileterminal.service.arquillian.helper;

import fish.focus.schema.mobileterminal.polltypes.v1.*;
import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.dao.TerminalDaoBean;
import fish.focus.uvms.mobileterminal.entity.*;
import fish.focus.uvms.mobileterminal.entity.types.PollTypeEnum;
import fish.focus.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import fish.focus.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import fish.focus.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Stateless
@LocalBean
public class TestPollHelper {

    private final Calendar cal = Calendar.getInstance();
    private final Random rnd = new Random();

    @EJB
    private TerminalDaoBean terminalDao;

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Inject
    private AssetDao assetDao;

    public MobileTerminal createBasicMobileTerminal() {
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mobileTerminal.setArchived(false);
        mobileTerminal.setActive(true);

        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);
        mobileTerminal.setPlugin(mtp);

        String serialNumber = generateARandomStringWithMaxLength(10);

        mobileTerminal.setSatelliteNumber("S" + generateARandomStringWithMaxLength(4));
        mobileTerminal.setAntenna("A");
        mobileTerminal.setTransceiverType("A");
        mobileTerminal.setSoftwareVersion("A");
        mobileTerminal.setSerialNo(serialNumber);
        mobileTerminal.setInstalledBy("Mike Great");

        Channel channel = createChannel("VMS", true, true, true);
        channel.setMobileTerminal(mobileTerminal);
        mobileTerminal.getChannels().clear();
        mobileTerminal.getChannels().add(channel);

        return mobileTerminal;
    }

    public String generateARandomStringWithMaxLength(int len) {
        Random random = new Random();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = random.nextInt(10);
            ret.append(val);
        }
        return ret.toString();
    }

    public PollRequestType createPollRequestType() {
        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID());
        prt.setUserName("TEST");
        prt.setPollType(PollType.MANUAL_POLL);
        PollMobileTerminal pollMobileTerminal = createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);

        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        String startDate = DateUtils.dateToEpochMilliseconds(Instant.now());
        pollAttribute.setValue(startDate);

        prt.getAttributes().add(pollAttribute);
        return prt;
    }

    private PollMobileTerminal createPollMobileTerminal() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());

        MobileTerminal mobileTerminal = createAndPersistMobileTerminal(asset);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setMobileTerminalId(mobileTerminal.getId().toString());
        Channel channel = mobileTerminal.getChannels().iterator().next();
        pmt.setComChannelId(channel.getId().toString());
        return pmt;
    }

    public MobileTerminal createAndPersistMobileTerminalOceanRegionSupport(Asset asset, boolean aorE, boolean aorW, boolean por, boolean ior) {
        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);

        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(UUID.randomUUID().toString());
        mt.setUpdatetime(Instant.now());
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);
        mt.setInstalledBy("Mike the not so Great");

        // only set if true so we can see if code defaults to false
        if (aorE) mt.setEastAtlanticOceanRegion(aorE);
        if (aorW) mt.setWestAtlanticOceanRegion(aorW);
        if (por) mt.setPacificOceanRegion(por);
        if (ior) mt.setIndianOceanRegion(ior);


        Set<MobileTerminalPluginCapability> capabilityList = new HashSet<>();
        MobileTerminalPluginCapability mtpc = new MobileTerminalPluginCapability();
        mtpc.setPlugin(mtp.getId());
        mtpc.setName("test");
        mtpc.setValue("test");
        mtpc.setUpdatedBy("TEST_USER");
        mtpc.setUpdateTime(Instant.now());
        capabilityList.add(mtpc);

        mtp.getCapabilities().addAll(capabilityList);

        if (asset != null) {
            mt.setAsset(asset);
        }

        Channel pollChannel = createChannel("VMS-POLL", false, false, true);
        pollChannel.setMobileTerminal(mt);

        Channel channel = createChannel("VMS", false, false, false);
        channel.setMobileTerminal(mt);

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        channels.add(pollChannel);
        mt.getChannels().clear();
        mt.getChannels().addAll(channels);
        terminalDao.createMobileTerminal(mt);
        return mt;
    }

    public MobileTerminal createAndPersistMobileTerminal(Asset asset) {
        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);

        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(UUID.randomUUID().toString());
        mt.setUpdatetime(Instant.now());
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);
        mt.setInstalledBy("Mike the not so Great");

        Set<MobileTerminalPluginCapability> capabilityList = new HashSet<>();
        MobileTerminalPluginCapability mtpc = new MobileTerminalPluginCapability();
        mtpc.setPlugin(mtp.getId());
        mtpc.setName("test");
        mtpc.setValue("test");
        mtpc.setUpdatedBy("TEST_USER");
        mtpc.setUpdateTime(Instant.now());
        capabilityList.add(mtpc);

        mtp.getCapabilities().addAll(capabilityList);

        if (asset != null) {
            mt.setAsset(asset);
        }

        Channel pollChannel = createChannel("VMS-POLL", false, false, true);
        pollChannel.setMobileTerminal(mt);

        Channel channel = createChannel("VMS", false, false, false);
        channel.setMobileTerminal(mt);

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        channels.add(pollChannel);
        mt.getChannels().clear();
        mt.getChannels().addAll(channels);
        terminalDao.createMobileTerminal(mt);
        return mt;
    }

    public MobileTerminal createBasicMobileTerminalWithAsset(Asset asset) {
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mobileTerminal.setArchived(false);
        mobileTerminal.setActive(true);
        mobileTerminal.setAsset(asset);
        mobileTerminal.setInstalledBy("kanalbolaget");

        MobileTerminalPlugin mtp = new MobileTerminalPlugin();
        mtp.setPluginServiceName(UUID.randomUUID().toString());
        mtp.setName("Thrane&Thrane&Thrane");
        mtp.setPluginSatelliteType("INMARSAT_C");
        mtp.setPluginInactive(false);
        mobileTerminalPluginDao.createMobileTerminalPlugin(mtp);
        mobileTerminal.setPlugin(mtp);
        mobileTerminal.setTransceiverType("TRANSPONDERTYP_100");
        mobileTerminal.setSerialNo("SN" + generateARandomStringWithMaxLength(10));

        Channel channel = createChannel("VMS", false, false, false);
        channel.setMobileTerminal(mobileTerminal);
        mobileTerminal.getChannels().clear();
        mobileTerminal.getChannels().add(channel);
        return mobileTerminal;
    }

    public Channel createChannel(String name, boolean defaultChannel, boolean configChannel, boolean pollChannel) {
        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setMemberNumber(Integer.parseInt(generateARandomStringWithMaxLength(3)));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(60));
        channel.setExpectedFrequency(Duration.ofSeconds(60));
        channel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        channel.setLesDescription("Thrane&Thrane");

        channel.setDnid(Integer.parseInt("1" + generateARandomStringWithMaxLength(3)));
        channel.setName(name);
        channel.setDefaultChannel(defaultChannel);
        channel.setConfigChannel(configChannel);
        channel.setPollChannel(pollChannel);
        return channel;
    }

    public MobileTerminalPlugin createMobileTerminalPlugin() {
        MobileTerminalPlugin plugin = new MobileTerminalPlugin();
        plugin.setPluginInactive(false);
        plugin.setName("Thrane&Thrane");
        plugin.setPluginSatelliteType("INMARSAT_C");
        plugin.setPluginServiceName("fish.focus.uvms.plugins.inmarsat");
        return plugin;
    }

    public ProgramPoll createProgramPoll(String connectId, Instant startDate, Instant stopDate, Instant latestRun) {
        ProgramPoll pp = new ProgramPoll();
        // create a valid mobileTerminal
        Asset asset = null;
        if (connectId != null) {
            asset = assetDao.getAssetById(UUID.fromString(connectId));
        }
        MobileTerminal mobileTerminal = createAndPersistMobileTerminal(asset);

        UUID terminalConnect = UUID.randomUUID();
        pp.setMobileterminal(mobileTerminal);
        pp.setChannelId(UUID.randomUUID());
        pp.setAssetId(terminalConnect);
        pp.setUpdatedBy("TEST");
        pp.setComment("Comment");
        pp.setFrequency(1);
        pp.setLatestRun(latestRun);
        pp.setPollState(ProgramPollStatus.STARTED);
        pp.setStartDate(startDate);
        pp.setStopDate(stopDate);
        pp.setCreateTime(latestRun);
        pp.setUpdatedBy("TEST");
        pp.setPollTypeEnum(PollTypeEnum.PROGRAM_POLL);
        return pp;
    }

    public Instant getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return cal.toInstant();
    }

    public Instant getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.toInstant();
    }

    public Instant getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2059);
        return cal.toInstant();
    }

    public String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
