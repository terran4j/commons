package com.terran4j.commons.api2page;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UIFormItem {

    FormItemType type() default FormItemType.Auto;

}
