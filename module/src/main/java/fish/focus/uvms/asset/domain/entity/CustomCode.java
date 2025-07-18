package fish.focus.uvms.asset.domain.entity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static fish.focus.uvms.asset.domain.entity.CustomCode.*;

@Entity
@Table(name = "customcode")
@NamedQueries({
        @NamedQuery(name = CUSTOMCODES_GETALLFOR, query = "SELECT m FROM CustomCode m WHERE UPPER(m.primaryKey.constant) LIKE UPPER(:constant)"),
        @NamedQuery(name = CUSTOMCODES_GETALLCONSTANTS, query = "SELECT distinct m.primaryKey.constant FROM CustomCode m "),
        @NamedQuery(name = CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE, query = "SELECT  m FROM CustomCode m where m.primaryKey.constant = :constant and  m.primaryKey.code = :code and ( :aDate Between m.primaryKey.validFromDate and m.primaryKey.validToDate)"),
})
public class CustomCode {

    public static final String CUSTOMCODES_GETALLFOR = "CUSTOMCODES.GETALLFOR";
    public static final String CUSTOMCODES_GETALLCONSTANTS = "CUSTOMCODES.GETALLCONSTANTS";
    public static final String CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE = "CUSTOMCODES.GETCUSTOMCODE_FOR_SPECIFIC_DATE";

    @EmbeddedId
    CustomCodesPK primaryKey;

    @Column(name = "description")
    private String description;

    @Column(name = "namevalue")
    private Map<String, String> nameValue = new HashMap<>();

    public CustomCode() {
        // for json
    }

    @EmbeddedId
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

    @ElementCollection(fetch = FetchType.EAGER) // this is a collection of primitives
    @MapKeyColumn(name = "key") // column name for map "key"
    @Column(name = "value") // column name for map "value"
    public Map<String, String> getNameValue() {
        return nameValue;
    }

    public void setNameValue(Map<String, String> nameValue) {
        this.nameValue = nameValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CustomCode that = (CustomCode) o;
        return Objects.equals(primaryKey, that.primaryKey) &&
                Objects.equals(description, that.description) &&
                Objects.equals(nameValue, that.nameValue);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(primaryKey);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(nameValue);
        return result;
    }
}
