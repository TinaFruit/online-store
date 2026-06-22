# 我要在哪个基础镜像上开始搭
FROM maven:3.9-eclipse-temurin-17 AS build
# 在容器内部创建一个叫 /app 的文件夹，并且"切换"进去
WORKDIR /app
# 把 pom.xml 这一个文件复制进容器
COPY pom.xml .
# 让 Maven 根据 pom.xml 把所有依赖包（JWT、MyBatis、Redis starter 这些）提前下载下来
RUN mvn dependency:go-offline -B
# 复制源代码（放在依赖下载之后，享受缓存优化）
COPY src ./src
# 编译打包，生成 target/xxx.jar
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
# 去第一阶段那个已经编译好的环境里，只拿走那一个编译好的 jar 文件，其他的（Maven、源代码、中间文件）一概不要
COPY --from=build /app/target/*.jar app.jar
# 声明这个容器打算用 8080 端口对外提供服务（Spring Boot 默认端口）。
EXPOSE 8080
# 这里就是用 Java 运行那个 jar 包，等价于你在终端手动敲 java -jar app.jar。这是整个容器存在的意义：跑这个命令，把你的 Spring Boot 应用启动起来。
ENTRYPOINT ["java", "-jar", "app.jar"]