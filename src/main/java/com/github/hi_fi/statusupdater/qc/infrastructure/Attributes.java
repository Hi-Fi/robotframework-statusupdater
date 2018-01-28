package com.github.hi_fi.statusupdater.qc.infrastructure;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**

 * Java class for anonymous complex type.
 *

 * The following schema fragment specifies the expected content
 * contained within this class.
 * 
 * {@code
 * <complexType>
 *   <complexContent>
 *     <restriction base=
 *         "{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element ref=
 *             "{}Attribute" maxOccurs="unbounded"
 *             minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }
 *

 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "attribute" })
@XmlRootElement(name = "Attributes")
public class Attributes {

    @XmlElement(name = "Attribute")
    protected List<Attribute> attribute;

    /**
     * Gets the value of the attribute property.
     *
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object. This is
     * why there is not a <CODE>set</CODE> method for the attribute property.
     *
     * For example, to add a new item, do as follows:
     *
     * getAttribute().add(newItem);
      *
     * Objects of the following type(s) are allowed in the
     * list {@link Attribute }
     * 
     * @return List attributes as List
     *
     *
     */
    public List<Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Attribute>();
        }
        return this.attribute;
    }

}