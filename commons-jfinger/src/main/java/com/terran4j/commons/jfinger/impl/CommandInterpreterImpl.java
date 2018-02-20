package com.terran4j.commons.jfinger.impl;

import com.terran4j.commons.jfinger.*;
import com.terran4j.commons.util.error.BusinessException;
import org.apache.commons.cli.CommandLine;
import org.springframework.util.StringUtils;

import java.io.PrintStream;
import java.util.Properties;

public class CommandInterpreterImpl implements CommandInterpreter {

    private final PrintStream out;

    private final CommandLine commandLine;

    private final CommandDefine command;

    public CommandInterpreterImpl(PrintStream out, CommandLine commandLine, CommandDefine command) {
        super();
        this.out = out;
        this.commandLine = commandLine;
        this.command = command;
    }

    public void print(String msg) {
        out.print(msg);
    }

    public void println(String msg) {
        out.println(msg);
    }

    public void println(String msg, Object... args) {
        msg = msg.replaceAll("\\{\\}", "%s");
        String str = String.format(msg, args);
        println(str);
    }

    public void println() {
        out.println();
    }

    public boolean hasOption(String key) {
        return commandLine.hasOption(key);
    }

    public int getOption(String key, int defaultValue) throws BusinessException {

        CommandOptionDefine option = command.getOption(key);
        String name = option.getName();

        String value = commandLine.getOptionValue(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        value = value.trim();

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new CommandException(
                    CommandErrorCode.ARG_PARSE_TO_INT_ERROR.getName(), e)
                    .put(key, key).put(name, name).put("value", value)
                    .setMessage("解析选项值出错，选项 -${key} 或 --${name} 的值不是数字类型： {value}");
        }
    }

    public String getOption(String key) {
        return commandLine.getOptionValue(key);
    }

    public String getOption(String key, String defaultValue) {
        String value = getOption(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Properties getOption(String key, Properties defaultValue) {
        Properties value = commandLine.getOptionProperties(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

}
