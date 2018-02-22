
## 目录

* 项目背景
* Api2Doc 简介
* 引入 Api2Doc 依赖
* 启用 Api2Doc 服务
* 给 Controller 类上添加文档注解
* @Api2Doc 注解详述
* @ApiComment 注解详述
* 给文档菜单项排序
* 补充自定义文档
* 定制文档的欢迎页
* 定制文档的标题及图标
* 关闭 Api2Doc 服务


## 项目背景

在互联网/移动互联网软件的研发过程中，大多数研发团队前后台分工是非常明确的，
后台工程师负责服务端系统的开发，一般是提供 HTTP/HTTPS 的 API 接口，
前端工程师则负责 Android、iOS、H5页面的开发，需要调用 API 接口。

这就需要有一套 API 文档，以帮助两方在 API 接口进行沟通，并达成一致意见。
一般情况下，编写文档的工作都会落在后台工程师身上，毕竟 API 是他们提供的嘛。

但问题是，编写 API 文档是一件既繁琐、又费时、还对提高技术能力没啥帮助的苦差事，
尤其在是快速迭代、需求频繁修改的项目中，改了代码还要同步改文档，
哪点改错了或改漏了都可能产生前后端实现的不一致，
导致联调时发现 BUG，这个锅最终还是要后台工程师来背（宝宝心里苦啊...）。

因此，业界就出现了一些帮助自动生成文档的开源项目，
与 Spring Boot 结合比较好的是 Swagger2，
Swagger2 通过读取 Controller 代码中的注解信息，来自动生成 API 文档，
可以节省大量的手工编写文档的工作量。

本项目作者之前也是用的 Swagger2，但发现 Swagger2 也有好多问题：

第一，Swagger2 的注解非常臃肿，我们看下这段代码：

```java

@RestController
@RequestMapping(value = "/user3")
public class UserController2Swagger2 {

    @ApiOperation(value = "获取指定id用户详细信息",
            notes = "根据user的id来获取用户详细信息",
            httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名",
                    paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "用户密码",
                    paramType = "query", required = true, dataType = "String")
    })
    @RequestMapping(name = "用户注册", value = "/regist",
            method = RequestMethod.GET)
    public UserInfo regist(@RequestParam("userName") String userName,
                           @RequestParam("password") String password) {
        return new UserInfo();
    }
}
```

@ApiOperation、@ApiImplicitParam 都是 Swagger2 提供的注解，用于定义 API 信息。
其实，API 方法本身就包含了很多信息，如HTTP Method、参数名、参数类型等等，
像 @ApiImplicitParam 中除了 value 属性有用外，其它都是重复描述。

第二，Swagger2 的页面排版不太友好，它是一个垂直排列的方式，不利于信息的展示。
并且看 API 详细信息还要一个个展开，中间还夹杂着测试的功能，反正作为文档是不易于阅读；
至于作为测试工具嘛...，现在专业的测试工具也有很多，测试人员好像也不选它。

第三，Swagger2 还有好多细节没做好，比如看这个图：

![swgger2-1.png](http://upload-images.jianshu.io/upload_images/4489584-575b3f94d746d921.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

红框中的 API 其实对应的是同一个方法，之所以有这么多，
只是因为写这个方法时没有指定 method：

```java
@RequestMapping(value = "/do_something")
public void doSomethingRequiredLogon() {
}
``` 

（当没指定 method 时，Spring Boot 会默认让这个接口支持所有的 method）

因此，考虑到与其长长久久忍受 Swagger2 的各种不爽，
不如花些时间自己做一个更好用的“自动化文档系统”，
于是就诞生了本项目： Api2Doc 。 


## Api2Doc 简介

Api2Doc 专注于 Http API 文档的自动生成，
它的原理与 Swagger2 是类似的，都是通过反射的方式，分析 Controller 中的信息生成文档。
但它的易用性要比 Swagger2 好很多。

举个例子： 虽然 Api2Doc 也需要开发者在代码上添加注解，但比起 Swagger2 来看少了很多，
我们看下使用 Api2Doc 注解修饰后的代码：

```java
@Api2Doc(id = "users2", name = "用户接口")
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo2")
public class UserController2 {

    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name, UserType type) {
        return null; // TODO:  还未实现。
    }
    
    // 其它方法，这里省略...
}
```

看，方法上仅加了一行 @ApiComment 注解代码，但生成的文档可一点不含糊：


![api2doc-2-1.png](http://upload-images.jianshu.io/upload_images/4489584-98f94cb360c0ccde.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![api2doc-2-2.png](http://upload-images.jianshu.io/upload_images/4489584-fedf2897f5c217b1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

有的朋友可能会觉得很奇怪：文档页面上的说明、示例值等内容，
在代码中没有写啊，这些是哪来的呢？

这里涉及到 Api2Doc 的核心设计理念，就是：
它尽可能多的去“分析和推断”，自动补全文档所需的信息，从而让用户少写。

说得有点抽象哈，下面我们来回答下这个问题，请大家注意这个类上有一个注解：

```java
@ApiComment(seeClass = User.class)
```

它意思是： 在 API 方法上遇到没写说明信息时，请参照 User 类中的定义的说明信息。

下面是 User 类的代码：

```java
public class User {

    @ApiComment(value = "用户id", sample = "123")
    private Long id;

    @ApiComment(value = "用户名", sample = "terran4j")
    private String name;

    @ApiComment(value = "账号密码", sample = "sdfi23skvs")
    private String password;

    @ApiComment(value = "用户所在的组", sample = "研发组")
    private String group;

    @ApiComment(value = "用户类型", sample = "admin")
    private UserType type;

    @ApiComment(value = "是否已删除", sample = "true")
    @RestPackIgnore
    private Boolean deleted;

    @ApiComment(value = "创建时间\n也是注册时间。")
    private Date createTime;

    // 省略  getter / setter 方法。
}
```

大家看明白了没？ API 方法中的参数，如果与 User 类的属性同名的话，
就用类属性的 @ApiComment 说明信息自动填充。

其实这也符合实际的业务逻辑。
因为在大部分项目中，有的字段会在多个实体类、多个 API 方法中用到，
完全没有必要重复编写其说明信息，只要有一个地方定义好了，然后其它地方参照就行了。

当然，这只是 Api2Doc 比 Swagger2 好用的特性之一，
下面我们就来全面讲解它的用法，希望可以帮助开发者们从文档编写的苦海中解脱出来。

## 引入 Api2Doc 依赖

如果是 maven ，请在 pom.xml 中添加依赖，如下所示：

```xml
        <dependency>
            <groupId>com.github.terran4j</groupId>
            <artifactId>terran4j-commons-api2doc</artifactId>
            <version>${api2doc.version}</version>
        </dependency>
```

如果是 gradle，请在 build.gradle 中添加依赖，如下所示：

```groovy
compile "com.github.terran4j:terran4j-commons-api2doc:${api2doc.version}"
```

${api2doc.version} **最新稳定版，请参考 [这里](https://github.com/terran4j/commons/blob/master/version.md)**


## 启用 Api2Doc 服务

本教程的示例代码在 src/test/java 目录的 com.terran4j.demo.api2doc 中，
您也可以从 [这里](https://github.com/terran4j/commons/tree/master/commons-api2doc/src/test/java/com/terran4j/demo/api2doc) 获取到。

首先，我们需要在有 @SpringBootApplication 注解的类上，
添加 @EnableApi2Doc 注解，以启用 Api2Doc 服务，
如下代码所示：

```java
package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.config.EnableApi2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//  文档访问地址： http://localhost:8080/api2doc/home.html
@EnableApi2Doc
@SpringBootApplication
public class Api2DocDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(Api2DocDemoApp.class, args);
    }

}
``` 

## 给 Controller 类上添加文档注解

然后我们在 RestController 类添加 @Api2Doc 注解，
在需要有文档说明的地方添加 @ApiComment 注解即可，
如下所示：

```java
package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api2Doc(id = "demo1", name = "用户接口1")
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo1")
public class UserController1 {

    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name,
                        @ApiComment("用户类型") UserType type) {
        return null; // TODO:  还未实现。
    }
}
```

这个方法的返回类型 User 类的定义为：

```java
public class User {

    @ApiComment(value = "用户id", sample = "123")
    private Long id;

    @ApiComment(value = "用户名", sample = "terran4j")
    private String name;

    @ApiComment(value = "账号密码", sample = "sdfi23skvs")
    private String password;

    @ApiComment(value = "用户所在的组", sample = "研发组")
    private String group;

    @ApiComment(value = "用户类型", sample = "admin")
    private UserType type;

    @ApiComment(value = "是否已删除", sample = "true")
    @RestPackIgnore
    private Boolean deleted;

    @ApiComment(value = "创建时间\n也是注册时间。")
    private Date createTime;

    // 省略  getter / setter 方法。
}
```

以及 type 属性的类型，也就是 UserType 类的定义为：

```java
package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.ApiComment;

public enum UserType {

    @ApiComment("管理员")
    admin,

    @ApiComment("普通用户")
    user
}
```

编写好代码后，我们运行 main 函数，访问 Api2Doc 的主页面：

```
http://localhost:8080/api2doc/home.html
```

文档页面如下：

![api2doc-2.png](http://upload-images.jianshu.io/upload_images/4489584-7ebd93408d4ec409.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

说明 Api2Doc 服务起作用了，就是这么简单！


## @Api2Doc 注解详述

Api2Doc 一共就两个注解：@Api2Doc 及 @ApiComment。

@Api2Doc 用于对文档的生成进行控制。

@Api2Doc 修饰在类上，表示这个类会参与到文档生成过程中，
Api2Doc 服务会扫描 Spring 容器中所有的 Controller 类，
只有类上有 @Api2Doc 的类，才会被生成文档，
一个类对应于文档页面左侧的一级菜单项，@Api2Doc 的 name 属性则表示这个菜单项的名称。

@Api2Doc 也可以修饰在方法，不过在方法上的  @Api2Doc 通常是可以省略，
 Api2Doc 服务会扫描这个类的所有带有 @RequestMapping 的方法，
 每个这样的方法对应文档页面的左侧的二级菜单项，
 菜单项的名称取 @RequestMapping 的 name 属性，
 当然您仍然可以在方法上用  @Api2Doc 的 name 属性进行重定义。
 
 
 ## @ApiComment 注解详述
 
 @ApiComment 用于对 API 进行说明，它可以修饰在很多地方：
 * 修饰在类上，表示对这组 API 接口进行说明；
 * 修饰在方法上，表示对这个 API 接口进行说明；
 * 修饰在参数上，表示对这个 API 接口的请求参数进行说明；
 * 修饰在返回类型的属性上，表示对这个 API 接口的返回字段进行说明；
 * 修饰在枚举项上，表示对枚举项进行说明；

如果相同名称、相同意义的属性或参数字段，其说明已经在别的地方定义过了，
可以用 @ApiComment 的 seeClass 属性表示采用指定类的同名字段上的说明信息，
所以如这段代码：

```java
@Api2Doc(id = "demo1", name = "用户接口1")
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo1")
public class UserController1 {

    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name, UserType type) {
        return null; // TODO:  还未实现。
    }
}
```

虽然 group, name ,type 三个参数没有用 @ApiComment 进行说明，
但由于这个类上有 @ApiComment(seeClass = User.class) ，
因此只要 User 类中有 group, name ,type 字段并且有  @ApiComment 的说明就行了。


## 给文档菜单项排序

我们可以用 @Api2Doc 中的 order 属性给菜单项排序，
order 的值越小，该菜单项就越排在前面，
比如对于这段代码：

```java
package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api2Doc(id = "demo2", name = "用户接口2", order = 1)
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo2")
public class UserController2 {

    @Api2Doc(order = 10)
    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(
            @ApiComment("用户组名称") String group,
            @ApiComment("用户名称") String name,
            @ApiComment("用户类型") UserType type) {
        return null; // TODO:  还未实现。
    }

    @Api2Doc(order = 20)
    @ApiComment("根据用户id，查询此用户的信息")
    @RequestMapping(name = "查询单个用户",
            value = "/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        return null; // TODO:  还未实现。
    }

    @Api2Doc(order = 30)
    @ApiComment("查询所有用户，按注册时间进行排序。")
    @RequestMapping(name = "查询用户列表",
            value = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return null; // TODO:  还未实现。
    }

    @Api2Doc(order = 40)
    @ApiComment("根据指定的组名称，查询该组中的所有用户信息。")
    @RequestMapping(name = "查询用户组",
            value = "/group/{group}", method = RequestMethod.GET)
    public UserGroup getGroup(@PathVariable("group") String group) {
        return null; // TODO:  还未实现。
    }
}
```

显示的结果为：

![api2doc-3.png](http://upload-images.jianshu.io/upload_images/4489584-0818fdef543c8c07.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在类上的 @Api2Doc 同样可以给一级菜单排序，规则是一样的，这里就不演示了。


## 补充自定义文档

有时候光有自动生成的 API 文档似乎还不太完美，或许我们想补充点别的什么东西，
比如： 对项目的背景介绍、技术架构说明之类，那这个要怎么弄呢？

Api2Doc 允许用 md 语法手工编写文档，并集成到自动生成的 API 文档之中，
方法如下：

首先，要在类上的 @Api2Doc 定义 id 属性，比如对下面这个类：

```java
package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api2Doc(id = "demo3", name = "用户接口3")
@RestController
@RequestMapping(value = "/api2doc/demo3")
public class UserController3 {

    @Api2Doc(order = 10)
    @RequestMapping(name = "接口1", value = "/m1")
    public void m1() {
    }

    @Api2Doc(order = 20)
    @RequestMapping(name = "接口2", value = "/m2")
    public void m2() {
    }
}
``` 

@Api2Doc(id = "demo3", name = "用户接口3") 表示 id 为 demo3。

然后，我们在 src/main/resources 中创建目录  api2doc/demo3，
前面的 api2doc 是固定的，后面的 demo3 表示这个目录中的文档是添加到
id 为 demo3 的一级文档菜单下。

然后我们在 api2doc/demo3 目录中编写 md 格式的文档，如下图所示：

![api2doc-4.png](http://upload-images.jianshu.io/upload_images/4489584-a76a84061f2771d3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

文件名的格式为 ${order}-${docName}.md，
即 - 号前面的数字表示这个文档的排序，与 @Api2Doc 中的 order 属性是一样的，
而 - 号后面是文档名称，也就是二级菜单的名称。

因此，最后文档的显示效果为：

![api2doc-5.png](http://upload-images.jianshu.io/upload_images/4489584-73814ce5bde91b2d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

看，补充文档与 API 文档在排序上完美的组合在一起。


## 定制文档的欢迎页

每次访问文档页面 http://localhost:8080/api2doc/home.html 时，
中间的内容是非常简单的一句：

```
欢迎使用 Api2Doc ！
```

似乎有点不太好，不过没关系，我们可以编写自己的欢迎页。

方法很简单，在 src/main/resources 目录的 api2doc 目录下编写一个名为
welcome.md 的文件，然后用 md 语法编写内容就可以。


## 配置文档的标题及图标

可以在 application.yml 中配置文档系统的标题及图标，如下所示：

```yaml
api2doc:
  title: Api2Doc示例项目——接口文档
  icon: https://spring.io/img/homepage/icon-spring-framework.svg
```

图标为一个全路径的或本站点相对路径的 URL 就行。

配置后的显示效果为： 
![api2doc-6.png](http://upload-images.jianshu.io/upload_images/4489584-494a0c8042aaffb3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## 关闭 Api2Doc 服务 

您可以在不同环境下，选择关闭 Api2Doc 服务，如：

```yaml
# 本地环境
api2doc:
  title: Api2Doc示例项目——接口文档
  icon: https://spring.io/img/homepage/icon-spring-framework.svg

---
# 线上环境
spring:
  profiles: online

api2doc:
  enabled: false
```

api3doc.enabled 为 false 表示关闭 Api2Doc 服务，不写或为 true 表示启用。

由于  Api2Doc 服务没有访问权限校验，
建议您在受信任的网络环境（如公司内网）中才启用。