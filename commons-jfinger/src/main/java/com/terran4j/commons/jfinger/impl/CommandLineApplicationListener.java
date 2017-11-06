package com.terran4j.commons.jfinger.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.terran4j.commons.jfinger.Command;
import com.terran4j.commons.jfinger.CommandDefine;
import com.terran4j.commons.jfinger.CommandGroup;
import com.terran4j.commons.jfinger.CommandGroupDefine;
import com.terran4j.commons.jfinger.CommandGroups;
import com.terran4j.commons.jfinger.CommandOption;
import com.terran4j.commons.jfinger.CommandOptionDefine;

@Service
public class CommandLineApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
	
	public static final String PROP_KEY_DISABLE = "terran4j.jfinger.disable";
	
	public static final String PROP_KEY_PROMPT = "terran4j.jfinger.prompt";
	
	public static final String PROMPT_DEFAULT = "jfinger";
	
	private static final Logger log = LoggerFactory.getLogger(CommandLineApplicationListener.class);

	private static String PROMPT = PROP_KEY_PROMPT;
	
	public static final String getPrompt() {
		return PROMPT;
	}
	
	private CommandLineService service = null;
	
	private Thread thread = null;
	
	

	public CommandLineApplicationListener() {
		super();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		ApplicationContext app = event.getApplicationContext();
		
		// 从配置上判断用户是否禁用了命令行服务。
		String cliDisable = app.getEnvironment().getProperty(PROP_KEY_DISABLE);
		if (cliDisable != null && cliDisable.trim().equalsIgnoreCase("true")) {
			if (log.isWarnEnabled()) {
				log.warn("Command Line Service was disable, will not be started.");
			}
			return;
		}
		
		// 如果没有任何命令被注册，也不需要启用命令行服务。
		Map<String, Object> beans = app.getBeansWithAnnotation(CommandGroup.class);
		if (beans == null || beans.size() == 0) {
			if (log.isWarnEnabled()) {
				log.warn("No Bean has Annotation: @{}. Command Line Service will not be started.",//
						CommandGroup.class.getSimpleName());
			}
			return;
		}
		
		// 获取提示符。
		String cliPrompt = app.getEnvironment().getProperty(PROP_KEY_PROMPT);
		if (!StringUtils.isEmpty(cliPrompt)) {
			cliPrompt = cliPrompt.trim();
		} else {
			cliPrompt = PROMPT_DEFAULT;
		}
		PROMPT = cliPrompt;

		// 解析所有的命令行。
		CommandGroups groups = new CommandGroups();
		Iterator<String> it = beans.keySet().iterator();
		while (it.hasNext()) {
			String beanName = it.next();
			Object bean = app.getBean(beanName);
			Class<?> beanClass = bean.getClass();
			List<Method> commandMethods = getCommandMethod(beanClass);
			if (commandMethods.size() == 0) {
				continue;
			}
			
			// 解析命令组。
			CommandGroup commandGroup = beanClass.getAnnotation(CommandGroup.class);
			CommandGroupDefine commandGroupDefine = new CommandGroupDefine(commandGroup);
			if (StringUtils.isEmpty(commandGroup.name())) {
				String groupName = beanName;
				if (groupName.endsWith("Command") && groupName.length() > "Command".length()) {
					groupName = groupName.substring(0, groupName.length() - "Command".length());
				}
				commandGroupDefine.setName(groupName);
			}
			
			// 解析通用命令选项。
			List<CommandOptionDefine> commonOptions = new ArrayList<CommandOptionDefine>();
			CommandOption[] commandGroupOptions = commandGroup.options();
			if (commandGroupOptions != null && commandGroupOptions.length > 0) {
				for (CommandOption commandOption : commandGroupOptions) {
					CommandOptionDefine commonOption = new CommandOptionDefine(commandOption);
					commonOptions.add(commonOption);
				}
			}
			
			// 解析本组命令。
			for (Method commandMethod : commandMethods) {
				CommandDefine commandDefine = new CommandDefine(commandGroupDefine, bean, commandMethod, beanName);
				commandDefine.addOptions(commonOptions);
				commandGroupDefine.addCommand(commandDefine);
			}
			
			// 记录下本命令组。
			groups.addCommandGroup(commandGroupDefine);
		}
		
		service = new CommandLineService();
		service.setCommands(groups);
		service.setPrompt(cliPrompt);
		
		thread = new CommandLineTask(service);
		thread.setDaemon(false); // 线程一直存在，不让主进程退出。
		thread.setName("JFinger Demo Thread");
		thread.start();
		
		if (log.isInfoEnabled()) {
			log.info("Command Line Service is started.");
		}
	}
	
	private List<Method> getCommandMethod(Class<?> beanClass) {
		List<Method> commandMethods = new ArrayList<Method>();
		
		Method[] methods = beanClass.getMethods();
		if (methods == null || methods.length == 0) {
			return commandMethods;
		}
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Command command = method.getAnnotation(Command.class);
			if (command == null) {
				continue;
			}
			commandMethods.add(method);
		}
		
		return commandMethods;
	}

}
