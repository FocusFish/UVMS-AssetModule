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
package fish.focus.uvms.mobileterminal.model.mapper;

import fish.focus.schema.mobileterminal.source.v1.GetMobileTerminalRequest;
import fish.focus.schema.mobileterminal.types.v1.MobileTerminalDataSourceMethod;
import fish.focus.schema.mobileterminal.types.v1.MobileTerminalId;
import fish.focus.uvms.asset.model.exception.AssetException;
import fish.focus.uvms.asset.model.mapper.JAXBMarshaller;

public class MobileTerminalDataSourceRequestMapper {

    public static String mapGetMobileTerminal(MobileTerminalId mobileTerminalId) throws AssetException {
        GetMobileTerminalRequest request = new GetMobileTerminalRequest();
        request.setMethod(MobileTerminalDataSourceMethod.GET);
        request.setId(mobileTerminalId);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
}
