# big-test-module
Jahia 7 Test Module

## Injecting JCRTemplate through spring @Autowired

JCRTemplate is just another spring bean that can be injected using normal IoC mechanics.

```
@Autowired
private JCRTemplate jcrTemplate;
```

## Adding a module customization page

Each module can also have a template set deployed with it. If we create a page template and apply it to `jnt:virtualsite` we can then select a JSP that represents it's rendering
