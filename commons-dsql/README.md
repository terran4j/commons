

## 目录

* 项目背景
* DSQL 简介
* 引入 DSQL 依赖
* 启用 RestPack
* @RestPackController 注解
* RestPack 异常处理
* 日志输出


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

因此，DSQL 项目便应运而生了，它对 JPA 进行了扩展，主要解决 JPA 不擅长编写复杂 SQL 的问题，
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
1. 有一个表记录所有的地理位置信息，包括位置的名称、位置的经纬度，等字段。
2. 根据指定的位置，查询附近的位置列表，并返回位置之间的距离。
3. 可以根据位置名称模糊匹配。
4. 可根据位置远近进行排序条件，可指定是由近及远排序，还是由远及近排序。

这个示例程序的代码，已经放在 src/test/java 目录中 com.terran4j.demo.dsql 包里面，
本地装好数据库的情况下，是可以运行的，大家在学习过程中可以参考。

 ## 定义实体类
 
DSQL 是在 JPA 的基础上扩展而来的，因此它的用法与 JPA 非常相似。
在定义实体类方面，可以说是完全相同，没有任何的区别，
在本示例程序中，我们定义了一个 Address 的实体类，代码如下所示：

```java
package com.terran4j.demo.dsql;

import javax.persistence.*;

@Entity(name = "demo_address")
@Table(indexes = {
		@Index(name = "idx_gps", columnList = "lon,lat"),
        @Index(name = "idx_name", columnList = "name")
})
public class Address {

    public Address() {
    }

    public Address(String name, Double lon, Double lat) {
        this.name = name;
        this.lon = lon;
        this.lat = lat;
    }

	@Id
	@GeneratedValue
	@Column(length = 20)
	private Long id;

    @Column(length = 100)
    private String name;

	@Column(length = 20, precision = 8)
	private Double lon;

	@Column(length = 20, precision = 8)
	private Double lat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
```

仍然是 JPA 的规范。

## 编写 JpaRepository

由于 DSQL 依赖 JPA， 所以我们完全可以按JPA 的方式编写一个 Repository 类，
代码如下所示：

```java

package com.terran4j.demo.dsql;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressDAO extends JpaRepository<Address, Long> {

}

```

然后我们就可以写一个 main 函数测试一下了，代码如下所示：

```java
package com.terran4j.demo.dsql.appjpa;

import com.terran4j.commons.test.DatabaseTestConfig;
import com.terran4j.commons.util.Strings;
import com.terran4j.demo.dsql.Address;
import com.terran4j.demo.dsql.AddressDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@EntityScan(basePackageClasses = Address.class)
@EnableJpaRepositories(basePackageClasses = AddressDAO.class)
@Import(DatabaseTestConfig.class) // 自动装配默认的数据库配置。
@SpringBootApplication
public class JpaDemoApplication implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(JpaDemoApplication.class);

    @Autowired
    private AddressDAO addressDAO;

    @Override
    public void run(ApplicationArguments appArgs) throws Exception {
        // 清空表中的数据，以避免旧数据干扰运行。
        addressDAO.deleteAll();

        // 添加几条位置数据，以方便下面的查询。
        List<Address> addresses = new ArrayList<>();
        Address address1 = new Address("金域国际中心",
                116.3139456511, 40.0676693732);
        addresses.add(address1);
        Address address2 = new Address("龙泽地铁站",
                116.3193368912, 40.0707811250);
        addresses.add(address2);
        Address address3 = new Address("回龙观地铁站",
                116.3362830877, 40.0707770199);
        addresses.add(address3);
        addressDAO.save(addresses);

        List<Address> result = addressDAO.findAll();
        if (log.isInfoEnabled()) {
            log.info("\n查询结果：{}", Strings.toString(result));
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JpaDemoApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
        System.exit(0);
    }
}
```

注意：这个类上有一行代码：`@Import(DatabaseTestConfig.class)`，
DatabaseTestConfig 是 terran4j-commons-test 子项目提供的一个配置类，
它主要是注入了一个默认的数据源配置，
其作用相当于在 application.properties 文件中自动加入如下配置项：

```
spring.datasource.driverClassName = com.mysql.jdbc.Driver
spring.datasource.url = jdbc:mysql://127.0.0.1:3306/test?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8
spring.datasource.username = root
spring.datasource.password = 
spring.jpa.hibernate.ddl-auto = create
spring.jpa.show-sql = true
spring.jpa.jackson.serialization.indent_output = true
```

这是一种约定优先的设计思想，如果你在本机开发时，本地数据库名为 test，
用户名用 root，无密码（反正本地都是测试数据，无所谓安全性），
也就是符合这些配置约定时，就可以直接引用并运行 main 函数了，
否则还是老老实实的在 application 配置文件中定义吧。

最后我们运行下 main 函数，发现数据写入数据库中了，
所以说 JPA 入手容易，写写简单的 CURD 还是非常容易的。

 
## 编写 DsqlRepository 

以上都还只是 JPA 的知识，从这里开始 DSQL 要闪亮登场了。

这一节，我们要实现一个查询需求：
* 根据指定的位置，查询与其距离最近的一个位置。
* 除了返回位置信息外，还要返回两个位置之间的距离，单位为米。
这个 SQL 就有点小复杂了，我们用 DSQL 来实现。

与 JPA 类似， DSQL 的 Repository 也是需要继承一个接口，代码如下所示：

```java
package com.terran4j.demo.dsql;

import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressDistanceDAO extends DsqlRepository<AddressDistance> {

    @Query("address-nearest")
    AddressDistance getNearest(
            @Param("lat") double lat, @Param("lon") double lon);

}
```

注意，这里的 @Param 仍复用了 JPA 提供的注解，
但 @Query 却是由 DSQL 所定义的，其值定义了一个动态 SQL 文件的名称，
如上面的 @Query("address-nearest") 表示要在这个接口所在的包里面，
定义一个名为 address-nearest.sql.ftl 的文件。
之所以要用 .sql.ftl 作为文件的后缀名，是因为文件里面本质上是要写一段 SQL ，
但可以用 Freemarker 的语法进行渲染，使其具备动态性，
如 address-nearest.sql.ftl 文件内容如下：

```ftl
SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
    POW(SIN(( @{lat} * PI() / 180 - lat * PI() / 180) / 2),2)
    + COS( @{lat} * PI() / 180) * COS(lat * PI() / 180)
    * POW(SIN(( @{lon} * PI() / 180 - lon * PI() / 180) / 2), 2)
)) * 1000) AS distance
FROM demo_address
ORDER BY distance ASC
limit 0, 1
```

这段 SQL 用 ROUND 函数计算了当前位置与入参位置的距离，并且被命名为 distance ，
这样就可以用 distance 字段排序了，整个逻辑都是用 SQL 实现。

注意可以用 @{...} 来引用 DAO 方法中的入参，如上面的 @{lat}, @{lon} 等等。

这段 SQL 查询出来的列，有 address 实体类中的所有列，还新多出来一个  distance 列，
因此不能用 List<Address> 来接收，我们这里定义了一个名为  AddressDistance 的类：

```java
package com.terran4j.demo.dsql;

import com.terran4j.commons.util.Strings;

public class AddressDistance {

    // 位置记录
    private Address address;

    // 此位置与入参所指定位置的距离，单位为米。
    private Long distance;

    // 省略 getter / setter  等方法
    
    public String toString() {
        return Strings.toString(this);
    }

}
```

也就是说，新的类可以复合之前已定义好的实体类，然后只要添加之前所没有字段就可以了，
DSQL 非常的智能，可以根据数据库字段名自动映射到类的属性名，
映射规则与 JPA 也完全一样，即从下划线命名法映射到驼峰命名法，
例如，数据库字段名如果为 max_distance, 会自动映射到名为 maxDistance 的属性上。

最后，我们编写一个新的 main 函数来测试一下：

```java
package com.terran4j.demo.dsql.appdsql;

import com.terran4j.commons.dsql.EnableDsqlRepositories;
import com.terran4j.commons.test.DatabaseTestConfig;
import com.terran4j.demo.dsql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@EntityScan(basePackageClasses = Address.class)
@EnableJpaRepositories(basePackageClasses = AddressDAO.class)
@EnableDsqlRepositories(basePackageClasses = AddressDistanceDAO.class)
@Import(DatabaseTestConfig.class) // 自动装配默认的数据库配置。
@SpringBootApplication
public class DsqlDemoApplication implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DsqlDemoApplication.class);

    @Autowired
    private AddressDAO addressDAO;

    @Autowired
    private AddressDistanceDAO addressDistanceDAO;

    @Override
    public void run(ApplicationArguments appArgs) throws Exception {
        // 清空表中的数据，以避免旧数据干扰运行。
        addressDAO.deleteAll();

        // 添加几条位置数据，以方便下面的查询。
        List<Address> addresses = new ArrayList<>();
        Address address1 = new Address("金域国际中心",
                116.3139456511, 40.0676693732);
        addresses.add(address1);
        Address address2 = new Address("龙泽地铁站",
                116.3193368912, 40.0707811250);
        addresses.add(address2);
        Address address3 = new Address("回龙观地铁站",
                116.3362830877, 40.0707770199);
        addresses.add(address3);
        addressDAO.save(addresses);

        // 当前位置，作为查询的参数
        Address currentAddress = new Address("融泽嘉园一号院",
                116.3086509705, 40.0668729389);

        AddressDistance addressDistance = addressDistanceDAO.getNearest(
                currentAddress.getLat(), currentAddress.getLon());
        if (log.isInfoEnabled()) {
            log.info("\n查询最近位置（指定参数名），当前位置：{},\n最近位置： {}",
                    currentAddress, addressDistance);
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DsqlDemoApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
        System.exit(0);
    }
}
```

注意： 在 private AddressDistanceDAO addressDistanceDAO 这一行可能会报错：

```Could not autowire. No beans of 'AddressDistanceDAO' type found```

原因是 IDEA 不能识别这个自动注入的 Bean，
不过这个错不会影响程序的运行，可以按以下方法忽略掉：
1. 在 IDEA 中打开： Settings -> Editor -> Inspections
2. 在窗口中间找到 Spring -> Spring Core -> Code 
3. 点击 Autowiring for Bean Class 这一项，将右侧的 Severity 从 Error 改为 Weak Warning
4. 点击右下方的 OK 按钮，以完成设置。

注意，在具有 @SpringBootApplication 的类上指定

```
@EnableDsqlRepositories(basePackageClasses = AddressDistanceDAO.class)
```

Spring 容器就会扫描 AddressDistanceDAO 类所在的包，
将这个包（不含子包）中所有继承了 DsqlRepository 接口的接口，都自动注入到容器中，
这与 JPA 提供的 @EnableJpaRepositories 的用法非常的类似。

最后，我们运行一下程序，可以看到控制台有这样的输出：
```
2018-02-20 19:54:27.722  INFO 10876 --- [           main] c.t.commons.dsql.impl.DsqlBuilder        : 
SQL（模板解析后）: 
SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
    POW(SIN(( @{lat} * PI() / 180 - lat * PI() / 180) / 2),2)
    + COS( @{lat} * PI() / 180) * COS(lat * PI() / 180)
    * POW(SIN(( @{lon} * PI() / 180 - lon * PI() / 180) / 2), 2)
)) * 1000) AS distance
FROM demo_address
ORDER BY distance ASC
limit 0, 1
参数: {lon=116.3086509705, lat=40.0668729389}
2018-02-20 19:54:27.768  INFO 10876 --- [           main] com.terran4j.commons.util.Expressions    : parseExpression done: #lat
2018-02-20 19:54:27.772  INFO 10876 --- [           main] com.terran4j.commons.util.Expressions    : parseExpression done: #lon
2018-02-20 19:54:27.848  INFO 10876 --- [           main] c.t.commons.dsql.impl.DsqlExecutorImpl   : 
SQL（变量替换后）: 
SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
    POW(SIN(( ? * PI() / 180 - lat * PI() / 180) / 2),2)
    + COS( ? * PI() / 180) * COS(lat * PI() / 180)
    * POW(SIN(( ? * PI() / 180 - lon * PI() / 180) / 2), 2)
)) * 1000) AS distance
FROM demo_address
ORDER BY distance ASC
limit 0, 1
参数: [ 40.0668729389, 40.0668729389, 116.3086509705 ]
```

DSQL 会对 SQL 经过两次处理：
* 第一次是用 FreeMarker 模板引擎对 .sql.ftl 文件中的内容进行解析，
    获得解析后 SQL 。
* 第二次是对形如 @{...} 的变量占位符进行变量替换后，获得最终可执行的 SQL 。
这两次的 SQL 及对应的参数均会打印到日志中，以方便开发人员排查问题。


# 省略 @Param 注解

@Param 注解是可以省略的，省略后在 .sql.ftl 中参数只能以 arg0, arg1 的方式引用。
如对于方法：

```java
    @Query("address-nearest-2")
    AddressDistance getNearest2(double lat, double lon);
``` 

对应的 .sql.ftl 就得写成这样：
```ftl
SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
    POW(SIN(( @{arg0} * PI() / 180 - lat * PI() / 180) / 2),2)
    + COS( @{arg0} * PI() / 180) * COS(lat * PI() / 180)
    * POW(SIN(( @{arg1} * PI() / 180 - lon * PI() / 180) / 2), 2)
)) * 1000) AS distance
FROM demo_address
ORDER BY distance ASC
limit 0, 1
```

对 java 的反射有了解的朋友可能知道，java 编译成 .class 时参数名没有保留下来，
因此反射调取参数名时，均是 arg0, arg1... 的形式。
后来 java 1.8 时允许指定编译开关 javac -parameters ，以保留参数名，
有兴趣的朋友们可以试试看。

另外，如果方法的参数有且仅有一个，且没有 @Param 注解修饰，
还可以用 args 表示这个参数，如对于 DAO 方法：

```java
    @Query("address-list")
    List<AddressDistance> getAll(AddressQuery params);
```

其中 AddressQuery 类的定义为：

```java
package com.terran4j.demo.dsql;

import com.terran4j.commons.util.Strings;

public class AddressQuery {

    private Double lat;

    private Double lon;

    private String name;

    private boolean nearFirst = true;

    public AddressQuery(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    // 省略 getter / setter  toString 等方法
}
```

对应的 .sql.ftl 文件内容可以这样写：

```ftl
SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
    POW(SIN(( @{args.lat} * PI() / 180 - lat * PI() / 180) / 2),2)
    + COS( @{args.lat} * PI() / 180) * COS(lat * PI() / 180)
    * POW(SIN(( @{args.lon} * PI() / 180 - lon * PI() / 180) / 2), 2)
)) * 1000) AS distance
FROM demo_address
where 1 = 1
<#if args.name ??>
    and name like @{args.name}
</#if>
ORDER BY distance <#if args.nearFirst>ASC<#else>DESC</#if>
```

在 @{...} 中，可以用 Spring EL 表达式的写法，引用参数对象的属性，甚至属性的属性。
只要是 Spring EL 表达式支持就可以。

注意 <#if args.nearFirst>ASC<#else>DESC</#if> 这一行， 
<#if> 是 FreeMarker 的语法，表示分支判断。
FreeMarker 是 Web 开发中非常常用的模板引擎工具，
对 FreeMarker 不了解的朋友们，建议先在网上学习一下 FreeMarker 的语法，
非常的简单，应该不到 30 分钟就可以掌握。 


##  返回结果的类型映射

当 DAO 接口继承 DsqlRepository 接口时，要求指定一个泛型类型，
其方法可以有 3 种返回类型
1. 指定的泛型类型的 List 类型， 这时 SQL 的查询结果可以有 0 到多条记录。
2. 指定的泛型类型，这时 SQL 的查询结果必须是 0 到 1 条记录，多条时会报错。
3. int 类型，这时 SQL 查询结果必须是一个数字，如： SELECT count(*) from ...  的形式。
如下代码所示：

```java

public interface AddressDistanceDAO extends DsqlRepository<AddressDistance> {

    @Query("address-nearest")
    AddressDistance getNearest(
            @Param("lat") double lat, @Param("lon") double lon);

    @Query("address-nearest-2")
    AddressDistance getNearest2(double lat, double lon);

    @Query("address-list")
    List<AddressDistance> getAll(AddressQuery params);

    @Query("address-count")
    int count(@Param("lat") double lat, @Param("lon") double lon,
              @Param("maxDistance") int maxDistance);

}
```


