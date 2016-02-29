package org.jahia.utils;

public enum SiteGroups {
	MODERATORS("moderateurs"),
	PROFESSIONALS("professionnels"),
	MEMBERS("membres");
	
	private String name;
	
	SiteGroups(String name) {
		this.name= name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name= name;
	}
}
