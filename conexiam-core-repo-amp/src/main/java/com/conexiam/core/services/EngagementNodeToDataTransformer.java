package com.conexiam.core.services;

import com.conexiam.core.model.CoreModel;
import com.conexiam.core.model.EngagementData;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

import java.util.Date;
import java.util.List;

public class EngagementNodeToDataTransformer {
    public static EngagementData transform(NodeService nodeService, NodeRef engagement) {
        EngagementData engagementData = new EngagementData();

        Date engagementEndDate = (Date) nodeService.getProperty(engagement, CoreModel.PROP_ENGAGEMENT_END_DATE);
        engagementData.setEngagementEndDate(engagementEndDate);

        Date retentionEndDate = (Date) nodeService.getProperty(engagement, CoreModel.PROP_RETENTION_END_DATE);
        engagementData.setRetentionEndDate(retentionEndDate);

        List<AssociationRef> clientGroupAssocs = nodeService.getTargetAssocs(engagement, CoreModel.ASSOC_CLIENT_GROUP);
        if (clientGroupAssocs != null && clientGroupAssocs.size() >= 1) {
            NodeRef clientGroup = clientGroupAssocs.get(0).getTargetRef();
            engagementData.setClientGroup((String) nodeService.getProperty(clientGroup, ContentModel.PROP_AUTHORITY_NAME));
        }

        List<AssociationRef> projectGroupAssocs = nodeService.getTargetAssocs(engagement, CoreModel.ASSOC_PROJECT_GROUP);
        if (projectGroupAssocs != null && projectGroupAssocs.size() >= 1) {
            NodeRef projectGroup = projectGroupAssocs.get(0).getTargetRef();
            engagementData.setProjectGroup((String) nodeService.getProperty(projectGroup, ContentModel.PROP_AUTHORITY_NAME));
        }

        List<AssociationRef> engagementLeadAssocs = nodeService.getTargetAssocs(engagement, CoreModel.ASSOC_ENGAGEMENT_LEAD);
        if (engagementLeadAssocs != null && engagementLeadAssocs.size() >= 1) {
            NodeRef engagementLead = engagementLeadAssocs.get(0).getTargetRef();
            engagementData.setEngagementLead((String) nodeService.getProperty(engagementLead, ContentModel.PROP_USERNAME));
        }

        return engagementData;
    }
}
