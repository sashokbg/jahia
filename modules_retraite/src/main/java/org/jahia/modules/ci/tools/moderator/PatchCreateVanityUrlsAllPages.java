package org.jahia.modules.ci.tools.moderator;

import java.util.Iterator;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.seo.VanityUrl;
import org.jahia.services.seo.jcr.NonUniqueUrlMappingException;
import org.jahia.services.seo.jcr.VanityUrlService;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;
import org.jahia.taglibs.jcr.node.JCRTagUtils;
import org.springframework.stereotype.Component;

@Component
public class PatchCreateVanityUrlsAllPages extends Patch{

	public PatchCreateVanityUrlsAllPages() {
		super();
		setName("Create vanity urls for all pages");
		StringBuilder description = new StringBuilder();
		description
		.append("Ce patch va générer des urls vanity pour toutes les pages du site\n");
		
		setDescription(description.toString());
		setVersion("1.0");
	}
	
	@Override
	public boolean apply(JCRSessionWrapper session) {
		final Logger log = Logger.getLogger(PatchCreateVanityUrlsAllPages.class);
		
		try{
			log.info("//////\n// Create vanity urls for all pages \n////////");
			
			log.info("Loading site manager");
			JahiaSitesService sitesService = ServicesRegistry.getInstance().getJahiaSitesService();
			log.info(".. OK");
			
			JahiaSite site = null;
			Iterator<JahiaSite> sitesIterator = sitesService.getSites();
			while(sitesIterator.hasNext()){
				JahiaSite siteItem = sitesIterator.next();
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
			
			JCRNodeWrapper siteNode = session.getNode(site.getNode().getPath());
			
			for (NodeIterator iterator = JCRTagUtils.getDescendantNodes(siteNode, "jnt:page"); iterator.hasNext(); ) {
				JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
				log.info("found page: "+node);
				String siteKey = node.getResolveSite().getSiteKey();
				
				VanityUrlService vanityUrlService =(VanityUrlService) SpringContextSingleton.getBean(VanityUrlService.class.getName());
				String path = node.getPath().replace("/sites/"+siteKey, "");
				VanityUrl vanityUrl = new VanityUrl(path, siteKey, site.getDefaultLanguage(), true, true);
				
				log.info("Removing existing vanityUrls");
				
				try{
					node.getNode("vanityUrlMapping").remove();
					node.removeMixin("jmix:vanityUrlMapped");
					session.save();
				}catch(PathNotFoundException e){
					log.info(".. No existing vanity url mappings found");
				}
				
				try{
					vanityUrlService.saveVanityUrlMapping(node, vanityUrl);
				}catch(NonUniqueUrlMappingException e){
					log.error("Vanity mapping already exists for url: "+vanityUrl.getUrl());
				}
			}
			session.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		} catch (JahiaException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
