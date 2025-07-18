package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.schema.mobileterminal.polltypes.v1.ProgramPollStatus;
import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.dao.PollProgramDaoBean;
import fish.focus.uvms.mobileterminal.dao.TerminalDaoBean;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.entity.ProgramPoll;
import fish.focus.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import fish.focus.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import fish.focus.uvms.tests.BuildAssetServiceDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-05-02.
 */

@RunWith(Arquillian.class)
public class PollProgramDaoBeanTest extends BuildAssetServiceDeployment {

    private final Calendar cal = Calendar.getInstance();
    private final int startYear = 1999;
    private final int latestRunYear = 2017;
    private final Random rnd = new Random();

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @EJB
    private TerminalDaoBean terminalDaoBean;

    @EJB
    private MobileTerminalPluginDaoBean testDaoBean;

    @Before
    public void cleanupProgramPolls() {
        List<ProgramPoll> programPollRunningAndStarted = pollProgramDao.getProgramPollRunningAndStarted();
        programPollRunningAndStarted.forEach(programPoll -> pollProgramDao.removeProgramPollAfterTests(programPoll.getId().toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        assertNotNull(pollProgram.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();
        ProgramPoll fetchedPollProgram = pollProgramDao.getProgramPollById(guid);

        assertEquals(guid, fetchedPollProgram.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_updateUserConstraintViolation() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        pollProgram.setUpdatedBy(new String(updatedBy));

        assertThrows(EJBTransactionRolledbackException.class, () -> pollProgramDao.createProgramPoll(pollProgram));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_withNullWillFail() {
        assertThrows(EJBException.class, () -> pollProgramDao.createProgramPoll(null));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        pollProgram.setUpdatedBy("update");
        pollProgramDao.updateProgramPoll(pollProgram);

        ProgramPoll fetchedPollProgram = pollProgramDao.getProgramPollById(pollProgram.getId());

        assertNotNull(fetchedPollProgram.getId());
        assertEquals("update", fetchedPollProgram.getUpdatedBy());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram_WithNonePersistedEntityWillFail() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.updateProgramPoll(pollProgram);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        List<ProgramPoll> pollsAlive = pollProgramDao.getProgramPollsAlive();

        boolean found = pollsAlive.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithCurrentDateBiggerThenStopDate() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();

        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, startYear - 1);
        Instant stopDate = Instant.now();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        List<ProgramPoll> pollsAlive = pollProgramDao.getProgramPollsAlive();

        boolean found = pollsAlive.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithPollStateArchived() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(ProgramPollStatus.ARCHIVED);

        pollProgramDao.createProgramPoll(pollProgram);
        List<ProgramPoll> pollsAlive = pollProgramDao.getProgramPollsAlive();

        boolean found = pollsAlive.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollRunningAndStarted();
        boolean found = pollPrograms.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenStartDateBiggerThenNow() {
        cal.setTime(new Date(System.currentTimeMillis()));

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        Instant startDate = ZonedDateTime.now(ZoneOffset.UTC).plusYears(1).toInstant();  //starting the poll one year in the future should mean that it is not running now

        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollRunningAndStarted();

        boolean found = pollPrograms.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
        assertTrue(pollPrograms.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenPollStateIsNotStarted() {
        cal.setTime(new Date(System.currentTimeMillis()));

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(ProgramPollStatus.STOPPED);

        pollProgramDao.createProgramPoll(pollProgram);

        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollRunningAndStarted();

        boolean found = pollPrograms.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
        assertTrue(pollPrograms.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid_ShouldFailWithInvalidGuid() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        ProgramPoll fetchedPollProgram = pollProgramDao.getProgramPollById(UUID.randomUUID());

        assertNull(fetchedPollProgram);
    }

    private ProgramPoll createPollProgramHelper(String mobileTerminalSerialNo, Instant startDate,
                                                Instant stopDate, Instant latestRun) {
        ProgramPoll pp = new ProgramPoll();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(mobileTerminalSerialNo);

        UUID terminalConnect = UUID.randomUUID();
        pp.setChannelId(UUID.randomUUID());
        pp.setMobileterminal(mobileTerminal);
        pp.setAssetId(terminalConnect);
        pp.setFrequency(1);
        pp.setLatestRun(latestRun);
        pp.setPollState(ProgramPollStatus.STARTED);
        pp.setStartDate(startDate);
        pp.setStopDate(stopDate);
        pp.setCreateTime(latestRun);
        pp.setUpdatedBy("TEST");
        pp.setComment("Test Comment");

        return pp;
    }

    private MobileTerminal createMobileTerminalHelper(String serialNo) {
        MobileTerminal mt = new MobileTerminal();
        MobileTerminalPlugin mtp;
        List<MobileTerminalPlugin> plugs = testDaoBean.getPluginList();
        mtp = plugs.get(0);
        mt.setSerialNo(serialNo);
        mt.setUpdatetime(Instant.now());
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);

        return terminalDaoBean.createMobileTerminal(mt);
    }

    // we want to be able to tamper with the dates for proper test coverage
    private Instant getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        return cal.toInstant();
    }

    private Instant getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.toInstant();
    }

    private Instant getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2059);
        return cal.toInstant();
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
