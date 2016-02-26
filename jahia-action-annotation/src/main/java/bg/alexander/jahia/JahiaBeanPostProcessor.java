package bg.alexander.jahia;

import org.apache.commons.lang.StringUtils;
import org.jahia.bin.Action;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class JahiaBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean.getClass().getAnnotation(JahiaAction.class) != null) {
			String name = bean.getClass().getAnnotation(JahiaAction.class).name();
			if (StringUtils.isEmpty(name)) {
				name = beanName;
			}
			boolean requireAuthenticatedUser = bean.getClass().getAnnotation(JahiaAction.class)
					.requireAuthenticatedUser();
			Action jahiaActionBean = (Action) bean;
			jahiaActionBean.setName(name);
			jahiaActionBean.setRequireAuthenticatedUser(requireAuthenticatedUser);
		}

		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
