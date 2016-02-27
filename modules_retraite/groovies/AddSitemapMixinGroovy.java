import javax.jcr.NodeIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FixUserPreferences {
//NOT THE LATEST VERSION - see PatchSitemapProperties.java 
	static {

		
	try{
			log.info("//////\n// Add sitemap.xml module to  all pages \n////////");
			
			log.info("Loading site manager");
			org.jahia.services.sites.JahiaSitesService sitesService =  org.jahia.registries.ServicesRegistry.getInstance().getJahiaSitesService();
			log.info(".. OK");
			
			org.jahia.registries.JahiaSite site = null;
			java.util.Iterator<JahiaSite> sitesIterator = sitesService.getSites();
			while(sitesIterator.hasNext()){
				 org.jahia.registries.JahiaSite siteItem = sitesIterator.next();
				if(siteItem.getNode().isNodeType("jmix:ciSiteConfiguration")){
					site = siteItem;
					log.info("site found");
					break;
				}
			}
			
			if(site==null){
				log.error("site 'preparonsmaretraite' not found - ABORT");
				return false;
			}
			
			org.jahia.services.content.JCRNodeWrapper siteNode = session.getNode(site.getNode().getPath());
			
			for (org.jahia.taglibs.jcr.node.JCRTagUtils iterator = org.jahia.taglibs.jcr.node.JCRTagUtils.getDescendantNodes(siteNode, "jnt:page"); iterator.hasNext(); ) {
				org.jahia.services.content.JCRNodeWrapper node = (org.jahia.services.content.JCRNodeWrapper) iterator.next();
				log.info("found page: "+node);
				
				log.info("Removing existing vanityUrls");
				try{
					node.removeMixin("jmix:sitemap");
					session.save();
				}catch(PathNotFoundException e){
					log.info(".. No existing sitemap found");
				}
				
				log.info("Adding sitemap jmix");
				node.addMixin(jmix:sitemap);
			}
			session.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		} catch (JahiaException e) {
			e.printStackTrace();
			return false;
		}
				
	}
}
