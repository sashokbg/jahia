import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeleteModuleGroovy {

	final static Log log = LogFactory.getLog(MapGroovy.class);

	static {
		log.info("//////\n// START GROOVY SCRIPT\n////////");
		org.jahia.services.sites.JahiaSite jahiaSite = org.jahia.registries.ServicesRegistry.getInstance().getJahiaSitesService()
				.getSiteByKey("terredavenir-2");
		javax.jcr.Value[] valuesOld = jahiaSite.getNode().getProperty("j:installedModules").getValues();
		java.util.ArrayList<javax.jcr.Value> valuesNew = new java.util.ArrayList<javax.jcr.Value>();
		for (int i = 0; i < valuesOld.length; i++) {
			javax.jcr.Value value = valuesOld[i];
			log.info("verifying value : " + value.getString());
			if (!value.getString().equals("error-pages-module")) {
				valuesNew.add(value);
				log.info("added new value : " + value.getString());
			}
		}
		jahiaSite.getNode().setProperty("j:installedModules", (javax.jcr.Value[]) valuesNew.toArray());
		jahiaSite.getNode().saveSession();
		log.info("//////\n// END GROOVY SCRIPT\n////////");
	}
}
