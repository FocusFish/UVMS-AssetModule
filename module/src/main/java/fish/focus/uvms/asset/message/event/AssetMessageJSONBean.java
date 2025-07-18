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
package fish.focus.uvms.asset.message.event;

import fish.focus.uvms.asset.bean.AssetServiceBean;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.dto.AssetBO;
import fish.focus.uvms.commons.date.JsonBConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetMessageJSONBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMessageJSONBean.class);

    @Inject
    private AssetServiceBean assetService;

    private Jsonb jsonb;

    @PostConstruct
    public void init() {
        jsonb = new JsonBConfigurator().getContext(null);
    }

    public void upsertAsset(TextMessage message) throws IOException, JMSException {
        AssetBO assetBo = jsonb.fromJson(message.getText(), AssetBO.class);
        try {
            String updatedBy = assetBo.getAsset().getUpdatedBy() == null ? "UVMS (JMS)" : assetBo.getAsset().getUpdatedBy();
            assetService.upsertAssetBO(assetBo, updatedBy);
        } catch (EJBTransactionRolledbackException ex) {
            Asset asset = assetBo.getAsset();
            LOG.error("Got error in upsertAsset for {} : {}", asset, getRootCause(ex).getMessage());
        }
    }

    public void updateAssetInformation(TextMessage message) throws IOException, JMSException {
        List<Asset> assetBos = jsonb.fromJson(message.getText(), new ArrayList<Asset>() {
        }.getClass().getGenericSuperclass());

        for (Asset oneAsset : assetBos) {
            assetService.updateAssetInformation(oneAsset, oneAsset.getUpdatedBy() == null ? "UVMS (JMS)" : oneAsset.getUpdatedBy());
        }

        LOG.info("Processed update asset list of size: {}", assetBos.size());
    }

    private Throwable getRootCause(Throwable e) {
        // simple cause loop breaker
        List<Throwable> allCauses = new ArrayList<>();

        Throwable cause = e;
        while (cause != null && !allCauses.contains(cause)) {
            allCauses.add(cause);
            cause = cause.getCause();
        }

        return allCauses.get(allCauses.size() - 1);
    }
}
