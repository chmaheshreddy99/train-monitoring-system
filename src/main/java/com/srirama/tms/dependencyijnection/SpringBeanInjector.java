package com.srirama.tms.dependencyijnection;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.srirama.tms.config.ApplicationConfig;

public class SpringBeanInjector {

	private static final ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

	public static void inject(Object target) {
		Class<?> clazz = target.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Autowired.class)) {
				Class<?> fieldType = field.getType();
				Object bean = context.getBean(fieldType);
				try {
					field.setAccessible(true);
					field.set(target, bean);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Failed to inject Spring Bean into field: " + field.getName(), e);
				}
			}
		}
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}
}
