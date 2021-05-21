## 基于 prometheus 的监控
### 接入方式
#### spring-boot 接入方式

```xml
<dependency>
  <groupId>com.sentry</groupId>
  <artifactId>sentry-agent-core</artifactId>
  <version>1.2.0</version>
</dependency>
```

- pom中新增依赖
- 新增哨兵配置文件 sentry-agent.yml，放在 resources 目录下， 程序读取的方式是 classpath:sentry-agent.yml
> 配置文件中， javaMethods 表示对 java方法的监控， 其中 pattern 以正则匹配方法类名

- url 支持 exclude 与 include 两种方式， exclude 优先
> url 以 method 方式匹配， method 有以下几种：regex, startwith, endwith, equal, all, rest, include
> exclude 支持 pattern 与 method 两个参数
> include 支持 pattern, method, action, target, successCode 四个参数
> - action 表示以参数区分请求，支持特定参数
> - target 表示设置某一类请求的记录方式为 target， 适用于 pathVariable 的场景

- tags 表示在 prometheus 收集数据时，额外的标签，是一个 hashMap
> spring-boot 环境下因加载顺序问题，需要在 bootstrap.yml 下增加 ，不需要在 sentry-agent.yml 中添加 tags

```yaml
management:
  metrics:
    tags:
      application: ${spring.application.name}
```

```yaml
sentry:
  agent:
    javaMethods:
    - pattern: com\.sentry\.demo\..*

    url:
    excludes:
        - method: endwith
        pattern: .png,.PNG,.jpg,.JPG,.gif,.ico,.js,.css,.html

    includes:
        - method: all
```


#### spring mvc 接入方式
- pom.xml 新增依赖

```xml
<dependency>
  <groupId>com.sentry</groupId>
  <artifactId>sentry-agent-spring</artifactId>
  <version>1.2.0</version>
</dependency>
```

- web.xml 中新增 contextparam
```xml
<context-param>
  <param-name>contextInitializerClasses</param-name>
  <param-value>com.sentry.agent.spring.SentryAgentContextInitializer</param-value>
</context-param>
```

- 新增配置文件，与 spring-boot 配置方式相同， 需要单独设置 tags

#### 一般 servlet 接入方式
- pom.xml 新增依赖
```xml
<dependency>
  <groupId>com.sentry</groupId>
  <artifactId>sentry-agent-core</artifactId>
  <version>1.2.0</version>
</dependency>
```

- web.xml 中新增 listener
- 新增project.name （也可以通过系统参数传入， mvn 下是  -Dproject.name=xxx， java 命令下是 --project.name=xxx），这个参数用于查看字节码修改的日志， 如果没有的话会命名为unknown

```xml
<listener>
  <listener-class>com.sentry.agent.core.web.SentryListener</listener-class>
</listener>

<context-param>
  <param-name>project.name</param-name>
  <param-value>myProject</param-value>
</context-param>
```

- resources 下新增  sentry-agent.xml（注意这里是 xml 格式）
```xml
<root name="sentry-demo">
    <javaMethod>
        <class pattern="com\.sentry\.demo..*"/>
    </javaMethod>
    <url>
        <exclude method="endwith" pattern=".jpg,.png"/>
        <include method="all"/>
    </url>
    <tags>
        <tag key="application" value="sentry-demo"/>
    </tags>
</root>
```

支持维度
- 总览
![img.png](https://github.com/moonaries90/simple-sentry/raw/main/imgs/img6.png)
- jvm 监控
![img.png](https://github.com/moonaries90/simple-sentry/raw/main/imgs/img.png)
- url 监控
![img_1.png](https://github.com/moonaries90/simple-sentry/raw/main/imgs/img_1.png)
- javaMethod监控
![img_2.png](https://github.com/moonaries90/simple-sentry/raw/main/imgs/img_2.png)
- sql 监控（支持mysql5、mysql8、oracle）
![img_3.png](https://github.com/moonaries90/simple-sentry/raw/main/imgs/img_3.png)
- httpClient监控（目前仅支持 org.apache.httpcomponents）
![img_4.png](https://github.com/moonaries90/simple-sentry/raw/main/imgs/img_4.png)
- redis 监控
![img_5.png](https://github.com/moonaries90/simple-sentry/raw/main/imgs/img_5.png)
- 自定义业务监控项
```java
// 支持自增长的数据，例如访问量
CustomAggregator.addAndGet("testMyCustom", 123);
// 支持变化的数据， 例如当前在线人数
CustomAggregator.getAndSet("testGet", 123);
```



### 支持
- 支持 javaMethod 维度
- 支持 sql（oracle6、oracle7、mysql5、mysql8）
- 支持 url
- 支持 httpClient
- 支持 redis

### releases-changes
- 1.0.0 基于 spring-boot 版本
- 1.0.1 新增基于 servlet 的版本
- 1.0.2 新增基于 spring-mvc 的版本
- 1.0.4 新增 java logging 日志，用于排查问题
- 1.0.5 bug fix, 处理 url code 无法正常记录的问题
- 1.0.6 sql、javaMethod、url 新增开关
- 1.0.7 新增 mysql 支持， 优化 section 统计维度
- 1.0.8 新增 apache httpClient 支持, 新增自定义监控项
- 1.0.9 修复 url 监控的错误
- 1.1.0 处理自定义类加载器卸载类导致 transformer 失效的问题
- 1.1.1 新增 javaMethod 忽略自定义异常
- 1.2.0 字节码增强由 javassist 修改为 asm，新增 redis
- 1.2.1 修复 aggregator 在热部署时的统计不生效的BUG，millis 修改为 nano
