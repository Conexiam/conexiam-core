package com.conexiam.tests;

import com.conexiam.core.exceptions.EngagementServiceException;
import com.conexiam.core.model.CoreModel;
import com.conexiam.core.model.EngagementData;
import com.conexiam.core.services.EngagementService;
import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/application-context.xml")
public class EngagementServiceTest {

    private static final String ADMIN_USER_NAME = "admin";

    static Logger logger = Logger.getLogger(EngagementServiceTest.class);

    @Autowired
    @Qualifier("NodeService")
    protected NodeService nodeService;

    @Autowired
    @Qualifier("SiteService")
    protected SiteService siteService;

    @Autowired
    @Qualifier("SearchService")
    protected SearchService searchService;

    @Autowired
    @Qualifier("engagement-service")
    protected EngagementService engagementService;

    @Test
    public void testBadSiteId() {
        AuthenticationUtil.setFullyAuthenticatedUser(ADMIN_USER_NAME);

        boolean exceptionThrown = false;
        try {
            engagementService.getEngagement("bad-site-id");
        } catch (EngagementServiceException ese) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testEngagementService() {
        AuthenticationUtil.setFullyAuthenticatedUser(ADMIN_USER_NAME);

        String siteShortName = "test-site-" + System.currentTimeMillis();

        SiteInfo testSite = siteService.createSite("paid-engagement", siteShortName, "test site", "test site description", SiteVisibility.PUBLIC);

        // Set up some test data
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);
        Date engagementEndDate = cal.getTime();
        cal.add(Calendar.DATE, 30);
        Date retentionEndDate = cal.getTime();
        String clientGroup = "GROUP_ALFRESCO_ADMINISTRATORS";
        String projectGroup = "GROUP_ALFRESCO_ADMINISTRATORS";
        String engagementLead = "admin";

        // Put it in a pojo
        EngagementData engagementData = new EngagementData();
        engagementData.setEngagementEndDate(engagementEndDate);
        engagementData.setRetentionEndDate(retentionEndDate);
        engagementData.setClientGroup(clientGroup);
        engagementData.setProjectGroup(projectGroup);
        engagementData.setEngagementLead("admin");

        // Persist it
        NodeRef engagement = null;
        try {
            engagement = engagementService.saveEngagement(siteShortName, engagementData);
        } catch (EngagementServiceException ese) {
        }
        assertNotNull(engagement);

        // Check the node that gets created
        assertEquals(engagementEndDate, nodeService.getProperty(engagement, CoreModel.PROP_ENGAGEMENT_END_DATE));
        assertEquals(retentionEndDate, nodeService.getProperty(engagement, CoreModel.PROP_RETENTION_END_DATE));

        // Check that the service can return it
        engagementData = null;
        try {
            engagementData = engagementService.getEngagement(siteShortName);
        } catch (EngagementServiceException ese) {
        }
        assertNotNull(engagementData);

        assertEquals(engagementEndDate, engagementData.getEngagementEndDate());
        assertEquals(retentionEndDate, engagementData.getRetentionEndDate());
        assertEquals(projectGroup, engagementData.getProjectGroup());
        assertEquals(clientGroup, engagementData.getClientGroup());
        assertEquals(engagementLead, engagementData.getEngagementLead());

        // Do an update of a property and an association
        cal.add(Calendar.DATE, 30);
        retentionEndDate = cal.getTime();
        engagementData.setRetentionEndDate(retentionEndDate);

        projectGroup = "GROUP_EMAIL_CONTRIBUTORS";
        engagementData.setProjectGroup(projectGroup);

        boolean exceptionThrown = false;
        try {
            engagementService.saveEngagement(siteShortName, engagementData);
        } catch (EngagementServiceException ese) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);

        engagementData = null;
        try {
            engagementData = engagementService.getEngagement(siteShortName);
        } catch (EngagementServiceException ese) {
        }
        assertNotNull(engagementData);

        assertEquals(engagementEndDate, engagementData.getEngagementEndDate());
        assertEquals(retentionEndDate, engagementData.getRetentionEndDate());
        assertEquals(projectGroup, engagementData.getProjectGroup());
        assertEquals(clientGroup, engagementData.getClientGroup());
        assertEquals(engagementLead, engagementData.getEngagementLead());

        // Delete the engagement
        exceptionThrown = false;
        try {
            engagementService.deleteEngagement(siteShortName);
        } catch (EngagementServiceException ese) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);

        // Check that it is gone
        engagementData = null;
        exceptionThrown = false;
        try {
            engagementData = engagementService.getEngagement(siteShortName);
        } catch (EngagementServiceException ese) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
        assertNull(engagementData);

        // Clean up the test site
        siteService.deleteSite(siteShortName);
    }
}