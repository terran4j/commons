package com.terran4j.commons.httpinvoker;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.gson.JsonObject;
import com.terran4j.commons.jfinger.Command;
import com.terran4j.commons.jfinger.CommandGroup;
import com.terran4j.commons.jfinger.CommandInterpreter;
import com.terran4j.commons.jfinger.CommandOption;
import com.terran4j.commons.jfinger.OptionType;
import com.terran4j.commons.util.Jsons;
import com.terran4j.commons.util.error.BusinessException;

/**
 * 定义一个命令组。
 * 
 * @author wei.jiang
 */
@CommandGroup(desc = "Http命令工具。")
public class HttpCommand implements ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(HttpCommand.class);
	
	private ApplicationContext context = null;

	private HttpClient httpClient = null;
	
	private Session session = null;

	public HttpCommand() {
		super();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
		httpClient = HttpClient.create(context);
	}
	
	public ApplicationContext getContext() {
		return context;
	}
	
	@Command(desc = "加载Http API的配置", options = { 
			@CommandOption(key = "f", name = "file", desc = "配置文件路径") })
	public void load(CommandInterpreter ci) {
		String filePath = ci.getOption("f");
		File file = new File(filePath);
		if (!file.exists()) {
			ci.println("Http API配置文件不存在: %s", filePath);
			return;
		}
		if (file.isDirectory()) {
			ci.println("Http API配置文件需要是一个json内容的文本文件，但 %s 是文件夹。", filePath);
			return;
		}
		httpClient = HttpClient.create(context, file);
		session = null;
		ci.println("加载Http API的配置成功。");
	}
	
	@Command(desc = "执行一个登录请求，并保留当前会话", options = { 
			@CommandOption(key = "a", name = "action", desc = "HTTP指令的id"),
			@CommandOption(key = "P", desc = "动态属性", type = OptionType.PROPERTIES) })
	public void login(CommandInterpreter ci) {
		HttpClient client = getHttpClient();
		if (client == null) {
			ci.println("未加载Http API配置，请用\n\thttp load -f 文件名\n加载配置文件。");
			return;
		}
		
		String action = ci.getOption("a");
		Properties props = ci.getOption("P", new Properties());
		if (log.isInfoEnabled()) {
			log.info("action: {},  params: {}", action, props);
		}
		
		try {
			Session session = getHttpClient().create();
			session.action(action).params(props).exe();
			this.session = session;
		} catch (HttpException e) {
			e.printStackTrace();
			ci.println("error: " + e.getMessage());
		}
	}

	@Command(desc = "执行一条HTTP指令", options = { @CommandOption(key = "a", name = "action", desc = "HTTP指令的id"),
			@CommandOption(key = "P", desc = "动态属性", type = OptionType.PROPERTIES),
			@CommandOption(key = "c", name = "count", desc = "执行次数") })
	public void exe(CommandInterpreter ci) throws BusinessException {
		HttpClient client = getHttpClient();
		if (client == null) {
			ci.println("未加载Http API配置，请用\n\thttp load -f 文件名\n加载配置文件。");
			return;
		}
		if (session == null) {
			session = client.create();
		}
		
		String action = ci.getOption("a");
		int count = ci.getOption("c", 1);
		Properties props = ci.getOption("P", new Properties());
		if (log.isInfoEnabled()) {
			log.info("action: {},  params: {}", action, props);
		}
		
		if (count > 1) {
			try {
				session.action(action).params(props).exe(count, 1, 0);
			} catch (HttpException e) {
				e.printStackTrace();
				ci.println("error: " + e.getMessage());
			}
		} else {
			try {
				Response response = session.action(action).params(props).exe();
				JsonObject json = response.getJsonRoot();
				String jsonText = json.toString();
				jsonText = Jsons.format(jsonText);
				ci.println(jsonText);
			} catch (HttpException e) {
				e.printStackTrace();
				ci.println("error: " + e.getMessage());
			}
		}
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
}