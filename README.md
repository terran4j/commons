
## 项目背景

本项目作者 terran4j 是一位有十多年开发经验的互联网老兵，
之前在多家传统 IT 企业及互联网公司工作过，
现在为一名自由职业者，发起并维护一套项目开发常用的工具类库，
也就是本项目 terran4j-commons，纯开源，欢迎大家免费使用。

## 项目简介

本项目由多个子项目组成，每个子项目聚焦解决一个问题。
这里先简单介绍下这些子项目：
* commons-util:  
    通用工具类库，提供了常见的字符串操作、Java反射操作、异常处理等功能。
* [commons-restpack](https://github.com/terran4j/commons/tree/master/commons-restpack):  
    Http Restful 数据包装工具，可以将Http Restful API返回的结果包装成统一的格式。
* [commons-dsql](https://github.com/terran4j/commons/tree/master/commons-dsql):  
    在 JPA 的基础上进行扩展，可以基于 JPA 很容易的实现动态复杂的 SQL 操作，
    从而让开发者享受到 JPA & MyBatis 两者的优点，大大提高了持久层代码的开发效率。
* [commons-api2doc](https://github.com/terran4j/commons/tree/master/commons-api2doc):  
    Http API 文档工具，它通过反射的方式，读取 Controller 类的信息，
    然后自动生成易于阅读的在线 API 文档，节省开发者手工编写 API 文档的工作量。
    

## 适用用户

适合有 Java / Kotlin + Maven + SpringBoot 开发经验的开发者们使用。

如果您有 Java 开发经验但对Spring Boot 还不熟悉的话，建议先阅读笔者写过的一本书
[ 《Spring Boot 快速入门》 ](http://www.jianshu.com/nb/14688855?order_by=seq)。
这本书的目标是帮助有 Java 开发经验的程序员们快速掌握使用 Spring Boot 开发的基本技巧，感受到 Spring Boot 的极简开发风格及超爽编程体验。


## 软件版本

本项目中所用到的基础软件，均基于以下版本构建：
* Java:  1.8
* Maven:  3.3.9
* SpringBoot:  1.5.9.RELEASE

本项目及所有子项目均在以上版本测试过，可以正常运行。
其它版本理论上相同，应该没啥区别，若遇到问题，欢迎反馈！
