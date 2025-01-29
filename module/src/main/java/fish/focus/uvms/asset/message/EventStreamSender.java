package fish.focus.uvms.asset.message;

import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.asset.message.event.UpdatedAssetEvent;
import fish.focus.uvms.asset.remote.dto.AssetMergeInfo;
import fish.focus.uvms.commons.date.JsonBConfigurator;
import fish.focus.uvms.commons.message.api.MessageConstants;
import fish.focus.uvms.commons.message.context.MappedDiagnosticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.jms.*;
import javax.json.bind.Jsonb;

@Stateless
public class EventStreamSender {

    private final static Logger LOG = LoggerFactory.getLogger(EventStreamSender.class);
    @Inject
    @JMSConnectionFactory("java:/ConnectionFactory")
    JMSContext context;
    @Resource(mappedName = "java:/" + MessageConstants.EVENT_STREAM_TOPIC)
    private Destination destination;
    private Jsonb jsonb;

    @PostConstruct
    public void init() {
        jsonb = new JsonBConfigurator().getContext(null);
    }

    public void updatedAsset(@Observes(during = TransactionPhase.AFTER_SUCCESS) @UpdatedAssetEvent Asset asset) {
        try {
            if (asset != null) {
                String outgoingJson = jsonb.toJson(asset);
                sendMessageOnEventStream(outgoingJson, "Updated Asset");
            }
        } catch (Exception e) {
            LOG.error("Error while sending message on event stream: ", e);
            throw new RuntimeException(e);
        }
    }

    public void mergeAsset(@Observes(during = TransactionPhase.AFTER_SUCCESS) @UpdatedAssetEvent AssetMergeInfo mergeInfo) {
        try {
            if (mergeInfo != null) {
                String outgoingJson = jsonb.toJson(mergeInfo);
                sendMessageOnEventStream(outgoingJson, "Merged Asset");
            }
        } catch (Exception e) {
            LOG.error("Error while sending message on event stream: ", e);
            throw new RuntimeException(e);
        }
    }

    public void sendMessageOnEventStream(String outgoingJson, String eventName) throws JMSException {
        TextMessage message = this.context.createTextMessage(outgoingJson);
        message.setStringProperty(MessageConstants.EVENT_STREAM_EVENT, eventName);
        message.setStringProperty(MessageConstants.EVENT_STREAM_SUBSCRIBER_LIST, null);
        MappedDiagnosticContext.addThreadMappedDiagnosticContextToMessageProperties(message);

        context.createProducer()
                .setDeliveryMode(DeliveryMode.PERSISTENT)
                .send(destination, message);
    }
}
