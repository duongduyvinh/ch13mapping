# Stage 1: Build ứng dụng bằng Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy file cấu hình Maven và toàn bộ mã nguồn vào container
COPY pom.xml .
COPY src ./src

# Chạy lệnh build ra file .war (bỏ qua bước test để build nhanh hơn)
RUN mvn clean package -DskipTests

# Stage 2: Khởi tạo Tomcat 9 để chạy ứng dụng
FROM tomcat:9.0-jre17

# Xóa các app mặc định của Tomcat để deploy app của bạn lên thư mục gốc (ROOT)
RUN rm -rf /usr/local/tomcat/webapps/*

# TẢI DRIVER POSTGRESQL VÀO THƯ MỤC LIB CỦA TOMCAT (Bắt buộc cho JNDI)
ADD https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.8/postgresql-42.7.8.jar /usr/local/tomcat/lib/

# Copy file .war vừa được build ở Stage 1 sang thư mục webapps của Tomcat
# Đổi tên thành ROOT.war để khi chạy ứng dụng không cần gõ thêm context path (như /email)
COPY --from=build /app/target/WEB_SQL_W6-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Mở cổng 8080 (Render thường route vào cổng này)
EXPOSE 8080

# Chạy Tomcat
CMD ["catalina.sh", "run"]
