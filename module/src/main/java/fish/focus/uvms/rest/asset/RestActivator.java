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
package fish.focus.uvms.rest.asset;

import fish.focus.uvms.rest.security.UnionVMSFeatureFilter;
import fish.focus.uvms.asset.util.JsonBConfiguratorAsset;
import fish.focus.uvms.rest.asset.filter.AssetRestExceptionMapper;
import fish.focus.uvms.rest.asset.service.*;
import fish.focus.uvms.rest.mobileterminal.services.MobileTerminalRestResource;
import fish.focus.uvms.rest.mobileterminal.services.PluginRestResource;
import fish.focus.uvms.rest.mobileterminal.services.PollRestResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class RestActivator extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivator() {
        set.add(AssetRestExceptionMapper.class);
        set.add(UnionVMSFeatureFilter.class);
        set.add(JsonBConfiguratorAsset.class);
        set.add(SSEResource.class);

        //V2
        set.add(AssetConfigRestResource.class);
        set.add(AssetRestResource.class);
        set.add(CustomCodesRestResource.class);
        set.add(InternalRestResource.class);
        set.add(MobileTerminalRestResource.class);
        set.add(PluginRestResource.class);
        set.add(PollRestResource.class);
        set.add(AssetFilterRestResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
