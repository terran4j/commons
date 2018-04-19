package com.terran4j.commons.api2doc.controller;

import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.impl.Api2DocProperties;
import com.terran4j.commons.api2doc.impl.Api2DocService;
import com.terran4j.commons.api2doc.impl.DocMenuBuilder;
import com.terran4j.commons.api2doc.impl.DocPageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api2doc")
public class Api2DocController {

    private static final Logger log = LoggerFactory.getLogger(Api2DocController.class);

    @Autowired
    private DocMenuBuilder docMenuBuilder;

    @Autowired
    private DocPageBuilder docPageBuilder;

    @Autowired
    private Api2DocService apiDocService;

    @Autowired
    private Api2DocProperties api2DocProperties;

    /**
     * http://localhost:8080/api2doc/home.html
     * 整个文档页面，包含顶部标题栏、左侧菜单栏、右侧用 iframe 嵌入的内容区。
     */
    @RequestMapping(value = "/home.html", method = RequestMethod.GET)
    public String home(@RequestParam(value = "p", required = false) String p,
                       Map<String, Object> model) throws Exception {

        String title = api2DocProperties.getApi2docTitle();
        if (StringUtils.isEmpty(title)) {
            String serviceName = api2DocProperties.getServiceName();
            if (StringUtils.hasText(serviceName)) {
                title = serviceName.trim() + "——接口文档";
            }
        }
        if (StringUtils.isEmpty(title)) {
            title = "Api2Doc 接口文档";
        }
        model.put("title", title);

        String icon = api2DocProperties.getApi2docIcon();
        if (StringUtils.hasText(icon)) {
            model.put("icon", icon);
        }

        List<MenuData> menus = docMenuBuilder.getMenuGroups();
        model.put("menus", menus);

        // 当前要显示的内容。
        String docPath = getDocPath(p);
        model.put("docPath", docPath);
        model.put("v", apiDocService.getComponentVersion());

        p = p == null ? "" : p;
        model.put("p", p);

        if (log.isInfoEnabled()) {
            log.info("request home.html, model:\n{}", model);
        }
        return "api2doc/home";
    }

    private String getDocPath(String p) {
        String docPath = null;
        if (StringUtils.hasText(p)) {
            String[] strs = p.split("-");
            if (strs.length >= 3) {
                String docType = strs[0];
                String docGroup = strs[1];
                String docId = strs[2];
                docPath = String.format("/api2doc/%s/%s/%s.html",
                        docType, docGroup, docId);
            }
        }

        if (docPath == null) {
            docPath = "/api2doc/welcome.html";
        }

        return apiDocService.addAppDocVersion(docPath);
    }

    /**
     * http://localhost:8080/api2doc/welcome.html
     * 文档首页内容。
     */
    @RequestMapping(value = "/welcome.html", method = RequestMethod.GET)
    public String welcome(Map<String, Object> model) throws Exception {
        String md = docPageBuilder.loadMdFromResource("welcome.md");
        return md2HtmlPage(md, null, model);
    }

    /**
     * http://localhost:8080/api2doc/overview.html
     */
    @RequestMapping(value = "/md/{folderId}/{docId}.html", method = RequestMethod.GET)
    public String md(@PathVariable("folderId") String folderId,
                     @PathVariable("docId") String docId,
                     Map<String, Object> model) throws Exception {
        String md = docPageBuilder.loadMdFromResource(folderId, docId);
        return md2HtmlPage(md, null, model);
    }

    @RequestMapping(value = "/api/{fid}/{id}.html", method = RequestMethod.GET)
    public String api2doc(
            @PathVariable("fid") String folderId, @PathVariable("id") String id,
            Map<String, Object> model) throws Exception {
        ApiDocObject doc = docPageBuilder.getDocObject(folderId, id);
        String md = docPageBuilder.doc2Md(doc);
        String title = doc.getName();
        return md2HtmlPage(md, title, model);
    }

    public String md2HtmlPage(String md, String title,
                              Map<String, Object> model) throws Exception {
        if (title != null) {
            model.put("title", title);
        }
        String html = docPageBuilder.md2Html(md);
        model.put("content", html);

        model.put("v", apiDocService.getComponentVersion());

        return "api2doc/doc";
    }

}