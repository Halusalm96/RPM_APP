package com.example.rpm_project;

import android.app.Application;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 데이터베이스 연결 설정
        try {
            // MariaDB JDBC 드라이버 로드
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 필요에 따라 다른 초기화 로직을 추가할 수 있습니다.
}
