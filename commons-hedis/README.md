
本项目基于 Redis 提供两个常用的功能：
2. 分布式锁；
3. 轻量级分布式任务调度；

此项目已经放到 github 中，需要源码的朋友请点击
[这里](https://github.com/terran4j/commons/tree/master/commons-hedis)

## 目录

* 项目背景
* ...

## 项目背景

Redis 是一个高性能的 key-value 数据库。 
Redis的出现，很大程度补偿了 memcached 这类 key-value 存储的不足，
在部分场合可以对关系数据库起到很好的补充作用。

Redis 的最常用的功能就是缓存数据了，不过它提供的功能已经超越缓存系统了，
我们可以用 Redis 作更多的事情。

本项目的目标是为了提供一个更简单易用的 Redis 客户端框架，
它集成了 spring-data-redis， Jedis，Redisson 等多种优秀的 Redis 框架，
并针对具体的场景，提供了更方便的使用方式。

本项目被命名为 Hedis ，是 Happy for using Redis 之意，希望大家用 Redis 用得更爽。



## 引入 Hedis 依赖

如果是 maven ，请在 pom.xml 中添加依赖，如下所示：

```xml
        <dependency>
            <groupId>com.github.terran4j</groupId>
            <artifactId>terran4j-commons-hedis</artifactId>
            <version>${hedis.version}</version>
        </dependency>
```

如果是 gradle，请在 build.gradle 中添加依赖，如下所示：

```groovy
compile "com.github.terran4j:terran4j-commons-hedis:${hedis.version}"
```

${hedis.version} **最新稳定版，请参考 [这里](https://github.com/terran4j/commons/blob/master/version.md)**


## 配置 Redis

在 Spring Boot 应用程序中的 application.yml 文件中配置 Redis 服务器的信息，
如下所示：

```yaml

spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: asd123
    pool:
      max-total: 8
      max-idle: 8
      min-idle: 0
      max-wait: 1
      
```

其中：
* host： 表示 Redis 服务器的 IP 或域名，不配置默认是 127.0.0.1 ；
* port： 表示 Redis 服务的端口，默认是 6379 ；
* password： 表示连接 Redis 服务端的认证密码，默认为空（即不需要密码）；
* pool.max-total： 表示 Redis 客户端连接池的总大小，不配置时默认是 8 ；
* pool.max-idle： 表示 Redis 客户端连接池中连接的最大空闲数，
    当空闲连接数大于此值时，多余的连接会释放掉，
    当配置大于连接池总大小，会自动设置成等于连接池总大小；
* pool.min-idle： 表示 Redis 客户端连接池中连接的最大空闲数，
    不能超过最大空闲数的一半，当不配置时默认是 0 ；
* pool.max-wait： 表示 Redis 客户端获取连接时的最大超时时间，单位为毫秒，
    不配置默认是 -1 ，表示永不超时；
* pool.max-total： 表示 Redis 客户端连接池的大小，不配置默认是 8 ；
* cache.defaultExpirationSecond： 表示在 Spring 缓存管理器中，
    默认的缓存过期时间，单位为秒，不配置时默认为 30 秒。


# 启用 Hedis 服务

本教程的示例代码在 src/test/java 目录的 com.terran4j.demo.hedis 中，
您也可以从 [这里](https://github.com/terran4j/commons/tree/master/commons-hedis/src/test/java/com/terran4j/demo/hedis) 获取到。

首先，我们需要在有 @SpringBootApplication 注解的类上，添加 @EnableHedis 注解，
以启用 Hedis 服务，如下代码所示：

```java

package com.terran4j.demo.hedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.hedis.config.EnableHedis;

@EnableHedis
@SpringBootApplication
public class HedisDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HedisDemoApplication.class);
		app.setWebEnvironment(false);
		app.run(args);
	}

}

``` 

注意，启动 Hedis 服务后， Hedis 会按配置去连接 Redis 服务器，
请确保 Redis 服务器是开启的并配置正确。


# 使用 CacheService 

