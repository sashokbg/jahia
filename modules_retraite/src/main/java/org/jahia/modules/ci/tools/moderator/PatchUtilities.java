package org.jahia.modules.ci.tools.moderator;

import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;

import java.util.Iterator;

public class PatchUtilities {

    public static JahiaSite findPreparonsMaRetraite() throws JahiaException {
        return findSite("preparonsmaretraite");
    }

    public static JahiaSite findSite(String siteName) throws JahiaException {
        JahiaSitesService sitesService = ServicesRegistry.getInstance()
                .getJahiaSitesService();

        JahiaSite site = null;
        Iterator<JahiaSite> sitesIterator = sitesService.getSites();
        while (sitesIterator.hasNext()) {
            JahiaSite siteItem = sitesIterator.next();
            if (siteItem.getSiteKey().equals(siteName)) {
                site = siteItem;
                break;
            }
        }
        return site;
    }
}
