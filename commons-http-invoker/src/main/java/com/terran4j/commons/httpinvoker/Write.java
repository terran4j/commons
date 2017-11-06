package com.terran4j.commons.httpinvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.ValueSources;

public class Write {
	
	private static final Logger log = LoggerFactory.getLogger(Write.class);

	private String key;
	
	private String value;
	
	private String to;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void doWrite(Session session, final ValueSources<String, String> values) {
		String actualValue = Strings.format(value, values);
		WriteTo writeTo = WriteTo.valueOf(to);
		if (writeTo == WriteTo.headers) {
			session.getHeaders().put(key, actualValue);
			if (log.isInfoEnabled()) {
				log.info("write to {}[{} = {}]", to, key, actualValue);
			}
		}
		if (writeTo == WriteTo.locals) {
			session.getLocals().put(key, actualValue);
			if (log.isInfoEnabled()) {
				log.info("write to {}[{} = {}]", to, key, actualValue);
			}
		}
	}
	
}
