
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

Redis 的最常用的功能就是缓存数据了，但它提供的很多强大功能，可以帮助
应用系统做更多的事情，比如它的