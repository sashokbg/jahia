import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserGroovy {

	final static Log log = LogFactory.getLog(MapGroovy.class);

	static {
		log.info("//////\n// START GROOVY SCRIPT\n////////");
		org.jahia.services.usermanager.JahiaUser fame = org.jahia.registries.ServicesRegistry.getInstance().getJahiaUserManagerService()
				.lookupUser("SAME_J_NODENAME");
		org.jahia.services.usermanager.JahiaUser cazin = org.jahia.registries.ServicesRegistry.getInstance().getJahiaUserManagerService()
				.lookupUser("CAZIN_J_NODENAME");
		java.util.List<String> groupNames = org.jahia.registries.ServicesRegistry.getInstance().getJahiaGroupManagerService()
				.getUserMembership(fame);
		log.info("// List of fame group names : " + groupNames);
		for (String groupName : groupNames) {
			org.jahia.services.usermanager.JahiaGroup jahiaGroup = org.jahia.registries.ServicesRegistry.getInstance()
					.getJahiaGroupManagerService().lookupGroup(groupName);
			if (jahiaGroup != null && !"guest".equals(groupName) && !"users".equals(groupName)) {
				jahiaGroup.addMember(cazin);
				log.info("// ------------------> added cazin to group : " + groupName);
				log.info("//" + groupName + "\t[" + jahiaGroup + "]");
			}
		}
		log.info("//////\n// END GROOVY SCRIPT\n////////");
	}
}
