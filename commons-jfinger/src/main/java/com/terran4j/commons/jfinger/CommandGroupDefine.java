package com.terran4j.commons.jfinger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wei.jiang
 */
public class CommandGroupDefine {

	private String name;
	
	private String desc;
	
	private List<CommandDefine> commandsList = new ArrayList<CommandDefine>();
	
	private Map<String, CommandDefine> commandsMap = new HashMap<String, CommandDefine>();
	
	public CommandGroupDefine() {
		super();
	}
	
	public CommandGroupDefine(CommandGroup anno) {
		super();
		this.name = anno.name();
		this.desc = anno.desc();
	}

	public List<CommandDefine> getCommands() {
		return commandsList;
	}
	
	public int size() {
		return commandsList.size();
	}
	
	public void addCommand(CommandDefine commandDefine) {
		commandsList.add(commandDefine);
		String key = commandDefine.getName();
		commandsMap.put(key, commandDefine);
	}
	
	public CommandDefine getCommand(String name) {
		return commandsMap.get(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	
	
}
