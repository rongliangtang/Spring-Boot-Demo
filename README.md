# Spring-Boot-Demo

## 项目简介

这是一个深入学习 SpringBoot 3 的项目，包含实现不同需求的**示例和教程**。

**SpringBoot 2 官方已经不再维护。这个仓库也许对旧项目升级，或者做新项目的你有所帮助。**

目前已经实现以下 Demo，正在不断更新中。

如果有不清楚的地方，可以提 issue。

| **demo**  |      **介绍**      |
|:---------:|:----------------:|
|  demo-ut  |      实现集成测试      |
|  demo-it  |      实现单元测试      |
| demo-i18n | 实现异常消息的 i18n 国际化 |
|    ...    |       ...        |

## 分支介绍

- master 分支 : 存放目前已经实现的 demo，每个文件夹是一个可以独立运行的 demo。
- dev 分支 : 开发分支，新的 demo 在这个分支上编写，然后 merge 到 master 分支上。

## 开发环境

- Spring Boot 3.1.12
- JDK 21
- MySql 8.0.33
- IDEA 2024.1.4



## 运行方式

1. `git clone https://github.com/rongliangtang/Spring-Boot-Demo.git`
2. 使用 IDEA 打开想要运行的 demo

   点击"File"菜单，选择"New"，再选择 "Project from Existing Sources"，最好选择 "maven"，就成功导入了。
3. 找到 Application 类就可以运行了

**注意：每个 demo 均有详细的 README 教程**

**注意：运行 demo 之前，有些是需要初始化数据库数据的**

## TODO

1. 增加 Spring Boot 接入 日记系统 相关 demo
2. 增加 Spring Security 安全框架 相关 demo
3. 增加 Spring Boot 接入 动态数据源 相关 demo
3. 增加 Spring Boot 接入 ORM 相关 demo
4. 增加 Spring Boot 接入 redis 相关 demo
5. 增加 Spring Boot 接入 websocket 相关 demo
5. 增加 Spring Boot 接入 minio 相关 demo
6. 增加 Spring Boot 接入 fastdfs 相关 demo
7. 增加 Spring Boot 接入 neo4j 相关 demo
8. 增加 Spring Boot 接入 flyway 相关 demo
9. 增加 Spring Boot 接入 OpenTelemetry 相关 demo
10. 增加 Spring Boot 接入 OpenFeign 服务调用 相关 demo
11. 增加 Spring Boot 接入 SkyWalking 链路追踪 相关 demo
12. 增加 Spring Boot Actuator 服务监控 相关 demo
10. 增加 Spring Cloud Stream 接入 RocketMQ 消息队列 相关 demo
11. 增加 Spring Cloud 接入 Spring Cloud Alibaba Nacos 服务注册和发现 相关 demo
12. 增加 Spring Cloud 接入 Spring Cloud Alibaba Sentinel 熔断限流 相关 demo
13. 增加 Spring Cloud 接入 Spring Cloud Alibaba Seata 分布式事务 相关 demo



