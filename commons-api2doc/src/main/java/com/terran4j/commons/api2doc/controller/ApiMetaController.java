package com.terran4j.commons.api2doc.controller;

import com.terran4j.commons.api2doc.meta.ApiMetaService;
import com.terran4j.commons.api2doc.meta.ClassMeta;
import com.terran4j.commons.restpack.RestPackController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RestPackController
@RequestMapping(value = "/api2doc/meta")
public class ApiMetaController {

    @Autowired
    private ApiMetaService apiMetaService;

    @RequestMapping(value = "/classes", method = RequestMethod.GET)
    public List<ClassMeta> getClassMetaList() throws Exception {
        return apiMetaService.toClassMetaList();
    }

    @RequestMapping(value = "/apiInfo/{fid}/{id}", method = RequestMethod.GET)
    public ApiInfo getApiInfo(@PathVariable String fid, @PathVariable String id) throws Exception {
        return apiMetaService.toApiInfo(fid, id);
    }

}