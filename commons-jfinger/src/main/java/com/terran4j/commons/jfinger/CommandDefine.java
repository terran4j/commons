package com.terran4j.commons.jfinger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.terran4j.commons.jfinger.impl.DynMethodCommandExecutor;

/**
 * 
 * @author wei.jiang
 */
public class CommandDefine {
	
	private final Method method;
	
	private final CommandGroupDefine group;
	
	private final Command command;
	
	private final CommandExecutor executor;
	
	private final String beanName;
	
	private String name;
	
	private String desc;
	
	private final List<CommandOptionDefine> options = new ArrayList<CommandOptionDefine>();

	/**
	 * 
	 * @param method
	 */
	public CommandDefine(CommandGroupDefine group, Object bean, Method method, String beanName) {
		super();
		this.method = method;
		this.group = group;
		this.beanName = beanName;
		this.command = this.method.getAnnotation(Command.class);
		this.executor = new DynMethodCommandExecutor(bean, method, this.beanName);
		
		String name = command.name();
		if (StringUtils.isEmpty(name)) {
			name = method.getName();
			setName(name);
		}
		
		setDesc(command.desc());
		
		CommandOption[] commandOptions = command.options();
		if (commandOptions != null) {
			for (CommandOption commandOption : commandOptions) {
				CommandOptionDefine option = new CommandOptionDefine(commandOption);
				addOption(option);
			}
		}
	}
	
	public void addOption(CommandOptionDefine option) {
		this.options.add(option);
	}
	
	public void addOptions(CommandOptionDefine[] options) {
		this.options.addAll(Arrays.asList(options));
	}
	
	public void addOptions(List<CommandOptionDefine> options) {
		this.options.addAll(options);
	}
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the executor
	 */
	public final CommandExecutor getExecutor() {
		return executor;
	}

	/**
	 * @return the options
	 */
	public final List<CommandOptionDefine> getOptions() {
		return options;
	}
	
	public final CommandOptionDefine getOption(String key) {
		for (CommandOptionDefine option : options) {
			if (option.getKey().equals(key)) {
				return option;
			}
		}
		return null;
	}

	/**
	 * @return the CommandGroupDefine
	 */
	public final CommandGroupDefine getGroup() {
		return group;
	}
	
	public String getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("命令：  ").append(group.getName()).append(" ").append(name).append(" [选项]");
		
		if (options.size() > 0) {
			sb.append("\n选项列表:\n");
			
			// 找出选项占位最长值。
			int max = -1;
			int[] optSizes = new int[options.size()];
			for (int i = 0; i < optSizes.length; i++) {
				CommandOptionDefine option = options.get(i);
				optSizes[i] = option.getKey().length();
				
				String name = option.getName();
				if (!StringUtils.isEmpty(name)) {
					optSizes[i] += (4 + name.length());
				}
				
				OptionType type = option.getType();
				if (type != OptionType.BOOLEAN) {
					optSizes[i] += (3 + type.getName().length());
				}
				
				if (max < optSizes[i]) {
					max = optSizes[i];
				}
			}
			
			for (int i = 0; i < optSizes.length; i++) {
				CommandOptionDefine option = options.get(i);
				
				String key = option.getKey();
				sb.append("    ").append("-").append(key);
				
				String name = option.getName();
				if (!StringUtils.isEmpty(name)) {
					sb.append(", --").append(name);
				}
				
				OptionType type = option.getType();
				if (type != OptionType.BOOLEAN) {
					sb.append(" <").append(type.getName()).append(">");
				}
				
				String desc = option.getDesc();
				if (!StringUtils.isEmpty(desc)) {
					
					// 补齐不同选项中的空间。
					int left = max - optSizes[i] + 4;
					for (int j = 0; j < left; j++) {
						sb.append(" ");
					}
					sb.append(desc);
				}
				
				sb.append("\n");
			}
		}
		
		String commandDesc = getDesc();
		if (StringUtils.hasText(commandDesc)) {
			commandDesc = commandDesc.replaceAll("\n", "\n    ");
			sb.append("\n说明：  ").append(commandDesc);
		}
		
		return sb.toString();
	}

	/**
	 * @return the desc
	 */
	public final String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public final void setDesc(String desc) {
		this.desc = desc;
	}
	
}
