package com.conexiam.core.authority.resolvers;

import com.conexiam.acl.templates.authority.resolvers.AuthorityResolver;
import com.conexiam.core.exceptions.EngagementServiceException;
import com.conexiam.core.model.EngagementData;
import com.conexiam.core.services.EngagementService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;

public class ProjectGroupResolver implements AuthorityResolver {
    static Logger logger = Logger.getLogger(ProjectGroupResolver.class);

    // Dependencies
    EngagementService engagementService;

    public String resolve(NodeRef nodeRef) {

        EngagementData engagementData = null;
        try {
            engagementData = engagementService.getEngagement(nodeRef);
        } catch (EngagementServiceException ese) {
            logger.error("Could not get engagement data for node: " + nodeRef.getId());
        }

        String projectGroup = null;
        if (engagementData != null && engagementData.getProjectGroup() != null) {
            projectGroup = "GROUP_" + engagementData.getProjectGroup();
        }

        return projectGroup;
    }

    public void setEngagementService(EngagementService engagementService) {
        this.engagementService = engagementService;
    }

}
