package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class MobileTerminalPluginDaoBeanIntTest extends TransactionalTests {

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginListWithNewPlugin() {
        List<MobileTerminalPlugin> mobileTerminalPluginListBefore = mobileTerminalPluginDao.getPluginList();
        assertNotNull(mobileTerminalPluginListBefore);

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        List<MobileTerminalPlugin> mobileTerminalPluginList = mobileTerminalPluginDao.getPluginList();

        assertNotNull(mobileTerminalPlugin.getId());
        assertNotNull(mobileTerminalPluginList);
        assertEquals(mobileTerminalPluginListBefore.size() + 1, mobileTerminalPluginList.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        MobileTerminalPlugin mobileTerminalPluginAfterCreation = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        MobileTerminalPlugin mobileTerminalPluginReadFromDatabase = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

        assertNotNull(mobileTerminalPluginAfterCreation);
        assertNotNull(mobileTerminalPluginReadFromDatabase);
        assertSame(mobileTerminalPlugin, mobileTerminalPluginAfterCreation);
        assertEquals(mobileTerminalPlugin.getPluginServiceName(), mobileTerminalPluginReadFromDatabase.getPluginServiceName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_persistNullEntityFailsWithTerminalDaoException() {
        assertThrows(EJBTransactionRolledbackException.class, () -> mobileTerminalPluginDao.createMobileTerminalPlugin(null));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_nameConstraintViolation() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] name = new char[41];
        Arrays.fill(name, 'x');
        mobileTerminalPlugin.setName(new String(name));

        assertThrows(ConstraintViolationException.class, () -> createMobileTerminalPluginAndFlush(mobileTerminalPlugin));
    }

    private void createMobileTerminalPluginAndFlush(MobileTerminalPlugin mobileTerminalPlugin) {
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_descriptionConstraintViolation() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] description = new char[81];
        Arrays.fill(description, 'x');
        mobileTerminalPlugin.setDescription(new String(description));

        assertThrows(ConstraintViolationException.class, () -> createMobileTerminalPluginAndFlush(mobileTerminalPlugin));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_serviceNameConstraintViolation() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] serviceName = new char[501];
        Arrays.fill(serviceName, 'x');
        mobileTerminalPlugin.setPluginServiceName(new String(serviceName));

        assertThrows(ConstraintViolationException.class, () -> createMobileTerminalPluginAndFlush(mobileTerminalPlugin));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_satelliteTypeConstraintViolation() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] satelliteType = new char[51];
        Arrays.fill(satelliteType, 'x');
        mobileTerminalPlugin.setPluginSatelliteType(new String(satelliteType));

        assertThrows(ConstraintViolationException.class, () -> createMobileTerminalPluginAndFlush(mobileTerminalPlugin));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_updateUserConstraintViolation() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        mobileTerminalPlugin.setUpdatedBy(new String(updatedBy));

        assertThrows(ConstraintViolationException.class, () -> createMobileTerminalPluginAndFlush(mobileTerminalPlugin));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName() {
        final String serviceName = "test_serviceName";
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        MobileTerminalPlugin mobileTerminalPluginAfterGetter = mobileTerminalPluginDao.getPluginByServiceName(serviceName);

        assertNotNull(mobileTerminalPlugin.getId());
        assertNotNull(mobileTerminalPluginAfterGetter);
        assertEquals(serviceName, mobileTerminalPluginAfterGetter.getPluginServiceName());
    }

    @Test()
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName_wrongServiceNameThrowsNoEntityFoundException() {
        MobileTerminalPlugin nonExistingPlugin = mobileTerminalPluginDao.getPluginByServiceName("thisServiceNameDoesNotExist");
        assertThat(nonExistingPlugin, is(nullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin() {
        final String newServiceName = "change_me";
        MobileTerminalPlugin created = createMobileTerminalPluginHelper();
        created = mobileTerminalPluginDao.createMobileTerminalPlugin(created);

        created.setPluginServiceName(newServiceName);
        MobileTerminalPlugin updated = mobileTerminalPluginDao.updateMobileTerminalPlugin(created);

        assertNotNull(created);
        assertEquals(updated.getId(), created.getId());
        assertEquals(updated.getPluginServiceName(), created.getPluginServiceName());
        assertEquals(newServiceName, created.getPluginServiceName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin_updateInsteadOfPersistFailsWithTerminalDaoException() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        MobileTerminalPlugin obj = mobileTerminalPluginDao.updateMobileTerminalPlugin(mobileTerminalPlugin);
        assertNotNull(obj);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin_persistNullEntityFailsWithTerminalDaoException() {
        assertThrows(EJBTransactionRolledbackException.class, () -> mobileTerminalPluginDao.updateMobileTerminalPlugin(null));
    }

    private MobileTerminalPlugin createMobileTerminalPluginHelper() {
        MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
        String testName = UUID.randomUUID().toString();

        mobileTerminalPlugin.setName(testName);
        mobileTerminalPlugin.setDescription("test_description");
        mobileTerminalPlugin.setPluginServiceName("test_serviceName");
        mobileTerminalPlugin.setPluginSatelliteType("test_satelliteType");
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPlugin.setUpdateTime(Instant.now());
        mobileTerminalPlugin.setUpdatedBy("test_user");

        return mobileTerminalPlugin;
    }
}
