# **心晴日记（MoodBloom）**

一款智能日记系统，可以写点东西~

# 配置与部署说明

本项目由后端（基于 Spring Boot）和前端（基于 Vue.js）组成。以下详细描述了系统的配置与部署步骤，以便开发人员能够高效、准确地完成安装和部署。

[TOC]

## 1. 环境要求

- **JDK**: 系统需要 JDK 11 或更高版本，建议配置环境变量 `JAVA_HOME`，以确保工具和依赖可以正确定位 JDK。

- **Maven**: 系统要求安装 Maven（推荐版本：3.6.3 及以上），并配置 `MAVEN_HOME` 环境变量以实现构建自动化。

  也可以选择通过Spring Initializer初始化项目或者在pom.xml文件中配置Maven。

- **Node.js 和 npm**: 需要 Node.js（推荐版本：16.x 及以上）和 npm（推荐版本：8.x 及以上），用于前端项目的依赖管理与打包。

- **数据库**: MySQL 8.0 及以上版本。请确保 MySQL 服务已启动且允许远程连接，必要时开放 3306 端口。

- **Nginx**: 部署前端时，使用 Nginx 作为静态资源服务器。

- **腾讯云轻量应用服务器**: 服务器环境为腾讯云轻量应用服务器，提供较高的性价比和易于管理的界面，适合中小型应用的部署。

  

## 2. 配置步骤

### 2.1 后端（Spring Boot）

1. **克隆项目**

   ```bash
   git clone https://github.com/Yuen647/MoodBloom.git
   cd moodbloom_springboot/moodbloom
   ```

2. **创建数据库**
   2.1 使用 MySQL 创建名为 `moodbloom_db` 的数据库：

   ```sql
   CREATE DATABASE moodbloom_db;
   ```

   2.2 运行以下命令导入 `moodbloom_db.sql` 文件：

   ```bash
   mysql -u root -p moodbloom < moodbloom_db.sql
   ```

3. **修改配置文件**
   编辑 `src/main/resources/application.yml`，确保数据库连接和服务器端口配置符合目标部署环境。

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/moodbloom_db
       username: <数据库用户名>
       password: <数据库密码>
     jpa:
       hibernate:
         ddl-auto: update
   server:
     port: 8080
   ```

4. **安装依赖并构建**
   使用 Maven 安装项目依赖并执行构建。

   ```bash
   mvn clean install
   ```

5. **运行后端服务**
   使用以下命令启动后端服务：

   ```bash
   mvn spring-boot:run
   ```

### 2.2 前端（Vue.js）

1. **进入前端项目目录**

   ```bash
   cd moodbloom_vue
   ```

2. **安装依赖**
   使用 npm 安装项目所需依赖。

   ```bash
   npm install
   ```

3. **修改配置**
   编辑 `src/config.js` 文件，确保后端 API 地址正确配置。

   ```javascript
   export default {
     apiBaseUrl: 'http://<服务器公网 IP>:8080/api'
   };
   ```

4. **构建与运行**
   启动开发服务器进行调试，或构建生产环境版本。

   ```bash
   # 开发环境运行
   npm run serve
   
   # 生产环境构建
   npm run build
   ```



## 3. 部署步骤

### 3.1 后端部署

1. **准备服务器**：确保腾讯云轻量应用服务器上安装了 JDK 和 MySQL，并配置 `JAVA_HOME` 和 `MYSQL_HOME` 环境变量。

2. **打包后端应用**：使用 Maven 进行打包，生成 `.jar` 文件。

   ```bash
   mvn clean package
   ```

   打包后的 `.jar` 文件通常位于 `target` 目录下。

3. **上传至服务器**：通过 SCP 或使用腾讯云提供的文件管理工具将 `.jar` 文件上传至服务器。

4. **运行后端服务**：通过以下命令启动服务。

   ```bash
   java -jar target/moodbloom-0.0.1-SNAPSHOT.jar
   ```

5. **后台运行服务**：为了确保服务在关闭终端后继续运行，可以使用 `nohup` 命令：

   ```bash
   nohup java -jar target/moodbloom-0.0.1-SNAPSHOT.jar > log.out 2>&1 &
   ```

### 3.2 前端部署

1. **打包前端应用**：在本地执行以下命令以生成静态文件。

   ```bash
   npm run build
   ```

   生成的静态文件位于 `dist` 目录下。

2. **上传至服务器**：将 `dist` 目录中的文件上传至腾讯云轻量应用服务器根目录/var/www/html。

3. **配置 Nginx**：在服务器上配置 Nginx 以提供前端静态资源服务，并代理 API 请求至后端服务，同时启用https。

   ```nginx
   server {
       listen 80;
       listen [::]:80;
       server_name 124.222.156.13;
   
       # 重定向所有HTTP请求到HTTPS
       return 301 https://$server_name$request_uri;
   }
   
   server {
       listen 443 ssl http2;
       listen [::]:443 ssl http2;
       server_name 124.222.156.13;
   
       # 包含SSL配置
       include snippets/self-signed.conf;
       include snippets/ssl-params.conf;
   
       # 根目录配置
       root /var/www/html;
       index index.html index.htm;
   
       # 捕获所有404或其他错误，重定向到主页
       error_page 404 /index.html;
       error_page 500 502 503 504 /index.html;
   
       location / {
           try_files $uri $uri/ /index.html;
       }
   
       # 为 /images/ 路径指定新的物理目录
       location /images/ {
           alias /home/lighthouse/images/;
           autoindex on;
       }
   
       # 为 /api/txt2img/generate 单独配置
       location = /api/txt2img/generate {
           proxy_pass http://localhost:8800;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           proxy_set_header X-Forwarded-Port $server_port;
   
           # 添加特定的配置
           client_max_body_size 20M;
           proxy_connect_timeout 600s;
           proxy_send_timeout 600s;
           proxy_read_timeout 600s;
           send_timeout 600s;
       }
   
       # 代理 /api 请求到 Spring Boot 应用
       location /api/ {
           proxy_pass http://localhost:8800/api/;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           proxy_set_header X-Forwarded-Port $server_port;
       }
   }
   ```

4. **重启 Nginx**：修改配置后，需要重启 Nginx 以应用更改：

   ```bash
   sudo systemctl restart nginx
   ```



## 4. 注意事项

- 在部署过程中，确保数据库的用户名和密码安全，建议使用环境变量或加密工具（如腾讯云密钥管理服务）来管理敏感信息。

- 建议使用 HTTPS，以确保数据传输的安全性，可以通过 Let's Encrypt 配置免费的 SSL 证书。

- 确保前后端的 API 地址配置正确，并且服务器防火墙放行所需端口（如 80 和 8080）。

  

## 5. 故障排查

- **后端无法连接数据库**：

  - 检查 MySQL 服务是否已启动，确保 MySQL 监听正确的端口。
  - 确认 `application.yml` 中的数据库配置是否正确无误，包括用户名、密码及数据库地址。
  - 检查数据库用户是否具备足够的访问权限。

- **前端无法访问 API**：

  - 检查 Nginx 配置是否正确，确保 `/api` 路由正确代理至后端服务。
  - 确保后端服务正在运行，服务器防火墙规则允许 API 请求访问相应端口。

  
