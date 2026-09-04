# 毕业生就业去向数据可视化平台

基于 Spring Boot + Vue 的毕业生就业数据分析与可视化平台。

## 技术栈

- **后端**：Spring Boot 2.7 / MyBatis-Plus / MySQL / EasyExcel
- **前端**：Vue 3 + Vite / Element Plus / ECharts

## 功能模块

- 就业概况总览统计
- 行业分析、专业分析、地域分析、薪资分析
- 就业率等指标 0-100 边界校验
- 多条件组合筛选查询，19+ 个 RESTful 统计接口

## 快速启动

1. 初始化 MySQL 数据库（`backend-springboot/src/main/resources/db/schema.sql`）
2. 修改 `application.yml` 中的数据库连接配置
3. 后端：`cd backend-springboot && mvn spring-boot:run`
4. 前端：`cd frontend-vue && npm install && npm run dev`

> 数据文件位于 `data/` 目录，启动时自动导入。
