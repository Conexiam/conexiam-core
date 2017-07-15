package com.conexiam.core.services;

import com.conexiam.core.model.CoreModel;
import com.conexiam.core.model.EngagementData;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import java.util.Date;
import java.util.List;

public class EngagementNodeToDataTransformer {
    public static EngagementData transform(NodeService nodeService, NodeRef engagement) {
        EngagementData engagementData = new EngagementData();

        Date engagementEndDate = (Date) nodeService.getProperty(engagement, CoreModel.PROP_ENGAGEMENT_END_DATE);
        engagementData.setEngagementEndDate(engagementEndDate);

        Date retentionEndDate = (Date) nodeService.getProperty(engagement, CoreModel.PROP_RETENTION_END_DATE);
        engagementData.setRetentionEndDate(retentionEndDate);

        String clientGroupName = getGroupNameFromAssociation(nodeService, engagement, CoreModel.ASSOC_CLIENT_GROUP);
        if (clientGroupName != null && clientGroupName.length() > 0) {
            engagementData.setClientGroup(clientGroupName);
        }

        String projectGroupName = getGroupNameFromAssociation(nodeService, engagement, CoreModel.ASSOC_PROJECT_GROUP);
        if (projectGroupName != null && projectGroupName.length() > 0) {
            engagementData.setProjectGroup(projectGroupName);
        }

        List<AssociationRef> engagementLeadAssocs = nodeService.getTargetAssocs(engagement, CoreModel.ASSOC_ENGAGEMENT_LEAD);
        if (engagementLeadAssocs != null && engagementLeadAssocs.size() >= 1) {
            NodeRef engagementLead = engagementLeadAssocs.get(0).getTargetRef();
            engagementData.setEngagementLead((String) nodeService.getProperty(engagementLead, ContentModel.PROP_USERNAME));
        }

        return engagementData;
    }

    public static String getGroupNameFromAssociation(NodeService nodeService, NodeRef nodeRef, QName assocQName) {
        String groupName = null;
        List<AssociationRef> groupAssocs = nodeService.getTargetAssocs(nodeRef, assocQName);
        if (groupAssocs != null && groupAssocs.size() >= 1) {
            NodeRef clientGroup = groupAssocs.get(0).getTargetRef();
            String groupId = (String) nodeService.getProperty(clientGroup, ContentModel.PROP_AUTHORITY_NAME);
            if (groupId != null && groupId.length() > 0) {
                groupName = groupId.substring(6, groupId.length()); // Drop the GROUP_ prefix
            }
        }
        return groupName;
    }
}
