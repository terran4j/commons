package com.terran4j.commons.jfinger;

public class CommandOptionDefine {

	private final OptionType type;

	private final String key;
	
	private final boolean required;

	private final String name;
	
	private final String desc;
	
	public CommandOptionDefine(CommandOption option) {
		super();
		this.key = option.key();
		this.required = option.required();
		this.name = option.name();
		this.desc = option.desc();
		this.type = option.type();
	}

	/**
	 * @return the type
	 */
	public final OptionType getType() {
		return type;
	}

	/**
	 * @return the key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @return the required
	 */
	public final boolean isRequired() {
		return required;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the desc
	 */
	public final String getDesc() {
		return desc;
	}

}
