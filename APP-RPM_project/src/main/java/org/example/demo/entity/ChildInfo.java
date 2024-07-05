package org.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "child")
public class ChildInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_no")
    private int childNo;

    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY는 필요에 따라 설정
    @JoinColumn(name = "user_no", referencedColumnName = "user_no") // 외래 키 설정 수정
    private User user;

    @Column(name = "child_name")
    private String childName;

    @Column(name = "child_age")
    private int childAge;

    @Column(name = "child_height")
    private int childHeight;

    public ChildInfo() {
    }

    public ChildInfo(User user, String childName, int childAge, int childHeight) { // 생성자 수정
        this.user = user;
        this.childName = childName;
        this.childAge = childAge;
        this.childHeight = childHeight;
    }

    public int getChildNo() {
        return childNo;
    }

    public void setChildNo(int childNo) {
        this.childNo = childNo;
    }

    public User getUser() { // 수정된 getter
        return user;
    }

    public void setUser(User user) { // 수정된 setter
        this.user = user;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getChildAge() {
        return childAge;
    }

    public void setChildAge(int childAge) {
        this.childAge = childAge;
    }

    public int getChildHeight() {
        return childHeight;
    }

    public void setChildHeight(int childHeight) {
        this.childHeight = childHeight;
    }
}
