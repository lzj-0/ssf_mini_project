package com.recipes.FlavourFoundry.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.model.Report;
import com.recipes.FlavourFoundry.repository.ReportRepo;

@Service
public class ReportService {

    @Autowired
    ReportRepo reportRepo;

    public void addReport(Report report) {
        report.setDate((new Date()).getTime());
        report.setId(UUID.randomUUID().toString());
        reportRepo.addReport(report);
    }

    public Report getReportById(String id) {
        String reportStr = reportRepo.getReportById(id);

        Report r = new Report();
        String[] input = reportStr.split("\\|\\|\\|");
        r.setRecipeId(Integer.parseInt(input[0]));
        r.setId(input[1]);
        r.setCategory(input[2]);
        r.setSubject(input[3]);
        r.setIssue(input[4]);
        r.setDate(Long.parseLong(input[5]));

        return r;

    }

    public List<Report> getAllReport() {
        Set<String> reportsStr = reportRepo.getAllReportKeys();

        List<Report> reports = new ArrayList<>();
        for (String id : reportsStr) {
            reports.add(getReportById(id));
        }
        reports.sort((x, y) -> y.getDate().compareTo(x.getDate()));

        return reports;
    }

    public void resolveReportById(String id) {
        reportRepo.resolveReportById(id);
    }

    public Integer getReportCount() {
        return reportRepo.getCount();
    }
}
