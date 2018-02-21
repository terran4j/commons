
## 目录

* 项目背景
* RestPack 简介
* 引入 RestPack 依赖
* 启用 RestPack
* @RestPackController 注解
* RestPack 异常处理
* 日志输出
* 资源分享与技术交流


## 项目背景

在互联网、移动互联网、物联网繁荣的今天， 各种客户端设备层出不穷，
为了能用同一套服务端系统处理各种客户端的访问， HTTP Restful API 变得流行起来。

同时，一个研发团队前后台分工也越来越明确，这就需要有一套 API 文档，
让后台工程师与前端工程师在 API 接口的理解上达成一致意见，
而一般写文档的工作都会落在后台工程师身上，毕竟 API 是后台工程师提供的嘛。

但问题是，编写 API 文档是一件既繁琐、又费时、还对提高技术能力没啥帮助的苦差事，
尤其在是快速迭代、需求频繁修改的项目中，改了代码还要同步改文档，
哪点改错了或改漏了都可能产生前后端实现的不一致，
结果联调时发现 BUG，这个锅最终还是要后台工程师来背（宝宝心里苦，但宝宝不说...）。

因此，业界就出现了一些帮助自动生成文档的开源项目，
与 Spring Boot 结合比较好的是 swagger2，本项目作者之前也是用的 swagger2，
但发现 swagger2 也有好多问题：

第一，swagger2 的注解非常臃肿，我们看下这段代码：

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

其实，方法本身的定义就包含了很多信息，如HTTP Method、参数名、参数类型等等，
像 @ApiImplicitParam 中除了 value 属性有用外，其它都是重复的信息。

第二，swagger2 的页面排版不太友好，它是一个垂直排列的方式，不利于信息的展示。
并且看 API 详细信息还要一个个展开，中间还夹杂着测试的功能，反正作为文档是不易于阅读；
至于作为测试工具嘛...，现在专业的测试工具也有很多，测试人员好像也不选它。

第三，swagger2 还有好多细节没做好，比如看这个图：

![swgger2-1.png](http://upload-images.jianshu.io/upload_images/4489584-575b3f94d746d921.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

红框中的 API 其实对应的是同一个方法，之所以有这么多，
只是因为写这个方法时没有指定 method：

```java
@RequestMapping(value = "/do_something")
``` 

（Spring Boot 对没指定 method 时默认表示支持所有的 method）

因此，考虑到与其长长久久忍受 swagger2 的各种不爽，
不如花些时间自己做一个更好用的“自动化文档系统”，
于是就诞生了本项目： Api2Doc 。 


## Api2Doc 简介

Api2Doc 基于 Spring MVC 的基础上，专注于 Http API 文档的生成，
它通过反射的方式，提取 Controller 代码的信息，然后全自动的生成简洁的 API 文档，

虽然 Api2Doc 也需要开发者在代码上添加注解，但比起 swagger2 来看少了很多，
我们看下使用 Api2Doc 注解修饰后的代码：

```java

@Api2Doc(id = "users2", name = "用户接口")
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo2")
public class UserController2 {

    @ApiComment("根据用户id，查询此用户的信息")
    @RequestMapping(name = "查询单个用户",
            value = "/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        return null; // TODO:  还未实现。
    }

    @ApiComment("查询所有用户，按注册时间进行排序。")
    @RequestMapping(name = "查询用户列表",
            value = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return null; // TODO:  还未实现。
    }

    @ApiComment("根据指定的组名称，查询该组中的所有用户信息。")
    @RequestMapping(name = "查询用户组",
            value = "/group/{group}", method = RequestMethod.GET)
    public UserGroup getGroup(@PathVariable("group") String group) {
        return null; // TODO:  还未实现。
    }

    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name, UserType type) {
        return null; // TODO:  还未实现。
    }
}
```

看，每个方法仅加了一行 @ApiComment 注解代码，但生成的文档可一点不含糊：

![api2doc-1.png](http://upload-images.jianshu.io/upload_images/4489584-7121ff16d7e20d9b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

有的朋友可能会觉得很奇怪，比如这个接口：

```java
    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name, UserType type) {
        return null; // TODO:  还未实现。
    }
```

文档页面上有参数的中文名，但代码中没有定义啊，这是哪来的呢？

这里涉及到 Api2Doc 的核心设计理念，就是：
它尽可能多的去“分析和推断”，自动补全文档所需的信息，从而让用户少写。

具体我们来回答这个问题，请大家注意这个类上有一个注解：

```java
@ApiComment(seeClass = User.class)
```

它意思是： 在 API 方法上遇到没写注解的参数时，请参照 User 类中的定义。
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

大家明白了么？ API 方法中的参数，如果与 User 类的属性同名的话，
就用类属性的 @ApiComment 注解信息自动填充。
其实这也符合实际的业务逻辑，有的字段会在多个实体类、多个 API 方法中用到，
完全没有必要重复定义其文档的描述信息，只要有一个地方定义其它地方参照就行了。

Api2Doc 专注于文档的自动化生成，让大家花最小的成本，完成高质量的文档编写工作。

下面我们就来正式讲解它的用法。

## 引入 Api2Doc 依赖

```xml
        <dependency>
            <groupId>com.github.terran4j</groupId>
            <artifactId>terran4j-commons-api2doc</artifactId>
            <version>${terran4j.commons.version}</version>
        </dependency>
```

目前 terran4j-commons 的所有子项目都是按统一的版本号升级的，
**最新稳定版请参考 [首页](https://github.com/terran4j/commons) 的版本说明**

