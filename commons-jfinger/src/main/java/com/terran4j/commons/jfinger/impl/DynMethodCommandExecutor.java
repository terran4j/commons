package com.terran4j.commons.jfinger.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.terran4j.commons.jfinger.CommandExecutor;
import com.terran4j.commons.jfinger.CommandInterpreter;
import com.terran4j.commons.util.Strings;

public class DynMethodCommandExecutor implements CommandExecutor {

	private final Object bean;

	private final Method method;

	private final String beanName;

	public DynMethodCommandExecutor(Object bean, Method method, String beanName) {
		super();
		this.bean = bean;
		this.method = method;
		this.beanName = beanName;
	}

	@Override
	public void execute(CommandInterpreter ci) {
		Object[] args = { ci };
		try {
			method.invoke(bean, args);
		} catch (IllegalAccessException e) {
			String msg = String.format("无法访问Spring Bean【%s】的方法【%s】!", beanName, method.getName());
			ci.println(msg);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			String errorMessage = Strings.getString(e);
			ci.println(errorMessage);
		}
	}

}
