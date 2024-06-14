# 未来互联网物流管理系统开发

> 基于
>
> 作者与贡献比：**严文轩 **55% | **周明巍**25% | **单敬雯**10% | **李浩鑫**10%

## 项目提交日志



- 5.2
  
  - 上传脚手架与数据库 `严文轩`
  - 完成用户、管理员身份设计，完成路由管理 `严文轩`
  - 完成注册登录，个人信息管理功能 `严文轩`
  
- 5.5

  - 添加基础数据模型 `杨濛`

    > 定义了实体，然而不符合mybatis代码规范，没有启用

- 6.1

  - 基础Echarts地图端 `严文轩`
  - 在地图端静态展示站点和三种运输方式 `严文轩`
  - 站点管理后端 `严文轩`
  - 路径管理后端 `严文轩`
  - 货物管理后端 `严文轩`
  - 站点货物关联管理后端 `严文轩`
  - 实现了管理端站点与路径管理的初步界面 `严文轩`
  - 实现了地图端动态从数据库获取站点和三种路径 `严文轩`

- 6.2

  - 添加了站点管理端的部分相关功能 `单敬雯`
  - 添加了一二线城市、新一线城市相关信息作为常量，管理员可以选择常量直接部署站点，也可以选择手动输入站点经纬度添加临时站点  `严文轩`
  - 添加了货物管理端的部分相关功能 `李浩鑫`

- 6.5

  - 完成了监管者身份的数据库与后端以及管理界面  `严文轩`
  - 添加了三种路径的速度与成本常量  `严文轩`
  - 完成了货物表与站点货物表关联管理的功能  `严文轩`
  - 基于无向有权图的蚁群最短路径算法实现  `周明巍`

- 6.7

  - 完成物资根据站点分页查询  `严文轩`
  - 完成无向有权图的蚁群算法获取list内最佳城市结点 `周明巍`
  - 完善了站点管理端的相关功能 `单敬雯`

- 6.9

  -  对蚁群算法动态接入的部分尝试  `严文轩`
  - 管理端数据可视化Echarts图表展示 `李浩鑫`

- 6.10

  - 基于货物类型与名称的最快速调度算法路径选择静态实现  `严文轩`
  - 蚁群算法更新存储图 `周明巍`
  - 最快速调度算法添加修改后端数据与返回调货记录到前端的功能  `严文轩`

- 6.11

  - 最快速调度算法完成所有功能  `严文轩`
  - 最经济调度算法实现  `严文轩`
  - 前端适配算法最快速、最经济算法的部分功能 `严文轩`
  - 超管身份验证与功能分配 `严文轩`
  - 将蚁群算法返回结果展示在前端 `严文轩`
  - 修改蚁群算法，能够根据数据库动态生成图 `严文轩`
  - 添加了使用蚁群算法调货的逻辑 `严文轩`
  - 地图端添加展示算法规划的路径信息 `严文轩`

- 6.12

  - 地图端健壮测试以及debug `严文轩`
  - 管理端健壮测试以及debug `严文轩`
  - 算法健壮测试，返回信息调整以及debug `严文轩`




## IDEA Clone事项

1. 需要配置
   - nodejs16
   - jdk18
   - mysql5.7
   - idea



2. maven配置

IDEA克隆后右键点击pom.xml,创建为maven项目





3. vue配置

- 在项目根目录下终端执行

  ```
  cd vue
  npm i
  ```

  安装Vue

- 进行前端启动配置npm run serve

  > 编辑配置-> + -> npm -> package.jason(P)：选择jason -> 脚本(T)选择：serve



4. Echarts安装

   在根目录下安装Echarts，在项目根目录下终端执行

```
npm install echarts --save
```



- 启动前后端



## 项目结构

springboot-vue智能物流管理系统项目结构

### 后端

springboot
│  ├─ pom.xml
│  └─ src
│      └─ main
│          ├─ java
│          │  └─ com.example
│          │      ├─ SpringbootApplication.java
│          │      ├─ common
│          │      │  ├─ City.java
│          │      │  ├─ Constants.java
│          │      │  ├─ Result.java
│          │      │  ├─ config
│          │      │  │  ├─ CorsConfig.java
│          │      │  │  ├─ JwtInterceptor.java
│          │      │  │  └─ WebConfig.java
│          │      │  └─ enums
│          │      │      ├─ MemberEnum.java
│          │      │      ├─ ResultCodeEnum.java
│          │      │      └─ RoleEnum.java
│          │      ├─ controller
│          │      │  ├─ AdminController.java
│          │      │  ├─ DispatchController.java
│          │      │  ├─ FileController.java
│          │      │  ├─ GoodsController.java
│          │      │  ├─ NoticeController.java
│          │      │  ├─ RouteController.java
│          │      │  ├─ StationController.java
│          │      │  ├─ StationGoodsController.java
│          │      │  ├─ SupervisorController.java
│          │      │  └─ UserController.java
│          │      ├─ dispatch
│          │      │  └─ PathNode.java
│          │      ├─ dto
│          │      │  ├─ DispatchResult.java
│          │      │  └─ Operation.java
│          │      ├─ entity
│          │      │  ├─ Account.java
│          │      │  ├─ Admin.java
│          │      │  ├─ Goods.java
│          │      │  ├─ GoodsTypeNum.java
│          │      │  ├─ Notice.java
│          │      │  ├─ Route.java
│          │      │  ├─ Station.java
│          │      │  ├─ StationGoods.java
│          │      │  ├─ Supervisor.java
│          │      │  └─ User.java
│          │      ├─ exception
│          │      │  ├─ CustomException.java
│          │      │  └─ GlobalExceptionHandler.java
│          │      ├─ graph
│          │      │  ├─ graph.json
│          │      │  ├─ Ant
│          │      │  │  └─ AntColonyOptimization.java
│          │      │  └─ graph_base
│          │      │      ├─ Edge.java
│          │      │      ├─ Graph.java
│          │      │      └─ Node.java
│          │      ├─ services
│          │      │  └─ BestNode.java
│          │      ├─ mapper
│          │      │  ├─ AdminMapper.java
│          │      │  ├─ GoodsMapper.java
│          │      │  ├─ NoticeMapper.java
│          │      │  ├─ RouteMapper.java
│          │      │  ├─ StationGoodsMapper.java
│          │      │  ├─ StationMapper.java
│          │      │  ├─ SupervisorMapper.java
│          │      │  └─ UserMapper.java
│          │      ├─ model
│          │      │  ├─ Goods.java
│          │      │  ├─ GoodsItem.java
│          │      │  ├─ Route.java
│          │      │  ├─ Station.java
│          │      │  ├─ TransportTask.java
│          │      │  └─ TransportType.java
│          │      ├─ service
│          │      │  ├─ AdminService.java
│          │      │  ├─ DispatchService.java
│          │      │  ├─ GoodsService.java
│          │      │  ├─ NoticeService.java
│          │      │  ├─ RouteService.java
│          │      │  ├─ RouteWeightCalculator.java
│          │      │  ├─ StationGoodsService.java
│          │      │  ├─ StationService.java
│          │      │  ├─ SupervisorService.java
│          │      │  └─ UserService.java
│          │      └─ utils
│          │          └─ TokenUtils.java
│          └─ resources
│              ├─ application.yml
│              └─ mapper
│                  ├─ AdminMapper.xml
│                  ├─ GoodsMapper.xml
│                  ├─ NoticeMapper.xml
│                  ├─ RouteMapper.xml
│                  ├─ StationGoodsMapper.xml
│                  ├─ StationMapper.xml
│                  ├─ SupervisorMapper.xml
│                  └─ UserMapper.xml



### 前端

vue
│  ├─ .env.development
│  ├─ .env.production
│  ├─ babel.config.js
│  ├─ jsconfig.json
│  ├─ package-lock.json
│  ├─ package.json
│  ├─ vue.config.js
│  ├─ public
│  │  ├─ favicon.ico
│  │  └─ index.html
│  └─ src
│      ├─ App.vue
│      ├─ main.js
│      ├─ assets
│      │  ├─ css
│      │  │  ├─ front.css
│      │  │  ├─ global.css
│      │  │  ├─ home.css
│      │  │  ├─ manager.css
│      │  │  └─ theme
│      │  │      ├─ index.css
│      │  │      └─ fonts
│      │  │          ├─ element-icons.ttf
│      │  │          └─ element-icons.woff
│      │  └─ imgs
│      │      ├─ bg.jpg
│      │      ├─ bg1.jpg
│      │      └─ logo.png
│      ├─ router
│      │  └─ index.js
│      ├─ utils
│      │  └─ request.js
│      └─ views
│          ├─ 404.vue
│          ├─ Front.vue
│          ├─ Login.vue
│          ├─ Manager.vue
│          ├─ Register.vue
│          ├─ front
│          │  ├─ Home.vue
│          │  └─ Person.vue
│          └─ manager
│              ├─ 403.vue
│              ├─ Admin.vue
│              ├─ AdminPerson.vue
│              ├─ Goods.vue
│              ├─ Home.vue
│              ├─ Notice.vue
│              ├─ Password.vue
│              ├─ Route.vue
│              ├─ Station.vue
│              ├─ Supervisor.vue
│              └─ User.vue


​          



