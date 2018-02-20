

## 目录

* 项目背景
* DSQL 简介
* 引入 DSQL 依赖
* 启用 RestPack
* @RestPackController 注解
* RestPack 异常处理
* 日志输出
* 资源分享与技术交流


## 项目背景

目前在编写持久层代码时，有两个主流的框架可选，一个是 JPA（实现是Hibernate），另一个是 MyBatis，
它们各有优缺点，适用于不同的应用场景。

JPA 的优点是：
* 会根据实体类自动生成数据库结构，
    在开发阶段，改改代码中类的属性，重启就自动改了数据库表结构，非常的方便。
* 一些简单的增删改查实现，JPA 可以按约定的方法定义 Repository 接口方法，
    如： findByNameAndAge(String name, int age) 意思就是根据 name 和 age 两个字段查询，
    连 SQL 都不用写了，也非常的方便。

当然，JPA 的缺点也很明显：
* 纯 ORM 的方式，学习成本比较高，
    JPA 可以用注解的方式，建立实体间的各种关联，如：
    one-to-one、one-to-many、many-to-one、many-to-many
    但用好这个比较难，如果不够精通，很容易搞出问题又难以解决。 
* 对复杂 SQL 编写支持不够好，
    虽然 JPA 提供了 JpaSpecificationExecutor 这种更灵活的 API，
    但这种把 SQL 逻辑写到代码的方式，实在是不敢恭维，
    万一开发人员要请 DBA 帮忙对 SQL 进行优化咋办，让 DBA 先学一遍 Java 和 JPA ？  
    有人或许会问：“@Query(nativeQuery) 不是可以嵌入原生 SQL 么？”，
    问题是 @Query 注解中的 SQL 不能动态啊，如果要根据参数不现，执行的 SQL 也不同咋办？

所以 JPA 更适合快速迭代的中小型项目，
开发时对象的变更非常频繁，又没有大量的复杂 SQL 需求。

我们再来看看 MyBatis，MyBatis 是一个能够灵活编写 SQL，
并将 SQL 的入参和查询结果映射实体对象的一个持久层框架。
因此 JPA 的缺点正是它的优点，只要您熟练掌握 SQL 就可以用好 MyBatis
（而大多数开发人员，是具备这一技能要求的）。
在性能优化的时候，使用 MyBatis 可以较为方便的调整 SQL 语句，
甚至可以交给 DBA 或懂 SQL 的业务人员直接对 SQL 进行调整。

综合以上分析来看，最佳的实践方案是： 
使用 JPA 实现实体类到数据库的自动维护，以及简单增删查改逻辑的实现，
而对于复杂 SQL 操作需求，还是使用类似于 MyBatis 一样，
手工编写 SQL 并将结果自动映射到实体对象的方式比较好。

因此，DSQL 项目便应运而生了，它对 JPA 进行了扩展，
解决 JPA 不擅长编写动态复杂 SQL 的问题，
让您的项目可以使用 JPA 和 MyBatis 两者的优点，可以说是鱼与熊掌兼得。


## 引入 DSQL 依赖

然后，您就可以在您的项目的 pom.xml 文件中，引用 dsql 的 jar 包了，如下所示：
```
		<dependency>
			<groupId>terran4j</groupId>
			<artifactId>terran4j-commons-dsql</artifactId>
			<version>Virgo.0.1</version>
		</dependency>
```

**目前 `terran4j-commons-sql` 的最新稳定版是 Virgo.0.1 ，后续有新的稳定版本会更新到本文档中。**


## 示例程序介绍

本教程会用一个“地理位置查询”的示例程序来讲述 DSQL 的用法。

“地理位置查询”的功能是这样的：
1. 有一个表记录所有的商户地理位置信息，包括商户的名称、位置（GPS经纬度）、位置描述等字段。
2. 根据指定的位置，查询距离最近的商户列表。
3. 可以根据商户名模糊匹配。
4. 可根据位置远近进行排序条件，可指定是由近及远排序，还是由远及近排序。

这个示例程序的代码，已经放在 src/test/java 目录中 com.terran4j.demo.dsql 包里面，
本地装好数据库的情况下，是可以运行的，大家在学习过程中可以参考。

 ## 定义实体类
 
DSQL 是在 JPA 的基础上扩展而来的，因此它的用法与 JPA 非常相似。
在定义实体类方面，可以说是完全相同，没有任何的区别：


 
## 编写 DsqlRepository 



