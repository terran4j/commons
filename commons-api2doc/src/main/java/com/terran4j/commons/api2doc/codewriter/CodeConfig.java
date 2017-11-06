package com.terran4j.commons.api2doc.codewriter;

import java.util.List;

import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiParamObject;

public class CodeConfig {

	public List<ApiParamObject> getExtraPrams(ApiDocObject doc) {
		return null;
	}

	private String pkgName;

	private String declaredComment;

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getDeclaredComment() {
		return declaredComment;
	}

	public void setDeclaredComment(String declareComment) {
		this.declaredComment = declareComment;
	}

}
