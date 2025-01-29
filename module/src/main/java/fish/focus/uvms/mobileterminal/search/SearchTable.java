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
package fish.focus.uvms.mobileterminal.search;

public enum SearchTable {

    ASSET("Asset", "a"),
    TERMINAL("MobileTerminal", "mt"),

    INMARSATC("MobileTerminalInmarsatC", "t"),
    INMARSATC_HISTORY("MobileTerminalInmarsatCHistory", "mh"),
    CHANNEL_INMARSATC("ChannelInmarsatC", "c"),
    CHANNEL_INMARSATC_HISTORY("ChannelInmarsatCHistory", "ch"),

    IRIDIUM("MobileTerminalIridium", "t"),
    IRIDIUM_HISTORY("MobileTerminalIridiumHistory", "mh"),
    CHANNEL_IRIDIUM("ChannelIridium", "c"),
    CHANNEL_IRIDIUM_HISTORY("ChannelIridiumHistory", "ch"),

    CONFIGURATION_POLL("ConfigurationPoll", "cp"),
    SAMPLING_POLL("SamplingPoll", "sp"),
    PROGRAM_POLL("ProgramPoll", "pp"),
    POLL_BASE("PollBase", "pb");

    private String tableName;
    private String tableAlias;

    SearchTable(String tableName, String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public String getTableName() {
        return tableName;
    }
}
