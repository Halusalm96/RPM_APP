package com.example.rpm_project;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_NO = "userNo";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Singleton 인스턴스
    private static SessionManager instance;

    SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // 사용자 식별 번호 저장
    public void setUserNo(int userNo) {
        editor.putInt(KEY_USER_NO, userNo);
        editor.apply();
    }

    // 사용자 식별 번호 가져오기
    public int getUserNo() {
        return pref.getInt(KEY_USER_NO, 0); // 기본값으로 0을 반환하거나 원하는 값 설정
    }

    // 사용자 세션 정보 지우기
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
