package bg.alexander.chat;

import org.jahia.services.usermanager.JahiaUser;
import org.springframework.stereotype.Component;

@Component
public class CustomUserManagerImpl implements CustomUserManager {

	@Override
	public JahiaUser getUser() {
		return null;
	}
}
