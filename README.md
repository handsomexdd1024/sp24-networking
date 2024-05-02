# 未来互联网物流管理系统开发

> tsim
>
> author：**Lev** | **Dream_Autumn** | **handsomexdd1024** | **周明巍** | **单敬雯**

## 项目日志



- 5.2
  - 上传脚手架与数据库`Lev`
  - 完成用户、管理员身份设计，完成路由管理`Lev`
  - 完成注册登录，个人信息管理功能`Lev`



## IDEA Clone事项

1. 需要配置
   - nodejs16
   - jdk1.8
   - mysql5.7
   - idea



2. maven配置

克隆后请检查pom.xml,如果为此图标

![image-20240502150621818](README.assets/image-20240502150621818.png)

则表明maven配置已完成，进行vue配置



3. vue配置

检查vue目录下是否有vue/node_modules

- 如果没有，在终端执行

  ```
  cd vue
  npm i
  ```

  安装Vue

- 如果有，进行启动配置

  > 编辑配置-> + -> npm -> package.jason(P)：选择jason -> 脚本(T)选择：serve

- 启动



## 项目结构



### manager



#### springboot[后端目录]



- src-main

  - java

    - com.example

    - common

      - config

        - CorsConfig.java

          > 后端跨域配置（允许前端访问接口）

        - JwtInterceptor.java

          > 拦截器（拦截请求，做过滤）

        - WebConfig.java

          > 配置Jwt拦截器，通过其中一个配置类生气

      - enums

        > 枚举，定义编码/信息

        - ResultCodeEnum.java

        > 定义状态码的枚举类

        - RoleEnum.java

          > 角色枚举类

      - Constants.java

      > 常量

      - Result.java

        > 接口返回的通用返回类，包装controller返回的数据

    - controller✨

      > mybatis架构返回数据两种方式：
      >
      > 1. 注解
      >
      > 2. xml
      >
      >    controller->service->mapper

    - entity

      - Account.java

        > 所有账户基类

      - Admin.java

    - exception

      - GlobalExceptionHandler.java

        > 全局异常处理

    - mapper

    - service

    - utils

  - resources

    - mapper
    - application.yml

- pom.xml

### vue[前端目录]



- public

  > 工程入口

- src

  - assets

    > 静态文件

  - cmoponents

    > 组件，封装验证码等

  - router

    > 路由（跳转地址栏变化）

  - utils

    - request.js

      > axios封装

  - views

    > 前后台页面

    - front
    - manager

  - App.vue

    > Vue的入口

  - main.js

    > 导入所有需要的文件

- .env.development

  > 后台开发URL

- .env.production

  > 后端打包URL

- package.json

> 依赖配置

- vue.config.js

> Vue全局配置文件

