package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.annotations.ApiError;
import com.terran4j.commons.api2doc.annotations.ApiErrors;
import com.terran4j.commons.api2doc.domain.*;
import com.terran4j.commons.restpack.RestPackController;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.value.KeyedList;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


@Service
public class Api2DocCollector implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(Api2DocCollector.class);

    private final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer
            = new LocalVariableTableParameterNameDiscoverer();

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
            log.info("prepare to get API Info by bean:  {}", beanName);
        }

        List<MappingMethod> methods = MappingMethod.getMappingMethods(clazz);
        // Classes.getMethods(RequestMapping.class, clazz);
        if (methods == null || methods.size() == 0) {
            // 整个类中都没有任何 RequestMapping 的方法，不用收集。
//            if (log.isInfoEnabled()) {
//                log.info("No any @RequestMapping /  method, no need to get, " +
//                        "beanName = {}", beanName);
//            }
            return null;
        }

        Api2Doc classApi2Doc = clazz.getAnnotation(Api2Doc.class);
        if (classApi2Doc != null && classApi2Doc.ignore()) {
            // 整个类的文档被忽略。
            if (log.isInfoEnabled()) {
                log.info("@Api2Doc ignore = true, no need to get, " +
                        "beanName = {}", beanName);
            }
            return null;
        }

        List<MappingMethod> ali2DocMethods = new ArrayList<>();
        for (MappingMethod mappingMethod : methods) {
            Method method = mappingMethod.getMethod();
            Api2Doc api2Doc = method.getAnnotation(Api2Doc.class);
            if (classApi2Doc == null && api2Doc == null) {
                // 本方法的文档被忽略。
                continue;
            }
            if (api2Doc != null && api2Doc.ignore()) {
                // 本方法的文档被忽略。
                continue;
            }
            ali2DocMethods.add(mappingMethod);
        }
        if (classApi2Doc == null && ali2DocMethods.size() == 0) {
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

        // 这组 API 是否用了 RestPack
        RestPackController restPackController = clazz.getAnnotation(RestPackController.class);
        folder.setRestPack(restPackController != null);

        // API 组的注释。
        ApiComment apiComment = clazz.getAnnotation(ApiComment.class);
        folder.setComment(ApiCommentUtils.getComment(
                apiComment, null, null));

        // 在类上的 seeClass ，是默认的。
        Class<?> defaultSeeClass = ApiCommentUtils.getDefaultSeeClass(
                apiComment, null);

        // API 组的路径前缀。
        String[] basePaths = getPath(classMapping);

        // 根据方法生成 API 文档。
        List<ApiDocObject> docs = new ArrayList<>();
        for (MappingMethod method : ali2DocMethods) {
            ApiDocObject doc = getApiDoc(method, basePaths, beanName,
                    classApi2Doc, defaultSeeClass);
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
        Collections.sort(docs);
        folder.addDocs(docs);

        return folder;
    }

    ApiDocObject getApiDoc(
            MappingMethod mappingMethod, String[] basePaths,
            String beanName, Api2Doc classApi2Doc,
            Class<?> defaultSeeClass) throws BusinessException {

        Method method = mappingMethod.getMethod();

        // 只要有 @ApiDoc 注解（无论是本方法上，还是类上），有会生成文档，没有这个注解就不会。
        Api2Doc api2Doc = method.getAnnotation(Api2Doc.class);
        if (api2Doc == null && classApi2Doc == null) {
            return null;
        }

        ApiDocObject doc = new ApiDocObject();

        doc.setSourceMethod(method);

        // 获取文档的 id，以 @Api2Doc、方法名 为顺序获取。
        String id = method.getName();
        if (api2Doc != null && StringUtils.hasText(api2Doc.value())) {
            id = api2Doc.value();
        }
        if (api2Doc != null && StringUtils.hasText(api2Doc.id())) {
            id = api2Doc.id();
        }
        doc.setId(id);
        checkId(id);

        // 获取文档的排序。
        if (api2Doc != null) {
            doc.setOrder(api2Doc.order());
        }

        // 获取文档名称，按 @Api2Doc 、@Mapping、方法名的顺序获取。
        String name = method.getName();
        String mappingName = mappingMethod.getName();
        if (StringUtils.hasText(mappingName)) {
            name = mappingName;
        }
        if (api2Doc != null && StringUtils.hasText(api2Doc.name())) {
            name = api2Doc.name();
        }
        doc.setName(name);

        // 获取 API 的注释信息。
        ApiComment apiComment = method.getAnnotation(ApiComment.class);
        defaultSeeClass = ApiCommentUtils.getDefaultSeeClass(apiComment, defaultSeeClass);
        String docComment = ApiCommentUtils.getComment(apiComment, defaultSeeClass, name);
        doc.setComment(docComment);
        String docSample = ApiCommentUtils.getSample(apiComment, defaultSeeClass, name);
        doc.setSample(docSample);

        // 获取 API 的访问路径。
        String[] paths = mappingMethod.getPath();
        paths = combine(basePaths, paths);
        doc.setPaths(paths);

        // 获取 HTTP 方法。
        doc.setMethods(mappingMethod.getRequestMethod());

        // 收集参数信息。
        List<ApiParamObject> apiParams = toApiParams(method, defaultSeeClass);
        if (apiParams != null && apiParams.size() > 0) {
            for (ApiParamObject apiParam : apiParams) {
                doc.addParam(apiParam);
            }
        }

        // 收集返回值信息。
        KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
        ApiResultObject resultObject = ApiResultObject.parseResultType(method, totalResults);
        if (resultObject != null) {
            resultObject.setComment(docComment);
            resultObject.setSample(docSample);
        }
        doc.setResultType(resultObject);
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


    public List<ApiParamObject> toApiParams(Method method, Class<?> defaultSeeClass) {
        List<ApiParamObject> result = new ArrayList<>();
        Set<String> paramIds = new HashSet<>();

        Parameter[] params = method.getParameters();
        if (params != null && params.length > 0) {
            String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
            for (int i = 0; i < params.length; i++) {
                Parameter param = params[i];

                Class<?> paramClass = param.getType();
                ApiComment paramClassComment = paramClass.getAnnotation(ApiComment.class);
                if (paramClassComment != null) {
                    // 从参数的类的属性中获取注释信息。
                    List<ApiParamObject> paramsFromClass = toApiParams(
                            paramClass, defaultSeeClass);
                    for (ApiParamObject paramFromClass : paramsFromClass) {
                        if (paramIds.contains(paramFromClass.getId())) {
                            continue;
                        }
                        paramIds.add(paramFromClass.getId());
                        result.add(paramFromClass);
                    }
                } else {
                    // 从参数本身中获取注释信息。
                    String paramName;
                    if (paramNames != null) {
                        paramName = paramNames[i];
                    } else {
                        paramName = param.getName();
                    }

                    ApiParamObject apiParamObject = toApiParam(param,
                            paramName, param.getType(), defaultSeeClass);
                    if (apiParamObject == null) {
                        continue;
                    }

                    String paramId = apiParamObject.getId();
                    if (paramIds.contains(paramId)) {
                        String msg = "参数id值重复： " + paramId + "，所在方法： " + method;
                        throw new BeanDefinitionStoreException(msg);
                    }

                    paramIds.add(paramId);
                    result.add(apiParamObject);
                }
            }
        }
        return result;
    }

    /**
     * 从类的属性中获取注释信息。
     *
     * @param beanClass
     * @param defaultSeeClass
     * @return
     */
    public List<ApiParamObject> toApiParams(Class<?> beanClass, Class<?> defaultSeeClass) {
        List<ApiParamObject> result = new ArrayList<>();

        PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(beanClass);
        if (props == null || props.length == 0) {
            return result;
        }

        for (PropertyDescriptor prop : props) {
            if (Api2DocUtils.isFilter(prop, beanClass)) {
                continue;
            }

            String fieldName = prop.getName();

            Method readMethod = prop.getReadMethod();
            if (readMethod == null) {
                continue;
            }
            Class<?> fieldType = readMethod.getReturnType();

            Field field = Classes.getField(fieldName, beanClass);
            if (field == null) {
                continue;
            }

            ApiParamObject param = toApiParam(field, fieldName, fieldType,
                    defaultSeeClass);
            if (param == null) {
                continue;
            }

            result.add(param);
        }

        Collections.sort(result);

        return result;
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

    ApiParamObject toApiParam(
            AnnotatedElement element, String elementName,
            Class<?> elementType, Class<?> defaultSeeClass) {

        ApiParamObject apiParamObject = new ApiParamObject();

        ApiParamLocation.collects(apiParamObject, element);

        String id = apiParamObject.getId();
        checkId(id);

        String name = apiParamObject.getName();
        if (StringUtils.isEmpty(name)) {
            name = elementName;
        }
        apiParamObject.setName(name);

        apiParamObject.setId(name);

        ApiComment apiComment = element.getAnnotation(ApiComment.class);
        ApiCommentUtils.setApiComment(apiComment, defaultSeeClass, apiParamObject);

        apiParamObject.setSourceType(elementType);

        ApiDataType dataType = convertType(elementType);
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
                || paramType.equals(Short.class)
                || paramType.equals(Byte.class)) {
            return ApiDataType.INT;
        }

        // Date 类型返回为 long 格式。
        ApiDataType dataType = DateConverter.dateAsLongType(paramType);
        if (dataType != null) {
            return dataType;
        }

        if (paramType.equals(Long.class)) {
            return ApiDataType.LONG;
        }

        if (paramType.equals(Float.class)
                || paramType.equals(Double.class)) {
            return ApiDataType.NUMBER;
        }

        if (paramType.equals(String.class)
                || paramType.equals(Character.class)
                || paramType.equals(StringBuffer.class)
                || paramType.equals(StringBuilder.class)) {
            return ApiDataType.STRING;
        }

        if (Classes.isInterface(paramType, Collection.class)) {
            return ApiDataType.ARRAY;
        }

        return ApiDataType.OBJECT;
    }

    private String[] getPath(RequestMapping mapping) {
        Set<String> allPaths = new HashSet<>();

        if (mapping != null) {
            String[] paths = mapping.path();
            if (paths != null && paths.length > 0) {
                allPaths.addAll(Arrays.asList(paths));
            }

            paths = mapping.value();
            if (paths != null && paths.length > 0) {
                allPaths.addAll(Arrays.asList(paths));
            }
        }

        return allPaths.toArray(new String[allPaths.size()]);
    }


    private void checkId(String id) {
        // TODO: CHECK id.
    }

}
