package com.terran4j.commons.api2doc.domain;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMethod;

import com.terran4j.commons.util.value.KeyedList;

public class ApiDocObject extends ApiObject {
	
	private ApiFolderObject folder;

	private String[] paths;
	
	private Method sourceMethod;

	private RequestMethod[] methods;

	private String returnTypeDesc;

	private List<ApiResultObject> results;

	private final KeyedList<String, ApiParamObject> params = new KeyedList<>();

	private final KeyedList<String, ApiErrorObject> errors = new KeyedList<>();
	
	public Method getSourceMethod() {
		return sourceMethod;
	}

	public void setSourceMethod(Method sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

	public RequestMethod[] getMethods() {
		return methods;
	}

	public void setMethods(RequestMethod[] methods) {
		this.methods = methods;
	}

	public final List<ApiParamObject> getParams() {
		return params.getAll();
	}
	
	public final ApiParamObject getParam(String id) {
		return params.get(id);
	}

	public final void addParam(ApiParamObject param) {
		this.params.add(param.getId(), param);
	}

	public List<ApiResultObject> getResults() {
		return results;
	}

	public void setResults(List<ApiResultObject> results) {
		this.results = results;
	}

	public List<ApiErrorObject> getErrors() {
		return errors.getAll();
	}

	public void addError(ApiErrorObject error) {
		this.errors.add(error.getId(), error);
	}

	public ApiFolderObject getFolder() {
		return folder;
	}

	public void setFolder(ApiFolderObject folder) {
		this.folder = folder;
	}

    public String getReturnTypeDesc() {
        return returnTypeDesc;
    }

    public void setReturnTypeDesc(String returnTypeDesc) {
        this.returnTypeDesc = returnTypeDesc;
    }

}