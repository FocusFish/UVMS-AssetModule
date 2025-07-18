package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.uvms.mobileterminal.dao.PollDaoBean;
import fish.focus.uvms.mobileterminal.entity.PollBase;
import fish.focus.uvms.mobileterminal.entity.types.PollTypeEnum;
import fish.focus.uvms.mobileterminal.search.PollSearchField;
import fish.focus.uvms.mobileterminal.search.PollSearchKeyValue;
import fish.focus.uvms.mobileterminal.search.poll.PollSearchMapper;
import fish.focus.uvms.tests.BuildAssetServiceDeployment;
import fish.focus.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PollDaoBeanIntTest extends BuildAssetServiceDeployment {

    @EJB
    private PollDaoBean pollDao;

    @Inject
    private TestPollHelper testPollHelper;

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll() {
        PollBase poll = createPollHelper();
        pollDao.createPoll(poll);

        assertNotNull(poll.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll_updateUserConstraintViolation() {
        PollBase poll = createPollHelper();
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        poll.setUpdatedBy(new String(updatedBy));

        assertThrows(EJBException.class, () -> pollDao.createPoll(poll));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll_WithNull() {
        assertThrows(EJBException.class, () -> pollDao.createPoll(null));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll_WithDefaultGuidGeneration() {
        PollBase poll = createPollHelper();
        poll.setId(null);
        pollDao.createPoll(poll);

        assertNotNull(poll.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollById() {
        PollBase poll = createPollHelper();
        pollDao.createPoll(poll);

        PollBase found = pollDao.getPollById(poll.getId());
        assertNotNull(found);
        assertEquals(poll.getId(), found.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollById_willFailWithWrongId() {
        UUID uuid = UUID.randomUUID();
        PollBase poll = pollDao.getPollById(uuid);
        assertNull(poll);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollById_willFailWithNull() {
        Exception exception = assertThrows(Exception.class, () -> pollDao.getPollById(null));
        assertThat(exception.getMessage(), containsString("id to load is required for loading"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_PollSearchField_POLL_ID() {
        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);
        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_TERMINAL_TYPE() {
        String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";
        List<String> listOfPollSearchKeyValues = Arrays.asList(testEnumValue1, testEnumValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);
        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_USER() {
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        List<String> listOfPollSearchKeyValues = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue = new PollSearchKeyValue();
        pollSearchKeyValue.setSearchField(PollSearchField.USER);
        pollSearchKeyValue.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue);
        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_PollSearchField_POLL_ID_And_TERMINAL_TYPE() {
        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";
        List<String> listOfPollSearchKeyValues2 = Arrays.asList(testEnumValue1, testEnumValue2);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues2);


        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_PollSearchField_POLL_ID_And_TERMINAL_TYPE_And_USER() {
        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";
        List<String> listOfPollSearchKeyValues2 = Arrays.asList(testEnumValue1, testEnumValue2);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues2);

        String testValue3 = "testValue3";
        String testValue4 = "testValue4";
        List<String> listOfPollSearchKeyValues = Arrays.asList(testValue3, testValue4);

        PollSearchKeyValue pollSearchKeyValue3 = new PollSearchKeyValue();
        pollSearchKeyValue3.setSearchField(PollSearchField.USER);
        pollSearchKeyValue3.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2, pollSearchKeyValue3);
        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetConfigurationPollListSearchCount_SearchField_ID() {
        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONFIGURATION_POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);
        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.CONFIGURATION_POLL);
        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_settingPollSearchField_CONNECT_ID() {
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);
        pollSearchKeyValue1.setValues(Collections.singletonList(UUID.randomUUID().toString()));

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);
        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);

        Long pollListSearchCount = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
        assertThat(pollListSearchCount, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_failOnSqlReplaceToken() {
        String sql = "SELECT COUNT (DISTINCT p) FROM PollBase p ";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_noSqlPhraseCausesException() {
        String emptySql = "";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();

        Exception exception = assertThrows(EJBException.class, () -> pollDao.getPollListSearchCount(emptySql, listOfPollSearchKeyValue));
        assertThat(exception.getMessage(), containsString("unexpected end of subtree []"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_malformedSqlPhraseCausesException() {
        String sql = "SELECT * FROM PollBase p";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();

        Exception exception = assertThrows(EJBException.class, () -> pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue));
        assertThat(exception.getMessage(), containsString("unexpected token: * near line"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_settingPollSearchField_CONNECT_ID() {
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);
        pollSearchKeyValue1.setValues(Collections.singletonList(UUID.randomUUID().toString()));

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        Integer pageNumber = 1;
        Integer pageSize = 2;

        String selectSearchSql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        List<PollBase> pollListSearchPaginated = pollDao.getPollListSearchPaginated(pageNumber, pageSize, selectSearchSql, listOfPollSearchKeyValue);
        assertThat(pollListSearchPaginated, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_pageCountLessThanZeroThrowsException() {
        Integer pageNumber = 0;
        Integer pageSize = 1;

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);
        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);

        Exception exception = assertThrows(EJBTransactionRolledbackException.class, () -> pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValue));

        String expectedErrorMessage = "Error building query with values: Page number: " + pageNumber + " and Page size: " + pageSize;
        assertThat(exception.getMessage(), containsString(expectedErrorMessage));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_POLL_ID() {
        PollBase poll = createPollHelper();
        pollDao.createPoll(poll);

        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> pollSearchKeyValueList = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(pollSearchKeyValueList);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        Integer pageNumber = 1;
        Integer pageSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        List<PollBase> pollList = pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValue);
        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetProgramPollListSearchPaginated_PollSearchField_POLL_TYPE() {
        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.PROGRAM_POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        Integer pageNumber = 1;
        Integer pageSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.PROGRAM_POLL);
        List<PollBase> pollList = pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValue);
        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_TERMINAL_TYPE() {
        String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";

        List<String> pollSearchKeyValuesList = Arrays.asList(testEnumValue1, testEnumValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setValues(pollSearchKeyValuesList);

        List<PollSearchKeyValue> listOfPollSearchKeyValues = Collections.singletonList(pollSearchKeyValue1);

        Integer pageNumber = 1;
        Integer pageSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValues, true, PollTypeEnum.MANUAL_POLL);
        List<PollBase> pollList = pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValues);
        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_USER() {
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        List<String> pollSearchKeyValueList = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.USER);
        pollSearchKeyValue1.setValues(pollSearchKeyValueList);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        Integer page = 2;
        Integer listSize = 1;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true, PollTypeEnum.MANUAL_POLL);
        List<PollBase> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue);
        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void findByAssetInTimespan() {
        PollBase poll = createPollHelper();
        poll.setAssetId(UUID.randomUUID());
        pollDao.createPoll(poll);

        List<PollBase> byAssetInTimespan = pollDao.findByAssetInTimespan(poll.getAssetId(), Instant.now().minus(1, ChronoUnit.DAYS), Instant.now());
        assertEquals(1, byAssetInTimespan.size());
        assertEquals(poll.getId(), byAssetInTimespan.get(0).getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void findByAssetInTimespanPollOutsideOfTimespan() {
        PollBase poll = createPollHelper();
        poll.setAssetId(UUID.randomUUID());
        poll.setCreateTime(Instant.now().minus(25, ChronoUnit.HOURS));
        pollDao.createPoll(poll);

        List<PollBase> byAssetInTimespan = pollDao.findByAssetInTimespan(poll.getAssetId(), Instant.now().minus(1, ChronoUnit.DAYS), Instant.now());
        assertEquals(0, byAssetInTimespan.size());
    }

    private PollBase createPollHelper() {
        PollBase poll = new PollBase();

        poll.setCreateTime(Instant.now());
        poll.setUpdatedBy("testUser");
        poll.setChannelId(UUID.randomUUID());
        poll.setMobileterminal(testPollHelper.createAndPersistMobileTerminal(null));
        poll.setComment("test comment");

        return poll;
    }
}
