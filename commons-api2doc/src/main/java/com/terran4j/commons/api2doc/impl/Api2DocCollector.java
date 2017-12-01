package com.terran4j.commons.api2doc.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import com.terran4j.commons.api2doc.controller.ApiObjectComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.annotations.ApiError;
import com.terran4j.commons.api2doc.annotations.ApiErrors;
import com.terran4j.commons.api2doc.domain.ApiDataType;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiErrorObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.api2doc.domain.ApiResultObject;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.value.KeyedList;

/**
 * 
 * @author jiangwei
 */
@Service
public class Api2DocCollector implements BeanPostProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(Api2DocCollector.class);

	@Autowired
	private Api2DocService apiDocService;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		ApiFolderObject folder;
		try {
			folder = toApiFolder(bean, beanName);
		} catch (BusinessException e) {
			throw new BeanDefinitionStoreException(
					"bean上的文档信息定义出错：" + e.getMessage());
		}
		if (folder == null) {
			return bean;
		}

		String id = folder.getId();
		ApiFolderObject existApiFolder = apiDocService.getFolder(id);
		if (existApiFolder != null) {
			String msg = "@Api2Doc id值重复： " + id;
			throw new BeanDefinitionStoreException(msg);
		}

		if (folder != null) {
			apiDocService.addFolder(folder);
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 解析 API 组，一组 API 对应一个 Controller 类， 其中每个 method 对应一个 api 。<br>
	 * 只要有 @ApiDoc 注解，有会生成文档，没有这个注解就不会。
	 * 
	 * @param bean
	 * @param beanName
	 * @return
	 */
	public ApiFolderObject toApiFolder(Object bean, String beanName) throws BusinessException {

		Class<?> clazz = Classes.getTargetClass(bean);
		Controller controller = AnnotationUtils.findAnnotation(clazz, Controller.class);
		if (controller == null) {
			// 不是 Controller 类，不用收集。
			return null;
		}
		if (log.isInfoEnabled()) {
			log.info("prepare to get API Info by bean, beanName = {}", beanName);
		}

		Method[] methods = Classes.getMethods(RequestMapping.class, clazz);
		if (methods == null || methods.length == 0) {
			// 整个类中都没有任何 RequestMapping 的方法，不用收集。
			if (log.isInfoEnabled()) {
				log.info("No any @RequestMapping method, no need to get, beanName = {}", beanName);
			}
			return null;
		}

		Api2Doc classApi2Doc = clazz.getAnnotation(Api2Doc.class);
		if (classApi2Doc != null && classApi2Doc.ignore()) {
			// 整个类的文档被忽略。
			if (log.isInfoEnabled()) {
				log.info("@Api2Doc ignore = true, no need to get, beanName = {}", beanName);
			}
			return null;
		}
		
		List<Method> api2DocMethos = new ArrayList<>();
		for (Method method : methods) {
			Api2Doc api2Doc = method.getAnnotation(Api2Doc.class);
			if (classApi2Doc == null && api2Doc == null) {
				// 本方法的文档被忽略。
				continue;
			}
			if (api2Doc != null && api2Doc.ignore()) {
				// 本方法的文档被忽略。
				continue;
			}
			api2DocMethos.add(method);
		}
		if (classApi2Doc == null && api2DocMethos.size() == 0) {
			// 整个类中的方法，都忽略从 API 生成文档，不用收集。
			if (log.isInfoEnabled()) {
				log.info("all method were ignored, no need to get, beanName = {}", beanName);
			}
			return null;
		}

		ApiFolderObject folder = new ApiFolderObject();
		
		folder.setSourceClass(clazz);

		String id = beanName;
		if (classApi2Doc != null && StringUtils.hasText(classApi2Doc.value())) {
			id = classApi2Doc.value();
		}
		if (classApi2Doc != null && StringUtils.hasText(classApi2Doc.id())) {
			id = classApi2Doc.id();
		}
		folder.setId(id);
		checkId(id);

		String pathPattern = "api2doc/" + id + "/*.md";
        try {
            Resource[] resources = Classes.scanResources(pathPattern);
            if (resources != null && resources.length > 0) {
                Map<String, String> mds = new HashMap<>();
                for (Resource resource : resources) {
                    String md = resource.getFilename();
                    mds.put(ApiFolderObject.name2Id(md), md);
                }
                folder.setMds(mds);
            }
        } catch (IOException e) {
            String msg = "scan classpath[" + pathPattern + "] failed: " + e.getMessage();
            throw new BeanDefinitionStoreException(msg);
        }
		
		if (classApi2Doc != null) {
			folder.setOrder(classApi2Doc.order());
		}

		// API 组的名称。
		String name = beanName;
		RequestMapping classMapping = clazz.getAnnotation(RequestMapping.class);
		if (classMapping != null && StringUtils.hasText(classMapping.name())) {
			name = classMapping.name();
		}
		if (classApi2Doc != null && StringUtils.hasText(classApi2Doc.name())) {
			name = classApi2Doc.name();
		}
		folder.setName(name);

		// API 组的注释。
		ApiComment apiComment = clazz.getAnnotation(ApiComment.class);
		folder.setComment(ApiCommentUtils.getComment(apiComment, null));
//		if (apiComment != null) {
//			String commentText = Api2DocUtils.getComment(apiComment);
//			folder.setComment(commentText);
//		}

		// API 组的路径前缀。
		String[] basePaths = getPath(classMapping);

		// 根据方法生成 API 文档。
		List<ApiDocObject> docs = new ArrayList<>();
		for (Method method : api2DocMethos) {
			ApiDocObject doc = getApiDoc(method, basePaths, beanName, classApi2Doc);
			if (doc == null) {
				continue;
			}

			String docId = doc.getId();
			ApiDocObject existDoc = folder.getDoc(docId);
			if (existDoc != null) {
				String msg = "文档id值重复： " + docId + "\n"
						+ "如果方法上没有用  @Api2Doc(id = \"xxx\") 来指定文档id，则重载方法会出现此问题。\n"
						+ "请在重载的方法上用 @Api2Doc(id = \"xxx\") 来指定一个不同的文档id";
				throw new BeanDefinitionStoreException(msg);
			}

			docs.add(doc);
			if (log.isInfoEnabled()) {
				log.info("add doc: {}/{}", folder.getId(), docId);
			}
		}
		Collections.sort(docs, new ApiObjectComparator());
		folder.addDocs(docs);

		return folder;
	}

	ApiDocObject getApiDoc(Method method, String[] basePaths, String beanName, Api2Doc classApi2Doc) throws BusinessException {
		
		// 只要有 @ApiDoc 注解（无论是本方法上，还是类上），有会生成文档，没有这个注解就不会。
		Api2Doc api2Doc = method.getAnnotation(Api2Doc.class);
		if (api2Doc == null && classApi2Doc == null) {
			return null;
		}
		
		ApiDocObject doc = new ApiDocObject();

		doc.setSourceMethod(method);

		String id = method.getName();
		if (api2Doc != null && StringUtils.hasText(api2Doc.value())) {
			id = api2Doc.value();
		}
		if (api2Doc != null && StringUtils.hasText(api2Doc.id())) {
			id = api2Doc.id();
		}
		doc.setId(id);
		checkId(id);
		
		if (api2Doc != null) {
			doc.setOrder(api2Doc.order());
		}

		String name = method.getName();
		RequestMapping mapping = method.getAnnotation(RequestMapping.class);
		if (StringUtils.hasText(mapping.name())) {
			name = mapping.name();
		}
		if (api2Doc != null && StringUtils.hasText(api2Doc.name())) {
			name = api2Doc.name();
		}
		doc.setName(name);

		ApiComment apiComment = method.getAnnotation(ApiComment.class);
        doc.setComment(ApiCommentUtils.getComment(apiComment, name));
		doc.setSample(ApiCommentUtils.getSample(apiComment, name));

//		if (apiComment != null && StringUtils.hasText(apiComment.sample())) {
//			Class<?> clazz = method.getDeclaringClass();
//			String sample = Api2DocUtils.getSample(apiComment, clazz);
//			if (StringUtils.hasText(sample)) {
//				doc.setSample(sample);
//			}
//		}

		String[] paths = getPath(mapping);
		paths = combine(basePaths, paths);
		doc.setPaths(paths);

		doc.setMethods(mapping.method());

		// 收集参数信息。
		Parameter[] params = method.getParameters();
		if (params != null && params.length > 0) {
			for (Parameter param : params) {
				ApiParamObject apiParamObject = getParam(param);
				if (apiParamObject == null) {
					continue;
				}
				
				String paramId = apiParamObject.getId();
				ApiParamObject existParam = doc.getParam(paramId);
				if (existParam != null) {
					String msg = "参数id值重复： " + paramId + "，所在方法： " + method;
					throw new BeanDefinitionStoreException(msg);
				}
				
				doc.addParam(apiParamObject);
			}
		}

		// 收集返回值信息。
		KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
		ApiResultObject.parseResultType(method, totalResults);
		doc.setResults(totalResults.getAll());

        // 确定返回类型的描述。
        String returnTypeDesc = null;
        List<ApiResultObject> results = doc.getResults();
        if (results != null && results.size() > 0) {
            ApiResultObject result = results.get(0);
            ApiDataType dataType = result.getDataType();
            if (dataType != null) {
                if (dataType == ApiDataType.ARRAY) {
                    returnTypeDesc = result.getSourceType().getSimpleName() + "[]";
                } else {
                    returnTypeDesc = result.getSourceType().getSimpleName();
                }
            }
        }
        if (returnTypeDesc == null) {
            Class<?> returnType = doc.getSourceMethod().getReturnType();
            if (returnType != null && returnType != void.class) {
                returnTypeDesc = returnType.getSimpleName();
            }
        }
		doc.setReturnTypeDesc(returnTypeDesc);

		// 收集错误码信息。
		ApiErrors errorCodes = method.getAnnotation(ApiErrors.class);
		if (errorCodes != null && errorCodes.value() != null 
				&& errorCodes.value().length > 0) {
			for (ApiError errorCode : errorCodes.value()) {
				ApiErrorObject error = getError(errorCode);
				if (error == null) {
					continue;
				}
				doc.addError(error);
			}
		} else {
			ApiError errorCode = method.getAnnotation(ApiError.class);
			ApiErrorObject error = getError(errorCode);
			if (error != null) {
				doc.addError(error);
			}
		}

		return doc;
	}
	
	ApiErrorObject getError(ApiError errorCode) {
		if (errorCode == null) {
			return null;
		}
		
		ApiErrorObject error = new ApiErrorObject();
		String code = errorCode.value();
		error.setId(code);
		error.setName(code);
		
		checkId(code);

		String comment = errorCode.comment();
		if (comment == null) {
			comment = BusinessException.getMessage(code);
		}
		if (comment == null) {
			comment = "";
		}
		error.setComment(comment);
		return error;
	}

	ApiParamObject getParam(Parameter param) {
		ApiParamObject apiParamObject = new ApiParamObject();
		
		ApiParamLocation.collects(apiParamObject, param);
		
		String id = apiParamObject.getId();
		checkId(id);
		
		String name = apiParamObject.getName();
		if (StringUtils.isEmpty(name)) {
			name = param.getName();
		}
		apiParamObject.setName(name);
		
		apiParamObject.setId(name);

		ApiComment apiComment = param.getAnnotation(ApiComment.class);
		ApiCommentUtils.setApiComment(apiComment, apiParamObject);
//		if (apiComment != null && StringUtils.hasText(apiComment.value())) {
//			String commentText = Api2DocUtils.getComment(apiComment);
//			apiParamObject.setComment(commentText);
//		}
//		if (apiComment != null && StringUtils.hasText(apiComment.sample())) {
//			if (StringUtils.hasText(apiComment.sample())) {
//				apiParamObject.setSample(apiComment.sample());
//			}
//		}

		Class<?> paramType = param.getType();
		apiParamObject.setSourceType(paramType);
		
		ApiDataType dataType = convertType(paramType);
		apiParamObject.setDataType(dataType);
		
		return apiParamObject;
	}

	String[] combine(String[] classPaths, String[] methodPaths) {
		if (classPaths == null || classPaths.length == 0) {
			return methodPaths;
		}

		List<String> paths = new ArrayList<>();
		for (String basePath : classPaths) {
			for (String srcPath : methodPaths) {
				String path = basePath + srcPath;
				if (paths.contains(path)) {
					continue;
				}
				paths.add(path);
			}
		}
		return paths.toArray(new String[paths.size()]);
	}

	ApiDataType convertType(Class<?> paramType) {
		if (paramType == null) {
			return null;
		}
		paramType = Classes.toWrapType(paramType);

		if (paramType.equals(Boolean.class)) {
			return ApiDataType.BOOLEAN;
		}

        if (paramType.equals(Integer.class)
                || paramType.equals(Short.class) || paramType.equals(Byte.class)) {
            return ApiDataType.INT;
        }

		// Date 类型返回为 long 格式。
		if (paramType.equals(Date.class) || paramType.equals(java.sql.Date.class)
                || paramType.equals(Long.class)) {
		    return ApiDataType.LONG;
        }

		if (paramType.equals(Float.class) || paramType.equals(Double.class)) {
			return ApiDataType.NUMBER;
		}

        if (paramType.equals(String.class)  || paramType.equals(Character.class)
                || paramType.equals(StringBuffer.class) //
                || paramType.equals(StringBuilder.class)) { //
            return ApiDataType.STRING;
        }

		if (Classes.isInterface(paramType, Collection.class)) {
		    return ApiDataType.ARRAY;
        }

		return ApiDataType.OBJECT;
	}

	private String[] getPath(RequestMapping mapping) {
		Set<String> allPaths = new HashSet<>();
		
		String[] paths = mapping.path();
		if (paths != null && paths.length > 0) {
			allPaths.addAll(Arrays.asList(paths));
		}

		paths = mapping.value();
		if (paths != null && paths.length > 0) {
			allPaths.addAll(Arrays.asList(paths));
		}
		
		return allPaths.toArray(new String[allPaths.size()]);
	}


	
	private void checkId(String id) {
		// TODO: CHECK id.
	}

}
