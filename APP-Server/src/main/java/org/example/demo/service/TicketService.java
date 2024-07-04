package org.example.demo.service;

import org.example.demo.entity.Ticket;
import org.example.demo.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public boolean isTicketCodeValid(String code) {
        Ticket ticket = ticketRepository.findByTicketCode(code);
        return ticket != null;
    }
}
