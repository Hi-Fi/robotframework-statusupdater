package com.github.hi_fi.statusupdater.qc.infrastructure;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;




@XmlRootElement(name = "EntityResourceDescriptor")
@XmlType(name = "", propOrder = {
        "table",
        "name",
        "label",
        "supportsHistory",
        "supportsAttachment",
        "supportsLock",
        "supportsGrouping",
        "supportsMailing",
        "supportsStorage",
        "supportsMultiValue",
        "supportsWorkflow",
        "supportsDataHidingFilter",
        "supportsVC",
        "supportsSubtypes",
        "supportsCopying",
        "extensionName",
        "siteEntity",
        "attributes",
        "firstLevelResource",
        "secondLevelResources" })
public final class EntityDescriptor {

    private String baseUrl;
    private String collectionName;
    private Collection<AttributeElement> attributes;
    private String extensionName;
    private IsFirstLevelResourceElement isFirstLevelResource;
    private String label;
    private String name;
    private Boolean isSiteEntity;
    private BooleanWithURLType supportsAttachment;
    private Boolean supportsDataHidingFilter;
    private BooleanWithURLType supportsGrouping;
    private BooleanWithURLType supportsHistory;
    private BooleanWithURLType supportsLock;
    private BooleanWithURLType supportsMailing;
    private BooleanWithURLType supportsStorage;
    private SupportSubtypeInfo supportsSubtypes;
    private Boolean supportsMultiValue;
    private BooleanWithURLType supportsVC;
    private Boolean supportsWorkflow;
    private String table;
    private Collection<IsSecondLevelResourceElement> secondLevelResources;
    private BooleanWithURLType supportsCopying;

    @XmlAttribute(name = "baseUrl", required = true)
    public final String getBaseUrl() {
        return baseUrl;
    }

    public final void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @XmlAttribute(name = "collectionName", required = true)
    public final String getCollectionName() {
        return collectionName;
    }

    public final void setCollectionName(final String collectionName) {
        this.collectionName = collectionName;
    }

    @XmlElementWrapper(name = "Attributes")
    @XmlElement(name = "Attribute")
    public Collection<AttributeElement> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<AttributeElement>();
        }

        return attributes;
    }

    @XmlElementWrapper(name = "SecondLevelResources",
        required = false)
    @XmlElement(name = "IsSecondLevelResource")
    public Collection<IsSecondLevelResourceElement> getSecondLevelResources() {
        if (secondLevelResources == null) {
            secondLevelResources = new ArrayList<IsSecondLevelResourceElement>();
        }

        return secondLevelResources;
    }

    @XmlElement(name = "ExtensionName")
    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    @XmlElement(name = "IsFirstLevelResource")
    public IsFirstLevelResourceElement isFirstLevelResource() {
        return isFirstLevelResource;
    }

    public void setFirstLevelResource
           (IsFirstLevelResourceElement firstLevelResource) {
        isFirstLevelResource = firstLevelResource;
    }

    @XmlElement(name = "Label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "IsSiteEntity")
    public Boolean getSiteEntity() {
        return isSiteEntity;
    }

    public void setSiteEntity(Boolean siteEntity) {
        isSiteEntity = siteEntity;
    }

    @XmlElement(name = "SupportsAttachment")
    public BooleanWithURLType getSupportsAttachment() {
        return supportsAttachment;
    }

    public void setSupportsAttachment(BooleanWithURLType supportsAttachment) {
        this.supportsAttachment = supportsAttachment;
    }

    @XmlElement(name = "SupportsDataHidingFilter")
    public Boolean getSupportsDataHidingFilter() {
        return supportsDataHidingFilter;
    }

    public void setSupportsDataHidingFilter(Boolean supportsDataHidingFilter) {
        this.supportsDataHidingFilter = supportsDataHidingFilter;
    }

    @XmlElement(name = "SupportsGrouping")
    public BooleanWithURLType getSupportsGrouping() {
        return supportsGrouping;
    }

    public void setSupportsGrouping(BooleanWithURLType supportsGrouping) {
        this.supportsGrouping = supportsGrouping;
    }

    @XmlElement(name = "SupportsHistory")
    public BooleanWithURLType getSupportsHistory() {
        return supportsHistory;
    }

    public void setSupportsHistory(BooleanWithURLType supportsHistory) {
        this.supportsHistory = supportsHistory;
    }

    @XmlElement(name = "SupportsLock")
    public BooleanWithURLType getSupportsLock() {
        return supportsLock;
    }

    public void setSupportsLock(BooleanWithURLType supportsLock) {
        this.supportsLock = supportsLock;
    }

    @XmlElement(name = "SupportsMailing")
    public BooleanWithURLType getSupportsMailing() {
        return supportsMailing;
    }

    @XmlElement(name = "SupportsStorage")
    public BooleanWithURLType getSupportsStorage() {
        return supportsStorage;
    }

    public void setSupportsMailing(BooleanWithURLType supportsMailing) {
        this.supportsMailing = supportsMailing;
    }

    public void setSupportsStorage(BooleanWithURLType supportsStorage) {
        this.supportsStorage = supportsStorage;
    }

    @XmlElement(name = "SupportsMultiValue")
    public Boolean getSupportsMultiValue() {
        return supportsMultiValue;
    }

    public void setSupportsMultiValue(Boolean supportsMultiValue) {
        this.supportsMultiValue = supportsMultiValue;
    }

    @XmlElement(name = "SupportsSubtypes")
    public SupportSubtypeInfo getSupportsSubtypes() {
        return supportsSubtypes;
    }

    public void setSupportsSubtypes(SupportSubtypeInfo supportsSubtypes) {
        this.supportsSubtypes = supportsSubtypes;
    }

    @XmlElement(name = "SupportsVC")
    public BooleanWithURLType getSupportsVC() {
        return supportsVC;
    }

    public void setSupportsVC(BooleanWithURLType supportsVC) {
        this.supportsVC = supportsVC;
    }

    @XmlElement(name = "SupportsWorkflow")
    public Boolean getSupportsWorkflow() {
        return supportsWorkflow;
    }

    public void setSupportsWorkflow(Boolean supportsWorkflow) {
        this.supportsWorkflow = supportsWorkflow;
    }

    @XmlElement(name = "Table")
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @XmlElement(name = "SupportsCopying")
    public BooleanWithURLType getSupportsCopying() {
        return supportsCopying;
    }

    public void setSupportsCopying(BooleanWithURLType supportsCopying) {
        this.supportsCopying = supportsCopying;
    }

    @XmlRootElement
    public static class BooleanWithURLType {

        private String url;
        private Boolean value;

        @XmlAttribute(name = "url", required = true)
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @XmlValue
        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }

    @XmlRootElement(name = "IsFirstLevelResource")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class IsFirstLevelResourceElement extends BooleanWithURLType {

        @XmlAttribute(name = "supportsPOST")
        protected boolean supportsPOST;

        @XmlAttribute(name = "supportsGET")
        protected boolean supportsGET;

        @XmlAttribute(name = "supportsPUT")
        protected boolean supportsPUT;

        @XmlAttribute(name = "supportsDELETE")
        protected boolean supportsDELETE;

        public boolean isSupportsPOST() {
            return supportsPOST;
        }

        public boolean isSupportsGET() {
            return supportsGET;
        }

        public boolean isSupportsPUT() {
            return supportsPUT;
        }

        public boolean isSupportsDELETE() {
            return supportsDELETE;
        }
    }

    @XmlRootElement(name = "Attribute")
    @XmlType
    public static final class AttributeElement {

        private String name;
        private String value;

        @XmlAttribute(name = "name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlValue
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XmlRootElement(name = "IsSecondLevelResource")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class IsSecondLevelResourceElement
       extends IsFirstLevelResourceElement {

        @XmlAttribute(name = "parentEntity", required = true)
        private String parentEntity;

        public String getParentEntity() {
            return parentEntity;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SupportSubtypeInfo extends BooleanWithURLType {

        @XmlAttribute(name = "subTypeFieldName")
        protected String subTypeFieldName;

        public String getSubTypeFieldName() {
            return subTypeFieldName;
        }
    }
}

