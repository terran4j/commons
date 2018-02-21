

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

在互联网、移动互联网、车联网、物联网繁荣的今天，各种客户端设备层出不穷，为了能用同一套服务端程序处理各种客户端的访问，[ HTTP Restful API ](http://www.ruanyifeng.com/blog/2014/05/restful_api.html) 变得流行起来。

但是客户端与服务端交互时，往往会有一些通用的需求，比如：
 * 服务端在处理一个 API 请求时，如果出异常了，总是希望在请求的返回结果中给出一个明确的错误码，客户端可以根据错误码作进一步的处理。
* 为了方便排查问题，总是希望对于每个请求，服务端会返回一个 requestId，后台可以将这个请求产生的日志与这个 requestId 相关联。 这样一旦前后端联调时发现了问题，前端工程师只要给出 requestId ，后台工程师就可以拿着这个 requestId 快速找出相关日志，方便分析排查问题。
......

为了满足这些非功能性需求，笔者总结了之前很多项目的开发经验，归纳出一套统一的数据返回格式，如下（分成功和失败两种情况）：

成功响应内容：
```json
{
  "requestId" : "d56c24d006aa4d5e9b8903b3256bf3e3",
  "serverTime" : 1502592752449,
  "spendTime" : 5,
  "resultCode" : "success",
  "data" : {
    "key1": "value1",
    "key2": "value2"
  }
}
```
* requestId ： 服务端生成的请求唯一ID号，
    当这个请求有问题时，可以拿着这个 ID 号，
    在海量日志快速查询到此请求的日志信息，以方便排查问题。
* serverTime ： 服务器时间，
    很多场景下需要使用当前时间值，但客户端本地的时间有可能不准，
    因为这里返回服务器端时间供客户端使用。
* spendTime ： 本次请求在服务器端处理所消耗的时间，
    这里显示出来以方便诊断慢请求相关问题。
* resultCode ： 结果码，
    "success" 表示成功，其它表示一个错误的错误码，
    错误码的值及具体含意由项目中客户端与服务端约定。 
* data :  实际的业务数据，内容由每个 API 的业务逻辑决定。

错误响应内容：
```
{
  "requestId" : "d7ab68ac513e4549896aa33f0cda3518",
  "serverTime" : 1502594589673,
  "spendTime" : 8,
  "resultCode" : "name.duplicate",
  "message" : "昵称重复： terran4j，请换个昵称！",
  "props" : {
    "key1": "value1",
    "key2": "value2",
    ......
  }
}
```
与成功响应类似，都有 requestId、serverTime、spendTime 等字段。
不同的是 resultCode 是一个自定义的错误码，并且多了message 、props 两个字段：
* message ： 错误信息描述，
    是一段易于人理解的字符串信息，方便开发人员知晓错误原因。
* props ： 错误上下文相关属性，
    本项可选，有的错误码可能需要前端在程序中作进一步处理，
    所以后台可以在 props 中提供一些 key - value 的属性值，
    方便程序读取（而不是让前端程序从 message 中解析文本内容获取这些值）。


## RestPack 简介

若要让项目中每个 API 的实现都遵循这套统一的数据规范，
无疑要在每个API方法中编写一些重复性的代码。
因此笔者根据实际项目经验总结，开发了一套名为 **RestPack** 的工具包，
可以帮助 Restful API 的开发者将API 的返回结果自动包装成统一格式的报文。

RestPack 一词中， Rest 代表 Http Restful API 的意思，
而 Pack 是 "包装、包裹" 的意思，合起来的意思就是在原本的 Http Restful API 基础上，
将返回数据再包裹一层，以符合之前所讲的数据规范。

本文主要目标是介绍 RestPack 的用法。


## 引入 RestPack 依赖

然后，您就可以在您的项目的 pom.xml 文件中，引用 restpack 的 jar 包了，如下所示：
```
		<dependency>
			<groupId>terran4j</groupId>
			<artifactId>terran4j-commons-restpack</artifactId>
			<version>Virgo.0.1</version>
		</dependency>
```

整个 pom.xml 内容类似于：
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>terran4j</groupId>
	<artifactId>terran4j-demo-restpack</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>terran4j-demo-restpack</name>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.9.RELEASE</version>
	</parent>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>terran4j</groupId>
			<artifactId>terran4j-commons-restpack</artifactId>
			<version>Virgo.0.1</version>
		</dependency>
	</dependencies>

</project>
```
**目前 `terran4j-commons-restpack` 的最新稳定版是 Virgo.0.1 ，后续有新的稳定版本会更新到本文档中。**


## 启用 RestPack 

为了在应用程序中启用 RestPack，需要在 SpringBootApplication 类上加`@EnableRestPack` 注解，
整个 main 程序代码，如下所示：

```java
package com.terran4j.demo.restpack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.restpack.EnableRestPack;

@EnableRestPack
@SpringBootApplication
public class RestPackDemoApp {

	public static void main(String[] args) {
		SpringApplication.run(RestPackDemoApp.class, args);
	}

}
```
**加上 @EnableRestPack 才能启用 RestPack 的功能，否则本文下面所讲的效果都不会起作用。**


## @RestPackController 注解

以前实现 HTTP Restful API，就是用 Spring Boot MVC 编写一个 Controller 类，
并在类上加上 @RestController 注解
（对于这一点不清楚的读者，请先阅读笔者之前写过的
《[ Spring Boot快速入门 ](http://www.jianshu.com/nb/14688855?order_by=seq)》
一书，其中《[ Spring Boot MVC ](http://www.jianshu.com/p/e2d44f38287e)》
这章详细描述了这一点）。

要在原有的 Controller 类上启用 RestPack 功能，
仅仅是将类上的注解由 @RestController 改成 @RestPackController 就可以了，
代码如下所示：

```java
package com.terran4j.demo.restpack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.terran4j.commons.restpack.HttpResultPackController;
import com.terran4j.commons.util.error.BusinessException;

@RestPackController
@RequestMapping("/demo/restpack")
public class RestPackDemoController {
    
    private static final Logger log = LoggerFactory.getLogger(RestPackDemoController.class);

	@RequestMapping(value = "/echo", method = RequestMethod.GET)
	public String echo(@RequestParam(value = "msg") String msg) throws BusinessException {
	    log.info("echo, msg = {}", msg);
		return msg;
	}
	
}
```

编写好这个类后，我们启动 main 程序，然后浏览器输入URL：

```
http://localhost:8080/demo/restpack/echo?msg=abc
```
浏览器中显示结果为：
```json
{
  "requestId" : "2141d927f1de453ba3edd83306ecdf3e",
  "serverTime" : 1502597485688,
  "spendTime" : 21,
  "resultCode" : "success",
  "data" : "abc"
}
```

如果我们去掉 @EnableRestPack （或将 @RestPackController 还原成 @RestController），
再访问的结果仅为：

```
abc
```
说明 RestPack 可以将原本的返回数据，自动包装成我们定义的数据规范格式了。

对于无返回值的方法， RestPack 同样有效果，
比如我们在上面的 RestPackDemoController 类中添加如下方法：

```java
@RequestMapping(value = "/void", method = RequestMethod.GET)
public void doVoid(@RequestParam(value = "msg") String msg) throws BusinessException {
    log.info("doVoid, msg = {}", msg);
}
```

重启程序后在浏览器输入URL:
```
http://localhost:8080/demo/restpack/void?msg=abc
```
显示的结果如下:
```json
{
  "requestId" : "2df4aa14dfab46e196ebf7e79b2b35d6",
  "serverTime" : 1502627058784,
  "spendTime" : 35,
  "resultCode" : "success"
}
```

由于方法没有返回值，所以"data"字段也不出现了，但其它字段都有了。

如果返回值是自定义的复杂对象，RestPack 同样能转化成 json 格式放在 "data" 字段中，
比如我们再添加如下代码：

```java
@RequestMapping(value = "/hello", method = RequestMethod.GET)
public HelloBean hello(@RequestParam(value = "name") String name) throws BusinessException {
    log.info("hello, name = {}", name);
    HelloBean bean = new HelloBean();
    bean.setName(name);
    bean.setMessage("Hello, " + name + "!");
    bean.setTime(new Date());
    return bean;
}
```

类 HelloBean 的定义如下：

```java
package com.terran4j.demo.restpack;

import java.util.Date;

public class HelloBean {
	
	private String name;
	
	private String message;
	
	private Date time;

    // 省略 getter /setter 方法。
	
}

```
重启程序后在浏览器输入URL:
```
http://localhost:8080/demo/restpack/hello?name=neo
```

显示的结果如下:

```json
{
  "requestId" : "ab5c43c3415042b682b290e17fad1358",
  "serverTime" : 1502957833154,
  "spendTime" : 30,
  "resultCode" : "success",
  "data" : {
    "name" : "neo",
    "message" : "Hello, neo!",
    "time" : "2017-08-17 16:17:13"
  }
}
```

发现 "data" 中的字段与 HelloBean 的属性是对应的。


## RestPack 异常处理

当服务端抛出异常时，RestPack 会将异常包装成错误报文返回。

从客户端的角度来看，异常分两种：
* 一种是业务异常，
    如： 注册时用户名已存在、用户输入错误，等。这种情况下，
    客户端需要明确的异常原因及关键字段数据，
    以便于客户端程序知晓如何在界面上给予用户提示。
* 另一种是系统异常，
    如： 数据库无法访问、程序BUG，等。
    这种异常需要客户端模糊处理（尽量避免暴露系统本身的问题），
    比如弹出一个“对不起，系统开小差了”，
    或“系统维护中，请稍后重试”之类的提示。

RestPack 提供了一个叫 BusinessException 的异常类来代表业务异常，
如果方法抛出的异常类是 BusinessException 类或其子类，
RestPack 就按业务异常处理，如果不是就按系统异常处理。
为了查看运行效果，我们添加一个新的方法：

```java
@RequestMapping(value = "/regist", method = RequestMethod.GET)
public void regist(@RequestParam(value = "name") String name) throws BusinessException {
    log.info("regist, name = {}", name);
    if (name.length() < 3) {
        String suggestName = name + "123";
        throw new BusinessException("name.invalid")
                .setMessage("您输入的名称太短了，建议为：${suggestName}")
                .put("suggestName", suggestName);
    }
    log.info("regist done, name = {}", name);
}
```

在 BusinessException 类中，构造方法中的参数(如上面的 "name.invalid" ) 就是错误码，
 `put(String, Object)`   方法用于设置一些异常上下文属性，会出现在返回报文的 props 字段中，
`setMessage(String)`  方法用于设置异常信息，可以用 `${}` 来引用 `put` 方法出现的字段。

重启程序，在浏览器中访问URL:
```
http://localhost:8080/demo/restpack/regist?name=ne
```
结果如下：
```json
{
  "requestId" : "22e5651199f645628fdf724e9f0826a3",
  "serverTime" : 1502627761012,
  "spendTime" : 1,
  "resultCode" : "name.invalid",
  "message" : "您输入的名称太短了，建议为：ne123",
  "props" : {
    "suggestName" : "ne123"
  }
}
```



## 日志输出

RestPack 在开始处理请求时，会生成唯一的 requestId，
这个 requestId 不但会在返回报文中出现，还会一开始就放到日志的**MDC**中，
对于 log4j 或 logback （它们都支持 MDC），
你可以在配置将 requestId 信息输出到日志中，这样每条日志就用 requestId 相关联了。

比如在项目中，将 logback.xml 配置如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false" scan="true" scanPeriod="1000">

	<appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>%date %level requestId=%X{requestId} -- %-40logger{35}[%line]: %msg%n</pattern>
		</encoder>
	</appender>

	<appender name="file" class="ch.qos.logback.core.FileAppender">
		<file>./restpack.log</file>
		<encoder>
			<pattern>%date %level requestId=%X{requestId} -- %-40logger{35}[%line]: %msg%n</pattern>
		</encoder>
	</appender>

	<root level="info">
		<appender-ref ref="stdout" />
		<appender-ref ref="file" />
	</root>

</configuration>

```

**重点是日志输出格式，也就是<pattern>中加上requestId=%X{requestId}**：

```
%date %level requestId=%X{requestId} -- %-40logger{35}[%line]: %msg%n
```
%X{} 是使用 MDC 中的字段，有关 logback / log4j 中 MDC 的用法，不清楚的读者请自行百度搜索。

logback.xml 配置好后，再重启服务，在浏览器中输入URL：
```
http://localhost:8080/demo/restpack/echo?msg=abc
```
结果控制台输出如下：
```
2017-08-17 16:34:08,570 INFO requestId=ca2a12a0031f493db97856a3300b917a -- c.t.commons.restpack.RestPackAspect     [120]: request '/demo/restpack/echo' begin, params:
{
  "msg" : "abc"
}
2017-08-17 16:34:08,571 INFO requestId=ca2a12a0031f493db97856a3300b917a -- c.t.d.r.RestPackDemoController          [29]: echo, msg = abc
2017-08-17 16:34:08,572 INFO requestId=ca2a12a0031f493db97856a3300b917a -- c.t.commons.restpack.RestPackAdvice     [63]: request '/demo/restpack/echo' end, response:
{
  "requestId" : "ca2a12a0031f493db97856a3300b917a",
  "serverTime" : 1502958848570,
  "spendTime" : 2,
  "resultCode" : "success",
  "data" : "abc"
}
```
可以看到日志中有`requestId=ca2a12a0031f493db97856a3300b917a` 这段内容。

这样的好处是排查日志方便，比如在 linux 环境中，对日志文件执行类似如下命令：
```
grep -n "requestId=ca2a12a0031f493db97856a3300b917a" xxx.log
```
(xxx.log 是程序产生的日志文件的名称)，
就可以在大量日志内容中快速过滤出这条请求的日志了。
