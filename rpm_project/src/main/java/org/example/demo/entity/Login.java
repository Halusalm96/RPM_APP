package org.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Integer userNo;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "user_pw")
    private String userPw;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_birth")
    private LocalDate userBirth;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_number")
    private String userNumber;

    // 기본 생성자
    public Login() {
    }

    // 생성자
    public Login(String userId, String userPw, String userName, LocalDate userBirth, String userEmail, String userNumber) {
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userBirth = userBirth;
        this.userEmail = userEmail;
        this.userNumber = userNumber;
    }

    // Getters and setters
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

    // toString() 메서드 추가 (선택 사항)
    @Override
    public String toString() {
        return "Login{" +
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
