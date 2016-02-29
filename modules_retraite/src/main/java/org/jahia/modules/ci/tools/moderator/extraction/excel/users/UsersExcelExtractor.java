package org.jahia.modules.ci.tools.moderator.extraction.excel.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jahia.modules.ci.tools.moderator.extraction.excel.AbstractExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

/**
 * 
 * @author foo
 * 
 */
public class UsersExcelExtractor extends AbstractExcelExtractor {

	protected List<JCRNodeWrapper> resourceNodes;
	protected List<String> subscriptionNames;
	protected boolean lookInResourceNode;

	public UsersExcelExtractor(String title) {
		super(title);
		resourceNodes = new ArrayList<JCRNodeWrapper>();
		subscriptionNames = new ArrayList<String>();
	}

	public List<JCRNodeWrapper> getResourceNodes() {
		return resourceNodes;
	}

	public void setResourceNodes(List<JCRNodeWrapper> resourceNodes) {
		this.resourceNodes = resourceNodes;
	}

	public List<String> getSubscriptionNames() {
		return subscriptionNames;
	}

	public void setSubscriptionNames(List<String> subscriptionNames) {
		this.subscriptionNames = subscriptionNames;
	}

	public boolean isLookInResourceNode() {
		return lookInResourceNode;
	}

	public enum Column implements IColumn {
		NOM("Nom", "", PROPERTIES_USER_LASTAME), //
		PRENOM("Pr" + Character.toString(Character.toChars(233)[0]) + "nom", "", PROPERTIES_USER_FIRSTNAME), //
		PSEUDO("Pseudo", "", PROPERTIES_USER_PSEUDODENAME), //
		EMAIL("Email", "", PROPERTIES_USER_MAIL), //
		NODENAME("Nom systeme", "", PROPERTIES_USER_USERNAME), //
		IS_MODERATOR("Mod" + Character.toString(Character.toChars(233)[0]) + "rateur", "false", PROPERTIES_USER_IS_MODERATOR), //
		IS_PROFESSIONAL("Professionnel(le)", "false", PROPERTIES_USER_IS_PROFESSIONAL), //
		IS_MEMBER("Membre du site", "false", PROPERTIES_USER_IS_MEMBER), //
		RETREAT_YEAR("", "", PROPERTIES_USER_RETREAT_YEAR), //
		PASSWORD("", "", PROPERTIES_USER_PASSWORD), //
		COUNTRY("", "", PROPERTIES_USER_COUNTRY), //
		ZIPCODE("Code postal", "", PROPERTIES_USER_ZIPCODE), //
		TYPE("Statut", "", PROPERTIES_USER_TYPE), //
		RUBRICS("", "", PROPERTIES_USER_SELECTED_RUBRICS), //
		THEMATICS("", "", PROPERTIES_USER_SELECTED_THEMATICS), //
		AVATAR("", "", PROPERTIES_USER_PICTURE), //
		SEXE("Sexe", "", PROPERTIES_USER_GENDER), //
		BIRTH_DATE("Date de naissance", "", PROPERTIES_USER_BIRTH_DATE), //
		ACTIVITY_CHOICE("", "", PROPERTIES_USER_ACTIVITY_CHOICE), //
		ACCOUNT_LOCKED("", "", PROPERTIES_USER_ACCOUNT_LOCKED), //
		EMAIL_NOTIF_DIS("", "", PROPERTIES_USER_EMAIL_NOTIF_DIS), //
		NB_OF_QUESTIONS("", "", PROPERTIES_USER_NB_OF_QUESTIONS), //
		NB_OF_REPLIES("", "", PROPERTIES_USER_NB_OF_REPLIES), //
		REPLIES_UUID("", "", PROPERTIES_USER_REPLIES_UUID), //
		ACCEPT_NEWS("", "", PROPERTIES_USER_ACCEPT_NEWS), //
		ACCEPT_NEWS_GROUP("", "", PROPERTIES_USER_ACCEPT_NEWS_GROUP), //
		ACCEPT_NOTIF("", "", PROPERTIES_USER_ACCEPT_NOTIF), //
		BIOGRAPHY("", "", PROPERTIES_USER_BIOGRAPHY), //
		INSCRIPTION_DATE("Date d'inscription", "", PROPERTIES_JCR_CREATED);

		private String label;
		private String propertyName;
		private String defaultValue;

		private Column(String label, String defaultValue, String propertyName) {
			this.label = label;
			this.propertyName = propertyName;
			this.defaultValue = defaultValue;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

	}

	public static class NewsletterSiteExtractor extends UsersExcelExtractor {
		public NewsletterSiteExtractor() {
			super("Abonn" + Character.toString(Character.toChars(233)[0]) + "s " + Character.toString(Character.toChars(224)[0])
					+ " la newsletter du site");
			this.lookInResourceNode = true;
			this.setSubscriptionNames(Arrays.asList(new String[] { PROPERTIES_USER_ACCEPT_NEWS }));
			this.setColumns(Arrays.asList(columns));
		}

		IColumn[] columns = new IColumn[] { Column.EMAIL, Column.NOM, Column.PRENOM, Column.IS_MEMBER, Column.IS_PROFESSIONAL };
	}

	public static class NewsletterGroupExtractor extends UsersExcelExtractor {
		public NewsletterGroupExtractor() {
			super("Abonn" + Character.toString(Character.toChars(233)[0]) + "s aux offres commerciales");
			this.lookInResourceNode = false;
			this.setSubscriptionNames(Arrays.asList(new String[] { PROPERTIES_USER_ACCEPT_NEWS_GROUP }));
			this.setColumns(Arrays.asList(columns));
		}

		IColumn[] columns = new IColumn[] { Column.EMAIL, Column.NOM, Column.PRENOM, Column.SEXE, Column.TYPE, Column.BIRTH_DATE,
				Column.ZIPCODE, Column.INSCRIPTION_DATE };
	}

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		return null;
	}
}
