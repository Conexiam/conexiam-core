package com.conexiam.core.webscripts;

import com.conexiam.core.exceptions.EngagementServiceException;
import com.conexiam.core.model.EngagementData;
import com.conexiam.core.services.EngagementService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteEngagement extends DeclarativeWebScript {
    // Dependencies
    EngagementService engagementService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, String> templateArgs =
                req.getServiceMatch().getTemplateVars();

        String siteId = templateArgs.get("siteId");
        if (siteId == null) {
            status.setCode(400, "Site ID is required");
            status.setRedirect(true);
            return model;
        }

        try {
            engagementService.deleteEngagement(siteId);
        } catch (EngagementServiceException ese) {
            status.setCode(500, ese.getMessage());
            status.setRedirect(true);
            return model;
        }

        model.put("message", "Deleted");
        return model;
    }

    public void setEngagementService(EngagementService engagementService) {
        this.engagementService = engagementService;
    }
}
