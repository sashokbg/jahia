package org.jahia.modules.ci.tools.moderator;

import org.jahia.services.content.JCRSessionWrapper;

/**
 * Abstract Patch class that will be used to process patches in the JCR database
 * @author ALKI
 *
 */
public abstract class Patch {
	private String name;
	private String description;
	private String version;
	
	
	public abstract boolean apply(JCRSessionWrapper session);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
