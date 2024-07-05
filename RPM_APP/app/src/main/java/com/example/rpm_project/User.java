package com.example.rpm_project;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("userNo")
    private Integer userNo;

    @SerializedName("userId")
    private String userId;

    @SerializedName("userPw")
    private String userPw;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userBirth")
    private String userBirth; // 타입을 LocalDate로 변경

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userNumber")
    private String userNumber;

    // 기본 생성자
    public User() {
    }

    // 생성자
    public User(Integer userNo, String userId, String userPw, String userName, String userBirth, String userEmail, String userNumber) {
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

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
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
