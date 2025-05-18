package com.srirama.tms.ui.components.factory;

import com.srirama.tms.dependencyijnection.SpringBeanInjector;

public class ComponentFactory {

	/**
	 * Creates an instance using a no-argument constructor, and injects dependencies which are annotated with @Autowired.
	 *
	 * @param clazz Class to instantiate
	 * @param <T>   Type of the class
	 * @return Instance of the class
	 */
	public static <T> T createInstance(Class<T> clazz) {
		try {
			T instance = clazz.getDeclaredConstructor().newInstance();
			SpringBeanInjector.inject(instance);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
		}
	}

	/**
	 * Creates an instance using a parameterized constructor, and injects dependencies which are annotated with @Autowired.
	 *
	 * @param clazz    Class to instantiate
	 * @param argTypes Array of constructor parameter types
	 * @param args     Array of constructor arguments
	 * @param <T>      Type of the class
	 * @return Instance of the class
	 */
	public static <T> T createInstance(Class<T> clazz, Class<?>[] argTypes, Object ...args) {
		try {
			T instance = clazz.getDeclaredConstructor(argTypes).newInstance(args);
			SpringBeanInjector.inject(instance);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate " + clazz.getName() + " with arguments", e);
		}
	}
}
