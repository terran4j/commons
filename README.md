
## 项目简介

本项目采用 SpringBoot 框架构建，致力于为 SpringBoot 应用程序的开发提升开发效率及编程体验。

本项目由多个子项目组成，每个子项目聚焦解决一个问题。
这里先简单介绍下这些子项目：
* [commons-api2doc](https://github.com/terran4j/commons/tree/master/commons-api2doc):  
    简称 Api2Doc，是一款 Http API 文档自动化生成工具，
    它通过反射的方式，读取 Controller 类的信息，
    然后自动生成易于阅读的在线 API 文档，节省开发者手工编写 API 文档的工作量。
* [commons-restpack](https://github.com/terran4j/commons/tree/master/commons-restpack):  
    简称 RestPack，是一款 Http API 数据包装框架，
    它可以将 Http API 的返回结果包装成统一的报文格式。
* [commons-dsql](https://github.com/terran4j/commons/tree/master/commons-dsql):  
    简称 DSQL，是一款从 SQL 到对象的自动映射框架，它尤其擅长动态复杂 SQL 的处理。
    它结合了现在两大主流持久层框架 JPA 及 MyBatis 的优点，
    从而更进一步的提高了持久层的开发效率。
* [commons-hedis](https://github.com/terran4j/commons/tree/master/commons-hedis):  
    简称 Hedis，是 Happy for using Redis 之意。它集成了当前主流的 Redis 客户端，
    如：spring-data-redis，Jedis，Redisson 等。
    Hedis 的目标是让 Redis 的使用更容易，并用 Redis 解决具体场景中的问题，
    比如说，它提供了缓存服务、分布式同步、轻量级分布式定时调度，等功能。 
    

## 适用用户

适合有 Java / Kotlin + SpringBoot 开发经验的开发者们使用。

如果您有 Java 开发经验但对Spring Boot 还不熟悉的话，建议先阅读笔者写过的一本书
[《Spring Boot 快速入门》](http://www.jianshu.com/nb/14688855?order_by=seq)。
这本书的目标是帮助有 Java 开发经验的程序员们快速掌握 Spring Boot 开发技巧，
感受到 Spring Boot 的极简开发风格及超爽编程体验。


## 软件版本

本项目中所用到的基础软件，均基于以下版本构建：
* Java:  1.8
* Maven:  3.3.9
* SpringBoot:  1.5.9.RELEASE

本项目及所有子项目均在以上版本测试过，可以正常运行。
其它版本理论上相同，应该没啥区别，若遇到问题，欢迎反馈！
