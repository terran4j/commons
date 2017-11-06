package com.terran4j.commons.util.reflect;

import com.terran4j.commons.util.Classes;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * 判断接口或父接口是否有指定接口的过滤器。
 */
public class InterfaceFilter implements TypeFilter {

    private final Class<?> interfaceClass;

    public InterfaceFilter(Class<?> interfaceClass) {
        super();
        if (interfaceClass == null || !interfaceClass.isInterface()) {
            throw new InvalidParameterException(interfaceClass
                    + " is NOT an interface class.");
        }
        this.interfaceClass = interfaceClass;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        ClassMetadata classMeta = metadataReader.getClassMetadata();
        if (!classMeta.isInterface()) {
            return false;
        }
        String[] interfaceNames = classMeta.getInterfaceNames();
        if (interfaceNames == null || interfaceNames.length == 0) {
            return false;
        }
        for (String interfaceName : interfaceNames) {
            if (isContainInterface(interfaceName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isContainInterface(String interfaceName) {
        if (interfaceClass.getName().equals(interfaceName)) {
            return true;
        }

        Class<?> currentClass = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            currentClass = classLoader.loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Classes.isInterfaceExtends(currentClass, interfaceClass);
    }

}
