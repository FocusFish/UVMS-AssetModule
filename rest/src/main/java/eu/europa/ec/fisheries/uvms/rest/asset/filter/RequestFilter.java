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
package eu.europa.ec.fisheries.uvms.rest.asset.filter;

import eu.europa.ec.fisheries.uvms.rest.asset.Constant;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class RequestFilter implements Filter {

    @Resource(lookup = "java:global/corsAllowedOriginList")
    private String corsOriginList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String HOST = httpServletRequest.getHeader("HOST");

        if(!getAllowedOriginList().contains(HOST))
            throw new ForbiddenException("You are not allowed to make any request from an external domain.");

        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader(Constant.ACCESS_CONTROL_ALLOW_ORIGIN, HOST);
        response.setHeader(Constant.ACCESS_CONTROL_ALLOW_METHODS, Constant.ACCESS_CONTROL_ALLOWED_METHODS);
        response.setHeader(Constant.ACCESS_CONTROL_ALLOW_HEADERS, Constant.ACCESS_CONTROL_ALLOW_HEADERS_ALL);

        chain.doFilter(request, res);
    }

    private List<String> getAllowedOriginList() {
        return Arrays.asList(corsOriginList.split(","));
    }

    @Override
    public void destroy() {
    }
}