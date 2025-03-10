/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.schema.mobileterminal.config.v1.ConfigList;
import fish.focus.schema.mobileterminal.config.v1.TerminalSystemType;
import fish.focus.schema.mobileterminal.types.v1.PluginService;
import fish.focus.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import fish.focus.uvms.mobileterminal.dao.ChannelDaoBean;
import fish.focus.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import fish.focus.uvms.mobileterminal.entity.MobileTerminalPlugin;
import fish.focus.uvms.mobileterminal.mapper.PluginMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigModelTest {

    @Mock
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Mock
    private PluginService pluginType;

    @Mock
    private MobileTerminalPlugin siriusOne;

    @Mock
    private MobileTerminalPlugin twoStage;

    @Mock
    private ChannelDaoBean channelDao;

    @InjectMocks
    private ConfigServiceBeanMT testModelBean;

    private AutoCloseable openedMocks;

    @Before
    public void setUp() {
        openedMocks = MockitoAnnotations.openMocks(this);
        when(siriusOne.getPluginSatelliteType()).thenReturn("IRIDIUM");
        when(twoStage.getPluginSatelliteType()).thenReturn("INMARSAT_C");
    }

    @After
    public void closeMocks() throws Exception {
        openedMocks.close();
    }

    @Test
    public void testGetAllTerminalSystemsEmpty() {
        List<MobileTerminalPlugin> pluginList = new ArrayList<>();
        when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);

        List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();

        assertEquals(0, terminalSystems.size());
    }

    @Test
    public void testGetAllTerminalSystems() {
        List<MobileTerminalPlugin> pluginList = new ArrayList<>();
        pluginList.add(siriusOne);
        pluginList.add(twoStage);
        when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);

        List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();
        assertEquals(2, terminalSystems.size());

        for (TerminalSystemType system : terminalSystems) {
            assertNotNull(system.getType());
        }
    }

    @Test
    public void testGetAllTerminalSystemsException() {
        List<MobileTerminalPlugin> list = mobileTerminalPluginDao.getPluginList();
        Assert.assertNotNull(list);
        List<TerminalSystemType> list2 = testModelBean.getAllTerminalSystems();
        Assert.assertNotNull(list2);
    }

    @Test
    public void testGetConfigValues() {
        List<ConfigList> configValues = testModelBean.getConfigValues();
        assertNotNull(configValues);
        assertEquals(3, configValues.size());

        for (ConfigList config : configValues) {
            assertNotNull(config.getName());
        }
    }

    @Test
    public void testUpdatePluginEquals() {
        String serviceName = "serviceName";
        when(pluginType.getServiceName()).thenReturn(serviceName);
        when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusOne);
        try (MockedStatic<PluginMapper> pluginMapper = mockStatic(PluginMapper.class)) {
            pluginMapper.when(() -> PluginMapper.equals(siriusOne, pluginType)).thenReturn(true);
            MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);

            assertNotNull(resEntity);
        }
    }

    @Test
    public void testUpdatePluginUpdate() {
        String serviceName = "serviceName";
        when(pluginType.getServiceName()).thenReturn(serviceName);
        when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusOne);
        try (MockedStatic<PluginMapper> pluginMapper = mockStatic(PluginMapper.class)) {
            pluginMapper.when(() -> PluginMapper.equals(siriusOne, pluginType)).thenReturn(false);
            pluginMapper.when(() -> PluginMapper.mapModelToEntity(siriusOne, pluginType)).thenReturn(siriusOne);

            when(mobileTerminalPluginDao.updateMobileTerminalPlugin(any(MobileTerminalPlugin.class))).thenReturn(siriusOne);

            MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
            assertNotNull(resEntity);
        }
    }

    @Test
    public void testUpdateNoPluginFound() {
        String serviceName = "serviceName";
        when(pluginType.getServiceName()).thenReturn(serviceName);

        try {
            MobileTerminalPlugin fetched = mobileTerminalPluginDao.getPluginByServiceName(serviceName);
            MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
            assertNull(resEntity);
        } catch (Throwable t) {
            Assert.fail();
        }
    }

    @Test
    public void testInactivatePluginsException() {
        List<MobileTerminalPlugin> list = mobileTerminalPluginDao.getPluginList();
        Assert.assertNotNull(list);

        Map<String, PluginService> map = new HashMap<>();
        testModelBean.inactivatePlugins(map);
    }

    @Test
    public void testInactivatePluginsNoPlugin() {
        Map<String, PluginService> map = new HashMap<>();
        List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);

        assertNotNull(resEntityList);
        assertEquals(0, resEntityList.size());
    }

    @Test
    public void testInactivatePluginsInactive() {
        String serviceName = "serviceName";
        Map<String, PluginService> map = new HashMap<>();
        List<MobileTerminalPlugin> entityList = new ArrayList<>();
        when(siriusOne.getPluginServiceName()).thenReturn(serviceName);
        when(siriusOne.getPluginInactive()).thenReturn(false);
        entityList.add(siriusOne);
        when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);

        List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);
        assertNotNull(resEntityList);
        assertEquals(1, resEntityList.size());
        for (MobileTerminalPlugin p : resEntityList) {
            assertFalse(p.getPluginInactive());
        }
    }

    @Test
    public void testInactivePluginsExist() {
        String serviceName = "serviceName";
        Map<String, PluginService> map = new HashMap<>();
        List<MobileTerminalPlugin> entityList = new ArrayList<>();
        when(siriusOne.getPluginServiceName()).thenReturn(serviceName);
        entityList.add(siriusOne);
        when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);
        map.put(serviceName, pluginType);

        List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);
        assertNotNull(resEntityList);
        assertEquals(0, resEntityList.size());
    }

    @Test
    public void testInactivePluginsAlreadyInactive() {
        String serviceName = "serviceName";
        Map<String, PluginService> map = new HashMap<>();
        List<MobileTerminalPlugin> entityList = new ArrayList<>();
        when(siriusOne.getPluginServiceName()).thenReturn(serviceName);
        when(siriusOne.getPluginInactive()).thenReturn(true);
        entityList.add(siriusOne);
        when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);

        List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);
        assertNotNull(resEntityList);
        assertEquals(0, resEntityList.size());
    }
}
