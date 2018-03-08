//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.08 at 10:11:39 AM CET 
//


package eu.europa.ec.fisheries.asset.types;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import eu.europa.ec.fisheries.asset.enums.NoteSourceEnum;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


/**
 * <p>Java class for AssetNotes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AssetNotes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="activity" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="readyDate" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="licenseHolder" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="contact" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sheetNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="document" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="source" type="{types.asset.wsdl.fisheries.ec.europa.eu}NoteSource"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssetNotes", propOrder = {
    "id",
    "date",
    "activity",
    "user",
    "readyDate",
    "licenseHolder",
    "contact",
    "sheetNumber",
    "notes",
    "document",
    "source"
})
public class AssetNotes implements Equals, HashCode
{

    @XmlElement(required = true)
    protected BigInteger id;
    @XmlElement(required = true)
    protected String date;
    @XmlElement(required = true)
    protected String activity;
    @XmlElement(required = true)
    protected String user;
    @XmlElement(required = true)
    protected String readyDate;
    @XmlElement(required = true)
    protected String licenseHolder;
    @XmlElement(required = true)
    protected String contact;
    @XmlElement(required = true)
    protected String sheetNumber;
    @XmlElement(required = true)
    protected String notes;
    @XmlElement(required = true)
    protected String document;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected NoteSourceEnum source;

    /**
     * Default no-arg constructor
     * 
     */
    public AssetNotes() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public AssetNotes(final BigInteger id, final String date, final String activity, final String user, final String readyDate, final String licenseHolder, final String contact, final String sheetNumber, final String notes, final String document, final NoteSourceEnum source) {
        this.id = id;
        this.date = date;
        this.activity = activity;
        this.user = user;
        this.readyDate = readyDate;
        this.licenseHolder = licenseHolder;
        this.contact = contact;
        this.sheetNumber = sheetNumber;
        this.notes = notes;
        this.document = document;
        this.source = source;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the activity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivity() {
        return activity;
    }

    /**
     * Sets the value of the activity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivity(String value) {
        this.activity = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the readyDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReadyDate() {
        return readyDate;
    }

    /**
     * Sets the value of the readyDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReadyDate(String value) {
        this.readyDate = value;
    }

    /**
     * Gets the value of the licenseHolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseHolder() {
        return licenseHolder;
    }

    /**
     * Sets the value of the licenseHolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseHolder(String value) {
        this.licenseHolder = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContact(String value) {
        this.contact = value;
    }

    /**
     * Gets the value of the sheetNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSheetNumber() {
        return sheetNumber;
    }

    /**
     * Sets the value of the sheetNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSheetNumber(String value) {
        this.sheetNumber = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocument(String value) {
        this.document = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link NoteSourceEnum }
     *     
     */
    public NoteSourceEnum getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoteSourceEnum }
     *     
     */
    public void setSource(NoteSourceEnum value) {
        this.source = value;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof AssetNotes)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final AssetNotes that = ((AssetNotes) object);
        {
            BigInteger lhsId;
            lhsId = this.getId();
            BigInteger rhsId;
            rhsId = that.getId();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "id", lhsId), LocatorUtils.property(thatLocator, "id", rhsId), lhsId, rhsId)) {
                return false;
            }
        }
        {
            String lhsDate;
            lhsDate = this.getDate();
            String rhsDate;
            rhsDate = that.getDate();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "date", lhsDate), LocatorUtils.property(thatLocator, "date", rhsDate), lhsDate, rhsDate)) {
                return false;
            }
        }
        {
            String lhsActivity;
            lhsActivity = this.getActivity();
            String rhsActivity;
            rhsActivity = that.getActivity();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "activity", lhsActivity), LocatorUtils.property(thatLocator, "activity", rhsActivity), lhsActivity, rhsActivity)) {
                return false;
            }
        }
        {
            String lhsUser;
            lhsUser = this.getUser();
            String rhsUser;
            rhsUser = that.getUser();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "user", lhsUser), LocatorUtils.property(thatLocator, "user", rhsUser), lhsUser, rhsUser)) {
                return false;
            }
        }
        {
            String lhsReadyDate;
            lhsReadyDate = this.getReadyDate();
            String rhsReadyDate;
            rhsReadyDate = that.getReadyDate();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "readyDate", lhsReadyDate), LocatorUtils.property(thatLocator, "readyDate", rhsReadyDate), lhsReadyDate, rhsReadyDate)) {
                return false;
            }
        }
        {
            String lhsLicenseHolder;
            lhsLicenseHolder = this.getLicenseHolder();
            String rhsLicenseHolder;
            rhsLicenseHolder = that.getLicenseHolder();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "licenseHolder", lhsLicenseHolder), LocatorUtils.property(thatLocator, "licenseHolder", rhsLicenseHolder), lhsLicenseHolder, rhsLicenseHolder)) {
                return false;
            }
        }
        {
            String lhsContact;
            lhsContact = this.getContact();
            String rhsContact;
            rhsContact = that.getContact();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "contact", lhsContact), LocatorUtils.property(thatLocator, "contact", rhsContact), lhsContact, rhsContact)) {
                return false;
            }
        }
        {
            String lhsSheetNumber;
            lhsSheetNumber = this.getSheetNumber();
            String rhsSheetNumber;
            rhsSheetNumber = that.getSheetNumber();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "sheetNumber", lhsSheetNumber), LocatorUtils.property(thatLocator, "sheetNumber", rhsSheetNumber), lhsSheetNumber, rhsSheetNumber)) {
                return false;
            }
        }
        {
            String lhsNotes;
            lhsNotes = this.getNotes();
            String rhsNotes;
            rhsNotes = that.getNotes();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "notes", lhsNotes), LocatorUtils.property(thatLocator, "notes", rhsNotes), lhsNotes, rhsNotes)) {
                return false;
            }
        }
        {
            String lhsDocument;
            lhsDocument = this.getDocument();
            String rhsDocument;
            rhsDocument = that.getDocument();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "document", lhsDocument), LocatorUtils.property(thatLocator, "document", rhsDocument), lhsDocument, rhsDocument)) {
                return false;
            }
        }
        {
            NoteSourceEnum lhsSource;
            lhsSource = this.getSource();
            NoteSourceEnum rhsSource;
            rhsSource = that.getSource();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "source", lhsSource), LocatorUtils.property(thatLocator, "source", rhsSource), lhsSource, rhsSource)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            BigInteger theId;
            theId = this.getId();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "id", theId), currentHashCode, theId);
        }
        {
            String theDate;
            theDate = this.getDate();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "date", theDate), currentHashCode, theDate);
        }
        {
            String theActivity;
            theActivity = this.getActivity();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "activity", theActivity), currentHashCode, theActivity);
        }
        {
            String theUser;
            theUser = this.getUser();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "user", theUser), currentHashCode, theUser);
        }
        {
            String theReadyDate;
            theReadyDate = this.getReadyDate();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "readyDate", theReadyDate), currentHashCode, theReadyDate);
        }
        {
            String theLicenseHolder;
            theLicenseHolder = this.getLicenseHolder();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "licenseHolder", theLicenseHolder), currentHashCode, theLicenseHolder);
        }
        {
            String theContact;
            theContact = this.getContact();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "contact", theContact), currentHashCode, theContact);
        }
        {
            String theSheetNumber;
            theSheetNumber = this.getSheetNumber();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "sheetNumber", theSheetNumber), currentHashCode, theSheetNumber);
        }
        {
            String theNotes;
            theNotes = this.getNotes();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "notes", theNotes), currentHashCode, theNotes);
        }
        {
            String theDocument;
            theDocument = this.getDocument();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "document", theDocument), currentHashCode, theDocument);
        }
        {
            NoteSourceEnum theSource;
            theSource = this.getSource();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "source", theSource), currentHashCode, theSource);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
