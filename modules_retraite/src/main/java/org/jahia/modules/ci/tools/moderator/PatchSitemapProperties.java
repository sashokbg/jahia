package org.jahia.modules.ci.tools.moderator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import org.apache.log4j.Logger;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;
import org.jahia.taglibs.jcr.node.JCRTagUtils;
import org.springframework.stereotype.Component;

@Component
public class PatchSitemapProperties extends Patch {

    final java.lang.String FREQUENCY_DAILY = "daily";
    final java.lang.String FREQUENCY_WEEKLY = "weekly";
    final java.lang.String FREQUENCY_MONTHLY = "monthly";
    final java.lang.String FREQUENCY_YEARLY = "yearly";

    final java.lang.String PRIORITY_DEFAULT = "0.5";
    final java.lang.String PRIORITY_MAX = "1.0";

    public PatchSitemapProperties() {
        super();
        setName("Add sitemap and set properties");
        StringBuilder description = new StringBuilder();
        description
                .append("Ce patch va ajouter le mixin jmix:sitemap dans toutes les pages.");
        setDescription(description.toString());
        setVersion("1.0");
    }

    @Override
    public boolean apply(JCRSessionWrapper session) {
        final Logger log = Logger.getLogger(PatchSitemapProperties.class);

        try {

            log.info("//////\n// Add sitemap.xml module to all pages \n////////");

            log.info("Loading site manager");
            JahiaSitesService sitesService = ServicesRegistry.getInstance()
                    .getJahiaSitesService();
            log.info(".. OK");

            JahiaSite site = null;

            Iterator<JahiaSite> sitesIterator = sitesService.getSites();

            while (sitesIterator.hasNext()) {
                org.jahia.services.sites.JahiaSite siteItem = sitesIterator
                        .next();
                if (siteItem.getNode().isNodeType("jmix:ciSiteConfiguration")) {
                    site = siteItem;
                    log.info("site found");
                    break;
                }
            }

            if (site == null) {
                log.error("site 'preparonsmaretraite' not found - ABORT");
                return false;
            }
            JCRNodeWrapper siteNode = session.getNode(site.getNode().getPath());
            for (NodeIterator iterator = JCRTagUtils.getDescendantNodes(
                    siteNode, "jnt:page"); iterator.hasNext(); ) {
                JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();

                //default values
                String frequency = FREQUENCY_MONTHLY;
                String priority = PRIORITY_DEFAULT;
                boolean addSitemap = Boolean.TRUE;

                List<String> pages = Arrays.asList("poser-une-question",
                        "pourquoi-ce-site",
                        "liste-des-professionnels",
                        "contact");

                //START set frequency and priority
                if (pages.contains(getProperty(node, "j:nodename"))) {
                    frequency = FREQUENCY_YEARLY;
                } else if ("true".equals(getProperty(node, "j:isHomePage"))) {
                    frequency = FREQUENCY_DAILY;
                    priority = PRIORITY_MAX;
                } else if ("true".equals(getProperty(node, "isForum"))
                        || "true".equals(getProperty(node, "isRubric"))
                        || "true".equals(getProperty(node, "isThematic"))) {
                    //AddQuestionAction.java should have the same value !!!
                    frequency = FREQUENCY_WEEKLY;
                } else if (getNode(node, "centerArea/article") != null) {
                    frequency = FREQUENCY_YEARLY;
                } else if (getNode(node, "centerArea/ciQuestion") != null) {
                    frequency = FREQUENCY_MONTHLY;
                } else {
                    addSitemap = Boolean.FALSE;
                }
                //END set frequency and priority
                if (addSitemap) {
                    node.addMixin("jmix:sitemap");
                    node.setProperty("changefreq", frequency);
                    node.setProperty("priority", priority);
                    session.save();
                }
            }
        } catch (JahiaException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        } catch (InvalidQueryException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        } catch (RepositoryException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    String getProperty(JCRNodeWrapper node, String propertyName) {
        return node.getPropertyAsString(propertyName);
    }

    JCRNodeWrapper getNode(JCRNodeWrapper node, String nodeName) {
        JCRNodeWrapper result = null;
        try {
            result = node.getNode(nodeName);
        } catch (PathNotFoundException e1) {
            return null;
        } catch (RepositoryException e1) {
            return null;
        }
        return result;
    }
}
