package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.schema.mobileterminal.config.v1.ConfigList;
import fish.focus.schema.mobileterminal.config.v1.TerminalSystemType;
import fish.focus.schema.mobileterminal.types.v1.PluginService;
import fish.focus.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import fish.focus.uvms.mobileterminal.constants.MobileTerminalConfigType;
import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import fish.focus.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {

    @EJB
    private ConfigServiceBeanMT configService;

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetConfig() {
        List<ConfigList> rs = configService.getConfigValues();
        assertNotNull(rs);
        assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TIME_SPAN.toString()));
        assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TYPE.toString()));
        assertTrue(configListContains(rs, MobileTerminalConfigType.TRANSPONDERS.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAddPlugin() {
        MobileTerminalPlugin plugin = configService.upsertPlugin(createPluginService());
        assertNotNull(plugin);
        assertEquals("TEST_SERVICE", plugin.getPluginServiceName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testInactivatePlugin() {
        PluginService pluginService = createPluginService();
        MobileTerminalPlugin plugin = configService.upsertPlugin(pluginService);
        assertNotNull(plugin);
        assertEquals("TEST_SERVICE", plugin.getPluginServiceName());

        MobileTerminalPlugin inactivatedPlugin = configService.inactivatePlugin(pluginService);
        assertEquals(true, inactivatedPlugin.getPluginInactive());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAddPluginUpdate() {
        PluginService pluginService = createPluginService();
        MobileTerminalPlugin plugin = configService.upsertPlugin(pluginService);
        assertNotNull(plugin);
        assertEquals("TEST_SERVICE", plugin.getPluginServiceName());

        String newLabelName = "NEW_IRIDIUM_TEST_SERVICE";
        pluginService.setLabelName(newLabelName);

        MobileTerminalPlugin updatedPlugins = configService.upsertPlugin(pluginService);
        assertNotNull(updatedPlugins);
        assertEquals(newLabelName, plugin.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadServiceName() {
        PluginService pluginService = createPluginService();
        pluginService.setServiceName("");
        assertThrows(Exception.class, () -> configService.upsertPlugin(pluginService));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadLabelName() {
        PluginService pluginService = createPluginService();
        pluginService.setLabelName("");

        assertThrows(Exception.class, () -> configService.upsertPlugin(pluginService));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadSatelliteType() {
        PluginService pluginService = createPluginService();
        pluginService.setSatelliteType("");
        assertThrows(Exception.class, () -> configService.upsertPlugin(pluginService));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetTerminalSystems() {
        MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
        mobileTerminalPlugin.setName("TEST");
        mobileTerminalPlugin.setPluginSatelliteType("TEST");
        mobileTerminalPlugin.setDescription("TEST");
        mobileTerminalPlugin.setPluginSatelliteType(MobileTerminalTypeEnum.INMARSAT_C.toString());
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        List<TerminalSystemType> rs = configService.getTerminalSystems();
        assertNotNull(rs);
        assertFalse(rs.isEmpty());
        assertTrue(terminalSystemsContains(rs, MobileTerminalTypeEnum.INMARSAT_C.toString()));
    }

    private PluginService createPluginService() {
        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("IRIDIUM_TEST_SERVICE");
        pluginService.setSatelliteType("IRIDIUM");
        pluginService.setServiceName("TEST_SERVICE");
        return pluginService;
    }

    private boolean terminalSystemsContains(List<TerminalSystemType> list, String type) {
        for (TerminalSystemType item : list) {
            if (item.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean configListContains(List<ConfigList> configLists, String value) {
        for (ConfigList item : configLists) {
            if (value.equals(item.getName())) {
                return true;
            }
        }
        return false;
    }
}
