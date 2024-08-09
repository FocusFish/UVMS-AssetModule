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
package fish.focus.uvms.mobileterminal.mapper;

import fish.focus.schema.exchange.service.v1.CapabilityType;
import fish.focus.schema.exchange.service.v1.ServiceResponseType;
import fish.focus.schema.mobileterminal.types.v1.PluginCapability;
import fish.focus.schema.mobileterminal.types.v1.PluginCapabilityType;
import fish.focus.schema.mobileterminal.types.v1.PluginService;

import java.util.ArrayList;
import java.util.List;

public class ServiceToPluginMapper {

    private ServiceToPluginMapper() {
    }

    public static PluginService mapToPlugin(ServiceResponseType service) {
        PluginService plugin = new PluginService();
        plugin.setInactive(!service.isActive());
        plugin.setLabelName(service.getName());
        plugin.setServiceName(service.getServiceClassName());
        plugin.setSatelliteType(service.getSatelliteType());
        if (service.getCapabilityList() != null) {
            plugin.getCapability().addAll(mapToPluginCapabilityList(service.getCapabilityList().getCapability()));
        }
        return plugin;
    }

    private static List<PluginCapability> mapToPluginCapabilityList(List<CapabilityType> capabilities) {
        List<PluginCapability> capabilityList = new ArrayList<>();
        if (capabilities != null) {
            for (CapabilityType capability : capabilities) {
                PluginCapability pluginCapability = new PluginCapability();
                pluginCapability.setName(PluginCapabilityType.fromValue(capability.getType().name()));
                pluginCapability.setValue(capability.getValue());
                capabilityList.add(pluginCapability);
            }
        }
        return capabilityList;
    }
}
