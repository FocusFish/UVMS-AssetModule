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
package fish.focus.uvms.asset.bean;

import fish.focus.schema.config.types.v1.SettingType;
import fish.focus.uvms.config.service.ParameterService;
import fish.focus.uvms.asset.domain.constant.UnitLength;
import fish.focus.uvms.asset.exception.AssetServiceException;
import fish.focus.uvms.asset.model.constants.UnitTonnage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ConfigServiceBean {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigServiceBean.class);

    @EJB
    private ParameterService parameterService;

    public Map<String, String> getParameters() throws AssetServiceException {
        try {
            Map<String, String> parameters = new HashMap<>();
            List<SettingType> allSettings = parameterService.getAllSettings();
            for (SettingType settingType : allSettings) {
                parameters.put(settingType.getKey(), settingType.getValue());
            }
            return parameters;
        } catch (Exception e) {
            LOG.error("[ Error when getting asset parameters from local database. ] {}", e);
            throw new AssetServiceException("Couldn't get parameters");
        }
    }

    private List<String> getGearTypes() {
        List<String> values = new ArrayList<>();
        return values;
    }

    private List<String> getLengthUnit() {
        List<String> values = new ArrayList<>();
        for (UnitLength unit : UnitLength.values()) {
            values.add(unit.name());
        }
        return values;
    }

    private List<String> getTonnageUnit() {
        List<String> values = new ArrayList<>();
        for (UnitTonnage unit : UnitTonnage.values()) {
            values.add(unit.name());
        }
        return values;
    }
}
