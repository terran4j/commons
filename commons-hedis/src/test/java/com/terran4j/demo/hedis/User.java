package com.terran4j.demo.hedis;

import com.terran4j.commons.util.Strings;

import java.util.Date;

public class User {

    private long id;

    private String name;

    private Date birthday;

    public User() {
    }

    public User(long id, String name, Date birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return Strings.toString(this);
    }
}
