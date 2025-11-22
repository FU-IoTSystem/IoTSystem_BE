#!/bin/bash
# Startup command cho Azure App Service
# File này chứa lệnh khởi động ứng dụng Spring Boot trên Azure Portal

# Kiểm tra xem JAR file đã tồn tại chưa
if [ -n "$PORT" ]; then
    # Nếu có biến PORT từ Azure, sử dụng nó
    echo "Đang chạy ứng dụng trên port $PORT..."
    java -Dserver.port=$PORT -jar target/IoTSystem-0.0.1-SNAPSHOT.jar
else
    # Nếu không có biến PORT, dùng port mặc định từ application.properties
    echo "Đang chạy ứng dụng trên port mặc định..."
    java -jar target/IoTSystem-0.0.1-SNAPSHOT.jar
fi
