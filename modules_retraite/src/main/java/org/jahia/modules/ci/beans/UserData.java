package org.jahia.modules.ci.beans;

import java.util.Properties;

import org.jahia.services.usermanager.JahiaUser;

/**
 * May contain all information about for user subscription on community and
 * especially for avatar management.
 * 
 * @author lakreb
 * 
 */
public class UserData {

	private String username;
	private String pseudoname;
	private String password;
	private String lastname;
	private String firstname;
	private String email;
	private boolean avatarRequested;
	private boolean removeAvatar;
	private AvatarBean avatarBean;
	private Properties properties;
	private JahiaUser user;

	public UserData() {
		this.avatarBean = new AvatarBean();
	}

	public String getPseudoname() {
		return pseudoname;
	}

	public void setPseudoname(String pseudoname) {
		this.pseudoname = pseudoname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAvatarRequested() {
		return avatarRequested;
	}

	public void setAvatarRequested(boolean avatarRequested) {
		this.avatarRequested = avatarRequested;
	}

	public boolean isRemoveAvatar() {
		return removeAvatar;
	}

	public void setRemoveAvatar(boolean removeAvatar) {
		this.removeAvatar = removeAvatar;
	}

	public AvatarBean getAvatarBean() {
		return avatarBean;
	}

	public void setAvatarBean(AvatarBean avatarBean) {
		this.avatarBean = avatarBean;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public JahiaUser getUser() {
		return user;
	}

	public void setUser(JahiaUser user) {
		this.user = user;
	}

		@Override
	public String toString() {
		return "UserData [username=" + username + ", pseudoname=" + pseudoname + ", password=" + password
				+ ", lastname=" + lastname + ", firstname=" + firstname + ", email=" + email + ", avatarRequested=" + avatarRequested
				+ ", removeAvatar=" + removeAvatar + ", avatarBean=" + avatarBean + ", properties=" + properties + ", user=" + user + "]";
	}
	
}