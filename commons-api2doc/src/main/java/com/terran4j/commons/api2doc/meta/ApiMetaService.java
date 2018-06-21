package com.terran4j.commons.api2doc.meta;

import com.terran4j.commons.api2doc.controller.ApiEntry;
import com.terran4j.commons.api2doc.controller.ApiInfo;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.api2doc.impl.Api2DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiMetaService {

    @Autowired
    private Api2DocService api2DocService;

    public ApiInfo toApiInfo(String folderId, String docId) throws Exception {
        ApiDocObject doc = api2DocService.getDocObject(folderId, docId);
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setUrl(doc.getPaths()[0]);
        apiInfo.setDefaultMethod(doc.getMethods()[0].name());

        List<ApiParamObject> apiParamObjects = doc.getParams();
        if (apiParamObjects != null && apiParamObjects.size() > 0) {
            List<ApiEntry> params = new ArrayList<>();
            List<ApiEntry> headers = new ArrayList<>();
            for (ApiParamObject apiParamObject : apiParamObjects) {
                ApiEntry entry = new ApiEntry();
                entry.setKey(apiParamObject.getId());
                entry.setValue(apiParamObject.getSample().getValue());
                ApiParamLocation paramLocation = apiParamObject.getLocation();
                if (paramLocation == ApiParamLocation.RequestParam) {
                    params.add(entry);
                } else if (paramLocation == ApiParamLocation.RequestHeader) {
                    headers.add(entry);
                }
            }
            apiInfo.setParams(params);
            apiInfo.setHeaders(headers);
        }

        return apiInfo;
    }

    public List<ClassMeta> toClassMetaList() {
        List<ClassMeta> classes = new ArrayList<>();
        List<ApiFolderObject> folderList = api2DocService.getFolders();
        if (folderList == null || folderList.isEmpty()) {
            return classes;
        }

        for(ApiFolderObject folder : folderList) {
            ClassMeta classMeta = toClassMeta(folder);
            classes.add(classMeta);
        }

        return classes;
    }

    public ClassMeta toClassMeta(ApiFolderObject folder) {
        ClassMeta classMeta = new ClassMeta();
        classMeta.setId(folder.getId());
        classMeta.setName(folder.getName());
        classMeta.setComment(folder.getComment().getValue());

        List<ApiDocObject> docs = folder.getDocs();
        if (docs != null && docs.size() > 0) {
            for(ApiDocObject doc : docs) {
                MethodMeta methodMeta = toMethodMeta(doc);
                classMeta.addMethod(methodMeta);
            }
        }

        return classMeta;
    }

    public MethodMeta toMethodMeta(ApiDocObject doc) {
        MethodMeta methodMeta = new MethodMeta();
        methodMeta.setId(doc.getId());
        methodMeta.setComment(doc.getComment().getValue());
        methodMeta.setName(doc.getName());
        methodMeta.setPaths(doc.getPaths());
        methodMeta.setRequestMethods(toRequestMethods(doc.getMethods()));

        List<ApiParamObject> params = doc.getParams();
        if (params != null && params.size() > 0) {
            for (ApiParamObject param : params) {
                ParamMeta paramMeta = toParamMeta(param);
                methodMeta.addParam(paramMeta);
            }
        }

        return methodMeta;
    }

    public ParamMeta toParamMeta(ApiParamObject param) {
        ParamMeta paramMeta = new ParamMeta();
        paramMeta.setId(param.getId());
        paramMeta.setComment(param.getComment().getValue());
        paramMeta.setDataType(param.getDataType().getName());
        paramMeta.setLocation(param.getLocation().name());
        paramMeta.setName(param.getName());
        paramMeta.setRequired(param.isRequired());
        return paramMeta;
    }

    private String[] toRequestMethods(RequestMethod[] methods) {
        if (methods == null || methods.length == 0) {
            return new String[]{};
        }

        String[] results = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            RequestMethod method = methods[i];
            results[i] = method.name();
        }
        return results;
    }
}
