package com.terran4j.commons.api2doc.codewriter;

import java.util.Set;

public class CodeUtils {

	public static final void addImport(Class<?> clazz, Set<String> imports) {
		if (clazz == null || clazz.getPackage() == null) {
			return;
		}

		String paramPkgName = clazz.getPackage().getName();

		if (paramPkgName.equals("java.lang")) {
			return;
		}

		if (paramPkgName.startsWith("java.") || paramPkgName.startsWith("javax.")) {
			imports.add(clazz.getName());
		}
	}
}
