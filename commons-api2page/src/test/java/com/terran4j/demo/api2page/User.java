package com.terran4j.demo.api2page;

import com.terran4j.commons.api2page.annotation.Field;
import com.terran4j.commons.api2page.annotation.Form;
import com.terran4j.commons.api2page.annotation.TableColumn;
import com.terran4j.commons.api2page.annotation.WidgetInput;

@Form(layout = {
        "name,job",
        "minAge,maxAge"
})
public class User {

    @TableColumn
    @WidgetInput
    @Field(name = "名称")
    private String name;

    @TableColumn(orderable = true)
    @WidgetInput
    @Field(name = "年龄")
    private int age;

    @TableColumn
    @WidgetInput
    @Field(name = "职业")
    private String job;

    @TableColumn
    @WidgetInput
    @Field(name = "职业")
    private String headImageURL;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
