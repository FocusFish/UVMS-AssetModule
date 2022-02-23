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
package fish.focus.uvms.mobileterminal.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the landearthstation database table.
 *
 */
@Entity
@Table(name = "plugin_capability", indexes = { @Index(columnList = "plugin_id", name = "plugin_capability_plugin_FK_INX10", unique = false),})
@NamedQueries({
	@NamedQuery(name = "PluginCapability.findAll", query = "SELECT p FROM MobileTerminalPluginCapability p"),
})
public class MobileTerminalPluginCapability implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "MOBILETERMINALPLUGINCAPABILITY_UUID")
    @GenericGenerator(name = "MOBILETERMINALPLUGINCAPABILITY_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Size(max = 25)
    @Column(name = "value")
    private String value;

    @Size(max = 25)
    @Column(name = "capability")
    private String name;

    @Column(name = "updattim")
    private Instant updateTime;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @Column(name = "plugin_id")
    private UUID plugin;
    
    public MobileTerminalPluginCapability() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public UUID getPlugin() {
        return plugin;
    }

    public void setPlugin(UUID plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileTerminalPluginCapability that = (MobileTerminalPluginCapability) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(value, that.value) &&
                Objects.equals(name, that.name) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MobileTerminalPluginCapability{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", name='" + name + '\'' +
                ", updateTime=" + updateTime +
                ", updatedBy='" + updatedBy + '\'' +
                ", plugin=" + plugin +
                '}';
    }
}
