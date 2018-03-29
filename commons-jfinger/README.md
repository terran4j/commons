
JFinger 是一个命令行开发框架，可以用 Java 很方便的开发一些基于命令行的功能。

## 目录

* 项目背景
* JFinger 简介
* 源码下载
* 软件版本说明
* 引入 JFinger 依赖
* 启用 JFinger
* JFinger 命令格式
* JFinger 帮助系统
* 自己编写命令
* 资源分享与技术交流


## 项目背景

虽然当前软件系统的人机交互是以“图形化”的交互模式为主，但对于“程序猿”这个特殊群体而言，仍非常偏爱“命令行”的交互模式，比如在服务器 OS 市场，占了 70% 以上的市场的 Unix / Linux 系统就是以命令行为主，虽然现代很多 Linux 的版本有图形化模式，但程序员们似乎并不买账，他们绝大多数仍然使用命令行的方式操作着服务器。
这也说明命令行方式在编程领域有着非常强大的生命力，这或许与它的专业性强、操作简洁、可编程性强的特点是分不开的。

笔者也非常酷爱命令行的操作方式，然而 Linux 上的命令都是针对操作系统功能本身的，不是针对自己开发的具体业务系统的。
有一次笔者在某 PAAS 平台的项目开发中，根据项目需要编写了一个诊断工具，这个工具以命令行的方式，提供了使用这个 PAAS 平台时的问题诊断功能。
后来笔者根据这个项目的经验，编写了一套 **命令行开发框架**，可以快速开发基于命令行的工具程序，后来这套“命令行开发框架”就演化成本项目。

## JFinger 简介

JFinger 是一个命令行开发框架，可以用 Java 很方便的开发一些基于命令行的功能。
本项目取名为“JFinger”， J 代表 Java 的意思， Finger 在英语中是“手指头”的意思，因为敲命令需要动手指头嘛，哈哈！

jfinger 是一个 Java 模块，以 jar 包的形式被集成到您的项目中，程序启动后会在控制台提供了命令行方式的交互功能，类似于：

```
......
2017-08-23 07:07:34.018  INFO 18300 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2017-08-23 07:07:34.027  INFO 18300 --- [           main] c.t.demo.jfinger.JFingerDemoApplication  : Started JFingerDemoApplication in 4.892 seconds (JVM running for 5.599)

JFinger Command Line Service is starting...

jfinger>

jfinger>log setLevel -n "com.terran4j" -l warn
成功设置包 com.terran4j 的日志级别为： WARN
```

也就是说，java程序控制台不再只是单纯输出日志了，而是出现了 `jfinger>` 提示符，您可以在提示符下输入命令，以完成一些与你的程序相关的操作。
比如上面执行了一条命令 `log setLevel -n "com.terran4j" -l warn`，意思是设置 slf4j / log4j 日志级别，将 com.terran4j 包设置为 WARN 级别。


## 源码下载

JFinger 现在已开源，欢迎大家使用，源代码放在了 [这里](https://git.oschina.net/terran4j-public/commons/tree/master/commons-jfinger) 。

本文所用的示例代码也放在“码云”上了，欢迎大家免费下载或浏览：
 * [ demo-jfinger ](https://git.oschina.net/terran4j-public/demo/tree/master/demo-jfinger)


## 软件版本说明

相关软件使用的版本：
* Java:  1.8
* Maven:  3.3.9
* SpringBoot:  1.5.9.RELEASE

程序在以上版本均调试过，可以正常运行。
其它版本理论上相同，但仅供参考。


## 适用读者

本文适合有Java + Maven + SpringBoot 开发经验的开发者们。
如果您有 Java 开发经验但对Spring Boot 还不熟悉的话，建议先阅读笔者写过的一本书[ 《Spring Boot 快速入门》 ](http://www.jianshu.com/nb/14688855?order_by=seq)。
这本书的目标是帮助有 Java 开发经验的程序员们快速掌握使用 Spring Boot 开发的基本技巧，感受到 Spring Boot 的极简开发风格及超爽编程体验。


## 引入 JFinger 依赖

JFinger 是笔者（terran4j）多个开源项目的其中一个项目，笔者为了方便大家使用，专门搭建了一个开放的 maven 仓库，并将所有开源项目的 jar 包发布到这个仓库中了，因此需要您在 maven 的 settings.xml 文件上配置上这个仓库，配置方法参见《[配置 terran4j 的 maven 仓库](http://www.jianshu.com/p/283cd7ce3e87)》。

配置好 maven 仓库后，您就可以在您的项目的 pom.xml 文件中引入  JFinger 的依赖了，如：

```xml
		<dependency>
			<groupId>terran4j</groupId>
			<artifactId>terran4j-commons-jfinger</artifactId>
			<version>1.0.5</version>
		</dependency>
```

terran4j-commons-jfinger 项目的当前最新稳定本是 1.0.5 ，若有更新升级会本这里给出最新版本号。

整个 pom.xml 文件代码如下所示：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>terran4j</groupId>
	<artifactId>demo-jfinger</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>demo-jfinger</name>
	<url>http://maven.apache.org</url>


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
			<artifactId>terran4j-commons-jfinger</artifactId>
			<version>1.0.5</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

注意：在 pom.xml 文件的最后面，我们引入了一个 maven 插件 `spring-boot-maven-plugin` 这是帮助我们打出 Spring Boot 独立运行的 jar 包的，Spring Boot 的 jar 包很特别，它将您的项目、以及项目中依赖的所有 jar 包都打在一起，打成一个“胖” jar 包，这样部署起来非常方便，相信用过的同学都能体会到。


## 启用 JFinger

JFinger 也是基于 Spring Boot 框架而设计的，要在程序中启用 JFinger 的功能，就必须在 SpringBootApplication 的类上面加上  **@EnableJFinger** 的注解，如下代码所示：

```java
package com.terran4j.demo.jfinger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.jfinger.EnableJFinger;

@EnableJFinger
@SpringBootApplication
public class JFingerDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JFingerDemoApplication.class, args);
	}

}
```

然后我们运行这个 main 函数，控制台输出变成这样了：

```
......
2017-08-23 07:07:34.018  INFO 18300 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2017-08-23 07:07:34.027  INFO 18300 --- [           main] c.t.demo.jfinger.JFingerDemoApplication  : Started JFingerDemoApplication in 4.892 seconds (JVM running for 5.599)

JFinger Command Line Service is starting...

jfinger>
```

多了个 jfinger> 的提示符，可以在后面输入命令。

无论您是在 Eclipse IDE 中运行、 还是在 Windows / Linux 中直接用 java -jar xxx.jar 的方式运行，都可以在控制台输入命令。
不过笔者发现在  Eclipse IDE 中，**输入命令时不能用中文输入法**，似乎是 Eclipse 控制台对中文支持有问题。
如果一定要在命令中含中文内容的话，建议在 Eclipse 之外把命令写好，再 Ctrl+C, Ctrl+V 拷贝进去执行。


## JFinger 命令格式

JFinger 的命令格式为：

```
【命令组名】 【命令名】 【选项1】 【选项2】 ... 【选项n】
```

比如上面提到的“设置日志级别”的命令：

```
log setLevel -n "com.terran4j" -l warn
```

log 就是命令组名，setLevel 就是命令名，-n "com.terran4j" 及 -l warn 都是这个命令的选项。

一个命令组可以包含多个命令，比如 system 命令组就至少包含两个命令：

```
system prop        显示所有的系统变量的值。
system env         显示所有的环境变量的值。
```

将命令分组是为了避免命令冲突，在开发命令系统时，不同的团队或不同的模块可以用不同的命令组名。这样命令名就不需要全局唯一，只需要在此命令组内部唯一就可以了。

命令行的选项是由  -<key> <value> 组成，如上面的 -l warn 。
value 值中如果有空格或其它特殊字符，也可以用 "" 号包裹起来，如上面的 -n "com.terran4j"  

你可以用 help 【命令组名】 【命令名】来查看此命令的选项列表。
如下所示，输入 help log setLevel 显示出了此命令的详细信息:

```
jfinger>help log setLevel
命令：  log setLevel [选项]
选项列表:
    -n, --loggerName <string>    日志名称，一般是用类全名(不支持通配)。
    -l, --logLevel <string>      日志级别

说明：  设置日志级别，如： log setLevel -n "com.terran4j" -l warn
```

选项列表中罗列了此命令的所有选项，以这一行为例：

```
-l, --logLevel <string>      日志级别
```

-l 表示此选项的 key 为 l ， --logLevel 表示此选项的 name 为 logLevel ，<string> 表示此选项的值是字符串类型的。

选项既可以用 -<key> <value> 来来表示，也可以用 --<name> <value> 来表示，如下面两个命令是等价的：

```
log setLevel -n "com.terran4j" -l warn
log setLevel --loggerName "com.terran4j" --logLevel warn
```

一般来讲，key 非常短（通常用一个字母表示）， 用  -<key> <value> 的写法非常方便；
而 name 是由完整拼写的单词组成， 用  -<key> <value> 的写法便于理解这个命令选项的意义。


## JFinger 帮助系统

JFinger 有一套帮助系统，可以快速了解当前程序中命令的用法。
这套帮助系统是基于一个特殊的 help 命令提供的，用 help 命令可以查询所有命令的用法。

如果你不知道当前程序中被注入了哪些命令，可以用 help 命令查看，如：

```
jfinger>help
欢迎使用由 terran4j 提供的命令行服务。

当前程序有以下命令可供使用：
命令：  spring [profile | prop | showBean]
说明：  Spring 相关命令，如查看 Spring 中的配置属性等。

命令：  system [env | prop | setProps]
说明：  系统命令，读取或写入本程序中的系统变量或环境变量等。

命令：  log [setLevel]
说明：  Logback 日志相关命令，可以用于调整日志级别等操作。

命令：  hello [say]
说明：  hello命令组，用于演示命令的开发方式。


请用 help 查询命令的详细用法，如：
输入：
    help
查看所有的命令组。

输入：
    help 【groupName】
查看指定命令组的详细信息，如：
    help system

输入：
    help 【groupName】 【commandName】
查看指定命令的详细信息，如：
    help system prop

```

help 命令罗列了程序内所有的命令的概要信息。

如果你知道某个命令组的名称，但不知道这个命令组中命令的细节，可以用 help 【命令组名】 来查看，如：

```
jfinger>help system
命令组：  system
说明：  系统命令，读取或写入本程序中的系统变量或环境变量等。
命令列表：
    env: 读取或写入环境变量的值
    prop: 显示或改写系统系统变量的值，如：
        system prop        显示所有的系统变量的值。
        system prop -k abc -v AAA        将 abc 的值改为 123。
        system prop -k abc        显示此系统变量的值。
    setProps: 改写系统变量值，如： system setProps -Dk1=123 -Dk2=456
```

如果你知道某个命令组及命令的名称，但不记得这个命令详细信息（如选项有哪些），可以用 help 【命令组名】 【命令名】来查看，如：

```
jfinger>help system prop
命令：  system prop [选项]
选项列表:
    -k, --key <string>      系统变量的键，不指定则显示所有的系统变量的值。
    -v, --value <string>    系统变量的值，不指定则表示显示此变量值，指定则表示改写此变量值。

说明：  显示或改写系统系统变量的值，如：
    system prop        显示所有的系统变量的值。
    system prop -k abc -v AAA        将 abc 的值改为 123。
    system prop -k abc        显示此系统变量的值。
```


## 自定义命令

下面，我们来自己编写了一个最简单的命令系统，一个 JFinger 版的 Hello, world 。

首先，我们写一个简单的 Spring Boot 服务类 HelloService ，如下所示：

```java
package com.terran4j.demo.jfinger;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

	private final AtomicInteger count = new AtomicInteger(0);

	public final String say(String name) {
		count.incrementAndGet();
		return "Hello, " + name + "!";
	}

	public final int count() {
		return count.get();
	}

}
```

这是一个非常的简单Spring Bean， say 方法返回一句“Hello, xxx”之类的问候句， 而 count 方法显示 say 方法调用的次数。

然后我们要编写一个 HelloCommand 类，让 say 方法和 count 方法可以在命令行中被调用，代码如下：

```java
package com.terran4j.demo.jfinger;

import org.springframework.beans.factory.annotation.Autowired;

import com.terran4j.commons.jfinger.Command;
import com.terran4j.commons.jfinger.CommandGroup;
import com.terran4j.commons.jfinger.CommandInterpreter;
import com.terran4j.commons.jfinger.CommandOption;
import com.terran4j.commons.jfinger.OptionType;
import com.terran4j.commons.util.error.BusinessException;

@CommandGroup(desc = "hello命令组，用于演示命令的开发方式。")
public class HelloCommand {

	@Autowired
	private HelloService helloService;

	@Command( //
			desc = "输出问候语，如： \n" //
					+ "hello say -n world -c 2 -s \n" //
					+ "将输出（执行多次时序号可能不同）：\n" //
					+ "1. Hello, world!" // 
					+ "2. Hello, world!", //
			options = { //
					@CommandOption(key = "n", name = "name", required = true, //
							desc = "目标名称"), //
					@CommandOption(key = "s", name = "sequenceNumber", type = OptionType.BOOLEAN, //
							desc = "需要序号（序号按调用次数递增）"), //
					@CommandOption(key = "c", name = "count", type = OptionType.INT, //
							desc = "本次调用多少次") //
			})
	public void say(CommandInterpreter ci) throws BusinessException {
		String name = ci.getOption("n");
		boolean sequenceNumber = ci.hasOption("s");
		int count = ci.getOption("c", 1);
		for (int i = 0; i < count; i++) {
			String message = helloService.say(name);
			if (sequenceNumber) {
				message = helloService.count() + ". " + message;
			}
			ci.println(message);
		}
	}

}
```

说明一下：
 * @CommandGroup 修饰在类上，表示这个类是一个命令组类，其中的 desc 属性是对这个命令组的描述信息（会出现在  help 信息中），
 * 类名建议以   XxxCommand 的方式命名，JFinger 会将 xxx 作为命令组的名称（即 XxxCommand 去掉 Command 然后首字母改为小写）。
 * [@Command](https://my.oschina.net/liuxiliang) 修饰在方法上，表示这个方法是一个命令，方法名就是命令的名称，这个方法**必须有且仅有一个 CommandInterpreter 类型的参数**，且无返回值。
 * 在 [@Command](https://my.oschina.net/liuxiliang) 注解中，**@CommandOption 表示这个方法的选项**。

@CommandGroup 的类定义为：

```
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CommandGroup {
    ......
}
```

表示 @CommandGroup 注解完全具备 @Component 注解的功能，所修饰的类完全可以当一个 Spring Bean 的类。
所以我们可以用：

```java
	@Autowired
	private HelloService helloService;
```

来注入 helloService Bean。

写完这个类后，我们重启程序，在 `jfinger>` 提示符下输入 help hello ，结果如下：

```
jfinger>help hello
命令组：  hello
说明：  hello命令组，用于演示命令的开发方式。
命令列表：
    say: 输出问候语，如： 
        hello say -n world -c 2 -s 
        将输出（执行多次时序号可能不同）：
        1. Hello, world!
        2. Hello, world!

``` 

这些对命令的解释内容，都是 JFinger 从 HelloCommand 类中提取并自动生成的。

然后我们再执行命令： hello say -n world -c 2 -s ， 结果如下：

```
jfinger>hello say -n world -c 2 -s
1. Hello, world!
2. Hello, world!
```

表示命令成功执行了。


## 资源分享与技术交流

如果你觉得本项目对你有用的话，希望可以定期收到更多分享的精彩技术干货，或者希望与笔者交流相关技术问题，可以加一下我们的 **SpringBoot及微服务** 微信公众号（免费的哦），请拿起手机扫描下面的二维码关注下吧！
![SpringBoot及微服务-公众号二维码](http://upload-images.jianshu.io/upload_images/4489584-f4f91efb322bd92c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)