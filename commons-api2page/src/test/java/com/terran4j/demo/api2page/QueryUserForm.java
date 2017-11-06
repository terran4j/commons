package com.terran4j.demo.api2page;

import com.terran4j.commons.api2.ApiInfo;
import com.terran4j.commons.api2page.FormItemType;
import com.terran4j.commons.api2page.UIFormItem;

import java.util.Date;

public class QueryUserForm {

    @UIFormItem(type = FormItemType.Input)
    @ApiInfo(name = "姓名")
    private String name;

    @UIFormItem(type = FormItemType.Date)
    @ApiInfo(name = "出生日期")
    private Date birthday;

    @UIFormItem(type = FormItemType.Select)
    @ApiInfo(name = "职业")
    private String job;

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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

}
