package com.terran4j.commons.reflux.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terran4j.commons.reflux.Message;
import com.terran4j.commons.reflux.OnMessage;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

@Service
public class MessageHandler implements BeanPostProcessor {
	
	private static final JsonParser jsonParser = new JsonParser();

	private static final Gson gson = new Gson();

	private static final Map<String, OnMessageInvoker> invokers = new ConcurrentHashMap<>();

	private static class OnMessageInvoker {

		public Method method;

		public Object bean;

		public Class<?> paramType;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> targetClass = Classes.getTargetClass(bean);

		Method[] methods = Classes.getMethods(OnMessage.class, targetClass);
		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				OnMessage onMessage = method.getAnnotation(OnMessage.class);

				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes == null || paramTypes.length != 1) {
					throw new BeanDefinitionValidationException("@OnMessage修饰的方法，必须有且仅有一个参数");
				}
				Class<?> paramType = paramTypes[0];

				String msgType = onMessage.type();
				if (StringUtils.isEmpty(msgType)) {
					msgType = paramType.getName();
				}

				Class<?> returnType = method.getReturnType();
				if (returnType != null && !("void".equals(returnType.getName()) || String.class.equals(returnType))) {
					throw new BeanDefinitionValidationException("@OnMessage修饰的方法，返回类型只能是 String 或 void 。");
				}

				String key = msgType;
				OnMessageInvoker invoker = new OnMessageInvoker();
				invoker.method = method;
				invoker.bean = bean;
				invoker.paramType = paramType;
				invokers.put(key, invoker);
			}
		}
		return bean;
	}

	public String onMessage(String message) throws BusinessException {
		JsonElement element = jsonParser.parse(message);
		JsonObject json = element.getAsJsonObject();
		
		Message result = new Message();
		
		String msgId = json.get("id").getAsString();
		result.setId(msgId);
		
		String msgType = json.get("type").getAsString();
		result.setType(msgType);
		
		OnMessageInvoker invoker = invokers.get(msgType);
		if (invoker == null) {
			throw new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND) //
					.put("msgType", msgType);
		}
		
		String msgContent = json.get("content").toString();
		Object param = gson.fromJson(msgContent, invoker.paramType);
		
		Object reply = null;
		try {
			reply = invoker.method.invoke(invoker.bean, param);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e) //
					.put("msgType", msgType).put("msgContent", msgContent);
		}
		if (reply == null) {
			return null;
		}
		
		String content = reply.toString();
		result.setContent(content);
		result.setStatus(0);

		return gson.toJson(result);
	}

}
