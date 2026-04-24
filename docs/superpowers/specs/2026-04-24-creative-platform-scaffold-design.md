# 文创手作定制交易平台基础骨架设计

## 目标

基于 `RuoYi-Vue-master` 前后端分离框架，搭建一个与毕业设计题目“基于 SpringBoot 的文创手作定制交易平台设计与实现”一致的基础项目框架。当前阶段只完成系统骨架，不实现完整业务闭环，不引入真实支付、推荐算法和敏感词过滤逻辑。

## 范围

本次骨架覆盖以下业务模块：

- 创作者管理 `creator`
- 文创分类管理 `category`
- 手作商品管理 `product`
- 定制需求管理 `demand`
- 定制报价管理 `quote`
- 定制订单管理 `order`
- 社区作品分享 `post`
- 社区评论互动 `comment`
- 收藏关注关系 `favorite`

本次不实现以下内容：

- 完整数据库初始化数据
- 真实支付接口接入
- 推荐算法实现
- 敏感词过滤实现
- 买家端商城风格前台页面

## 架构落点

### 后端

沿用若依经典四层风格：

- `ruoyi-admin`
  - 暴露 REST 接口控制器
- `ruoyi-system`
  - 承载业务领域对象、Mapper、Service
- `ruoyi-common`
  - 复用若依基础实体、响应对象、工具类

新增业务按 `creative` 主题分组，避免与系统模块混杂：

- `com.ruoyi.web.controller.creative`
- `com.ruoyi.system.domain.creative`
- `com.ruoyi.system.mapper.creative`
- `com.ruoyi.system.service.creative`
- `com.ruoyi.system.service.creative.impl`
- `ruoyi-system/src/main/resources/mapper/creative`

### 前端

沿用若依 `ruoyi-ui` 结构：

- `src/api/creative`
- `src/views/creative/*`

每个业务模块至少提供：

- API 封装文件
- 列表占位页
- 模块说明文案
- 后续 CRUD 的按钮/字段预留

## 模块边界

### 创作者管理

承载商家/手工艺人资料，作为商品发布、作品展示、关注关系的主体。

### 文创分类

承载平台分类树，服务商品分类与前台筛选。

### 手作商品

承载标准化商品信息，后续可衔接购物车与推荐。

### 定制需求

承载买家发起的个性化定制需求。

### 定制报价

承载商家对需求的响应报价，是需求向订单过渡的桥梁。

### 定制订单

承载交易主流程，后续可扩展支付、发货、售后。

### 社区作品分享

承载创作者晒作品、买家互动内容。

### 社区评论互动

承载对作品或内容的评论，后续可挂接敏感词过滤。

### 收藏关注关系

承载用户对商品或创作者的收藏/关注动作。

## 数据与命名原则

- 表名前缀统一使用 `creative_`
- Java 类名使用 `CreativeXxx`
- 前端接口前缀统一使用 `/creative/...`
- 权限标识统一使用 `creative:模块:动作`
- 菜单统一挂在“文创平台”目录下

## 交付物

本次交付包含：

- 基础骨架设计说明
- 实施计划文档
- 后端模块代码骨架
- 前端页面与 API 占位骨架
- 菜单 SQL 初始化脚本
- 业务基础表 SQL 草案

## 风险与后续

- 当前控制器、Mapper、XML 已具备 CRUD 外形，但未与真实数据库和菜单数据联调
- 前端页面为后台管理骨架，未覆盖前台商城展示
- 推荐算法、敏感词过滤、在线沟通可在下一阶段作为特色模块补充
