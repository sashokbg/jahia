package org.jahia.modules.ci.tools.moderator;

import org.apache.log4j.Logger;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.query.QueryResultWrapper;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.springframework.stereotype.Component;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

@Component
public class PatchDeleteFakeProfile extends Patch {

    public PatchDeleteFakeProfile() {
        super();
        setName("Delete hacker profiles");
        setDescription("Ce patch supprime les comptes des hackers.");
        setVersion("1.0");
    }

    @Override
    public boolean apply(JCRSessionWrapper session) {
        final Logger log = Logger.getLogger(this.getClass());

        try {
            log.info("Starting...");

            log.info("Loading site");
            JahiaSite site = PatchUtilities.findPreparonsMaRetraite();
            if (site == null) {
                log.error("site 'preparonsmaretraite' not found - ABORT");
                return Boolean.FALSE;
            }
            log.info(".. OK");

            log.info("Loading user manager...");
            JahiaUserManagerService userManager = ServicesRegistry
                    .getInstance().getJahiaUserManagerService();
            log.info(".. OK");

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            StringBuilder query = new StringBuilder("SELECT * FROM [jnt:user] as user where ").
                    append("user.preparonsmaretraite = 'true' ").
                    append("and  user.isMember='true'  ").append("order by pseudoname asc");
            Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
            q.setLimit(10);
            QueryResultWrapper queryResult;
            try {
                queryResult = (QueryResultWrapper) q.execute();
                NodeIterator iterator = queryResult.getNodes();
                while (iterator.hasNext()) {
                    JCRNodeWrapper user = (JCRNodeWrapper) iterator.next();
                    if (user.getPropertiesAsString().get("pseudoname") != null
                            && user.getPropertiesAsString().get("pseudoname").startsWith("&lt;")) {
                        log.info("To be deleted : " + user.getProperty("pseudoname").getString());
                        userManager.deleteUser(userManager.lookupUser(user.getName()));
                        log.info("User deleted");
                        session.save();
                    }
                }
            } catch (InvalidQueryException e) {
                log.warn("Invalid query exception : " + query);
            }

            log.info("Ending...");

        } catch (RepositoryException e) {
            log.warn("repository exception : " + e);
        } catch (JahiaException e) {
            log.warn("Jahia exception : " + e);
        }
        return Boolean.TRUE;
    }
}