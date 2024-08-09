package fish.focus.uvms.asset.client.model;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EmbeddedId;

public class CustomCode {

    @EmbeddedId
    CustomCodesPK primaryKey;
    private String description;
    private Map<String, String> nameValue = new HashMap<>();
    public CustomCode() {
        // for json
    }

    public CustomCodesPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(CustomCodesPK primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getNameValue() {
        return nameValue;
    }

    public void setNameValue(Map<String, String> nameValue) {
        this.nameValue = nameValue;
    }
}
