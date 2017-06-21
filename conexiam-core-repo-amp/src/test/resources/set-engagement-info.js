var SITE_ID = "paid-engagement-project-1";

var PROJECT_GROUP = "Project Test Project 1 All";
var CLIENT_GROUP = "Client Alpha";
var ENGAGEMENT_LEAD = "tuser2";
var ENGAGEMENT_END_DATE = new Date("01/01/2021");
var RETENTION_END_DATE = new Date("02/01/2021");

function dumpEngagementInfo(siteId) {
	var info = engagements.get(siteId);
	print("Client Group: " + info.getClientGroup());
	print("Project Group: " + info.getProjectGroup());
	print("Engagement Lead: " + info.getEngagementLead());
	print("Engagement End Date: " + info.getEngagementEndDate());
	print("Retention End Date: " + info.getRetentionEndDate());
}

function setEngagementInfo(siteId, projectGroup, clientGroup, engagementLead, engagementEndDate, retentionEndDate) {
	engagementInfo = new com.conexiam.core.model.EngagementData();
	engagementInfo.setProjectGroup(projectGroup);
	engagementInfo.setClientGroup(clientGroup);
	engagementInfo.setEngagementLead(engagementLead);
	engagementInfo.setEngagementEndDate(engagementEndDate);
	engagementInfo.setRetentionEndDate(retentionEndDate);

	engagements.save(siteId, engagementInfo);
}

setEngagementInfo(SITE_ID, PROJECT_GROUP, CLIENT_GROUP, ENGAGEMENT_LEAD, ENGAGEMENT_END_DATE, RETENTION_END_DATE);
dumpEngagementInfo(SITE_ID);