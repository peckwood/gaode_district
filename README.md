- 

This project fetch all Chinese districts(province, city, district, street) from [Gaode map](https://amap.com/) API and inserts them into database.

### How to run

1. import project in Idea
2. reload Gradle project
3. change `key` in `Application.java` with your own Gaode key
4. run

### TODO

- use external properties or yml
- add connection pool

### Tech stack

- gradle
- mysql
- Hibernate
- lombok
- log4j2
- Java 11 Http Client https://openjdk.java.net/groups/net/httpclient/intro.html
- Jackson (to convert json into Object)
- apache httpclient

### Skill points

- Gradle

  - how to build a quick start Java application with Gradle

- Hibernate

  - map entity to table and create table based on entity
    - how to ignore certain properties from being mapped to table columns
    - how to mark a property as auto increment for MySQL
  - Integrate Java application with Hibernate
  - Obtain session with HibernateUtil
  - log sql statements

- log4j2

  - use within quick start application

- Gaode Map

  - get districts using API https://lbs.amap.com/api/webservice/guide/api/district

- Jackson

  - convert json to object

  - how to implement a custom json deserializer for irregular json field(citycode)

    - > see `GaodeCityCodeDeserializer.java`

- Java 11 Http Client

  - how to build request
  - how to send synchronized request
  - how to fetch body from response
  
- apache httpclient

  - how to build URI

---

本项目从高德API获取所有的地区(省, 市, 区, 街道)并插入数据库

### How to run

1. import project in Idea
2. reload Gradle project
3. change `key` in `Application.java` with your own Gaode key
4. run

### TODO

- 使用外部配置文件properties或yml
- 使用连接池

### 技术栈

- gradle
- mysql
- Hibernate
- lombok
- log4j2
- Java 11 Http Client
- Jackson (to convert json into Object)
- apache httpclient

### 知识点

- Gradle

  - Gradle构建quick start Java项目

- Hibernate

  - 构建实体类与表的关系映射并通过实体类建表
    - 如何防止实体类的一些属性被创建为列
    - 如何给Mysql表指定自增字段
  - Integrate Java application with Hibernate
  - 通过HibernateUtil获取session
  - 显示运行的sql语句

- log4j2

- 高德地图API

  - 通过APIhttps://lbs.amap.com/api/webservice/guide/api/district获取地区

- Jackson

  - json转对象

  - 自定义反序列化方式来反序列化citycode列

    - > 见 `GaodeCityCodeDeserializer.java`

- Java 11 Http Client

  - 构建URI and request
  - 发送请求
  - 从响应中获取body
  
- apache httpclient

  - 构建URI
