package com.terran4j.commons.jfinger.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.terran4j.commons.jfinger.CommandDefine;
import com.terran4j.commons.jfinger.CommandErrorCode;
import com.terran4j.commons.jfinger.CommandException;
import com.terran4j.commons.jfinger.CommandExecutor;
import com.terran4j.commons.jfinger.CommandGroupDefine;
import com.terran4j.commons.jfinger.CommandGroups;
import com.terran4j.commons.jfinger.CommandInterpreter;
import com.terran4j.commons.jfinger.CommandOptionDefine;
import com.terran4j.commons.jfinger.OptionType;
import com.terran4j.commons.util.error.BusinessException;

@Service
public class CommandLineService {

	private static final Logger log = LoggerFactory.getLogger(CommandLineService.class);

	private static final Util util = new Util();

	private static final CommandLineParser parser = new DefaultParser();
	
	private String prompt = CommandLineApplicationListener.PROMPT_DEFAULT;
	
	private CommandGroups commands = new CommandGroups();

	public CommandLineService() {
		super();
	}

	public CommandGroups getCommands() {
		return commands;
	}

	public void setCommands(CommandGroups commands) {
		this.commands = commands;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public final boolean execute(String command, PrintStream out) {
		if (StringUtils.isEmpty(command)) {
			return true;
		}
		if (log.isInfoEnabled()) {
			log.info("command: {}", command);
		}

		command = command.trim();
		if ("quit".equals(command)) {
			return false;
		}

		// Print All Commands Help
		if (command.startsWith("help ") || command.equals("help")) {
			String[] helpArgs = util.split(command, " ");
			if (helpArgs.length > 2) {
				out.println(getHelp(helpArgs[1], helpArgs[2]));
				return true;
			}
			if (helpArgs.length > 1) {
				out.println(getHelp(helpArgs[1]));
				return true;
			}
			if (helpArgs.length > 0) {
				out.println(getHelp());
				return true;
			}
		}

		try {
			String[] args = parseCommand(command);
			if (args.length < 2) {
				out.println(getHelpPrompt());
				out.println("命令格式不正确，至少要输入：  [命令组名称] [命令名称] ，请用上面的 help 命令查询其详细用法");
				return true;
			}

			String group = args[0];
			CommandGroupDefine commandGroup = commands.get(group);
			if (commandGroup == null) {
				out.println(getHelpPrompt());
				out.println("不存在此命令组： " + group + "，请用上面的 help 命令查询其详细用法。\n");
				return true;
			}
			if (commandGroup.size() == 0) {
				out.println("命令组【 " + group + "】中没有任何命令！\n");
				return true;
			}

			String commandName = args[1];
			CommandDefine commandConfig = commandGroup.getCommand(commandName);
			if (commandConfig == null) {
				out.println("在命令组【" + group + "】中，不存在此命令： " + commandName);
				out.println(getHelp(group));
				return true;
			}

			String[] commandArgs = Arrays.copyOfRange(args, 2, args.length);
			execute(group, commandName, commandConfig, commandArgs, out);

		} catch (CommandException ce) {
			String msg = ce.getMessage();
			out.println(msg);
		} catch (Throwable e) {
			printHelpPrompt(e, out);
		}
		return true;
	}

	private void execute(String groupName, String commandName, CommandDefine command, String[] args, PrintStream out)
			throws BusinessException {

		Options options = new Options();
		List<CommandOptionDefine> optionConfigs = command.getOptions();
		if (optionConfigs != null && optionConfigs.size() > 0) {
			for (CommandOptionDefine optionConfig : optionConfigs) {
				String key = optionConfig.getKey();
				String name = optionConfig.getName();
				String desc = optionConfig.getDesc();

				OptionType type = optionConfig.getType();
				if (type == OptionType.BOOLEAN) {
					options.addOption(key, name, false, desc);
				} else if (type == OptionType.PROPERTIES) {
					Option option = Option.builder(key).argName("property=value") //
							.numberOfArgs(5).valueSeparator('=').desc(desc) //
							.build();
					options.addOption(option);
				} else {
					Builder builder = Option.builder(key).hasArg();
					if (!StringUtils.isEmpty(name)) {
						builder = builder.longOpt(name);
					}
					Option option = builder.desc(desc).build();
					options.addOption(option);
				}
			}
		}

		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
		} catch (UnrecognizedOptionException e) {
			String optionKey = e.getOption();
			out.println("无效的命令行选项： " + optionKey);
			out.println(getHelp(groupName, commandName));
			return;
		} catch (ParseException e) {
			out.println("解析命令出错：" + e.getMessage());
			out.println(getHelp(groupName, commandName));
			return;
		}

		CommandExecutor executor = command.getExecutor();
		CommandInterpreter ci = new CommandInterpreterImpl(out, commandLine, command);

		if (optionConfigs != null && optionConfigs.size() > 0) {
			for (CommandOptionDefine optionConfig : optionConfigs) {
				if (optionConfig.isRequired() &&
						optionConfig.getType() != OptionType.BOOLEAN) {
					String key = optionConfig.getKey();
					String name = optionConfig.getName();
					String value = ci.getOption(key);
					if (value == null) {
						throw new CommandException(CommandErrorCode.OPTION_KEY_EMPTY.getName()) //
								.put("group", groupName) // 
								.put("commandName", commandName) // 
								.put("optionKey", key) // 
								.put("optionName", name) // 
								.setMessage("命令 [${group} ${commandName}] 需要 ${optionName} 选项，"
										+ "请在命令后面附上 -${optionKey} [${optionName}]");
					}
				}
			}
		}

		executor.execute(ci);
	}

	private void printHelpPrompt(Throwable e, PrintStream out) {
		e.printStackTrace(out);
		out.println("执行命令出错: " + e.getMessage());
		out.println(getHelpPrompt());
	}

	/**
	 * @return
	 */
	public String getHelpPrompt() {
		StringBuilder sb = new StringBuilder();
		sb.append("请用 help 查询命令的详细用法，如：\n");
		sb.append("输入：\n");
		sb.append("    help\n");
		sb.append("查看所有的命令组。\n\n");

		sb.append("输入：\n");
		sb.append("    help [groupName]\n");
		sb.append("查看指定命令组的详细信息，如：\n");
		sb.append("    help system\n\n");

		sb.append("输入：\n");
		sb.append("    help [groupName] [commandName]\n");
		sb.append("查看指定命令的详细信息，如：\n");
		sb.append("    help system prop\n\n");

		return sb.toString();
	}

	public String getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("欢迎使用由 terran4j 提供的命令行服务。\n\n");
		sb.append("当前程序有以下命令可供使用：\n");
		Iterator<String> it = commands.keySet().iterator();
		while (it.hasNext()) {
			String groupName = it.next();
			if (StringUtils.isEmpty(groupName)) {
				continue;
			}
			CommandGroupDefine group = commands.get(groupName);
			if (group == null || group.size() == 0) {
				continue;
			}

			sb.append("命令：  ").append(groupName).append(" [");
			List<CommandDefine> commands = group.getCommands();
			boolean notFirst = false;
			for (int i = 0; i < commands.size(); i++) {
				CommandDefine command = commands.get(i);
				String name = command.getName();
				if (notFirst) {
					sb.append(" | ");
				} else {
					notFirst = true;
				}
				sb.append(name);
			}
			sb.append("]\n");
			sb.append("说明：  ").append(group.getDesc()).append("\n\n");
		}

		sb.append("\n").append(getHelpPrompt());

		return sb.toString();
	}

	public String getHelp(String groupName) {
		if (StringUtils.isEmpty(groupName)) {
			return getHelp();
		}
		groupName = groupName.trim();

		CommandGroupDefine group = commands.get(groupName);
		if (group == null) {
			return groupName + " not found, all command group list:\n" + getHelp();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("命令组：  ").append(groupName).append("\n");
		sb.append("说明：  ").append(group.getDesc()).append("\n");

		sb.append("命令列表：\n");
		List<CommandDefine> commandList = group.getCommands();
		for (int i = 0; i < commandList.size(); i++) {
			CommandDefine command = commandList.get(i);
			String name = command.getName();
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			// 描述中有多行时，每行前加 8 个空格。
			String desc = command.getDesc();
			desc = desc.replaceAll("\n", "\n        ");

			sb.append("    ").append(name).append(": ").append(desc).append("\n");
		}

		return sb.toString();
	}

	public String getHelp(String groupName, String name) {
		if (StringUtils.isEmpty(groupName)) {
			return getHelp();
		}
		groupName = groupName.trim();

		CommandGroupDefine group = commands.get(groupName);
		if (group == null) {
			return groupName + " not found, all command group list:\n" + getHelp();
		}

		if (StringUtils.isEmpty(name)) {
			return getHelp(groupName);
		}
		name = name.trim();

		CommandDefine command = group.getCommand(name);
		if (command == null) {
			return name + " not found in group, the commands list in the group:\n" + getHelp(groupName);
		}
		return command.getHelp();
	}

	public void printPrompt(PrintStream out) {
		out.print("\n" + getPrompt() + ">");
	}

	public static final int[] nextIndex(String command, int fromIndex) throws BusinessException {

		int currentIndex = fromIndex;
		int lastIndex = command.length() - 1;
		for (; currentIndex <= lastIndex; currentIndex++) {
			char currentChar = command.charAt(currentIndex);
			if (currentChar != ' ') {
				break;
			}
		}
		if (currentIndex >= lastIndex) {
			return null;
		}
		int startIndex = currentIndex;

		int endIndex = -1;
		char firstChar = command.charAt(startIndex);
		if (firstChar == '"') { // 以""包裹的参数。
			currentIndex = startIndex + 1;
			int nextIndex = command.indexOf("\"", currentIndex);
			while (nextIndex > currentIndex && nextIndex <= lastIndex) {
				char prevChar = command.charAt(nextIndex - 1);
				if (prevChar != '\\') {
					endIndex = nextIndex;
					break;
				}
				currentIndex = nextIndex + 1;
				nextIndex = command.indexOf("\"", currentIndex);
			}
			if (endIndex == -1) {
				String errorPart = command.substring(startIndex);
				if (errorPart.length() > 10) {
					errorPart = errorPart.substring(0, 10);
				}
				throw new CommandException(CommandErrorCode.ARG_QUOTE_NOT_CLOSE.getName())
						.put("startIndex", startIndex)
						.put("errorPart", errorPart)
						.put("command", command)
						.setMessage("命令中第${startIndex}个字符\"号没有另一个\"作为结束符: ${errorPart}");
			}

		} else { // 没有以""包裹的参数。
			endIndex = command.indexOf(" ", startIndex);
		}
		if (endIndex <= startIndex) {
			return null;
		}
		return new int[] { startIndex, endIndex };
	}

	public static final String[] parseCommand(String command) throws BusinessException {
		List<String> args = new ArrayList<String>();
		int fromIndex = 0;
		int lastIndex = command.length() - 1;
		while (fromIndex <= lastIndex) {
			String arg = null;
			int[] pos = nextIndex(command, fromIndex);
			if (pos == null) {
				arg = command.substring(fromIndex);
				fromIndex = lastIndex + 1;
			} else {
				arg = command.substring(pos[0], pos[1] + 1);
				fromIndex = pos[1] + 1;
			}
			if (StringUtils.isEmpty(arg)) {
				continue;
			}
			arg = arg.trim();
			if (arg.startsWith("\"") && arg.endsWith("\"")) {
				if (arg.length() <= 2) {
					arg = "";
				} else {
					arg = arg.substring(1, arg.length() - 1);
				}
			}
			args.add(arg);
		}
		return args.toArray(new String[args.size()]);
	}

}
