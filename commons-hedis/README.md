
本项目基于 Redis 提供两个常用的功能：
1. 分布式锁；
2. 轻量级分布式任务调度；

此项目已经放到 github 中，需要源码的朋友请点击
[这里](https://github.com/terran4j/commons/tree/master/commons-hedis)

## 目录

* 项目背景
* 引入 Hedis 依赖
* 在 application.yml 中配置 Redis 
* 启用 Hedis 服务
* 使用 CacheService 缓存服务
* 实现分布式同步
* 实现轻量级分布式定时调度

## 项目背景

Redis 是一个高性能的 key-value 数据库。 
Redis的出现，很大程度补偿了 memcache 这类 key-value 存储的不足，
在部分场合可以对关系数据库起到很好的补充作用。

Redis 的最常用的功能就是缓存数据了，不过它提供的功能已经超越了缓存系统，
我们可以用 Redis 做更多的事情。

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

本教程的示例代码在 src/test/java 目录的 com.terran4j.demo.hedis 包中，
您也可以从 [这里](https://github.com/terran4j/commons/tree/master/commons-hedis/src/test/java/com/terran4j/demo/hedis) 获取到。


## 在 application.yml 中配置 Redis 

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

Hedis 向 Spring 容器中注册了一个 CacheService 服务，
它提供读写缓存数据的常用接口方法，CacheService 的用法如下代码所示：

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

CacheService 提供了以下常用的缓存方法，如：
* 读取 / 写入 单个对象；
* 读取 / 写入 整个 Map 对象； 
* 读取 / 写入 一个 Map 中的单个条目。

这对于大多数缓存的使用场景已经足够了，若需要更多的 Redis 接口，可以直接引用：
* RedisTemplate<String, String> 
* Jedis

这两个服务之一（推荐用 RedisTemplate），里面有更多的接口方法可用。


## 实现分布式同步

Hedis 还集成了 [Redisson](https://blog.csdn.net/u014042066/article/details/72778440) 开源项目，
Redisson 基于 Redis 提供了很多强大的功能，其中就包括“分布式锁”。

用 Redis 实现分布式锁的原理，请参看 [这里](http://ifeve.com/redis-lock/) 。
简单来说，就是在执行同步块代码之前，先用 SETNX 操作向 Redis 写入一个 key - value 记录，
写入成功，表示获取到对应的锁，就可以真正执行同步块中的代码；
执行完成后，就删除这个 key - value 记录，表示释放了锁。
（当然，在分布式环境下，实际的算法其实比这个复杂得多）

而 Hedis 则在 Redisson 的基础上，让分布式锁的使用变得非常简单。

只需要在 Spring Bean 的方法上加上 `@DSynchronized`  注解，
就可以对这个方法用分布式锁实现同步，如下代码所示：

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
     * 对 incrementAndGet 方法加上分布式并发控制。
     */
    @DSynchronized("'incrementAndGet-' + #key")
    public int incrementAndGet(@Param("key") String key) {
        return doIncrementAndGet(key);
    }
    
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

}

```

@DSynchronized 类似于 java 提供的 synchronized 关键字，不过不同的是：
synchronized 只是在单个 JVM 范围内实现了多线程同步，而 @DSynchronized
是在应用的整个集群范围内，也就是多个节点之间实现了多线程同步。

在 @DSynchronized 中，可以用 Spring EL 表达式来定义申请分布式锁的 key ，
如上面的 `'incrementAndGet-' + #key` ，两个单引号 '' 包裹的是字符串常量，
用 # 号引用的是变量，用 + 号可以把这两部分拼接起来，这些都遵守
[Spring EL 表达式](https://www.jianshu.com/p/25dcb764654e) 的语法。
在方法中可以用 `@Param` 注解来使参数值成为 Spring EL 中的变量值。

这个在 @DSynchronized 中的 Spring EL 表达式，以后我们称为“锁表达式”。

如下面的代码：

```java

@Service
public class CountService {
    
    // 省略其它代码......
    
    @DSynchronized("'incrementAndGet-' + #key")
    public int incrementAndGet(@Param("key") String key) {
        return doIncrementAndGet(key);
    }
    
}
```

方法 incrementAndGet 的锁表达式为： 'incrementAndGet-' + #key 。
比如调用时参数 key = "k1"，则会在调用前申请一个名为 "incrementAndGet-k1" 的锁，
申请到这个锁了，才能继续执行此方法，没有申请到锁就只能等待别人执行完后释放锁。

也就是说，每次调用方法，都会根据锁表达式来生成一个 key，key 值相同才会竞争锁，
如果有参数值的参与，这个锁的粒度就更细了，因获取不到锁而被阻塞的概率就会小很多。

注意：请根据您的业务逻辑小心的定义锁表达式，请尽量的让锁的粒度更细。
当然，你也可以图省事而不定义锁表达式，如下代码所示：

```java

@Service
public class CountService {
    
    // 省略其它代码......
    
    @DSynchronized
    public int incrementAndGet(String key) {
        return doIncrementAndGet(key);
    }
    
}

```

这样 Hedis 会自动生成锁表达式 , 格式为：`${类名}::${方法名}(${参数类型列表})`，
如上面的示例代码，生成的锁表达式为： 
```
com.terran4j.demo.hedis.CountService::incrementAndGet(java.lang.String)
```
这样生成的锁有两个问题：
1. 代码重构（如包名、类名、方法名改了）后锁名就自动变了，
    重上线时原来申请到的锁会失效。
2. 一个线程申请到锁后，会阻塞住所有调用此方法的其它线程，性能可能会非常差。

从概念上讲，这有点像数据库中的表锁，如果带上了参数，就像是行锁，性能当然更好了。

因此：`强烈建议仔细的定义锁表达式，千万不要图省事而省略掉它`。

当然，对方法的同步虽然简单，也能满足于大多数同步的需求，
但还是有不少场景对分布式锁有更复杂的操作逻辑，
你可以直接引用`RedissonClient` （Hedis 已经将它注入到 Spring 容器中）来实现。


## 实现轻量级分布式定时调度

在业务系统中，我们经常需要在后台执行一些定时任务调度，
用 Spring Scheduling 框架可以很容易的实现，如下代码所示：

```java

package com.terran4j.demo.hedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LoopIncrementJob {

	private static final Logger log = LoggerFactory.getLogger(LoopIncrementJob.class);

	private static final String key = "demo3-counter";

	@Autowired
	private CountService countService;

	@Scheduled(cron = "0/1 * * * * *")
	public void loopIncrement() {
		int count = countService.doIncrementAndGet(key);
		log.info("\nloopIncrement, counter = {}", count);
	}

}

```

@Scheduled 是 Spring Scheduling 框架提供的注解，cron = "0/1 * * * * *" 是每秒执行一次的意思，
因此 @Scheduled(cron = "0/1 * * * * *") 的意思就是：
 Spring Scheduling 框架会每秒调用一次 loopIncrement() 方法。
 
但 Spring Scheduling 框架是不支持分布式的，也就是说：
当应用程序部署到一个多个节点时，每个节点都会独立的执行这个定时调度，
如果代码中要操作共享的资源时，可能会出问题。
 
业界有许多提供分布式任务调度的开源项目，如： quartz、TBSchedule、elastic-job、Saturn 等，
但都比较重量级，如：资源占用多，部署复杂，学习成本高。
所以 Hedis 提供了一种非常简单的方式进行分布式调度，
简单到只加一个 @DScheduling 注解就搞定了，如下代码所示：
 
 ```java

package com.terran4j.demo.hedis;

import com.terran4j.commons.hedis.dschedule.DScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LoopIncrementJob {

    private static final Logger log = LoggerFactory.getLogger(LoopIncrementJob.class);

    private static final String key = "demo3-scheduling-counter";

    @Autowired
    private CountService countService;

    @DScheduling(lockExpiredSecond = 3)
    @Scheduled(cron = "0/1 * * * * *")
    public void loopIncrement() {
        int count = countService.doIncrementAndGet(key);
        log.info("\nloopIncrement, counter = {}", count);
    }

}

```
 
@DScheduling 必须修饰在有 @Scheduled 修饰的方法上，
表示对这个定时任务采用分布式调度，它的算法为：
1. 每次调用此方法（如上面的 loopIncrement() 方法）时，都尝试申请一个分布式锁；
2. 如果没有获取到锁（通常意味其它节点获取到锁了），则本节点跳过此次执行。
3. 如果获取到锁了，还会检查上次调用的执行时间：
* 如果上次的执行时间超过了调度周期，则执行本次调度，并记录本次的执行时间（到Redis）；
* 如果上次的执行时间未超过调度周期，则跳过本次调度。

所谓“调度周期”，是指两次相邻的调度执行的时间差，如 cron = "0/1 * * * * *"
表示每秒调度一次，则“调度周期”为 1 秒。
@DScheduling 中有一个 lockExpiredSecond 属性，表示分布锁的过期时间，
建议比调度周期略长，并且要确保任务的执行时间要远小于此 lockExpiredSecond 值。


## 声明式缓存

Spring Cache 提供了声明式缓存，即在方法上定义一些注解即可对数据进行缓存，
不清楚的朋友们可先从这篇文章进行了解： [Spring Cache相关注解介绍](https://blog.csdn.net/poorcoder_/article/details/55258253)

而 Hedis 注入了 RedisCacheManager 服务，以实现了声明式缓存。
（其实主要是 Spring Cache 在起作用，本文就不过多介绍了，有问题请百度！）
