package com.hospital.controller;

import com.hospital.dto.CheckInRequest;
import com.hospital.dto.CheckInResponse;
import com.hospital.model.Ticket;
import com.hospital.service.QueueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    // used by the kiosk
    @PostMapping("/checkin")
    public CheckInResponse checkIn(@RequestBody CheckInRequest request) {
        return queueService.checkIn(request);
    }

    @GetMapping("/queue")
    public List<Ticket> getQueue() {
        return queueService.getWaitingQueue();
    }

    @GetMapping("/queue/called")
    public List<Ticket> getCalled() {
        return queueService.getCalledTickets();
    }

    @PostMapping("/queue/next")
    public Ticket callNext(@RequestParam String room) {
        return queueService.callNext(room);
    }

    @PostMapping("/tickets/{id}/done")
    public Ticket finishVisit(@PathVariable Long id, @RequestParam(required = false) String notes) {
        return queueService.finishVisit(id, notes);
    }
}
