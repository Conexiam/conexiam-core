package com.conexiam.core.model;

import java.util.Date;

public class EngagementData {
    private Date engagementEndDate;
    private Date retentionEndDate;
    private String clientGroup;
    private String projectGroup;
    private String engagementLead;
    private String id;

    public Date getEngagementEndDate() {
        return engagementEndDate;
    }

    public void setEngagementEndDate(Date engagementEndDate) {
        this.engagementEndDate = engagementEndDate;
    }

    public Date getRetentionEndDate() {
        return retentionEndDate;
    }

    public void setRetentionEndDate(Date retentionEndDate) {
        this.retentionEndDate = retentionEndDate;
    }

    public String getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(String clientGroup) {
        this.clientGroup = clientGroup;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getEngagementLead() {
        return engagementLead;
    }

    public void setEngagementLead(String engagementLead) {
        this.engagementLead = engagementLead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
