package com.recipes.FlavourFoundry.repository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.recipes.FlavourFoundry.model.Report;

@Repository
public class ReportRepo {

    @Autowired
    @Qualifier("reportTemplate")
    RedisTemplate<String, String> template;

    public void addReport(Report report) {
        template.opsForValue().set(report.getId(), report.toString());
    }

    public Set<String> getAllReportKeys() {
        return template.keys("*");
    }

    public String getReportById(String id) {
        return template.opsForValue().get(id);
    }

    public void resolveReportById(String id) {
        template.delete(id);
    }

    public Integer getCount() {
        return getAllReportKeys().size();
    }


}
