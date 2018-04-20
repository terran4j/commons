
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

本教程的示例代码在 src/test/java 目录的 com.terran4j.demo.hedis 中，
您也可以从 [这里](https://github.com/terran4j/commons/tree/master/commons-hedis/src/test/java/com/terran4j/demo/hedis) 获取到。


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


首先，我们需要在有 @SpringBootApplication 注解的类上，添加 @EnableHedis 注解，
以启用 Hedis 服务，如下代码所示：

```java

package com.terran4j.demo.hedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.hedis.config.EnableHedis;

@EnableHedis
@SpringBootApplication
public class HedisDemoApp {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HedisDemoApp.class);
		app.setWebEnvironment(false);
		app.run(args);
	}

}

``` 

注意，启动 Hedis 服务后， Hedis 会按配置去连接 Redis 服务器，
请确保 Redis 服务器是开启的并配置正确。


# 使用 CacheService 缓存服务

Hedis 向 Spring 容器中注册了一个 CacheService 服务，它提供读写缓存数据
的常用接口方法，CacheService 的用法如下代码所示：

```java

package com.terran4j.demo.hedis;

import com.terran4j.commons.hedis.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DemoCacheService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoCacheService.class);

    @Autowired
    private CacheService cacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("演示如何使用 CacheService 服务");

        //  向缓存写入对象。
        String key = "u1";
        User user = new User(1, "neo", new Date());
        cacheService.setObject(key, user, null);

        // 从 缓存中读取对象。
        User value = cacheService.getObject(key, User.class);
        log.info("cache key = {}, value = {}", key, value);
    }

}

```

CacheService 的实现是基于 Spring 提供的 RedisTemplate 类。
对象的“序列化 / 反序列化”是用的 Json 的方式，因而具有较高的灵活性。

为了让使用简单，CacheService 只提供了几个常用的缓存方法，如：
* 读取 / 写入 单个对象；
* 读取 / 写入 整个 Map 数据； 
* 读取 / 写入 一个 Map 中的单个条目。

这对于大多数缓存的场景，已经足够了，若需要更多的 Redis 接口，可以直接注入：
* RedisTemplate<String, String> 
* Jedis
这两个服务，里面有更多的接口方法可用。


## 使用分布式同步方法

Hedis 还集成了 [Redisson](https://blog.csdn.net/u014042066/article/details/72778440) 开源项目，
Redisson 基于 Redis 提供了很多的功能强大功能，其中就包括“分布式锁”。
（用Redis 实现分布式锁的原理，请参看 [这里](http://ifeve.com/redis-lock/) ）
而 Hedis 则基于 Redisson 让分布式锁的使用更简单。

只需要在 Spring Bean 的方法上加上 `@DSynchronized`  注解，
就可以对这个方法实现分布式同步控制，如下代码所示：

```java

package com.terran4j.demo.hedis;

import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.hedis.dsyn.DSynchronized;
import com.terran4j.commons.util.error.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class CountService {

    @Autowired
    private CacheService cacheService;

    /**
     * 一个没有并发控制的递增计算，需要调用方避免并发访问。
     */
    private int doIncrementAndGet(String key) {
        // 从 Redis 缓存中取出计数器变量：
        Integer counter;
        try {
            counter = cacheService.getObject(key, Integer.class);
            if (counter == null) {
                counter = 0;
            }
        } catch (BusinessException e1) {
            throw new RuntimeException(e1);
        }

        // 故意让线程休眠一段时间，让并发问题更严重。
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore.
        }

        // 在本地让计数器变量加 1：
        counter++;

        // 将变量写回 Redis 缓存：
        cacheService.setObject(key, counter, null);
        return counter;
    }

    /**
     * 对 incrementAndGet 方法加上分布式并发控制。
     */
    @DSynchronized("'incrementAndGet-' + #key")
    public int incrementAndGet(@Param("key") String key) {
        return doIncrementAndGet(key);
    }

}

```

@DSynchronized 类似于 java 提供的 synchronized 关键字，不过不同的是：
synchronized 只是在 JVM 范围内实现了并发同步，而 @DSynchronized 方法
是在整个应用程序的集群范围内，也就是多个节点之间实现了并发的同步控制。

@DSynchronized 内部是用 Redisson 分布式锁的原理实现的，所以它需要
提供一个字符串 key 来定义这个锁，这个 key 非常重要，因为会用这个 key 
在 Redis 中作为键用 SETNX 命令去写入一个值，写入成功就表示获取到这个锁了。
但如果有一个节点已写入相同的 key （且还未失效），则按 SETNX 的原理，
其它节点就无写入这个 key 了。

在 @DSynchronized 中，可以用 Spring EL 表达式来定义这个 key ，
如上面的 `'incrementAndGet-' + #key` ，两个单引号 '' 包裹的是字符串常量，
用 # 号引用的是变量，用 + 号可以把这两部分拼接起来，这些都