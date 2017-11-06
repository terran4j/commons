package com.terran4j.commons.jfinger.builtin;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.terran4j.commons.jfinger.Command;
import com.terran4j.commons.jfinger.CommandGroup;
import com.terran4j.commons.jfinger.CommandInterpreter;
import com.terran4j.commons.jfinger.CommandOption;
import com.terran4j.commons.jfinger.OptionType;

@CommandGroup(options = {}, desc = "Spring 相关命令，如查看 Spring 中的配置属性等。")
public class SpringCommand implements ApplicationContextAware {

	private ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	@Command( //
			desc = "查看spring中配置属性的值，如：\n" //
					+ "spring prop -k \"abc\"        查看 Spring 容器中 key 为 abc 的属性值", //
			options = { //
					@CommandOption(key = "k", name = "key", required = true, desc = "属性的键") //
			})
	public void prop(CommandInterpreter ci) {
		String propKey = ci.getOption("k");
		String propValue = context.getEnvironment().getProperty(propKey);
		ci.println(propValue);
	}

	@Command( //
			desc = "查看spring中的 profile，如：\n" //
					+ "spring profile -a        查看 Spring 容器中活跃的 profile 。", //
			options = { //
					@CommandOption(key = "a", name = "onlyActive", type = OptionType.BOOLEAN, //
							desc = "是否只看活跃的 profile") //
			})
	public void profile(CommandInterpreter ci) {
		boolean onlyActive = ci.hasOption("a");
		String[] profiles = null;
		if (onlyActive) {
			profiles = context.getEnvironment().getActiveProfiles();
		} else {
			profiles = context.getEnvironment().getDefaultProfiles();
		}
		if (profiles == null) {
			ci.println("null");
		} if (profiles.length == 0) {
			ci.println("[]");
		} else {
			for (String profile : profiles) {
				ci.println(profile);
			}
		}
	}
	
	@Command( //
			desc = "查看 Spring 容器中的 Bean 对象，如：\n" //
					+ "spring showBean        列出所有的 Bean 的名称。\n"
					+ "spring showBean -n \"Command\"        列出指定名称的 Bean 信息。", //
			options = { //
					@CommandOption(key = "n", name = "name", desc = "根据 Bean 名称精确搜索"), //
//					@CommandOption(key = "p", name = "pattern", desc = "正则表达式，根据 Bean 的名称模糊搜索。") //
			})
	public void showBean(CommandInterpreter ci) {
		String[] beanNames = context.getBeanDefinitionNames();
		
		String name = ci.getOption("n");
		if (StringUtils.hasText(name)) {
			name = name.trim();
			for (String beanName : beanNames) {
				if (beanName.contains(name.subSequence(0, name.length()))) {
					ci.println(beanName);	
				}
			}
			return;
		}

//		String patternText = ci.getOption("p");
//		if (StringUtils.hasText(patternText)) {
//			patternText = patternText.trim();
//			Pattern pattern = Pattern.compile(patternText);
//			for (String beanName : beanNames) {
//				if (pattern.matches(arg0, arg1)) {
//					ci.println(beanName);
//				}
//			}
//			return;
//		}
		
		for (String beanName : beanNames) {
			ci.println(beanName);
		}
	}

	// @Command(desc = "查看spring中配置属性的值", options = { //
	// @CommandOption(key = "k", name = "keyWord", desc = "属性的键") //
	// })
	// public void searchBean(CommandInterpreter ci) {
	// String keyWord = ci.getOption("k");
	// String[] beanNames = context.getBeanDefinitionNames();
	// }

}
