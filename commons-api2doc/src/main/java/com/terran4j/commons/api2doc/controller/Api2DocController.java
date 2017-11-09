package com.terran4j.commons.api2doc.controller;

import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.impl.Api2DocService;
import com.terran4j.commons.api2doc.impl.ClasspathFreeMarker;
import com.terran4j.commons.api2doc.impl.MarkdownService;
import com.terran4j.commons.util.IOUtils;
import com.terran4j.commons.util.Strings;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
@RequestMapping(value = "/api2doc")
public class Api2DocController {

    private static final Logger log = LoggerFactory.getLogger(Api2DocController.class);

    @Value("${service.name:}")
    private String serviceName;

    @Value("${api2doc.title:}")
    private String api2docTitle;

    @Value("${api2doc.icon:}")
    private String api2docIcon;

    @Autowired
    private MarkdownService markdownService;

    @Autowired
    private Api2DocService apiDocService;

    @Autowired
    private ClasspathFreeMarker freeMarker;

    @Autowired
    private ConfigurableApplicationContext context;

    private Template homeTemplate = null;

    private Template mdTemplate = null;

    private Template docTemplate = null;

    @PostConstruct
    public void init() {
        try {
            mdTemplate = freeMarker.getTemplate(getClass(), "doc.md.ftl");
            homeTemplate = freeMarker.getTemplate(getClass(), "home.html");
            docTemplate = freeMarker.getTemplate(getClass(), "doc.html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * http://localhost:8080/api2doc/home.html
     * 整个文档页面，包含顶部标题栏、左侧菜单栏、右侧用 iframe 嵌入的内容区。
     */
    @RequestMapping(value = "/home.html", method = RequestMethod.GET)
    public void home(@RequestParam(value = "p", required = false) String p,
                     HttpServletResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();

        String title = api2docTitle;
        if (StringUtils.isEmpty(title)) {
            if (StringUtils.hasText(serviceName)) {
                title = serviceName.trim() + "——接口文档";
            }
        }
        if (StringUtils.isEmpty(title)) {
            title = "Api2Doc 接口文档";
        }
        model.put("title", title);

        String icon = api2docIcon;
        if (StringUtils.hasText(icon)) {
            model.put("icon", icon);
        }

        List<MenuData> menus = getMenuGroups();
        model.put("menus", menus);

        // 当前要显示的内容。
        String docPath = null;
        if (StringUtils.hasText(p)) {
            int pos = p.indexOf("-");
            String fid = p.substring(0, pos);
            String id = p.substring(pos + 1);
            docPath = String.format("/api2doc/api/%s/%s.html", fid, id);
        } else {
            docPath = "/api2doc/welcome.html";
        }
        model.put("docPath", docPath);

        p = p == null ? "" : p;
        model.put("p", p);

        String html = freeMarker.build(homeTemplate, model);
        writeHtml(html, response);
    }

    /**
     * http://localhost:8080/api2doc/welcome.html
     * 文档首页内容。
     */
    @RequestMapping(value = "/welcome.html", method = RequestMethod.GET)
    public void welcome(HttpServletResponse response) throws Exception {
        writeMdByPath("welcome.md", response);
    }

    public void writeMdByPath(String path, HttpServletResponse response) throws Exception {
        if (StringUtils.isEmpty(path)) {
            throw new NullPointerException("path is null.");
        }
        path = path.trim();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        path = "api2doc" + path;

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String md = Strings.getResourceByPath(path, loader);
        writeMd(md, null, response);
    }

    /**
     * http://localhost:8080/api2doc/overview.html
     */
    @RequestMapping(value = "/md/{folderId}/{docId}.html", method = RequestMethod.GET)
    public void md(@PathVariable("folderId") String folderId,
                   @PathVariable("docId") String docId,
                   HttpServletResponse response) throws Exception {
        ApiFolderObject folder = apiDocService.getFolder(folderId);
        if (folder == null) {
            log.warn("ApiFolder NOT Found: {}", folderId);
            return;
        }

        Map<String, String> mds = folder.getMds();
        if (mds == null || !mds.containsKey(docId)) {
            log.warn("Markdown doc {} NOT Found in Folder: {}", docId, folderId);
            return;
        }

        String fileName = mds.get(docId);
        String path = folderId + "/" + fileName;
        writeMdByPath(path, response);
    }

    private List<MenuData> getMenuGroups() {
        List<MenuData> menuGroups = new ArrayList<>();

        List<ApiFolderObject> folders = apiDocService.getFolders();
        if (folders == null || folders.size() == 0) {
            return menuGroups;
        }

        for (ApiFolderObject folder : folders) {
            MenuData menuGroup = getMenuGroup(folder);
            menuGroups.add(menuGroup);
        }

        Collections.sort(menuGroups, new MenuComparator());

        return menuGroups;
    }

    private MenuData getMenuGroup(ApiFolderObject folder) {
        String folderId = folder.getId();
        String folderName = folder.getName();

        MenuData menuGroup = new MenuData();
        menuGroup.setId(folderId);
        menuGroup.setIndex(folderId);
        menuGroup.setName(folderName);
        menuGroup.setFolder(true);
        menuGroup.setOrder(folder.getOrder());

        List<MenuData> children = new ArrayList<>();

        Map<String, String> mds = folder.getMds();
        if (mds != null && mds.size() > 0) {
            for (String md : mds.values()) {
                MenuData menu = getMenu(md, folderId);
                children.add(menu);
            }
        }

        List<ApiDocObject> docs = folder.getDocs();
        if (docs != null) {
            for (ApiDocObject doc : docs) {
                MenuData menu = getMenu(doc, folderId);
                children.add(menu);
            }
        }

        Collections.sort(children, new MenuComparator());
        menuGroup.setChildren(children);
        return menuGroup;
    }

    private MenuData getMenu(String mdFileName, String folderId) {
        int offset = mdFileName.indexOf("-");
        String orderText = mdFileName.substring(0, offset);
        int order = Integer.parseInt(orderText);
        String docName = mdFileName.substring(offset + 1,
                mdFileName.length() - ".md".length());
        String docId = ApiFolderObject.name2Id(mdFileName);
        MenuData menu = new MenuData();
        menu.setId(folderId + "-" + docId);
        menu.setIndex(folderId + "-" + docId);
        menu.setFolder(false);
        menu.setName(docName);
        menu.setOrder(order);
        menu.setUrl("/api2doc/md/" + folderId + "/" + docId + ".html");
        return menu;
    }

    private MenuData getMenu(ApiDocObject doc, String folderId) {
        MenuData menu = new MenuData();
        menu.setId(folderId + "-" + doc.getId());
        menu.setIndex(folderId + "-" + doc.getId());
        menu.setFolder(false);
        menu.setName(doc.getName());
        menu.setOrder(doc.getOrder());
        menu.setUrl("/api2doc/api/" + folderId + "/" + doc.getId() + ".html");
        return menu;
    }

    @RequestMapping(value = "/css/{name}.css", method = RequestMethod.GET)
    public void css(@PathVariable("name") String name, HttpServletResponse response) throws Exception {
        writeResource(response, name + ".css",
                "text/css; charset=utf-8");
    }

    @RequestMapping(value = "/css/element-icons.ttf", method = RequestMethod.GET)
    public void fontOfTtf(HttpServletResponse response) throws Exception {
        writeResource(response, "element-icons.ttf",
                "application/x-font-ttf");
    }

    @RequestMapping(value = "/css/element-icons.woff", method = RequestMethod.GET)
    public void fontOfWoff(HttpServletResponse response) throws Exception {
        writeResource(response, "element-icons.woff",
                "application/x-font-woff");
    }

    @RequestMapping(value = "/js/{name}.js", method = RequestMethod.GET)
    public void js(@PathVariable("name") String name, HttpServletResponse response)
            throws Exception {
        writeResource(response, name + ".js",
                "application/javascript; charset=utf-8");
    }

    private void writeResource(HttpServletResponse response, String fileName,
                               String contentType) throws IOException {
        InputStream in = IOUtils.getInputStream(getClass(), fileName);
        if (in == null) {
            return;
        }
        long expires = System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 30;
        response.setDateHeader("expires", expires);
        response.setContentType(contentType);
        try {
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // ignore.
            }
        }
    }

    /**
     * http://localhost:8080/api2doc/overview.html
     */
    @RequestMapping(value = "/api/{fid}/{id}.html", method = RequestMethod.GET)
    public void apidoc(@PathVariable("fid") String folderId,
                       @PathVariable("id") String id,
                       HttpServletResponse response) throws Exception {

        ApiFolderObject folder = apiDocService.getFolder(folderId);
        if (folder == null) {
            log.warn("ApiFolder NOT Found: {}", folderId);
            return;
        }

        ApiDocObject doc = folder.getDoc(id);
        if (doc == null) {
            log.warn("ApiDoc NOT Found: {}", folderId);
            return;
        }

        String md = toDoc(folder, doc);
        String title = doc.getName();
        writeMd(md, title, response);
    }

    private void writeMd(String md, String title, HttpServletResponse response)
            throws Exception {

        if (log.isInfoEnabled()) {
            log.info("render md:\n{}", md);
        }
        if (StringUtils.isEmpty(md)) {
            return;
        }
        String content = markdownService.md2Html(md);

        Map<String, Object> model = new HashMap<String, Object>();
        if (title != null) {
            model.put("title", title);
        }
        model.put("content", content);
        String html = freeMarker.build(docTemplate, model);

        writeHtml(html, response);
    }

    private String toDoc(ApiFolderObject folder, ApiDocObject doc) {
        if (folder == null || doc == null) {
            return null;
        }

        try {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("folder", folder);
            model.put("doc", doc);

            String folderId = folder.getId();
            String upFirst = folderId.substring(0, 1).toUpperCase() +folderId.substring(1);
            String folderClasses = upFirst + "Service / " + upFirst + "Retrofit";
            model.put("folderClasses", folderClasses);

            String content = freeMarker.build(mdTemplate, model);
            return content;
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeHtml(String html, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(html);
    }

}