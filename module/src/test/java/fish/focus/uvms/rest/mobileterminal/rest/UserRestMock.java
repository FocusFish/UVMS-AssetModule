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
package fish.focus.uvms.rest.mobileterminal.rest;

import java.util.Arrays;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import fish.focus.uvms.rest.security.UnionVMSFeature;
import fish.focus.uvms.usm.jwt.JwtTokenHandler;

@Path("user/token")
@Stateless
public class UserRestMock {

    @EJB
    private JwtTokenHandler tokenHandler;

    @GET
    public Response getToken() {
        String token = tokenHandler.createToken("user",
                Arrays.asList(UnionVMSFeature.viewVesselsAndMobileTerminals.getFeatureId(), UnionVMSFeature.mobileTerminalPlugins.getFeatureId(),
                        UnionVMSFeature.manageVessels.getFeatureId(), UnionVMSFeature.viewMobileTerminalPolls.getFeatureId(), UnionVMSFeature.managePolls.getFeatureId(), UnionVMSFeature.manageMobileTerminals.getFeatureId()));
        return Response.ok(token).build();
    }
}
