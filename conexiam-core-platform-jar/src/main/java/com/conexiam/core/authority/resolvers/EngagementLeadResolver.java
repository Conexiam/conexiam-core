package com.conexiam.core.authority.resolvers;

import com.conexiam.acl.templates.authority.resolvers.AuthorityResolver;
import com.conexiam.core.exceptions.EngagementServiceException;
import com.conexiam.core.model.EngagementData;
import com.conexiam.core.services.EngagementService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;

public class EngagementLeadResolver implements AuthorityResolver {
    static Logger logger = Logger.getLogger(EngagementLeadResolver.class);

    // Dependencies
    EngagementService engagementService;

    public String resolve(NodeRef nodeRef) {

        EngagementData engagementData = null;
        try {
            engagementData = engagementService.getEngagement(nodeRef);
        } catch (EngagementServiceException ese) {
            logger.error("Could not get engagement data for node: " + nodeRef.getId());
        }

        String engagementLead = null;
        if (engagementData != null && engagementData.getEngagementLead() != null) {
            engagementLead = engagementData.getEngagementLead();
        }

        return engagementLead;
    }

    public void setEngagementService(EngagementService engagementService) {
        this.engagementService = engagementService;
    }

}
