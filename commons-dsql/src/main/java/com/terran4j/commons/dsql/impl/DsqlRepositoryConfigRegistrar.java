package com.terran4j.commons.dsql.impl;

import com.terran4j.commons.dsql.DsqlExecutor;
import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.EnableDsqlRepositories;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.reflect.InterfaceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DsqlRepositoryConfigRegistrar implements ImportBeanDefinitionRegistrar,
        ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(DsqlRepositoryConfigRegistrar.class);

//    private static final String BEAN_NAME_DSQL_EXECUTOR = "dsqlExecutor";

    private static ApplicationContext applicationContext = null;

    private static DefaultListableBeanFactory beanFactory = null;

    private static DsqlExecutorImpl executor = null;

    public static final ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static DsqlExecutor getDsqlExecutor() {
        if (beanFactory == null) {
            throw new IllegalStateException("Spring BeanFactory NOT found, " +
                    "maybe spring is not started yet.");
        }

        if (executor != null) {
            return executor;
        }

        synchronized (DsqlRepositoryConfigRegistrar.class) {
            if (executor != null) {
                return executor;
            }
            JdbcTemplate jdbcTemplate = beanFactory.getBean(JdbcTemplate.class);
            executor = new DsqlExecutorImpl(jdbcTemplate);
            return executor;
        }
    }

    private final Set<Package> scannedPackages = new HashSet<>();

    private final Set<Package> scannedClasses = new HashSet<>();

    private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DsqlRepositoryConfigRegistrar.applicationContext = applicationContext;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                .getAnnotationAttributes(EnableDsqlRepositories.class.getName()));
        Class<?>[] basePackageClasses = attributes.getClassArray("value");
        if (basePackageClasses == null || basePackageClasses.length == 0) {
            basePackageClasses = attributes.getClassArray("basePackageClasses");
        }

        if (registry instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) registry;
            if (beanFactory == null) {
                beanFactory = factory; // 缓存 beanFactory 对象。
            }

            registerBeanDefinitions(factory, basePackageClasses);
        } else {
            String msg = String.format("Current registry %s is NOT the class: %s, " +
                            "maybe the Spring Version is incompatible.",
                    registry.getClass(), DefaultListableBeanFactory.class);
            throw new UnsupportedOperationException(msg);
        }
    }

    void registerBeanDefinitions(DefaultListableBeanFactory registry, Class<?>[] basePackageClasses) {
        if (basePackageClasses == null || basePackageClasses.length == 0) {
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("register DsqlRepositories on basePackageClasses: "
                    + basePackageClasses);
        }

        for (Class<?> basePackageClass : basePackageClasses) {
            Package currentPackage = basePackageClass.getPackage();
            if (scannedPackages.contains(currentPackage)) {
                continue;
            }
            Set<Class<?>> daoClasses = scanClasses(basePackageClass);
            if (daoClasses == null || daoClasses.size() == 0) {
                continue;
            }
            for (Class<?> daoClass : daoClasses) {
                if (scannedClasses.contains(daoClass)) {
                    continue;
                }
                registBean(registry, daoClass);
            }
        }
    }

    final void registBean(DefaultListableBeanFactory registry, Class<?> daoClass) {
        if (log.isInfoEnabled()) {
            log.info("regist DsqlRepository: {}", daoClass);
        }
        BeanDefinition annotationProcessor = BeanDefinitionBuilder
                .genericBeanDefinition(daoClass).getBeanDefinition();
        String beanName = beanNameGenerator.generateBeanName(annotationProcessor, registry);
        registerBeanDefinitionIfNotExists(registry, beanName, daoClass);
    }

    boolean registerBeanDefinitionIfNotExists(
            DefaultListableBeanFactory registry, String beanName, Class<?> beanClass) {
        if (registry.containsBeanDefinition(beanName)) {
            return false;
        }

        String[] candidates = registry.getBeanDefinitionNames();
        for (String candidate : candidates) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
                return false;
            }
        }

        Object bean = DsqlRepositoryProxy.createProxyObject(beanClass);
        registry.registerSingleton(beanName, bean);
        return true;
    }

    final Set<Class<?>> scanClasses(Class<?> basePackageClass) {
        InterfaceFilter filter = new InterfaceFilter(DsqlRepository.class);
        try {
            return Classes.scanClasses(basePackageClass, false,
                    filter, basePackageClass.getClassLoader());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
