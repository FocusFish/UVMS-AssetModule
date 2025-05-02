package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.dao.TerminalDaoBean;
import fish.focus.uvms.mobileterminal.entity.Channel;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import fish.focus.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import fish.focus.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class TerminalDaoBeanIT extends TransactionalTests {

    private final Random rnd = new Random();

    @EJB
    private TerminalDaoBean terminalDaoBean;

    @EJB
    private MobileTerminalPluginDaoBean testDaoBean;

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);

        assertNotNull(created.getId());
        assertEquals(serialNo, created.getSerialNo());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createChannelWoName() throws SystemException, NotSupportedException {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        Channel channel = new Channel();
        channel.setMobileTerminal(mobileTerminal);
        channel.setConfigChannel(true);
        channel.setPollChannel(true);
        channel.setDefaultChannel(true);
        channel.setDnid(555);
        channel.setLesDescription("description");
        channel.setArchived(false);
        channel.setMemberNumber(5555);
        channel.setUpdateUser("tester");
        channel.setExpectedFrequency(Duration.ZERO);
        channel.setFrequencyGracePeriod(Duration.ZERO);
        channel.setExpectedFrequencyInPort(Duration.ZERO);
        channel.setName(null);

        mobileTerminal.getChannels().add(channel);

        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);

        assertThrows(Exception.class, () -> userTransaction.commit());
        userTransaction.begin();
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFailWithUpdateUserConstraintViolation() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        char[] chars = new char[61];
        Arrays.fill(chars, 'x');
        mobileTerminal.setUpdateuser(new String(chars));

        assertThrows(EJBTransactionRolledbackException.class, () -> createMobileTerminalAndFlush(mobileTerminal));
    }

    private void createMobileTerminalAndFlush(MobileTerminal mobileTerminal) {
        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);
        MobileTerminal fetched = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo_NON_EXISTING_SERIAL_NO() {
        MobileTerminal doesNotExist = terminalDaoBean.getMobileTerminalBySerialNo("does_not_exist");
        assertNull(doesNotExist);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_VERIFY_THAT_SETGUID_DOES_NOT_WORK_AT_CREATE() {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);
        String uuid = created.getId().toString();

        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        assertThat(fetchedBySerialNo, is(notNullValue()));
        assertThat(fetchedBySerialNo.getId(), is(notNullValue()));
        assertThat(fetchedBySerialNo.getId(), is(equalTo(uuid)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid() {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        createMobileTerminalAndFlush(mobileTerminal);
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
        UUID id = fetchedBySerialNo.getId();
        assertNotNull(id);
        MobileTerminal fetchedByGUID = terminalDaoBean.getMobileTerminalById(id);

        assertThat(fetchedBySerialNo.getSerialNo(), is(notNullValue()));
        assertThat(fetchedBySerialNo.getId(), is(notNullValue()));
        assertThat(fetchedBySerialNo.getSerialNo(), is(equalTo(serialNo)));

        assertThat(fetchedByGUID, is(notNullValue()));
        assertThat(fetchedByGUID.getId(), is(notNullValue()));
        assertThat(fetchedByGUID.getId(), is(equalTo(fetchedBySerialNo.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid_NON_EXISTING_GUID() {
        MobileTerminal mt = terminalDaoBean.getMobileTerminalById(UUID.randomUUID());
        assertNull(mt);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        String updateUser = UUID.randomUUID().toString();
        mobileTerminal.setUpdateuser(updateUser);

        createMobileTerminalAndFlush(mobileTerminal);

        String sql = "SELECT m FROM MobileTerminal m WHERE m.updateuser = '" + updateUser + "'";

        List<MobileTerminal> mobileTerminals = terminalDaoBean.getMobileTerminalsByQuery(sql);

        assertThat(mobileTerminals, is(notNullValue()));
        assertThat(mobileTerminals, is(not(empty())));

        assertThat(mobileTerminals, hasItem(hasProperty("updateUser", equalTo(updateUser))));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery_ShouldFailWithInvalidSqlQuery() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        createMobileTerminalAndFlush(mobileTerminal);

        String sql = "SELECT m FROM MobileTerminal m WHERE m.updateuser = 'test'"; // lower cases

        List<MobileTerminal> mobileTerminals = terminalDaoBean.getMobileTerminalsByQuery(sql);

        assertThat(mobileTerminals, anyOf(nullValue(), empty()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        createMobileTerminalAndFlush(mobileTerminal);

        mobileTerminal.setUpdateuser("NEW_TEST_USER");
        MobileTerminal updated = terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();

        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        assertThat(fetchedBySerialNo, is(notNullValue()));
        assertThat(fetchedBySerialNo.getId(), is(notNullValue()));
        assertThat(fetchedBySerialNo.getUpdateuser(), is(equalToIgnoringCase(updated.getUpdateuser())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithGuidConstraintViolation() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        createMobileTerminalAndFlush(mobileTerminal);
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        String uuid = UUID.randomUUID() + "length-violation";
        fetchedBySerialNo.setId(UUID.fromString(uuid));

        assertThrows(IllegalArgumentException.class, () -> updateMobileTerminalAndFlush(mobileTerminal));
    }

    private void updateMobileTerminalAndFlush(MobileTerminal mobileTerminal) {
        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithNoPersistedEntity() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        assertThrows(EJBTransactionRolledbackException.class, () -> updateMobileTerminalAndFlush(mobileTerminal));
    }

    private MobileTerminal createMobileTerminalHelper(String serialNo) {
        MobileTerminal mt = new MobileTerminal();
        MobileTerminalPlugin mtp;
        List<MobileTerminalPlugin> plugs;

        plugs = testDaoBean.getPluginList();
        mtp = plugs.get(0);
        mt.setSerialNo(serialNo);
        mt.setUpdatetime(Instant.now());
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);
        return mt;
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
