package com.example.backend.admin.report.controller;

import com.example.backend.admin.report.dto.AverageResolutionTimeResponse;
import com.example.backend.admin.report.dto.SlaBreachResponse;
import com.example.backend.admin.report.dto.TicketsByCategoryResponse;
import com.example.backend.admin.report.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/tickets-by-category")
    public List<TicketsByCategoryResponse> getTicketsByCategory() {
        return adminReportService.getTicketsByCategory();
    }

    @GetMapping("/average-resolution-time")
    public AverageResolutionTimeResponse getAverageResolutionTime() {
        return adminReportService.getAverageResolutionTime();
    }

    @GetMapping("/sla-breaches")
    public List<SlaBreachResponse> getSlaBreaches() {
        return adminReportService.getSlaBreaches();
    }
}