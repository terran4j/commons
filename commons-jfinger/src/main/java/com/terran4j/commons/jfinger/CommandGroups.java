package com.terran4j.commons.jfinger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandGroups {
	
	private static final Logger log = LoggerFactory.getLogger(CommandGroups.class);

	private final Map<String, CommandGroupDefine> commands = new HashMap<String, CommandGroupDefine>();
	
	public void addCommandGroup(CommandGroupDefine group) {
		if (group == null) {
			throw new NullPointerException("group is null.");
		}
		
		String groupName = group.getName();
		if (commands.containsKey(groupName)) {
			if (log.isWarnEnabled()) {
				CommandGroupDefine existed = commands.get(groupName);
				log.warn("CommandGroup[{}] is duplicated with another one[{}]", group, existed);
			}
			return;
		}
		
		this.commands.put(groupName, group);
	}

	public boolean containsKey(Object key) {
		return commands.containsKey(key);
	}

	public CommandGroupDefine get(Object key) {
		return commands.get(key);
	}

	public CommandGroupDefine put(String key, CommandGroupDefine value) {
		return commands.put(key, value);
	}

	public Set<String> keySet() {
		return commands.keySet();
	}
	
	
}
