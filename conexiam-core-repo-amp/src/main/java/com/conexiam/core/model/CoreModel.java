package com.conexiam.core.model;

import org.alfresco.service.namespace.QName;

public class CoreModel {
    public static final String CONEXIAM_CORE_NAMESPACE = "http://www.conexiam.com/model/content/1.0";
    public static final QName TYPE_ENGAGEMENT = QName.createQName(CONEXIAM_CORE_NAMESPACE, "engagement");
    public static final QName PROP_ENGAGEMENT_END_DATE = QName.createQName(CONEXIAM_CORE_NAMESPACE, "engagementEndDate");
    public static final QName PROP_RETENTION_END_DATE = QName.createQName(CONEXIAM_CORE_NAMESPACE, "retentionEndDate");
    public static final QName ASSOC_CLIENT_GROUP = QName.createQName(CONEXIAM_CORE_NAMESPACE, "clientGroup");
    public static final QName ASSOC_PROJECT_GROUP = QName.createQName(CONEXIAM_CORE_NAMESPACE, "projectGroup");
    public static final QName ASSOC_ENGAGEMENT_LEAD = QName.createQName(CONEXIAM_CORE_NAMESPACE, "engagementLead");
}
