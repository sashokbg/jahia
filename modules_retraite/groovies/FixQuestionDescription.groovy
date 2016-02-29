import org.apache.commons.lang.StringUtils
import org.jahia.registries.ServicesRegistry
import org.jahia.services.content.JCRCallback
import org.jahia.services.content.JCRNodeWrapper
import org.jahia.services.content.JCRSessionWrapper
import org.jahia.services.content.JCRTemplate
import org.jahia.services.sites.JahiaSite
import org.jahia.services.sites.JahiaSitesService
import org.jahia.taglibs.jcr.node.JCRTagUtils

import javax.jcr.NodeIterator
import javax.jcr.PathNotFoundException
import javax.jcr.RepositoryException
import javax.jcr.query.Query

def log = log;

JCRCallback callBack = new JCRCallback<Object>() {
    public Object doInJCR(JCRSessionWrapper jcrsession) throws RepositoryException {

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

        JCRNodeWrapper siteNode = jcrsession.getNode(site.getNode().getPath());

        for (NodeIterator iterator = JCRTagUtils.getDescendantNodes(
                siteNode, "jnt:page"); iterator.hasNext();) {
            JCRNodeWrapper page = (JCRNodeWrapper) iterator.next();
            if (getNode(page, "centerArea/ciQuestion") != null) {
                log.info("Setting description for page : " + page.getPath());

                String desc = page.getNode("centerArea").getNode("ciQuestion").getPropertyAsString("body");

                for (Locale locale : page.getExistingLocales()) {
                    try {
                        log.info("\tprocessing locale " + locale.displayName);
                        log.info("\tTitle : " + page.getI18N(locale).getProperty("jcr:title").getString());
                        log.info("\tCurrent Description : " + page.getI18N(locale).getProperty("jcr:description").getString());
                        log.info("\tNew Description : " + StringUtils.abbreviate(desc, 200));

                        page.getI18N(locale).setProperty("jcr:description", StringUtils.abbreviate(desc, 200))

                    } catch (PathNotFoundException e) {
                        log.info("\t.. No title found for locale " + locale.displayName);
                    }
                    jcrsession.save();
                }
            }
        }
        return null;
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

JCRTemplate.getInstance().doExecuteWithSystemSession(null, "live", callBack);