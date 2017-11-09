package com.terran4j.demo.api2page;

import com.terran4j.commons.api2page.ListQuery;
import com.terran4j.commons.api2page.annotation.Field;
import com.terran4j.commons.api2page.annotation.Form;
import com.terran4j.commons.api2page.annotation.WidgetInput;
import com.terran4j.commons.api2page.annotation.WidgetSelect;

@Form(layout = {
        "name,job",
        "minAge,maxAge"
})
public class UserQuery extends ListQuery {

    @WidgetInput()
    @Field(name = "姓名")
    private String name;

    @WidgetInput()
    @Field(name = "最小年龄")
    private int minAge;

    @WidgetInput()
    @Field(name = "最大年龄")
    private int maxAge;

    @WidgetSelect()
    @Field(name = "职业")
    private String job;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

}
