package com.terran4j.commons.api2doc.controller;

import java.util.Comparator;

import com.terran4j.commons.api2doc.domain.ApiObject;

public class ApiObjectComparator implements Comparator<ApiObject> {

	@Override
	public int compare(ApiObject o1, ApiObject o2) {
		if (o1 == null || o2 == null) {
			throw new NullPointerException();
		}

		// 优先按用户指定排序。
		if (o1.getOrder() < o2.getOrder()) {
			return -1;
		}
		if (o1.getOrder() > o2.getOrder()) {
			return 1;
		}

		// 其次按 id 字符串排序。
		return o1.getId().compareTo(o2.getId());
	}

}
