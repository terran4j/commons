package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.controller.MenuData;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DocMenuBuilder {

    @Autowired
    private Api2DocService apiDocService;


    public List<MenuData> getMenuGroups() {
        List<MenuData> menuGroups = new ArrayList<>();

        List<ApiFolderObject> folders = apiDocService.getFolders();
        if (folders == null || folders.size() == 0) {
            return menuGroups;
        }

        for (ApiFolderObject folder : folders) {
            MenuData menuGroup = getMenuGroup(folder);
            menuGroups.add(menuGroup);
        }

        Collections.sort(menuGroups);

        return menuGroups;
    }

    public MenuData getMenuGroup(ApiFolderObject folder) {
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

        Collections.sort(children);
        menuGroup.setChildren(children);
        return menuGroup;
    }

    public MenuData getMenu(String mdFileName, String folderId) {
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

    public MenuData getMenu(ApiDocObject doc, String folderId) {
        MenuData menu = new MenuData();
        menu.setId(folderId + "-" + doc.getId());
        menu.setIndex(folderId + "-" + doc.getId());
        menu.setFolder(false);
        menu.setName(doc.getName());
        menu.setOrder(doc.getOrder());
        menu.setUrl("/api2doc/api/" + folderId + "/" + doc.getId() + ".html");
        return menu;
    }
}
