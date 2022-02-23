package fish.focus.uvms.asset.util;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import fish.focus.uvms.asset.domain.entity.Asset;

public class JsonBAssetIdOnlySerializer implements JsonbSerializer<Asset> {
        public void serialize(Asset asset, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
            if (asset != null) {
                serializationContext.serialize( asset.getId(), jsonGenerator);
            } else {
                serializationContext.serialize(null, jsonGenerator);
            }
        }
}
