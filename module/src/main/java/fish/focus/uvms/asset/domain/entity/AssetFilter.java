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
package fish.focus.uvms.asset.domain.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import fish.focus.uvms.rest.asset.util.AssetFilterRestResponseAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static fish.focus.uvms.asset.domain.entity.AssetFilter.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "assetfilter")
@NamedQueries({
        @NamedQuery(name = ASSETFILTER_FIND_ALL, query = "SELECT a FROM AssetFilter a"),
        @NamedQuery(name = ASSETFILTER_BY_USER, query = "SELECT a FROM AssetFilter a WHERE a.owner = :owner"),
        @NamedQuery(name = ASSETFILTER_GUID_LIST, query = "SELECT a FROM AssetFilter a WHERE a.id IN :guidList"),
        @NamedQuery(name = ASSETFILTER_BY_ASSET_GUID, query = "SELECT af FROM AssetFilter af JOIN af.queries afq JOIN afq.values afv WHERE afq.type = 'GUID' AND afv.valueString = :assetId"),
})
@JsonbTypeAdapter(AssetFilterRestResponseAdapter.class)
public class AssetFilter implements Serializable {

    public static final String ASSETFILTER_FIND_ALL = "AssetFilter.findAll";
    public static final String ASSETFILTER_BY_USER = "AssetFilter.findByUser";
    public static final String ASSETFILTER_GUID_LIST = "AssetFilter.findByGuidList";
    public static final String ASSETFILTER_BY_ASSET_GUID = "AssetFilter.findByAssetGuid";
    private static final long serialVersionUID = -1218306334950687248L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "updatetime")
    private Instant updateTime;

    @Size(max = 255)
    @Column(name = "updatedby")
    private String updatedBy;

    @Size(max = 255)
    @Column(name = "owner")
    private String owner;

    @OneToMany(mappedBy = "assetFilter", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Column(name = "queries")
    private Set<AssetFilterQuery> queries;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<AssetFilterQuery> getQueries() {
        return queries;
    }

    public void setQueries(Set<AssetFilterQuery> queries) {
        this.queries = queries;
    }

}
