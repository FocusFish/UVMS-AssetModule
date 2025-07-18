package fish.focus.uvms.asset.domain.dao;

import fish.focus.uvms.asset.domain.entity.AssetFilter;
import fish.focus.uvms.asset.domain.entity.AssetFilterQuery;
import fish.focus.uvms.asset.domain.entity.AssetFilterValue;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Stateless
public class AssetFilterDao {

    @PersistenceContext
    private EntityManager em;

    public AssetFilter createAssetFilter(AssetFilter filter) {
        em.persist(filter);
        return filter;
    }

    public AssetFilter getAssetFilterByGuid(UUID filterId) {
        return em.find(AssetFilter.class, filterId);
    }

    public AssetFilter updateAssetFilter(AssetFilter filter) {
        return em.merge(filter);
    }

    public void deleteAssetFilter(AssetFilter filter) {
        em.remove(em.contains(filter) ? filter : em.merge(filter));
    }

    public List<AssetFilter> getAssetFilterAll() {
        TypedQuery<AssetFilter> query = em.createNamedQuery(AssetFilter.ASSETFILTER_FIND_ALL, AssetFilter.class);
        return query.getResultList();
    }

    public List<AssetFilter> getAssetFilterByUser(String user) {
        TypedQuery<AssetFilter> query = em.createNamedQuery(AssetFilter.ASSETFILTER_BY_USER, AssetFilter.class);
        query.setParameter("owner", user);
        return query.getResultList();
    }

    public List<AssetFilter> getAssetFiltersByValueGuidList(List<UUID> guidList) {
        TypedQuery<AssetFilter> query = em.createNamedQuery(AssetFilter.ASSETFILTER_GUID_LIST, AssetFilter.class);
        query.setParameter("guidList", guidList);
        return query.getResultList();
    }

    public List<AssetFilter> getAssetFiltersContainingAssetId(String assetId) {
        TypedQuery<AssetFilter> query = em.createNamedQuery(AssetFilter.ASSETFILTER_BY_ASSET_GUID, AssetFilter.class);
        query.setParameter("assetId", assetId);
        return query.getResultList();
    }

    public AssetFilterQuery create(AssetFilterQuery query) {
        em.persist(query);
        return query;
    }

    public AssetFilterQuery getAssetFilterQuery(UUID id) {
        try {
            TypedQuery<AssetFilterQuery> query = em.createNamedQuery(AssetFilterQuery.ASSETFILTER_QUERY_GETBYID,
                    AssetFilterQuery.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetFilterQuery update(AssetFilterQuery qvalue) {
        return em.merge(qvalue);
    }

    public AssetFilterQuery delete(AssetFilterQuery qvalue) {
        em.remove(qvalue);
        return qvalue;
    }

    public List<AssetFilterQuery> retrieveFilterQuerysForAssetFilter(AssetFilter assetFilter) {
        TypedQuery<AssetFilterQuery> query = em.createNamedQuery(AssetFilterQuery.ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER,
                AssetFilterQuery.class);
        query.setParameter("assetFilter", assetFilter);
        return query.getResultList();
    }

    public void removeValuesFromFilterQuery(AssetFilterQuery assetFilterQuery) {
        Query qry = em.createNamedQuery(AssetFilterValue.ASSETFILTER_VALUE_CLEAR);
        qry.setParameter("assetFilterQuery", assetFilterQuery);
        qry.executeUpdate();
    }

    public void removeQuerysFromFilter(AssetFilter assetFilter) {
        Query qry = em.createNamedQuery(AssetFilterQuery.ASSETFILTER_QUERY_CLEAR);
        qry.setParameter("assetFilter", assetFilter);
        qry.executeUpdate();
    }

    public AssetFilterValue getAssetFilterValueByGuid(UUID assetFilterValueId) {
        try {
            TypedQuery<AssetFilterValue> query = em.createNamedQuery(AssetFilterValue.ASSETFILTER_VALUE_GETBYID, AssetFilterValue.class);
            query.setParameter("id", assetFilterValueId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetFilterValue create(AssetFilterValue value) {
        em.persist(value);
        return value;
    }

    public AssetFilterValue get(UUID id) {
        try {
            TypedQuery<AssetFilterValue> query = em.createNamedQuery(AssetFilterValue.ASSETFILTER_VALUE_GETBYID,
                    AssetFilterValue.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetFilterValue update(AssetFilterValue value) {
        return em.merge(value);
    }

    public AssetFilterValue delete(AssetFilterValue value) {
        em.remove(value);
        return value;
    }

    public List<AssetFilterValue> retrieveValuesForFilterQuery(AssetFilterQuery assetFilterquery) {
        TypedQuery<AssetFilterValue> query = em.createNamedQuery(AssetFilterValue.ASSETFILTER_RETRIEVE_VALUES_FOR_QUERY,
                AssetFilterValue.class);
        query.setParameter("assetFilterQuery", assetFilterquery);
        return query.getResultList();
    }
}
