package com.terran4j.commons.api2doc.codewriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.terran4j.commons.api2doc.domain.*;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.terran4j.commons.api2doc.impl.ClasspathFreeMarker;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.value.KeyedList;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class RetrofitCodeWriter {
	
	private static final Logger log = LoggerFactory.getLogger(RetrofitCodeWriter.class);
	
	@Autowired
	private JavaBeanCodeWriter javaBeanCodeWriter;
	
	@Autowired
	private EnumCodeWriter enumCodeWriter;

	@Autowired
	private ClasspathFreeMarker classpathFreeMarker;

	private Template interfaceTemplate = null;

	@PostConstruct
	public void init() {
		try {
			interfaceTemplate = classpathFreeMarker.getTemplate(getClass(), //
					"retrofit.java.ftl");
			log.info("RetrofitCodeWriter inited done.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void writeCode(List<ApiFolderObject> folders, CodeOutput out, CodeConfig config) {
		if (folders == null || folders.size() == 0) {
			return;
		}

		if (out == null) {
			throw new NullPointerException("CodeWriter writer is null.");
		}

		Set<Class<?>> enumClasses = new HashSet<>();
		KeyedList<String, ApiResultObject> javaBeans = new KeyedList<String, ApiResultObject>();
		for (ApiFolderObject folder : folders) {
			Map<String, Object> model = toModel(folder, config, javaBeans, enumClasses);
			String className = toRetrofitClassName(folder.getId());
			String fileName = className + ".java";
			try {
				String fileContent = classpathFreeMarker.build(interfaceTemplate, model);
				out.writeCodeFile(fileName, fileContent);
			} catch (IOException | TemplateException e) {
				throw new RuntimeException(e);
			}
		}
		
		Set<Class<?>> writtenClasses = new HashSet<>();
		List<ApiResultObject> results = javaBeans.getAll();
		for (ApiResultObject result : results) {
			Class<?> clazz = result.getSourceType();
			if (writtenClasses.contains(clazz)) {
				continue;
			}
			
			String className = clazz.getSimpleName();
			try {
				javaBeanCodeWriter.writeCode(result, className, out, config);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			writtenClasses.add(clazz);
		}
		
		for (Class<?> currentClass : enumClasses) {
			String className = currentClass.getSimpleName();
			try {
				enumCodeWriter.writeCode(currentClass, className, out, config);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	private String toRetrofitClassName(String id) {
		String name = id.substring(0, 1).toUpperCase(Locale.ENGLISH) + id.substring(1);
		if (name.endsWith("Controller")) {
			name = name.replaceAll("Controller", "Retrofit");
		} else {
			name += "Retrofit";
		}
		return name;
	}
	
	private Map<String, Object> toModel(ApiFolderObject folder, CodeConfig config, //
			KeyedList<String, ApiResultObject> javaBeans, Set<Class<?>> enumClasses) {

		Map<String, Object> model = new HashedMap<>();
		
		if (config == null) {
			config = new CodeConfig();
		}
		model.put("config", config);

		String className = toRetrofitClassName(folder.getId());
		model.put("class", className);

		if (StringUtils.hasText(folder.getComment())) {
			model.put("comment", folder.getComment());
		}

		Set<String> imports = new HashSet<>();
		model.put("imports", imports);
		
		List<MethodInfo> methods = new ArrayList<>();
		List<ApiDocObject> docs = folder.getDocs();
		if (docs != null) {
			for (ApiDocObject doc : docs) {
				MethodInfo method = toMethodInfo(doc, config, imports);
				methods.add(method);
				
				List<ApiResultObject> results = doc.getResults();
				if (results == null || results.size() == 0) {
					continue;
				}

                ApiResultObject totalResult = results.get(0);


				for (ApiResultObject result : results) {
					String groupId = result.getGroupId();
					if (StringUtils.hasText(groupId) 
							&& !javaBeans.containsKey(groupId)) {
						javaBeans.add(groupId, result);
					}
					
					Class<?> clazz = result.getSourceType();
					if (clazz != null && clazz.isEnum()) {
						enumClasses.add(clazz);
					}
					List<ApiResultObject> children = result.getChildren();
					if (children != null) {
						for (ApiResultObject child : children) {
							clazz = child.getSourceType();
							if (clazz != null && clazz.isEnum()) {
								enumClasses.add(clazz);
							}
						}
					}
				}
			}
		}
		model.put("methods", methods);
		
		return model;
	}

	private MethodInfo toMethodInfo(ApiDocObject doc, CodeConfig config, Set<String> imports) {
		MethodInfo method = new MethodInfo();
		method.setName(doc.getId());
		method.setComment(doc.getComment());

		List<String> annos = new ArrayList<>();
		RequestMethod type = doc.getMethods()[0];
		if (type == RequestMethod.POST) {
			annos.add("@FormUrlEncoded");
		}
		String path = doc.getPaths()[0];
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		String anno = "@" + type.name() + "(\"" + path + "\")";
		annos.add(anno);
		method.setAnnos(annos);

		List<ApiParamObject> srcParams = new ArrayList<>();
		List<ApiParamObject> extraPrams = config.getExtraPrams(doc);
		if (extraPrams != null) {
			srcParams.addAll(extraPrams);
		}
		if (doc.getParams() != null) {
			srcParams.addAll(doc.getParams());
		}

		List<ParamInfo> params = new ArrayList<>();
		for (int i = 0; i < srcParams.size(); i++) {
			ApiParamObject srcParam = srcParams.get(i);
			ParamInfo param = toParam(srcParam, doc, imports);
			if (i < srcParams.size() - 1) {
				param.setExpression(param.getExpression() + ", ");
			}
			params.add(param);
		}
		method.setParams(params);

		String returnClass = null;
        List<ApiResultObject> results = doc.getResults();
        if (results != null && results.size() > 0) {
            ApiResultObject result = results.get(0);
            ApiDataType dataType = result.getDataType();
            if (dataType != null) {
                if (dataType == ApiDataType.ARRAY) {
                    returnClass = "List<" + result.getSourceType().getSimpleName() + ">";
                } else {
                    returnClass = result.getSourceType().getSimpleName();
                }
            }
        }
        if (returnClass == null) {
            Class<?> returnType = doc.getSourceMethod().getReturnType();
            if (returnType != null && returnType != void.class) {
                returnClass = returnType.getSimpleName();
            }
        }
        method.setReturnClass(returnClass);

		return method;
	}

	private ParamInfo toParam(ApiParamObject srcParam, ApiDocObject doc, Set<String> imports) {
		ParamInfo param = new ParamInfo();

		String id = srcParam.getId();
		param.setId(id);

		param.setComment(srcParam.getComment());

		StringBuffer expression = new StringBuffer();

		RequestMethod requestMethod = doc.getMethods()[0];
		ApiParamLocation location = srcParam.getLocation();
		String annoName = toParamAnnoName(location, requestMethod);
		expression.append("@").append(annoName) //
				.append("(\"").append(id).append("\")");

		Class<?> paramClass = Classes.toWrapType(srcParam.getSourceType());
		CodeUtils.addImport(paramClass, imports);
		expression.append(" ").append(paramClass.getSimpleName());
		expression.append(" ").append(id);

		param.setExpression(expression.toString());

		return param;
	}
	
	private String toParamAnnoName(ApiParamLocation location, RequestMethod requestMethod) {
		if (location == ApiParamLocation.Header) {
			return "Header";
		} else if (location == ApiParamLocation.Path) {
			return "Path";
		} else if (location == ApiParamLocation.Param) {
			if (requestMethod == RequestMethod.POST) {
				return "Field";
			} else {
				return "Query";
			}
		} else {
			throw new RuntimeException("ApiParamLocation location unsupported: " + location);
		}
	}

	public static final class ParamInfo {

		private String id;

		private String comment;

		private String expression;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getExpression() {
			return expression;
		}

		public void setExpression(String expression) {
			this.expression = expression;
		}

	}

	public static final class MethodInfo {

		private List<ParamInfo> params;

		private List<String> annos;

		private String comment;

		private String name;

		private String returnClass;

        public String getReturnClass() {
            return returnClass;
        }

        public void setReturnClass(String returnClass) {
            this.returnClass = returnClass;
        }

        public List<ParamInfo> getParams() {
			return params;
		}

		public void setParams(List<ParamInfo> params) {
			this.params = params;
		}

		public List<String> getAnnos() {
			return annos;
		}

		public void setAnnos(List<String> annos) {
			this.annos = annos;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}

