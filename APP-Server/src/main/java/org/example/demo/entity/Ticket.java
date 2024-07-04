package org.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_no")
    private int ticketNo;

    @Column(name = "ticket_code") // 컬럼명 명시
    private String ticketCode;

    @Column(name = "ticket_picture") // 컬럼명 명시
    private String ticketPicture;

    // getters and setters 생략

    public int getTicket_no() {
        return ticketNo;
    }

    public void setTicket_no(int ticket_no) {
        this.ticketNo = ticket_no;
    }

    public String getTicket_code() {
        return ticketCode;
    }

    public void setTicket_code(String ticket_code) {
        this.ticketCode = ticket_code;
    }

    public String getTicket_picture() {
        return ticketPicture;
    }

    public void setTicket_picture(String ticket_picture) {
        this.ticketPicture = ticket_picture;
    }
}
