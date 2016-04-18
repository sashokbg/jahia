#big-test-provider

Jahia allows to create custom providers. One such example is the UserProvider

The provider will appear in the main menu of Jahia. In order to make this work, the module should be declared as a **system**.

## Pom.xml config

In the pom.xml:

```
<properties>
        <jahia-module-type>system</jahia-module-type>
        <jahia-depends>default, ...</jahia-depends>
</properties>
```

#OSGI Service

In order to inject osgi services we need to declare them in the spring context:

```
<osgi:reference id="configurationAdmin"
		interface="org.osgi.service.cm.ConfigurationAdmin" />
<osgi:reference id="ExternalUserGroupService"
	interface="org.jahia.modules.external.users.ExternalUserGroupService" />
```
