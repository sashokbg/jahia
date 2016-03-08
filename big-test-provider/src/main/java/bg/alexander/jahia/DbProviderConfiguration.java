package bg.alexander.jahia;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.jahia.modules.external.users.ExternalUserGroupService;
import org.jahia.modules.external.users.UserGroupProviderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbProviderConfiguration implements UserGroupProviderConfiguration {

    private static final long serialVersionUID = 5386110682796331585L;
    @Autowired
    private ExternalUserGroupService externalUserGroupService;
    private String userGroupProviderClass;
    
    @PostConstruct
	public void init() {
        externalUserGroupService.setConfiguration("userGroupProviderClass", this);
    }
    
    @Override
    public String getName() {
        return "Base de Données";
    }

    @Override
    public boolean isCreateSupported() {
        return true;
    }

    @Override
    public String getCreateJSP() {
        return "/modules/df-users-groups-db-provider-showcase/userGroupProviderCreate.jsp";
    }

    @Override
    public String create(Map<String, Object> parameters, Map<String, Object> flashScope) throws Exception {
        return null;
    }

    @Override
    public boolean isEditSupported() {
        return true;
    }

    @Override
    public String getEditJSP() {
        return "/modules/df-users-groups-db-provider-showcase/userGroupProviderEdit.jsp";
    }

    @Override
    public void edit(String providerKey, Map<String, Object> parameters, Map<String, Object> flashScope)
            throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDeleteSupported() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void delete(String providerKey, Map<String, Object> flashScope) throws Exception {
        // TODO Auto-generated method stub

    }

}
