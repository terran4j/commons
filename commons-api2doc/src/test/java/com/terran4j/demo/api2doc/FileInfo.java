package com.terran4j.demo.api2doc;

import com.terran4j.commons.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileInfo {

    private String name;

    private String content;

    private String msg;

    public FileInfo() {
    }

    public static FileInfo parse(MultipartFile file, String name) throws IOException {
        String fileName = file.getOriginalFilename();
        String content = Strings.getString(file.getInputStream());
        String msg = "requestPart, file = " + fileName;
        return new FileInfo(name, content, msg);
    }

    public FileInfo(String name, String content, String msg) {
        this.name = name;
        this.content = content;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
