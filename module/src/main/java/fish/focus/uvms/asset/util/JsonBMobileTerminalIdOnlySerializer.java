package fish.focus.uvms.asset.util;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import fish.focus.uvms.mobileterminal.entity.MobileTerminal;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JsonBMobileTerminalIdOnlySerializer implements JsonbSerializer<Set<MobileTerminal>> {
    public void serialize(Set<MobileTerminal> mtSet, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        if (mtSet != null) {
            Set<UUID> idSet = mtSet.stream().map(MobileTerminal::getId).collect(Collectors.toSet());
            serializationContext.serialize(idSet, jsonGenerator);
        } else {
            serializationContext.serialize(null, jsonGenerator);
        }
    }
}
