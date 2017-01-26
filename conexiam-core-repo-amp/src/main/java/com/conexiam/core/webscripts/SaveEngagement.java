package com.conexiam.core.webscripts;

import com.conexiam.core.exceptions.EngagementServiceException;
import com.conexiam.core.model.EngagementData;
import com.conexiam.core.services.EngagementService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SaveEngagement extends DeclarativeWebScript {
    static Logger logger = Logger.getLogger(SaveEngagement.class);

    // Dependencies
    private EngagementService engagementService;

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

        Object content = req.parseContent();
        if (!(content instanceof JSONObject)) {
            status.setCode(Status.STATUS_BAD_REQUEST);
            status.setRedirect(true);
            return model;
        }

        JSONObject engagementJSON = (JSONObject) content;
        EngagementData engagementData = getEngagementDataFromJSON(engagementJSON);

        NodeRef engagement = null;
        try {
            engagement = engagementService.saveEngagement(siteId, engagementData);
        } catch (EngagementServiceException ese) {
            status.setCode(500, "Error saving engagement data: " + ese.getMessage());
            status.setRedirect(true);
            return model;
        }

        model.put("noderef", engagement.toString());
        return model;
    }

    private EngagementData getEngagementDataFromJSON(JSONObject engagementJSON) {
        EngagementData engagementData = new EngagementData();

        // Client Group
        String clientGroup = null;
        try {
            clientGroup = engagementJSON.getString("clientGroup");
        } catch (JSONException je) {
            logger.error("Unable to parse client group from JSON");
        }
        if (clientGroup != null) {
            engagementData.setClientGroup(clientGroup);
        }

        // Project Group
        String projectGroup = null;
        try {
            projectGroup = engagementJSON.getString("projectGroup");
        } catch (JSONException je) {
            logger.error("Unable to parse project group from JSON");
        }
        if (projectGroup != null) {
            engagementData.setProjectGroup(projectGroup);
        }

        // Engagement Lead
        String engagementLead = null;
        try {
            engagementLead = engagementJSON.getString("engagementLead");
        } catch (JSONException je) {
            logger.error("Unable to parse engagement lead from JSON");
        }
        if (engagementLead != null) {
            engagementData.setEngagementLead(engagementLead);
        }

        DateFormat df = DateFormat.getDateInstance(SimpleDateFormat.DEFAULT);

        // Engagement End Date
        Date engagementEndDate = null;
        try {
            String dateStr = engagementJSON.getString("engagementEndDate");
            if (dateStr != null && dateStr.length() > 0) {
                engagementEndDate = df.parse(dateStr);
            }
        } catch (Exception e) {
            logger.error("Unable to parse engagement end date from JSON");
        }
        if (engagementEndDate != null) {
            engagementData.setEngagementEndDate(engagementEndDate);
        }

        // Retention End Date
        Date retentionEndDate = null;
        try {
            String dateStr = engagementJSON.getString("retentionEndDate");
            if (dateStr != null && dateStr.length() > 0) {
                retentionEndDate = df.parse(dateStr);
            }
        } catch (Exception e) {
            logger.error("Unable to parse retention end date from JSON");
        }
        if (retentionEndDate != null) {
            engagementData.setRetentionEndDate(retentionEndDate);
        }

        return engagementData;
    }

    public void setEngagementService(EngagementService engagementService) {
        this.engagementService = engagementService;
    }
}
