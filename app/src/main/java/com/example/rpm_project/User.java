package com.example.rpm_project;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate; // LocalDate import 추가

public class User {
    @SerializedName("user_no")
    private Integer userNo;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("user_pw")
    private String userPw;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_birth")
    private LocalDate userBirth; // 타입을 LocalDate로 변경

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("user_number")
    private String userNumber;

    // 기본 생성자
    public User() {
    }

    // 생성자
    public User(Integer userNo, String userId, String userPw, String userName, LocalDate userBirth, String userEmail, String userNumber) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userBirth = userBirth;
        this.userEmail = userEmail;
        this.userNumber = userNumber;
    }

    // Getter 및 Setter 메서드들
    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(LocalDate userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userNo=" + userNo +
                ", userId='" + userId + '\'' +
                ", userPw='" + userPw + '\'' +
                ", userName='" + userName + '\'' +
                ", userBirth=" + userBirth +
                ", userEmail='" + userEmail + '\'' +
                ", userNumber='" + userNumber + '\'' +
                '}';
    }
}
