package org.jahia.modules.ci.tools.moderator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;
import org.jahia.taglibs.jcr.node.JCRTagUtils;
import org.springframework.stereotype.Component;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import java.util.Iterator;
import java.util.Locale;

@Component
public class PatchFixQuestionDescription extends Patch {


    public PatchFixQuestionDescription() {
        super();
        setName("Fix question Description");
        setDescription("Ce patch renomme la meta description des pages des questions");
        setVersion("3.5");
    }

    @Override
    public boolean apply(JCRSessionWrapper session) {
        final Logger log = Logger.getLogger(PatchFixQuestionDescription.class);

        try {
            log.info("//////\n// Add description to questions  \n////////");

            log.info("Loading site manager");
            JahiaSitesService sitesService = ServicesRegistry.getInstance().getJahiaSitesService();
            log.info(".. OK");

            JahiaSite site = null;
            Iterator<JahiaSite> sitesIterator = sitesService.getSites();
            while (sitesIterator.hasNext()) {
                JahiaSite siteItem = sitesIterator.next();
                if (siteItem.getSiteKey().equals("preparonsmaretraite")) {
                    site = siteItem;
                    log.info("site found");
                    break;
                }
            }

            if (site == null) {
                log.error("site 'preparonsmaretraite' not found - ABORT");
            }
            log.info("site found : " + site.getNode().getPath());

            JCRNodeWrapper siteNode = session.getNode(site.getNode().getPath());

            for (NodeIterator iterator = JCRTagUtils.getDescendantNodes(
                    siteNode, "jnt:page"); iterator.hasNext(); ) {
                JCRNodeWrapper page = (JCRNodeWrapper) iterator.next();
                if (getNode(page, "centerArea/ciQuestion") != null) {
                    log.info("Setting description for page : " + page.getPath());

                    String desc = page.getNode("centerArea").getNode("ciQuestion").getPropertyAsString("body");

                    for (Locale locale : page.getExistingLocales()) {
                        try {
                            log.info("\tprocessing locale " + locale.getDisplayName());
                            log.info("\tTitle : " + page.getI18N(locale).getProperty("jcr:title").getString());
                            // log.info("\tCurrent Description : " + page.getI18N(locale).getProperty("jcr:description").getString());
                            log.info("\tNew Description : " + StringUtils.abbreviate(desc, 200));

                            page.getI18N(locale).setProperty("jcr:description", StringUtils.abbreviate(desc, 200));
                        } catch (PathNotFoundException e) {
                            log.info("\t.. No title found for locale " + locale.getDisplayName());
                        }
                        session.save();
                    }
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

    JCRNodeWrapper getNode(JCRNodeWrapper node, String nodeName) {
        JCRNodeWrapper result;
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
