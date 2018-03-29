
Reflux 是基于 Spring Boot + Web Socket 技术编写的一套实现“服务端推送”
的技术框架，可以让 Java 开发人员非常简单的实现逆向通信（即客户端请求
建立连接后，服务端主动向客户端推送消息）。

## 目录

* 项目背景
* Reflux 简介
* 同类产品
* 源码下载
* 软件版本说明
* 适用读者
* clientId 详述
* demo 程序构成
* 配置 terran4j 的 Maven 仓库
* 创建 demo-reflux 项目
* 创建 demo-reflux-server 项目
* Reflux 服务端 —— 建立 WebSocket 端点
* 创建 demo-reflux-client 项目
* Reflux 客户端 —— 建立 WebSocket 连接
* 服务端推送消息到客户端
* 分布式解决方案


## 项目背景
传统的 HTTP 通信是单向的，即由客户端发送请求，服务端只能被动响应，
不能主动向客户端推送消息。
然而有很多业务场景是需要服务端主动向客户端推送消息的，为了能实现这个目的，
传统技术有两种：

1. 让客户端起一个 demo 线程，定期轮询请求服务端，服务端有消息就返回消息。
2. 让客户端发起一个长连接的 HTTP 请求，服务端对这个请求“hold”住不放，
    直到有消息时才返回消息。
3. 完全基于 TCP 层自定义协议实现。

前两种实现方式都只能算“伪推送”，网络消耗较大，运行效率较低。
第3种方式，可能要占用额外的端口（而不是直复用 HTTP 端口），并且开发较复杂，
部署也复杂。

在以上背景下，Web Socket 技术就应运而生了，它基于 TCP 底层实现真正的双向通信，
运行效率比“伪推送”高得多，又不占用额外的端口，可以说是完美解决了这个问题。

Spring Boot 也集成了 Web Socket 技术，但开发方面提供的支持较少，
要在实际场景下实现“服务端推送”仍需要编写很多代码。
本模块就是要基于 Spring Boot + Web Socket 技术，实现逆向通信（即客户端
请求建立连接后，能让服务端主动向客户端推送消息）。


## Reflux 简介

reflux 在英文中是“逆流、回流”的意思，考虑到web项目一般是client端主动
发起请求，服务端只是被动响应，而 reflux 利用 WebSocket 技术实现了逆向
推送（即服务端主动向客户端发送消息），因此用这个英文单词作为项目名称。

Reflux 在 Spring Boot 的基础上提供了 client 端和 server 端的 JAVA API，
它的使用流程如下：

1. 客户端向服务端发起 WebSocket 请求，请求中带一个名为 clientId 的参数，以表明此客户端的身份。
2. 服务端在校验了 clientId 的合法性后，接受请求以建立连接。
3. 服务端在以后（连接建立后）的任意时间点，都可以主动向客户端推送消息。

交互流程如下所示：

![reflux.png](http://upload-images.jianshu.io/upload_images/4489584-97386ef3b50b9d75.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## 同类产品

实际上业界也有基于以上说的3种方案做成独立产品的，如：

 * [goeasy](http://goeasy.io/)：  非开源、收费。
 * [socket.io](http://blog.csdn.net/jbboy/article/details/41787657)： 基于 nodejs 写的，也只有 js 的  API 。
 * [极光](http://docs.jiguang.cn/jpush/server/3rd/java_sdk/)：  非开源、收费。

大部分做得好的是独立的收费产品，很少有又好用、又开源免费的产品，希望本产品可以为广大开发者们提供一种新的选择。

 
## 源码下载

目前笔者已将本项目作为一个开源项目来维护，源代码放在了 [这里](https://git.oschina.net/terran4j-public/commons/tree/master/commons-reflux) 。

本文所用的示例代码也放在“码云”上了，欢迎大家免费下载或浏览：
 * [ demo-reflux-server ](https://git.oschina.net/terran4j-public/demo/tree/master/demo-reflux-server)
 : 服务端示例代码。
 * [ demo-reflux-client ](https://git.oschina.net/terran4j-public/demo/tree/master/demo-reflux-client)
 : 客户端示例代码。
 * [ demo-reflux ](https://git.oschina.net/terran4j-public/demo/tree/master/demo-reflux)
 : 客户端与服务端公共部分代码。


## 软件版本说明

相关软件使用的版本：
* Java:  1.8
* Maven:  3.3.9
* SpringBoot:  1.5.2.RELEASE

程序在以上版本均调试过，可以正常运行。
其它版本理论上相同，但仅供参考。


## 适用读者

本文适合有Java + Maven + SpringBoot 开发经验的开发者们。
如果您有 Java 开发经验但对Spring Boot 还不熟悉的话，建议先阅读笔者写过的一本书[ 《Spring Boot 快速入门》 ](http://www.jianshu.com/nb/14688855?order_by=seq)。
这本书的目标是帮助有 Java 开发经验的程序员们快速掌握使用 Spring Boot 开发的基本技巧，感受到 Spring Boot 的极简开发风格及超爽编程体验。


## clientId 详述

上一节所讲到的 clientId ，是客户端的身份凭证，至于如何管理 clientId 则不在 Reflux 范围之内。
“如何管理 clientId”，意指以下问题：

 * clientId 如何生成；
 * 客户端又如何获取到 clientId；
 * 建立连接时，服务端又怎么校验 clientId 的合法性；
 * 服务端如何存储 clientId 与客户端其它信息的关联。

我们列举两个场景来描述 clientId 在实际项目中是怎么管理的：

 * 在一个移动互联网类的项目中，client 端是移动端 App 应用程序，如果用户登录功能是类似于 OAuth2.0 的方式的话，那 client 会先访问登录请求，登录成功后获得一个 access_token，这个 access_token 就可以作为我们这里的 clientId 来使用，服务端也可以通过调用账号系统API来校验 clientId 的合法性。
 * 在一个 PAAS 平台类的项目中，client 端可能是使用 PAAS 平台的应用系统。一般来说，server端（PAAS平台）会给每个应用系统分配一个appKey + appSecret 作为应用系统的凭证，client 端（应用系统的一台实例）可以用 appSecret 作为密钥，给 “appKey + 实例IP” 加密，将密文作为 clientId，server 端校验 clientId 时再用 appSecret 解密即可获知应用方的 appKey 及客户端实例的 IP。

当然，这里是只是举两个场景作为例子，实际上使用“服务端推送”技术的场景是非常多的，开发者可以根据自身的业务需求进行处理。


## demo 程序构成

下面几节我们会讲解一个 demo 程序的开发，帮助我们理解 Reflux 的用法。
demo 程序分以下几个项目：

 * [ demo-reflux ](https://git.oschina.net/terran4j-public/demo/tree/master/demo-reflux)
 : 客户端与服务端公共部分代码，定义了一个名为`Hello`的Java Bean，作为消息的内容载体。
 * [ demo-reflux-server ](https://git.oschina.net/terran4j-public/demo/tree/master/demo-reflux-server)
 : 服务端代码，提供一个 WebSocket 端点供客户端连接，还提供一个Controller用于发起消息推送。
 * [ demo-reflux-client ](https://git.oschina.net/terran4j-public/demo/tree/master/demo-reflux-client)
 : 客户端代码，启动后会去连接服务端以建立 WebSocket 连接，同时会监听来自服务端的消息推送。


## 配置 terran4j 的 Maven 仓库

Reflux 是笔者（terran4j）多个开源项目的其中一个项目，笔者为了方便大家使用，专门搭建了一个开放的 maven 仓库，并将所有开源项目的 jar 包发布到这个仓库中了，因此需要您在 maven 的 settings.xml 文件上配置上这个仓库，配置方法参见《[配置 terran4j 的 maven 仓库](http://www.jianshu.com/p/283cd7ce3e87)》。


## 创建 demo-reflux 项目

首先，我们基于 Spring Boot 创建名为 demo-reflux 的项目，并在 pom.xml 文件中引入 reflux 的依赖：

```xml
		<dependency>
			<groupId>terran4j</groupId>
			<artifactId>terran4j-commons-reflux</artifactId>
			<version>1.0.2</version>
		</dependency>
```

terran4j-commons-reflux 项目的当前最新稳定本是 1.0.2 ，若有更新升级会本这里给出最新版本号。
另外 terran4j-commons-reflux 是发布在 terran4j 的 maven 仓库中，所以 **需要在您 maven 的 settings.xml 中配置此 maven 仓库**，配置方法请参见 这篇文档 。


整个 pom.xml 文件代码如下所示：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>terran4j</groupId>
	<artifactId>demo-reflux</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>demo-reflux</name>
	<url>http://maven.apache.org</url>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.2.RELEASE</version>
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
			<artifactId>terran4j-commons-reflux</artifactId>
			<version>1.0.2</version>
		</dependency>
	</dependencies>

</project>
```

然后我们定义一个名为 `Hello` 的 Java Bean，代码如下所示：

```java
package com.terran4j.demo.reflux;

import com.terran4j.commons.util.Strings;

public class Hello {

	private String name;
	
	private String greeting;
	
	private long currentTime;
	
	public Hello() {
		super();
	}
	
	public Hello(String name) {
		this(name, "Hello, " + name);
	}

	public Hello(String name, String greeting) {
		super();
		this.name = name;
		this.greeting = greeting;
		this.currentTime = System.currentTimeMillis();
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getGreeting() {
		return greeting;
	}

	public final void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	public final long getCurrentTime() {
		return currentTime;
	}

	public final void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	
	public final String toString() {
		return Strings.toString(this);
	}
	
}
```

这个 Hello 是对消息内容的描述，server 端推送消息时，发的是 Hello 对象，客户端接收消息时也是收的 Hello 对象，这点下面会再讲到。


## 创建 demo-reflux-server 项目

然后，我们创建服务端的项目 demo-reflux-server ，并在 pom.xml 文件中添加刚才 demo-reflux 项目的依赖，如：

```xml
		<dependency>
			<groupId>terran4j</groupId>
			<artifactId>demo-reflux</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
```

由于 demo-reflux 项目中已经添加了 terran4j-commons-reflux 的依赖了，所以这里不需要重复添加。

整个 pom.xml 文件如下所示：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>terran4j</groupId>
	<artifactId>demo-reflux-server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>demo-reflux-server</name>
	<url>http://maven.apache.org</url>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.2.RELEASE</version>
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
			<artifactId>demo-reflux</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
	</dependencies>

</project>
```

然后我们编写 main 函数，并在类上添加 @EnableRefluxServer, 代码如下所示：

```java
package com.terran4j.demo.reflux.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.reflux.server.EnableRefluxServer;

@EnableRefluxServer
@SpringBootApplication
public class RefluxServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefluxServerApplication.class, args);
	}
	
}
```

@EnableRefluxServer 会在 Spring 容器中定义很多 Spring Bean 对象，以提供 Reflux 服务端的能力。

我们还要在 application.yml 中定义下所用的端口：
```yml
server:
  port: 8081
```
为了避免端口冲突，demo-reflux-server 项目使用 8081 端口；后面的 demo-reflux-client 项目将使用 8082 端口。


## Reflux 服务端 —— 建立 WebSocket 端点

3. 编写  DemoServerEndpoint 类，以建立WebSocket端点，代码如下所示：

```java
package com.terran4j.demo.reflux.server;

import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.terran4j.commons.reflux.server.RefluxServerEndpoint;

@ServerEndpoint("/demo/connect")
@Component
public class DemoServerEndpoint extends RefluxServerEndpoint {

	@Override
	protected boolean authenticate(String clientId) {
		/**
		 * 这里为了简化演示代码，就不作 clientId 的校验了，直接返回 true 。
		 */
		return true;
	}

}
```

说明一下：
 * 必须继承于 RefluxServerEndpoint 类，RefluxServerEndpoint 提供了很多现成的处理 WebSocket 连接的方法。
 * 必须实现方法 `boolean authenticate(String clientId)` 用于校验 clientId 的合法性，这里为了简化演示代码，就不作 clientId 的校验了，直接返回 true 。
 * 类上必须加上 @ServerEndpoint 的注解，用于定义连接 WebSocket 时的请求路径。
 * 类上必须加上 @Component 注解，用于注册成为 Spring Bean。
 
建立好 WebSocket 端点后，既使客户端不是 java 的（比如： PHP, Javascript, Android, iOS），也可以按 Web Socket 的协议请求连接了。

当然如果是  java ，用 reflux 提供的 client API 就非常简单了，这点后面会讲到。


## 创建 demo-reflux-client 项目

现在我们尝试编写 Reflux 客户端项目 demo-reflux-client 。

与服务端一样，也是先创建 demo-reflux-client 项目，也是在 pom.xml 中添加依赖：

```xml
		<dependency>
			<groupId>terran4j</groupId>
			<artifactId>demo-reflux</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
```

整个 pom.xml 文件与服务端类似，这里就不重复了。

然后我们编写 main 函数，并在类上添加 @EnableRefluxClient, 代码如下所示：

```java
package com.terran4j.demo.reflux.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.reflux.client.EnableRefluxClient;

@EnableRefluxClient
@SpringBootApplication
public class RefluxClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefluxClientApplication.class, args);
	}

}
```

@EnableRefluxClient 会在 Spring 容器中定义很多 Spring Bean 对象，以提供 Reflux 客户端的能力。


## Reflux 客户端 —— 建立 WebSocket 连接

现在我们来编写一个名为 MyRefluxConnector 的类，来连接服务端：

```java
package com.terran4j.demo.reflux.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.terran4j.commons.reflux.RefluxClient;

@Service
public class MyRefluxConnector implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(MyRefluxConnector.class);

	@Value("${reflux.server.url}")
	private String serverURL;

	@Value("${reflux.client.id}")
	private String clientId;

	@Autowired
	private RefluxClient refluxClient;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 建立 Web Socket 连接。
		refluxClient.connect(serverURL, clientId);
		if (log.isInfoEnabled()) {
			log.info("connect server success, serverURL = {}, clientId = {}", //
					serverURL, clientId);
		}
	}

}
```

其实主要就两步：

 * 第一步，用 @Autowired 的方式注入 Bean: RefluxClient refluxClient 。
 * 第二步，调用 `refluxClient.connect(serverURL, clientId)` 方法建立连接。

就这么简单，serverURL 是一个 WebSocket 的URL，比如在本例中，它的值为：

```
ws://localhost:8081/demo/connect
```

ws 是 WebSocket 协议的意思，之所示路径是 `/demo/connect` ，是在服务端 `DemoServerEndpoint` 类中定义的，如：

```java
@ServerEndpoint("/demo/connect")
@Component
public class DemoServerEndpoint extends RefluxServerEndpoint
```

为了能在客户端程序启动时就连接 WebSocket，这个类实现了 `ApplicationRunner` 接口并将代码放在 run 方法中，当然这只是代码演示的方便，您可以根据实际业务的需求，选择什么时机进行连接。

好了，我们可以分别启动 RefluxServerApplication, RefluxClientApplication 两个 main 函数来看看效果了。
client端、server端两个程序都启动后，看到 client 端的控制台输出如下：
```
......
2017-08-20 16:05:24.786  INFO 8564 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8082 (http)
2017-08-20 16:05:24.791  INFO 8564 --- [           main] c.t.c.reflux.client.RefluxClientImpl     : connect server web socket url: ws://localhost:8081/demo/connect?clientId=12345
2017-08-20 16:05:25.094  INFO 8564 --- [           main] c.t.c.reflux.client.ClientConnection     : Opening client websocket, server = ws://localhost:8081/demo/connect
2017-08-20 16:05:25.098  INFO 8564 --- [           main] c.t.c.reflux.client.RefluxClientImpl     : 目标服务连接成功： ws://localhost:8081/demo/connect?clientId=12345
2017-08-20 16:05:25.100  INFO 8564 --- [           main] c.t.d.reflux.client.MyRefluxConnector    : connect server success, serverURL = ws://localhost:8081/demo/connect, clientId = 12345
2017-08-20 16:05:25.103  INFO 8564 --- [           main] c.t.d.r.client.RefluxClientApplication   : Started RefluxClientApplication in 4.605 seconds (JVM running for 5.146)
```

server 端的控制台输出如下：

```
......
2017-08-20 16:05:05.855  INFO 7052 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8081 (http)
2017-08-20 16:05:05.862  INFO 7052 --- [           main] c.t.d.r.server.RefluxServerApplication   : Started RefluxServerApplication in 4.986 seconds (JVM running for 5.583)
2017-08-20 16:05:24.998  INFO 7052 --- [nio-8081-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring FrameworkServlet 'dispatcherServlet'
2017-08-20 16:05:24.999  INFO 7052 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : FrameworkServlet 'dispatcherServlet': initialization started
2017-08-20 16:05:25.024  INFO 7052 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : FrameworkServlet 'dispatcherServlet': initialization completed in 25 ms
2017-08-20 16:05:25.106  INFO 7052 --- [nio-8081-exec-1] c.t.c.r.server.RefluxServerEndpoint      : 来自客户端的连接, clientId = 12345
2017-08-20 16:05:25.108  INFO 7052 --- [nio-8081-exec-1] c.t.c.reflux.server.RefluxServerImpl     : 有新连接加入! 当前连接数为 1
```

说明连接成功了。


## 服务端推送消息到客户端

建立连接后，服务端可以主动推送消息了，我们编写 DemoController 类来实现消息推送，如下所示：

```java
package com.terran4j.demo.reflux.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.terran4j.commons.reflux.RefluxServer;
import com.terran4j.demo.reflux.Hello;

@RestController
public class DemoController {

	private static final Logger log = LoggerFactory.getLogger(DemoController.class);

	@Autowired
	private RefluxServer refluxServer;

	@RequestMapping(value = "/demo/send", method = RequestMethod.GET)
	public String sendHello(@RequestParam("name") String name) {
		Hello hello = new Hello(name);
		refluxServer.sendAll(hello);
		if (log.isInfoEnabled()) {
			log.info("send hello message to ALL client done:\n{}", //
					hello);
		}
		return "success";
	}

}
```

其实主要就两步：
 * 第一步， 用 @Autowired 注入 Bean: RefluxServer refluxServer 。
 * 第二步，调用方法 `refluxServer.sendAll(hello);` 发送消息，消息内容是自定义的任意 Java Bean 对象（如这里的Hello hello）。

RefluxServer 提供了两个推送消息的方法，一个是 `int sendAll(Object content)` 推送消息到所有已建立连接的客户端，它返回 int 值表示推送成功的连接数量；
还有一个是 `boolean send(Object content, String clientId)` 方法，它会定向推送消息到指定 clientId 的某个客户端，它返回 boolean 值表示是否推送成功。


同时客户端需要编写代码以接收消息，我们在 demo-reflux-client 项目中编写 MyRefluxReceiver 类来接收消息，代码如下所示：

```java
package com.terran4j.demo.reflux.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terran4j.commons.reflux.OnMessage;
import com.terran4j.demo.reflux.Hello;

@Service
public class MyRefluxReceiver {

	private static final Logger log = LoggerFactory.getLogger(MyRefluxReceiver.class);

	@OnMessage
	public void onHello(Hello hello) {
		if (log.isInfoEnabled()) {
			log.info("receive message hello:\n{}", hello);
		}
	}
}
```

需要注意以下几点：

 * 在一个 Spring Bean 类中编写一个用于接受消息的方法，**并加上 @OnMessage 注解**，如上面的 onHello 方法。
 * 此方法的**入参有且仅有一个**，类型必需与发消息时一样的类，若发消息时的消息类与收消息时的消息类不一样的话，就会接收不到。
 * 此方法的**返回类型只是能 void 或 String 类型的**，void 表示无返回内容， String 表示有返回内容（通过WebSocket返回到服务端）。


最后，我们将 client 与 server 同时启动，然后在浏览器中输入 URL ：

```
http://localhost:8081/demo/send?name=terran4j
```

这会调用服务端的这个方法：

```java
	@RequestMapping(value = "/demo/send", method = RequestMethod.GET)
	public String sendHello(@RequestParam("name") String name) {
		Hello hello = new Hello(name);
		refluxServer.sendAll(hello);
		if (log.isInfoEnabled()) {
			log.info("send hello message to ALL client done:\n{}", //
					hello);
		}
		return "success";
	}
```

服务端控制台的输出如下所示：

```
2017-08-20 16:42:58.031  INFO 15712 --- [nio-8081-exec-2] c.t.demo.reflux.server.DemoController    : send hello message to ALL client done:
{
  "name" : "terran4j",
  "greeting" : "Hello, terran4j",
  "currentTime" : 1503218577967
}
```

而在客户端，Reflux 框架收到消息后也会调用下面这个 onHello 方法：

```java
	@OnMessage
	public void onHello(Hello hello) {
		if (log.isInfoEnabled()) {
			log.info("receive message hello:\n{}", hello);
		}
	}
```

结果客户端控制台输出如下：

```
2017-08-20 16:42:58.055  INFO 7848 --- [lient-AsyncIO-1] c.t.demo.reflux.client.MyRefluxReceiver  : receive message hello:
{
  "name" : "terran4j",
  "greeting" : "Hello, terran4j",
  "currentTime" : 1503218577967
}
```

说明消息的推送和接收都成功了。


## 分布式解决方案

目前讲的消息推送一直是在单机下进行的，如果服务端是有多台实例的分布式环境呢？

在分布式的环境下，会出现以下情况：
1. 服务端有多台实例，并且都是同构的。
2. 服务端实例个数是可能会动态增加或减少（但某台实例当机不应该影响服务的可用性）。
3. 某个客户端连接了其中一台服务端实例，但执行“向这个客户端推送消息”的是另一个实例。

一个有效的解决方案就是使用具有“发布-订阅”机制的消息中间件，比如： RabbitMQ, Kafka 等，也可以用 Redis 提供的“发布-订阅”功能。
具体来说，就是执行定向推送消息的实例，如果发现目标客户端连接的并不是自己，就发到消息中间件上，其它实例都在监听这一topic，如果此客户端是连接到本实例中，就执行推送，否则就忽略之。
另外，客户端内部有一个守护线程，轮询检查与服务端的连接是否中断，中断的话就重新请求连接。

至于是选  RabbitMQ, Kafka, 还是 Redis，则根据业务需求而定：

 * 业务上要求稳定，不能丢消息的场景下，建议用 RabbitMQ 。
 * 业务上要求超大并发、高吞吐量的场景下，建议用 Kafka 。
 * 业务上要求高实时、低延迟的场景下，建议用 Redis 。

由于需要根据业务场景而定，对分布式的支持的功能并未包含在本项目中，但根据以上的分析，在 Reflux 的基础上自行实现并不复杂，建议广大开发者发挥自己的聪明才智自行解决，更欢迎向本项目贡献代码。


## 资源分享与技术交流

如果你觉得本项目对你有用的话，希望可以定期收到更多分享的精彩技术干货，或者希望与笔者交流相关技术问题，可以加一下我们的 **SpringBoot及微服务** 微信公众号，请拿起手机扫描下面的二维码关注下吧！
![SpringBoot及微服务-公众号二维码](http://upload-images.jianshu.io/upload_images/4489584-f4f91efb322bd92c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)