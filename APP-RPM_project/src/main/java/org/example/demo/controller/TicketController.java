package org.example.demo.controller;

import org.example.demo.entity.Ticket;
import org.example.demo.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validate_code")
public class TicketController {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @PostMapping
    public String validateCode(@RequestParam String code) {
        Ticket ticket = ticketRepository.findByTicketCode(code);
        if (ticket != null) {
            // 코드가 일치할 때
            return "Welcome to the land!";
        } else {
            // 코드가 일치하지 않을 때
            return "Invalid code";
        }
    }
}