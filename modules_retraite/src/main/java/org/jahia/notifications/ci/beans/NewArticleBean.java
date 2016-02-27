package org.jahia.notifications.ci.beans;

import java.util.List;

import org.jahia.services.content.JCRNodeWrapper;

public class NewArticleBean {
	private JCRNodeWrapper node;
	private String title;
	private List<String> relatedThematics;
	private String thematic;
	private String url;
	
	public JCRNodeWrapper getNode() {
		return node;
	}
	public void setNode(JCRNodeWrapper node) {
		this.node = node;
	}
	public List<String> getRelatedThematics() {
		return relatedThematics;
	}
	public void setRelatedThematics(List<String> relatedThematics) {
		this.relatedThematics = relatedThematics;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThematic() {
		return thematic;
	}
	public void setThematic(String thematic) {
		this.thematic = thematic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString(){
		return title;
	}
}
