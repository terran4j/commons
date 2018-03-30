package com.terran4j.commons.api2doc.impl;

import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class MappingMethod {

    private final Method method;

    private MappingMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return requestMapping.name();
        }

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return getMapping.name();
        }

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return postMapping.name();
        }

        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return putMapping.name();
        }

        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            return patchMapping.name();
        }

        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return deleteMapping.name();
        }

        return null;
    }

    public RequestMethod[] getRequestMethod() {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            RequestMethod[] methods = requestMapping.method();
            if (methods == null || methods.length == 0) {
                return RequestMethod.values();
            }
            return methods;
        }

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return new RequestMethod[]{RequestMethod.GET};
        }

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return new RequestMethod[]{RequestMethod.POST};
        }

        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return new RequestMethod[]{RequestMethod.PUT};
        }

        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            return new RequestMethod[]{RequestMethod.PATCH};
        }

        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return new RequestMethod[]{RequestMethod.DELETE};
        }

        return RequestMethod.values();
    }

    public String[] getPath() {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return merge(requestMapping.path(), requestMapping.value());
        }

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return merge(getMapping.path(), getMapping.value());
        }

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return merge(postMapping.path(), postMapping.value());
        }

        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return merge(putMapping.path(), putMapping.value());
        }

        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            return merge(patchMapping.path(), patchMapping.value());
        }

        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return merge(deleteMapping.path(), deleteMapping.value());
        }

        return null;
    }

    private String[] merge(String[] strs1, String[] strs2) {
        Set<String> strSet = new HashSet<>();
        if (strs1 != null) {
            strSet.addAll(Arrays.asList(strs1));
        }
        if (strs2 != null) {
            strSet.addAll(Arrays.asList(strs2));
        }

        List<String> list = new ArrayList<>(strSet);
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }

    public static List<MappingMethod> getMappingMethods(Class<?> clazz) {
        List<MappingMethod> mappingMethods = new ArrayList<>();

        Method[] methods = clazz.getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return mappingMethods;
        }

        for (Method method : methods) {
            if (isMappingMethod(method)) {
                MappingMethod mappingMethod = new MappingMethod(method);
                mappingMethods.add(mappingMethod);
            }
        }

        return mappingMethods;
    }

    public static boolean isMappingMethod(Method method) {
        Annotation mapping = method.getAnnotation(RequestMapping.class);
        if (mapping != null) {
            return true;
        }

        mapping = method.getAnnotation(GetMapping.class);
        if (mapping != null) {
            return true;
        }

        mapping = method.getAnnotation(PostMapping.class);
        if (mapping != null) {
            return true;
        }

        mapping = method.getAnnotation(PutMapping.class);
        if (mapping != null) {
            return true;
        }

        mapping = method.getAnnotation(PatchMapping.class);
        if (mapping != null) {
            return true;
        }

        mapping = method.getAnnotation(DeleteMapping.class);
        if (mapping != null) {
            return true;
        }

        return false;
    }
}
