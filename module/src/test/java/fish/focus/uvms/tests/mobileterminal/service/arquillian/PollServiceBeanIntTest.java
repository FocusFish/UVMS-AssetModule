package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.schema.mobileterminal.polltypes.v1.*;
import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.mobileterminal.bean.PollServiceBean;
import fish.focus.uvms.mobileterminal.dao.PollProgramDaoBean;
import fish.focus.uvms.mobileterminal.dto.PollDto;
import fish.focus.uvms.mobileterminal.dto.PollKey;
import fish.focus.uvms.mobileterminal.dto.PollValue;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.ProgramPoll;
import fish.focus.uvms.mobileterminal.mapper.PollDtoMapper;
import fish.focus.uvms.mobileterminal.mapper.PollEntityToModelMapper;
import fish.focus.uvms.mobileterminal.model.dto.CreatePollResultDto;
import fish.focus.uvms.tests.BuildAssetServiceDeployment;
import fish.focus.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import fish.focus.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PollServiceBeanIntTest extends BuildAssetServiceDeployment {

    private static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";
    private final Calendar cal = Calendar.getInstance();

    @Inject
    private PollServiceBean pollServiceBean;

    @EJB
    private TestPollHelper testPollHelper;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @Inject
    private AssetDao assetDao;

    @Before
    public void reset() {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPoll() {
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(pollRequestType);
        assertNotNull(createPollResultDto);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPoll_FromMPSBIT() {   //MPSBIT = Mapped Poll Service Bean Int Test, a test class for a, now removed, middle layer
        PollRequestType pollRequestType = helper_createPollRequestType(PollType.MANUAL_POLL);

        // create a poll
        CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(pollRequestType);

        if (createPollResultDto.getSentPolls().isEmpty() && createPollResultDto.getUnsentPolls().isEmpty()) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollWithBrokenJMS_WillFail() {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "true");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();

        CreatePollResultDto pollResult = pollServiceBean.createPoll(pollRequestType);

        assertThat(pollResult.isUnsentPoll(), is(true));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRunningProgramPolls() {
        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        int numberOfProgramB4 = pollServiceBean.getRunningProgramPolls().size();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        List<PollDto> runningProgramPolls = pollServiceBean.getRunningProgramPolls();
        assertNotNull(runningProgramPolls);
        assertEquals(numberOfProgramB4 + 1, runningProgramPolls.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll() {
        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        ProgramPoll responseType = pollServiceBean.startProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
        assertNotNull(responseType);
        assertEquals(ProgramPollStatus.STARTED, responseType.getPollState());
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll_FromMPSBIT() {
        // we want to be able to tamper with the dates for proper test coverage
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        ProgramPoll pollProgram = testPollHelper.createProgramPoll(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();

        ProgramPoll program = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollResponseType pollResponse = PollEntityToModelMapper.mapToPollResponseType(program);
        PollDto startedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValues();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll() {
        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        ProgramPoll responseType = pollServiceBean.stopProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
        assertNotNull(responseType);
        assertEquals(ProgramPollStatus.STOPPED, responseType.getPollState());
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll_FromMPSBIT() {
        // we want to be able to tamper with the dates for proper test coverage
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        ProgramPoll pollProgram = testPollHelper.createProgramPoll(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();

        ProgramPoll program = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollResponseType pollResponse = PollEntityToModelMapper.mapToPollResponseType(program);
        PollDto startedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        program = pollServiceBean.stopProgramPoll(String.valueOf(guid), username);
        pollResponse = PollEntityToModelMapper.mapToPollResponseType(program);
        PollDto stoppedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(stoppedProgramPoll);

        List<PollValue> values = stoppedProgramPoll.getValues();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll() {
        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        ProgramPoll responseType = pollServiceBean.inactivateProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
        assertNotNull(responseType);
        assertEquals(ProgramPollStatus.ARCHIVED, responseType.getPollState());
    }

    @Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll_FromMPSBIT() {
        // we want to be able to tamper with the dates for proper test coverage
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        ProgramPoll pollProgram = testPollHelper.createProgramPoll(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();

        ProgramPoll program = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollResponseType pollResponse = PollEntityToModelMapper.mapToPollResponseType(program);
        PollDto startedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValues();
        boolean isRunning = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(isRunning);

        program = pollServiceBean.inactivateProgramPoll(String.valueOf(guid), username);
        pollResponse = PollEntityToModelMapper.mapToPollResponseType(program);
        PollDto inactivatedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(inactivatedProgramPoll);

        List<PollValue> values1 = inactivatedProgramPoll.getValues();
        boolean isStopped = validatePollKeyValue(values1, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(isStopped);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() {
        int startedProgramPollsBefore = pollServiceBean.getPollProgramRunningAndStarted().size();

        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        List<ProgramPoll> responseList = pollServiceBean.getPollProgramRunningAndStarted();

        assertNotNull(responseList);
        assertEquals(startedProgramPollsBefore + 1, responseList.size());

        assertEquals(ProgramPollStatus.STARTED, responseList.get(0).getPollState());
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll_ShouldFailWithNullAsPollId() {
        assertThrows(EJBException.class, () -> pollServiceBean.startProgramPoll(null, "TEST"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll_ShouldFailWithNullAsPollId() {
        assertThrows(EJBException.class, () -> pollServiceBean.stopProgramPoll(null, "TEST"));
    }

    private PollRequestType helper_createPollRequestType(PollType pollType) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String startDate = format.format(calendar.getTime());
        calendar.set(Calendar.YEAR, 2020);
        String endDate = format.format(calendar.getTime());

        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID());
        prt.setUserName("TEST");
        prt.setPollType(pollType);
        PollMobileTerminal pollMobileTerminal = helper_createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);

        PollAttribute psStart = new PollAttribute();
        PollAttribute psEnd = new PollAttribute();
        PollAttribute psFreq = new PollAttribute();

        psStart.setKey(PollAttributeType.START_DATE);
        psStart.setValue(startDate);
        prt.getAttributes().add(psStart);

        psEnd.setKey(PollAttributeType.END_DATE);
        psEnd.setValue(endDate);
        prt.getAttributes().add(psEnd);

        psFreq.setKey(PollAttributeType.FREQUENCY);
        psFreq.setValue("300000");
        prt.getAttributes().add(psFreq);

        return prt;
    }

    private PollMobileTerminal helper_createPollMobileTerminal() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());

        MobileTerminal mobileTerminal = testPollHelper.createAndPersistMobileTerminal(asset);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setMobileTerminalId(mobileTerminal.getId().toString());

        Set<Channel> channels = mobileTerminal.getChannels();
        Channel channel = channels.iterator().next();
        UUID channelId = channel.getId();
        pmt.setComChannelId(channelId.toString());
        return pmt;
    }

    private boolean validatePollKeyValue(List<PollValue> values, PollKey key, String value) {
        for (PollValue v : values) {
            PollKey pollKey = v.getKey();
            if (pollKey.equals(key) && v.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private Instant getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return cal.toInstant();
    }

    private Instant getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.toInstant();
    }

    private Instant getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return cal.toInstant();
    }
}
