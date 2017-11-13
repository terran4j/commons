package com.terran4j.demo.commons.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.restpack.RestPackIgnore;

import java.util.Date;

public class User {

    @Api2Doc(order = 10)
    @ApiComment(value = "账号id", sample = "123")
    private Long id;

    @Api2Doc(order = 20)
    @ApiComment(value = "账号用户名", sample = "terran4j")
    private String username;

    @Api2Doc(order = 30)
    @ApiComment(value = "账号密码", sample = "sdfi23skvs")
    private String password;

    @Api2Doc(order = 30)
    @ApiComment(value = "用户状态", sample = "open")
    private UserState state;

    @Api2Doc(order = 40)
    @ApiComment(value = "是否已删除", sample = "true")
    @RestPackIgnore
    private Boolean deleted;

    @Api2Doc(order = 50)
    @ApiComment(value = "创建时间")
    private Date createTime;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
