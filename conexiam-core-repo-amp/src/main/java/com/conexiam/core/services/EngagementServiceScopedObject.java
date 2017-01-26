package com.conexiam.core.services;

import com.conexiam.core.exceptions.EngagementServiceException;
import com.conexiam.core.model.EngagementData;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.apache.log4j.Logger;

public class EngagementServiceScopedObject extends BaseScopableProcessorExtension {
    static Logger logger = Logger.getLogger(EngagementServiceScopedObject.class);

    // Dependencies
    private EngagementService engagementService;

    public EngagementData get(String siteId) {
        EngagementData engagementData = null;
        try {
            engagementData = engagementService.getEngagement(siteId);
        } catch (EngagementServiceException ese) {
            logger.error(ese);
        }
        return engagementData;
    }

    public void setEngagementService(EngagementService engagementService) {
        this.engagementService = engagementService;
    }
}
