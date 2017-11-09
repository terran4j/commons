package com.terran4j.commons.api2doc.domain;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.controller.ApiObjectComparator;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.value.KeyedList;

public class ApiResultObject extends ApiObject {

	private static final Logger log = LoggerFactory.getLogger(ApiResultObject.class);

	private ApiDataType dataType;
	
	private Class<?> sourceType;

	private String typeName = "";

	private String refGroupId = null;

	private String groupId = null;

	private String groupName = null;

	private final List<ApiResultObject> children = new ArrayList<>();
	
	public Class<?> getSourceType() {
		return sourceType;
	}

	public void setSourceType(Class<?> sourceType) {
		this.sourceType = sourceType;
	}

	public String getRefGroupId() {
		return refGroupId;
	}

	public void setRefGroupId(String refGroupId) {
		this.refGroupId = refGroupId;
	}

	public final ApiDataType getDataType() {
		return dataType;
	}

	public final void setDataType(ApiDataType dataType) {
		this.dataType = dataType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public final List<ApiResultObject> getChildren() {
		return children;
	}

	public final void addChild(ApiResultObject child) {
		this.children.add(child);
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final String getEnumComment(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		if (!clazz.isEnum()) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer("<br/>可选值为：");
		Class<Enum<?>> enumClass = (Class<Enum<?>>) clazz;
		Enum[] enums = enumClass.getEnumConstants();
		for (Enum e : enums) {
			String name = e.name();
			Field field = null;
			try {
				field = enumClass.getDeclaredField(name);
			} catch (NoSuchFieldException | SecurityException e1) {
				log.error("Can't get field \"" + name + "\" from Enum: " + clazz.getName(), e1);
				continue;
			}
			ApiComment comment = field.getAnnotation(ApiComment.class);
			String value = "";
			if (comment != null && StringUtils.hasText(comment.value())) {
				value = getComment(comment);
			}
			if (sb.length() > 0) {
				sb.append("<br/>");
			}
			sb.append(name).append(": ").append(value).append("; ");
		}

		return sb.toString();
	}
	
	private static final String getTypeName(Class<?> clazz, ApiDataType dataType) {
		if (clazz.isEnum()) {
			return ApiDataType.STRING.name().toLowerCase() + "(枚举值)";
		} else if (dataType != null && dataType.isSimpleType()){
			return dataType.name().toLowerCase();
		} else {
			return clazz.getSimpleName();
		}
	}

	/**
	 * 找到一个方法返回类型中字段，收集它的 Api2Doc 信息。
	 * 
	 * @param method
	 * @param totalResults
	 * @return
	 */
	public static final ApiResultObject parseResultType(Method method, //
			KeyedList<String, ApiResultObject> totalResults) {

		if (method == null) {
			throw new NullPointerException("parseResultType, but method is null.");
		}

		if (totalResults == null) {
			totalResults = new KeyedList<>();
		}

		final Class<?> clazz = method.getReturnType();
		final ApiDataType dataType = ApiDataType.toDataType(clazz);
		if (dataType == null) {
			return null;
		}
		String typeName = getTypeName(clazz, dataType);

		// 基本类型，直接处理。
		if (dataType.isSimpleType()) {
			ApiResultObject result = new ApiResultObject();
			result.setSourceType(clazz);
			result.setDataType(dataType);
			result.setTypeName(typeName);
			result.insertComment(getEnumComment(clazz));
			result.setId("");
			return result;
		}

		// 子类型。
		Class<?> elementType = null;

		// 数组类型，找到它的元素的具体类型，然后处理具体类型。
		if (dataType.isArrayType()) {
			typeName = typeName + "[]";

			elementType = getArrayElementClass(method);
			if (elementType == null) {
				log.warn("Can't find element class by method: {}", method);
				return null;
			}

			ApiDataType elementDataType = ApiDataType.toDataType(elementType);
			typeName = getTypeName(elementType, elementDataType) + "[]";
			
			// 数组类型，但元素是基本类型的，也直接处理。
			if (elementDataType != null && elementDataType.isSimpleType()) {
				ApiResultObject result = new ApiResultObject();
				result.setSourceType(elementType);
				result.setDataType(elementDataType);
				result.setTypeName(typeName);
				result.insertComment(getEnumComment(clazz));
				result.setId("");
				return result;
			}
		}

		// 复杂类型的情况。
		ApiResultObject result = new ApiResultObject();
		result.setDataType(dataType);
		result.setSourceType(clazz);
		result.setTypeName(typeName);
		result.setId("");

		if (dataType.isObjectType()) {
			elementType = method.getReturnType();
		}

		// 没有子类型，直接返回。
        // TODO:  暂时不解析 Map 内部的类型。
		if (elementType == null || Map.class.equals(elementType)) {
			return result;
		}
		
		result.setSourceType(elementType);

		// 没有子类型，直接返回。
		PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(elementType);
		if (props == null || props.length == 0) {
			return result;
		}

		// 根据类型生成字段集的 id 和 name 。
		String groupId = getGroupId(elementType);
		result.setGroupId(groupId);
		String groupName = elementType.getSimpleName();
		result.setGroupName(groupName);

		// 加入到结果字段集中。
		if (totalResults.containsKey(groupId)) {
			return result;
		} else {
			totalResults.add(groupId, result);
		}

		// 有子类型，补充子类型信息。
		for (PropertyDescriptor prop : props) {
			if (isFilter(prop, clazz)) {
				continue;
			}

			Method subMethod = prop.getReadMethod();

			// 处理子类型。
			ApiResultObject childPropResult = parseResultType(subMethod, totalResults);

			// 补充子类型信息。
			if (childPropResult != null) {

				// 补充到当前节点中。
				result.addChild(childPropResult);

				String id = prop.getName();
				childPropResult.setId(id);
				childPropResult.setName(id);

				Class<?> childPropClass = subMethod.getReturnType();
				ApiDataType childPropDataType = ApiDataType.toDataType(childPropClass);
				childPropResult.setDataType(childPropDataType);

				Api2Doc childApi2Doc = null;
				ApiComment childApiComment = null;
				Field field = Classes.getField(id, elementType);
				if (field != null) {
					childApiComment = field.getAnnotation(ApiComment.class);
					childApi2Doc = field.getAnnotation(Api2Doc.class);
				} else {
					childApiComment = subMethod.getAnnotation(ApiComment.class);
					childApi2Doc = subMethod.getAnnotation(Api2Doc.class);
				}
				
				String comment = "";
				if (childApiComment != null && StringUtils.hasText(childApiComment.value())) {
					comment = getComment(childApiComment);
				}
				childPropResult.insertComment(comment);
				
				String sample = "";
				if (childApiComment != null && StringUtils.hasText(childApiComment.sample())) {
					sample = childApiComment.sample();
				}
				childPropResult.setSample(sample);
				
				if (childApi2Doc != null) {
					childPropResult.setOrder(childApi2Doc.order());
				}
				
				// 记录所引用的类型。
				Class<?> childSubType = null;
				if (childPropDataType != null) {
					if (childPropDataType.isArrayType()) {
						childSubType = getArrayElementClass(subMethod);
					} else if (childPropDataType.isObjectType()) {
						childSubType = subMethod.getReturnType();
					}
				}
				if (childSubType != null) {
					String refGroupId = getGroupId(childSubType);
					childPropResult.setRefGroupId(refGroupId);
				}
			}
		}

		Collections.sort(result.getChildren(), new ApiObjectComparator());

		return result;
	}
	
	public static final String getComment(ApiComment apiComment) {
		String comment = apiComment.value();
		if (StringUtils.isEmpty(comment)) {
			return null;
		}
		return comment.replaceAll("\n", "<br/>");
	}

	public static final String getSample(ApiComment apiComment, Class<?> clazz) throws BusinessException {
		String sample = apiComment.sample();
		if (StringUtils.isEmpty(sample)) {
			return null;
		}
		sample = sample.trim();

		if (!(sample.startsWith("@") && sample.endsWith("@"))) {
			return sample.replaceAll("\n", "<br/>");
		}

		String fileName = sample.substring(1, sample.length() - 1);
		String json = Strings.getString(clazz, fileName);
		if (StringUtils.isEmpty(json)) {
			throw new BusinessException(ErrorCodes.CONFIG_ERROR)
					.put("package", clazz.getPackage().getName())
					.put("fileName", fileName)
					.setMessage("在包 ${package} 中找不到文件： ${fileName}");
		}
		return json;
	}

	public static final String getGroupId(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		String groupId = ApiDocUtils.getId(clazz);
		return groupId;
	}

	public static final boolean isFilter(PropertyDescriptor prop, Class<?> clazz) {
		String name = prop.getName();
		if ("class".equals(name)) {
			return true;
		}
		return false;
	}

	public static final Class<?> getArrayElementClass(Method method) {

		Class<?> returnType = method.getReturnType();
		if (returnType.isArray()) {
			Class<?> elementClass = returnType.getComponentType();
			return elementClass;
		}

		if (Classes.isInterface(returnType, Collection.class)) {
			Type gType = method.getGenericReturnType();
			Type elementType = getGenericType(gType);
			if (elementType instanceof Class<?>) {
				Class<?> elementClass = (Class<?>) elementType;
				return elementClass;
			}
		}

		return null;
	}

	public static final Type getGenericType(Type gType) {
		// 如果gType是泛型类型对像。
		if (gType instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) gType;
			// 获得泛型类型的泛型参数
			Type[] gArgs = pType.getActualTypeArguments();
			return gArgs[gArgs.length - 1];
		} else {
			System.out.println("获取泛型信息失败");
			return null;
		}
	}

}
