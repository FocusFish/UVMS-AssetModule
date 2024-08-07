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

import fish.focus.schema.mobileterminal.polltypes.v1.PollType;
import fish.focus.uvms.audit.model.mapper.AuditLogModelMapper;
import fish.focus.uvms.mobileterminal.model.constants.AuditObjectTypeEnum;
import fish.focus.uvms.mobileterminal.model.constants.AuditOperationEnum;

public class AuditModuleRequestMapper {

    public static String mapAuditLogMobileTerminalCreated(String guid, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.CREATE.getValue(), guid, AuditOperationEnum.CREATE.getValue(), username);
    }

    public static String mapAuditLogMobileTerminalUpdated(String guid, String comment, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.UPDATE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalAssigned(String guid, String comment, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.LINKED.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalUnassigned(String guid, String comment, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.UNLINKED.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalActivated(String guid, String comment, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.ACTIVATE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalInactivated(String guid, String comment, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.INACTIVATE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalArchived(String guid, String comment, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.ARCHIVE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalUnarchived(String guid, String comment, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.UNARCHIVE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogPollCreated(PollType pollType, String guid, String comment, String username) {
        AuditObjectTypeEnum pollTypeEnum;
        switch (pollType) {
            case PROGRAM_POLL:
                pollTypeEnum = AuditObjectTypeEnum.PROGRAM_POLL;
                break;
            case AUTOMATIC_POLL:
                pollTypeEnum = AuditObjectTypeEnum.AUTOMATIC_POLL;
                break;
            case CONFIGURATION_POLL:
                pollTypeEnum = AuditObjectTypeEnum.CONFIGURATION_POLL;
                break;
            default:
                pollTypeEnum = AuditObjectTypeEnum.POLL;
                break;
        }
        return mapToAuditLog(pollTypeEnum.getValue(), AuditOperationEnum.CREATE.getValue(), guid, comment, username);
    }

    private static String mapToAuditLog(String objectType, String operation, String affectedObject, String comment, String username) {
        return AuditLogModelMapper.mapToAuditLog(objectType, operation, affectedObject, comment, username);
    }

    public static String mapAuditLogProgramPollStarted(String guid, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.PROGRAM_POLL.getValue(), AuditOperationEnum.START.getValue(), guid, AuditOperationEnum.START.getValue(), username);
    }

    public static String mapAuditLogProgramPollStopped(String guid, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.PROGRAM_POLL.getValue(), AuditOperationEnum.STOP.getValue(), guid, AuditOperationEnum.STOP.getValue(), username);
    }

    public static String mapAuditLogProgramPollInactivated(String guid, String username) {
        return mapToAuditLog(AuditObjectTypeEnum.PROGRAM_POLL.getValue(), AuditOperationEnum.INACTIVATE.getValue(), guid, AuditOperationEnum.INACTIVATE.getValue(), username);
    }
}
