package com.terran4j.commons.api2doc.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.util.value.KeyedList;

@Service
public class Api2DocService {
	
	private final KeyedList<String, ApiFolderObject> folders = new KeyedList<>();
	
	public boolean hasFolder(String id) {
		return folders.containsKey(id);
	}
	
	public ApiFolderObject getFolder(String id) {
		return folders.get(id);
	}
	
	public void addFolder(ApiFolderObject folder) {
		if (folder == null) {
			throw new NullPointerException("ApiFolderObject is null");
		}
		folders.add(folder.getId(), folder);
	}
	
	public List<ApiFolderObject> getFolders() {
		return folders.getAll();
	}

    public String getVersion() {
        return String.valueOf(System.currentTimeMillis());
    }


    public String addVersion(String path) {
        return path + "?v=" + getVersion();
    }
	
}
