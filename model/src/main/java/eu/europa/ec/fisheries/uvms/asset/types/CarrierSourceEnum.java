//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.16 at 01:51:18 PM CET 
//


package eu.europa.ec.fisheries.uvms.asset.types;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CarrierSourceEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CarrierSourceEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="INTERNAL"/&gt;
 *     &lt;enumeration value="NATIONAL"/&gt;
 *     &lt;enumeration value="XEU"/&gt;
 *     &lt;enumeration value="THIRD_COUNTRY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CarrierSourceEnum", namespace = "enums.asset.fisheries.ec.europa.eu")
@XmlEnum
public enum CarrierSourceEnum {

    INTERNAL,
    NATIONAL,
    XEU,
    THIRD_COUNTRY;

    public String value() {
        return name();
    }

    public static CarrierSourceEnum fromValue(String v) {
        return valueOf(v);
    }

}