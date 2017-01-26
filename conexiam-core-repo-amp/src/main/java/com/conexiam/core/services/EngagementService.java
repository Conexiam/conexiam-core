package com.conexiam.core.services;

import com.conexiam.core.exceptions.EngagementServiceException;
import com.conexiam.core.model.CoreModel;
import com.conexiam.core.model.EngagementData;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EngagementService {
    static Logger logger = Logger.getLogger(EngagementService.class);

    // Dependencies
    private SiteService siteService;
    private NodeService nodeService;
    private AuthorityService authorityService;
    private PersonService personService;

    private final String DATA_FOLDER = "engagementInfo";
    private final String ENGAGEMENT_NODE_NAME = "engagement-info";

    /**
     * Returns an EngagementData object for the specified site ID.
     * @param siteId The id of a Share site.
     * @return An object containing information about the engagement.
     * @throws EngagementServiceException when the site cannot be found.
     */
    public EngagementData getEngagement(String siteId) throws EngagementServiceException {
        // make sure the site exists, throw an exception if it does not
        SiteInfo siteInfo = siteService.getSite(siteId);
        if (siteInfo == null) {
            throw new EngagementServiceException("Site not found: " + siteId);
        }
        NodeRef dataFolder = siteService.getContainer(siteId, DATA_FOLDER);
        if (dataFolder == null) {
            return null;
        }
        NodeRef engagement = getEngagement(dataFolder);
        if (engagement == null) {
            return null;
        }
        return EngagementNodeToDataTransformer.transform(nodeService, engagement);
    }

    /**
     * Persists the EngagementData for a specified site ID. If the data exists, it will be updated, otherwise it will
     * be created.
     * @param siteId The id of a Share site.
     * @param engagementData An EngagementData object with information about the engagement.
     * @throws EngagementServiceException when the site cannot be found.
     */
    public NodeRef saveEngagement(String siteId, EngagementData engagementData) throws EngagementServiceException {
        logger.debug("Inside saveEngagement");

        // make sure the site exists, throw an exception if it does not
        SiteInfo siteInfo = siteService.getSite(siteId);
        if (siteInfo == null) {
            throw new EngagementServiceException("Site not found: " + siteId);
        }

        // grab the engagement info folder, create it if it does not exist
        NodeRef dataFolder = siteService.getContainer(siteId, DATA_FOLDER);
        if (dataFolder == null) {
            dataFolder = createDataFolder(siteId);
        }

        // if the engagement info does not exist, create it
        NodeRef engagement = getEngagement(dataFolder);
        if (engagement == null) {
            engagement = createEngagement(dataFolder, engagementData);
        } else {
            engagement = updateEngagement(engagement, engagementData);
        }

        return engagement;
    }

    /**
     * Deletes the engagement for the specified site ID.
     * @param siteId The id of a Share site.
     * @throws EngagementServiceException when the site cannot be found.
     */
    public void deleteEngagement(String siteId) throws EngagementServiceException {
        // make sure the site exists, throw an exception if it does not
        SiteInfo siteInfo = siteService.getSite(siteId);
        if (siteInfo == null) {
            throw new EngagementServiceException("Site not found: " + siteId);
        }
        NodeRef dataFolder = siteService.getContainer(siteId, DATA_FOLDER);
        if (dataFolder == null) {
            return;
        }
        NodeRef engagement = getEngagement(dataFolder);
        if (engagement != null) {
            nodeService.deleteNode(engagement);
        }
    }

    private NodeRef getEngagement(NodeRef dataFolder) {
        List<ChildAssociationRef> children = nodeService.getChildAssocs(dataFolder);
        if (children.size() <= 0) {
            return null;
        } else {
            return children.get(0).getChildRef();
        }
    }

    private NodeRef createEngagement(NodeRef dataFolder, EngagementData engagementData) {
        logger.debug("Inside createEngagement");

        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(CoreModel.PROP_ENGAGEMENT_END_DATE, engagementData.getEngagementEndDate());
        props.put(CoreModel.PROP_RETENTION_END_DATE, engagementData.getRetentionEndDate());

        // Create the node
        NodeRef engagement = nodeService.createNode(
                dataFolder,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ENGAGEMENT_NODE_NAME),
                CoreModel.TYPE_ENGAGEMENT,
                props).getChildRef();
        logger.debug("Created the node");

        // Create associations between the node and other objects
        String projectGroupName = engagementData.getProjectGroup();
        if (projectGroupName != null && projectGroupName.length() != 0) {
            NodeRef projectGroup = authorityService.getAuthorityNodeRef(projectGroupName);
            if (projectGroup != null) {
                nodeService.createAssociation(engagement, projectGroup, CoreModel.ASSOC_PROJECT_GROUP);
            }
        }
        logger.debug("Created the project group assoc");

        String clientGroupName = engagementData.getClientGroup();
        if (clientGroupName != null && clientGroupName.length() != 0) {
            NodeRef clientGroup = authorityService.getAuthorityNodeRef(clientGroupName);
            if (clientGroup != null) {
                nodeService.createAssociation(engagement, clientGroup, CoreModel.ASSOC_CLIENT_GROUP);
            }
        }
        logger.debug("Created the client group assoc");

        String engagementLeadName = engagementData.getEngagementLead();
        if (engagementLeadName != null && engagementLeadName.length() != 0) {
            NodeRef engagementLead = personService.getPerson(engagementLeadName);
            if (engagementLead != null) {
                nodeService.createAssociation(engagement, engagementLead, CoreModel.ASSOC_ENGAGEMENT_LEAD);
            }
        }
        logger.debug("Created the engagement lead assoc");

        return engagement;
    }

    private NodeRef updateEngagement(NodeRef engagement, EngagementData updatedData) {

        EngagementData currentData = EngagementNodeToDataTransformer.transform(nodeService, engagement);

        // Modify the properties if needed
        if (!currentData.getRetentionEndDate().equals(updatedData.getRetentionEndDate()) ||
                !currentData.getRetentionEndDate().equals(updatedData.getRetentionEndDate())) {
            Map<QName, Serializable> props = new HashMap<QName, Serializable>();
            props.put(CoreModel.PROP_ENGAGEMENT_END_DATE, updatedData.getEngagementEndDate());
            props.put(CoreModel.PROP_RETENTION_END_DATE, updatedData.getRetentionEndDate());

            // Set the properties
            nodeService.setProperties(engagement, props);
        }

        // Modify the associations if needed
        if (!currentData.getProjectGroup().equals(updatedData.getProjectGroup())) {
            String projectGroupName = updatedData.getProjectGroup();
            if (projectGroupName != null && projectGroupName.length() != 0) {
                NodeRef projectGroup = authorityService.getAuthorityNodeRef(projectGroupName);
                if (projectGroup != null) {
                    // if the association exists, remove it
                    List<AssociationRef> assocs = nodeService.getTargetAssocs(engagement, CoreModel.ASSOC_PROJECT_GROUP);
                    if (assocs != null && assocs.size() > 0) {
                        nodeService.removeAssociation(engagement, assocs.get(0).getTargetRef(), CoreModel.ASSOC_PROJECT_GROUP);
                    }
                    // create a new one
                    nodeService.createAssociation(engagement, projectGroup, CoreModel.ASSOC_PROJECT_GROUP);
                }
            }
        }

        if (!currentData.getClientGroup().equals(updatedData.getClientGroup())) {
            String clientGroupName = updatedData.getClientGroup();
            if (clientGroupName != null && clientGroupName.length() != 0) {
                NodeRef clientGroup = authorityService.getAuthorityNodeRef(clientGroupName);
                if (clientGroup != null) {
                    // if the association exists, remove it
                    List<AssociationRef> assocs = nodeService.getTargetAssocs(engagement, CoreModel.ASSOC_CLIENT_GROUP);
                    if (assocs != null && assocs.size() > 0) {
                        nodeService.removeAssociation(engagement, assocs.get(0).getTargetRef(), CoreModel.ASSOC_CLIENT_GROUP);
                    }
                    // create a new one
                    nodeService.createAssociation(engagement, clientGroup, CoreModel.ASSOC_CLIENT_GROUP);
                }
            }
        }

        if (!currentData.getEngagementLead().equals(updatedData.getEngagementLead())) {
            String engagementLeadName = updatedData.getEngagementLead();
            if (engagementLeadName != null && engagementLeadName.length() != 0) {
                NodeRef engagementLead = personService.getPerson(engagementLeadName);
                if (engagementLead != null) {
                    // if the association exists, remove it
                    List<AssociationRef> assocs = nodeService.getTargetAssocs(engagement, CoreModel.ASSOC_ENGAGEMENT_LEAD);
                    if (assocs != null && assocs.size() > 0) {
                        nodeService.removeAssociation(engagement, assocs.get(0).getTargetRef(), CoreModel.ASSOC_ENGAGEMENT_LEAD);
                    }
                    // create a new one
                    nodeService.createAssociation(engagement, engagementLead, CoreModel.ASSOC_ENGAGEMENT_LEAD);
                }
            }
        }

        return engagement;
    }

    private NodeRef createDataFolder(String siteId) {
        logger.debug("Creating data folder");
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(ContentModel.PROP_NAME, DATA_FOLDER);
        return siteService.createContainer(
                siteId,
                DATA_FOLDER,
                ContentModel.TYPE_FOLDER,
                props
        );
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}
