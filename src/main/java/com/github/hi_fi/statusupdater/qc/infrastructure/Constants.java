package com.github.hi_fi.statusupdater.qc.infrastructure;

/**
 *
 * These constants are used throughout the code to set the server to work with.
 * To execute this code, change these settings to fit those of your server.
 */
public class Constants {
    private Constants() {
    }

    /**
     * Supports running tests correctly on both versioned and non-versioned
     * projects.
     * 
     * @param entityType QC entity type
     * @param domain QC domain
     * @param project QC project
     * @return true if entities of entityType support versioning
     * @throws Exception Thrown if connection is unsuccessful or versioning support is not obrained correctly 
     */
    public static boolean isVersioned(String entityType, final String domain, final String project) throws Exception {

        RestConnector con = RestConnector.getInstance();
        String descriptorUrl = con
                .buildUrl("rest/domains/" + domain + "/projects/" + project + "/customization/entities/" + entityType);

        String descriptorXml = con.httpGet(descriptorUrl, null, null).toString();
        EntityDescriptor descriptor = EntityMarshallingUtils.marshal(EntityDescriptor.class, descriptorXml);

        boolean isVersioned = descriptor.getSupportsVC().getValue();

        return isVersioned;
    }

    public static String generateFieldXml(String field, String value) {
        return "<Field Name=\"" + field + "\"><Value>" + value + "</Value></Field>";
    }

    /**
     * This string used to create new "requirement" type entities.
     */
    public static final String entityToPostName = "req" + Double.toHexString(Math.random());
    public static final String entityToPostFieldName = "type-id";
    public static final String entityToPostFieldValue = "1";
    public static final String entityToPostFormat = "<Entity Type=\"requirement\">" + "<Fields>"
            + Constants.generateFieldXml("%s", "%s") + Constants.generateFieldXml("%s", "%s") + "</Fields>"
            + "</Entity>";

    public static final String entityToPostXml = String.format(entityToPostFormat, "name", entityToPostName,
            entityToPostFieldName, entityToPostFieldValue);

    public static final CharSequence entityToPostFieldXml = generateFieldXml(Constants.entityToPostFieldName,
            Constants.entityToPostFieldValue);

}
