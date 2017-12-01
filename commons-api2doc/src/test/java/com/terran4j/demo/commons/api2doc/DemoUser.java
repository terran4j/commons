package com.terran4j.demo.commons.api2doc;

import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.restpack.RestPackIgnore;

import java.util.Date;

public class DemoUser {

    @ApiComment(value = "用户id", sample = "123")
    private Long id;

    @ApiComment(value = "用户名", sample = "terran4j")
    private String name;

    @ApiComment(value = "账号密码", sample = "sdfi23skvs")
    private String password;

    @ApiComment(value = "用户状态", sample = "open")
    private UserState state;

    @ApiComment(value = "是否已删除", sample = "true")
    @RestPackIgnore
    private Boolean deleted;

    @ApiComment(value = "创建时间\n也是注册时间。")
    private Date createTime;

    @ApiComment(value = "头衔\n包括公司职位、学术职称、社会身份等",
            sample = "XXX公司创始人兼CEO\nYYY公司投资人\nZZZ大学客座教授")
    private String titles;

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    /**
     * 获取创建时间
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
