package com.terran4j.commons.jfinger.builtin;

import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.terran4j.commons.jfinger.Command;
import com.terran4j.commons.jfinger.CommandGroup;
import com.terran4j.commons.jfinger.CommandInterpreter;
import com.terran4j.commons.jfinger.CommandOption;
import com.terran4j.commons.jfinger.OptionType;

@CommandGroup(options = {}, desc = "系统命令，读取或写入本程序中的系统变量或环境变量等。")
public class SystemCommand {

	private static final Logger log = LoggerFactory.getLogger(SystemCommand.class);

	@Command(desc = "改写系统变量值，如： system setProps -Dk1=123 -Dk2=456", options = {
			@CommandOption(key = "D", type = OptionType.PROPERTIES) })
	public void setProps(CommandInterpreter ci) {
		Properties props = ci.getOption("D", new Properties());
		System.setProperties(props);
		if (log.isInfoEnabled()) {
			log.info("setProps done: {}", props);
		}
	}

	@Command(desc = "显示或改写系统系统变量的值，如：\n" //
			+ "system prop        显示所有的系统变量的值。\n" //
			+ "system prop -k abc -v AAA        将 abc 的值改为 123。\n" //
			+ "system prop -k abc        显示此系统变量的值。", //
			options = { //
					@CommandOption(key = "k", name = "key", //
							desc = "系统变量的键，不指定则显示所有的系统变量的值。"), //
					@CommandOption(key = "v", name = "value", //
							desc = "系统变量的值，不指定则表示显示此变量值，指定则表示改写此变量值。") })
	public void prop(CommandInterpreter ci) {
		if (log.isDebugEnabled()) {
			log.debug("system prop");
		}

		String key = ci.getOption("k");
		if (log.isDebugEnabled()) {
			log.debug("key = {}", key);
		}

		// 不指定key，则打印所有的系统变量
		if (StringUtils.isEmpty(key)) {
			if (log.isDebugEnabled()) {
				log.debug("key is empty, will print all properties.");
			}
			Iterator<Object> it = System.getProperties().keySet().iterator();
			ci.println("all System Properties:");
			while (it.hasNext()) {
				Object propKey = it.next();
				if (propKey instanceof String) {
					String propKeyStr = (String) propKey;
					Object propValue = System.getProperty(propKeyStr);
					ci.println(propKeyStr + " = " + propValue);
				}
			}
			return;
		}

		String value = ci.getOption("v");
		if (StringUtils.isEmpty(value)) {
			String propValue = System.getProperty(key);
			ci.println(key + " = " + propValue);
		} else {
			String propValueOld = System.getProperty(key);
			ci.println(key + " = " + propValueOld);
			System.setProperty(key, value);
			String propValueNew = System.getProperty(key);
			ci.println("System setProperty done.");
			ci.println(key + " = " + propValueNew);
		}
	}

	@Command(desc = "读取或写入环境变量的值", options = { //
			@CommandOption(key = "k", name = "key", desc = "环境变量的键，不指定则打印所有的环境变量") //
	})
	public void env(CommandInterpreter ci) {
		String key = ci.getOption("k");
		if (log.isDebugEnabled()) {
			log.debug("key = {}", key);
		}

		// 不指定key，则打印所有的环境变量
		if (StringUtils.isEmpty(key)) {
			if (log.isDebugEnabled()) {
				log.debug("key is empty, will print all env vars.");
			}
			Iterator<String> it = System.getenv().keySet().iterator();
			ci.println("all Env Vars:");
			while (it.hasNext()) {
				String envKey = it.next();
				Object envValue = System.getenv(envKey);
				ci.println(envKey + " = " + envValue);
			}
			return;
		}

		String envValue = System.getenv(key);
		ci.println(key + " = " + envValue);
	}

}
