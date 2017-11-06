package com.terran4j.commons.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetClassAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Joiner;

public class Classes {

	private static final Logger log = LoggerFactory.getLogger(Classes.class);

	private static final Map<String, Class<?>> baseTypes = new HashMap<>();

	static {
		baseTypes.put("boolean", Boolean.class);
		baseTypes.put("byte", Byte.class);
		baseTypes.put("char", Character.class);
		baseTypes.put("short", Short.class);
		baseTypes.put("int", Integer.class);
		baseTypes.put("long", Long.class);
		baseTypes.put("float", Float.class);
		baseTypes.put("double", Double.class);
	}

	/**
	 * 如果是基本类型，则转换成对应的包裹类型；<br>
	 * 如果不是基本类型，则原样返回。<br>
	 * 如：<br>
	 * 输入为 int, 返回 java.lang.Integer; <br>
	 * 输入为 java.lang.String, 返回 java.lang.String
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class<?> toWrapType(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		if (clazz.isPrimitive()) {
			return baseTypes.get(clazz.getName());
		} else {
			return clazz;
		}
	}

	private static boolean isMatched(Class<?>[] paramClasses, Object[] paramObjects) {
		if (paramClasses == null) {
			paramClasses = new Class<?>[0];
		}
		if (paramObjects == null) {
			paramObjects = new Object[0];
		}

		if (paramClasses.length != paramObjects.length) {
			return false;
		}

		for (int i = 0; i < paramObjects.length; i++) {
			Object arg = paramObjects[i];
			if (arg == null) {
				continue;
			}
			Class<?> argClass = arg.getClass();
			if (equals(argClass, paramClasses[i]) || isSuperClass(argClass, paramClasses[i])) {
				continue;
			}

			return false;
		}

		return true;
	}

	/**
	 * 判断两个类是否相等，并且无视基本类型与包裹类型的差别，如：<br>
	 * srcClass = int; destClass = java.lang.Integer 也算是相等的。
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Class<?> a, Class<?> b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		a = toWrapType(a);
		b = toWrapType(b);
		return a == b;
	}

	/**
	 * 根据方法名、参数获取对应的方法对象，并且可以指定其上的注释进行过滤。
	 * 
	 * @param clazz
	 *            类对象。
	 * @param methodName
	 *            方法
	 * @param args
	 *            参数值
	 * @param annoClass
	 *            注解类，如果为 null 表示不要求有注解，否则要求方法上一定要有这个注解。
	 * @return 方法对象。
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Object[] args,
			Class<? extends Annotation> annoClass) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (!method.getName().equals(methodName)) {
				continue;
			}

			Class<?>[] paramClasses = method.getParameterTypes();
			if (!isMatched(paramClasses, args)) {
				continue;
			}

			if (annoClass != null && method.getAnnotation(annoClass) == null) {
				continue;
			}

			return method;
		}
		return null;
	}

	/**
	 * 判断一个类是否是另一个类的子孙类。<br>
	 * 如果两个类没有直接、间接继承关系，或相等，都会返回 false。
	 * 
	 * @param child
	 *            子孙类
	 * @param parent
	 *            祖先类
	 * @return true 表示有直接或间接继承关系。
	 */
	public static boolean isSuperClass(Class<?> child, Class<?> parent) {
		if (child == null || parent == null) {
			return false;
		}

		Class<?> currentSuperClass = child.getSuperclass();
		while (currentSuperClass != null) {
			if (currentSuperClass == parent) {
				return true;
			}
			currentSuperClass = currentSuperClass.getSuperclass();
		}

		return false;
	}

	/**
	 * 判断一个类是否实现了指定接口，不向上追溯。
	 * 
	 * @param clazz
	 * @param interfaceClass
	 * @return
	 */
	public static boolean isInterface(Class<?> clazz, Class<?> interfaceClass) {
		if (clazz == null || interfaceClass == null) {
			return false;
		}

		Class<?>[] interfaces = clazz.getInterfaces();
		if (interfaceClass != null) {
			for (Class<?> theInterfaceClass : interfaces) {
				if (theInterfaceClass == interfaceClass) {
					return true;
				}
			}
		}

		return false;
	}

    /**
     * 判断一个类是否实现了指定接口，会向上追溯到父接口。
     * @param clazz
     * @param parentInterface
     * @return
     */
	public static final boolean isInterfaceExtends(Class<?> clazz, Class<?> parentInterface) {
	    if (parentInterface == null || !parentInterface.isInterface()) {
	        throw new NullPointerException("parentInterface is null or is NOT interface");
        }

        if (parentInterface.equals(clazz)) {
            return true;
        }

        // 向上遍历父接口。
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> childClass : interfaces) {
            if (isInterfaceExtends(childClass, parentInterface)) {
                return true;
            }
        }

		// 检查父类及其接口。
		Class<?> supperClass = clazz.getSuperclass();
	    if (supperClass != null && isInterface(supperClass, parentInterface)) {
	        return true;
        }

		return false;
	}

	/**
	 * 对包及子包进行扫描，找出有指定注解的类。
	 * 
	 * @param basePackageClass
	 *            待扫描的包中的直接任意类，也就是根据这个类找到它的包。
	 * @param annotationFilter
	 *            对注解过滤，只有上面有这个注解的类才会被找出来；如果为 null，则不过滤。
	 * @return 符合条件的类集合。
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Set<Class<?>> scanClasses(Class<?> basePackageClass,
			Class<? extends Annotation> annotationFilter) throws IOException, ClassNotFoundException {
		TypeFilter filter = annotationFilter == null ? null : new AnnotationTypeFilter(annotationFilter, false);
		return scanClasses(basePackageClass, true, filter, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 扫描指定的包，将有指定注解的类找出来。
	 * 
	 * @param basePackageClass
	 *            待扫描的包中的直接任意类，也就是根据这个类找到它的包。
	 * @param recursively
	 *            是否递归的方式在子包中找， true表示要在所有子包中找，false表示只在本包中找。
	 * @param filter
	 *            指定的过滤条件，只有符合过滤条件的类才会被找出来；如果为 null，则不过滤。
	 * @return 符合条件的类集合。
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Set<Class<?>> scanClasses(Class<?> basePackageClass, boolean recursively, TypeFilter filter,
			ClassLoader classLoader) throws IOException, ClassNotFoundException {
		if (basePackageClass == null) {
			throw new NullPointerException("basePackageClass is null.");
		}

		final Set<Class<?>> classes = new HashSet<Class<?>>();

		String packageName = basePackageClass.getPackage().getName();
		final String resourcePattern = recursively ? "/**/*.class" : "/*.class";
		String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ ClassUtils.convertClassNameToResourcePath(packageName) + resourcePattern;
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

		final String KEY_maxCount = "terran4j.util.maxScanClassCount";
		String maxCountText = System.getProperty(KEY_maxCount, "1024").trim();
		int maxCount = 1024;
		try {
			maxCount = Integer.parseInt(maxCountText);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Can't parse as int of java property[" + KEY_maxCount + "]: " + maxCountText);
		}

		Resource[] resources = resourcePatternResolver.getResources(pattern);
		for (Resource resource : resources) {
			if (resource.isReadable()) {
				// 找到这个类。
				MetadataReader reader = readerFactory.getMetadataReader(resource);
				String className = reader.getClassMetadata().getClassName();

				boolean matched = false;
				if (filter != null) {
					matched = filter.match(reader, readerFactory);
				} else {
					matched = true;
				}

				if (matched) {
					Class<?> clazz = classLoader.loadClass(className);
					classes.add(clazz);

					// 调用方指定的范围太大的话，
					if (classes.size() > maxCount) {
						throw new RuntimeException("too many classes be scaned, can't more than: " + maxCount);
					}
				}
			}
		}

		// 输出日志
		if (log.isInfoEnabled()) {
			if (filter == null) {
				log.info("Found classes in package[{}]: \n{}", packageName, Joiner.on("\n").join(classes.iterator()));
			} else {
				log.info("Found classes with filter[{}] in package[{}]: \n{}", filter, packageName,
						Joiner.on("\n").join(classes.iterator()));
			}

		}
		return classes;
	}

    /**
     * 搜索匹配路径的搜索。
     * @param pathPattern
     * @return
     * @throws IOException
     */
    public static final Resource[] scanResources(String pathPattern) throws IOException {
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pathPattern;
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(pattern);
        return resources;
    }

	/**
	 * 获取一个对象的类<br>
	 * 在Spring AOP的拦截下，一些对象可能是由Cglib技术生成的代理类所创建，直接调用其getClass()方法获取到的类是这样的：<br>
	 * com.terran4j.XxxService$$EnhancerBySpringCGLIB$$da7b00df <br>
	 * 其实原始的类应该是： com.terran4j.XxxService。<br>
	 * 但由于Cglib的代理技术导致多了后面的 $$EnhancerBySpringCGLIB$$da7b00df。<br>
	 * 本方法可以自动识别对象是否是Cglib代理对象，如果是则找到它的原始类。
	 * 
	 * @param object
	 * @return
	 */
	public static Class<?> getTargetClass(Object object) {
		Assert.notNull(object, "Object must not be null");
		Class<?> result = null;
		if (object instanceof TargetClassAware) {
			result = ((TargetClassAware) object).getTargetClass();
		}
		if (result == null) {
			result = (ClassUtils.isCglibProxy(object) ? object.getClass().getSuperclass() : object.getClass());
		}
		return result;
	}

	/**
	 * 根据字段名，找到指定字段。<br>
	 * 如果本类找不到，会向上在父类中找，直到找到为止。<br>
	 * 
	 * @param name 字段名称。
	 * @param clazz 类对象。
	 * @return 字段对象。
	 */
	public static Field getField(String name, Class<?> clazz) {
		if (StringUtils.isEmpty(name) || clazz == null) {
			return null;
		}

		Field[] fields = clazz.getDeclaredFields();
		if (fields == null) {
			return null;
		}

		for (Field field : fields) {
			String fieldName = field.getName();
			if (fieldName != null && fieldName.equals(name)) {
				return field;
			}
		}

		return getField(name, clazz.getSuperclass());
	}

	/**
	 * 根据注解，找到指定的字段。
	 * 
	 * @param annotationClass
	 * @param clazz
	 * @return
	 */
	public static Field getField(Class<? extends Annotation> annotationClass, Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		Field[] fields = clazz.getDeclaredFields();
		if (fields == null) {
			return null;
		}

		for (Field field : fields) {
			Annotation annotation = field.getAnnotation(annotationClass);
			if (annotation != null) {
				return field;
			}
		}

		return getField(annotationClass, clazz.getSuperclass());
	}

	public static Field[] getFields(Class<? extends Annotation> annotationClass, final Class<?> clazz) {
		List<Field> fieldList = new ArrayList<Field>();

		// 将本类及所有的父类压栈。
		Stack<Class<?>> classStack = new Stack<Class<?>>();
		Class<?> currentClass = clazz;
		while (currentClass != null) {
			classStack.push(currentClass);
			currentClass = currentClass.getSuperclass();
		}

		// 将栈中的类一个个取出来，加载注解的属性。
		while (!classStack.isEmpty()) {
			currentClass = classStack.pop();
			loadFields(annotationClass, currentClass, fieldList);
		}

		return fieldList.toArray(new Field[fieldList.size()]);
	}

	/**
	 * 加载本类中拥有指定注解的域，范围只限于本类。
	 * 
	 * @param annotationClass
	 * @param clazz
	 * @param fieldList
	 */
	private static void loadFields(Class<? extends Annotation> annotationClass, Class<?> clazz, List<Field> fieldList) {
		Field[] fields = clazz.getDeclaredFields();
		if (fields == null) {
			return;
		}

		for (Field field : fields) {
			Annotation annotation = field.getAnnotation(annotationClass);
			if (annotation != null) {
				fieldList.add(field);
			}
		}
	}
	
	public static <T extends Annotation> T getAnnotation(Class<?> clazz, 
			Class<T> annoClass) {
		T anno = clazz.getAnnotation(annoClass);
		if (anno != null) {
			return anno;
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return null;
		}
		return getAnnotation(superClass, annoClass);
	}

	public static Method getMethod(Class<? extends Annotation> annotationClass, Class<?> clazz) {

		Method[] methods = clazz.getDeclaredMethods();
		if (methods == null) {
			return null;
		}

		for (Method method : methods) {
			Annotation annotation = method.getAnnotation(annotationClass);
			if (annotation != null) {
				return method;
			}
		}

		return null;
	}

	public static Method[] getMethods(Class<? extends Annotation> annotationClass, Class<?> clazz) {
		List<Method> methodList = new ArrayList<Method>();

		Method[] methods = clazz.getDeclaredMethods();
		if (methods == null) {
			return null;
		}

		for (Method method : methods) {
			Annotation annotation = method.getAnnotation(annotationClass);
			if (annotation != null) {
				methodList.add(method);
			}
		}

		return methodList.toArray(new Method[methodList.size()]);
	}

	public static final String toIdentify(Method method) {
		StringBuffer sb = new StringBuffer();
		sb.append(method.getDeclaringClass().getName()).append("#").append(method.getName()).append("(");
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes != null && paramTypes.length > 0) {
			String paramsText = Joiner.on(",").join(paramTypes);
			sb.append(paramsText);
		}
		sb.append(")");
		return sb.toString();
	}

	public static boolean equals(Method m1, Method m2) {
		if (m1 == null || m2 == null) {
			return false;
		}
		if (!m1.getName().equals(m2.getName())) {
			return false;
		}
		Class<?>[] m1t = m1.getParameterTypes();
		Class<?>[] m2t = m2.getParameterTypes();
		if (m1t == null && m2t == null) {
			return true;
		}
		if (m1t == null || m2t == null || m1t.length != m2t.length) {
			return false;
		}
		for (int i = 0; i < m1t.length; i++) {
			if (!m1t[i].equals(m2t[i])) {
				return false;
			}
		}
		return true;
	}
}
