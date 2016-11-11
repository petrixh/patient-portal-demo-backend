package com.vaadin.demo.controllers;

import com.vaadin.demo.controllers.dto.PatientDTO;
import com.vaadin.demo.repositories.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    PatientsRepository patientsRepository;

    @RequestMapping(path = "/age", method = RequestMethod.GET)
    public Collection<Map<String, Object>> getStatsByAge() {
        return patientsRepository
                .findAll()
                .stream()
                .map(PatientDTO::new)
                .collect(groupingBy(getAgeRange(), counting()))
                .entrySet()
                .stream()
                .map(e -> {
                    HashMap<String, Object> stats = new HashMap<>();
                    stats.put("doctor", e.getKey());
                    stats.put("patients", e.getValue());
                    return stats;
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/gender", method = RequestMethod.GET)
    public Collection<Map<String, Object>> getStatsByGender() {
        return patientsRepository
                .findAll()
                .stream()
                .map(PatientDTO::new)
                .collect(groupingBy(PatientDTO::getGender, counting()))
                .entrySet()
                .stream()
                .map(e -> {
                    HashMap<String, Object> stats = new HashMap<>();
                    stats.put("gender", e.getKey());
                    stats.put("patients", e.getValue());
                    return stats;
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/doctor", method = RequestMethod.GET)
    public Collection<Map<String, Object>> getStatsByDoctor() {
        return patientsRepository
                .findAll()
                .stream()
                .map(PatientDTO::new)
                .collect(groupingBy(PatientDTO::getDoctor, counting()))
                .entrySet()
                .stream()
                .map(e -> {
                    HashMap<String, Object> stats = new HashMap<>();
                    stats.put("age", e.getKey());
                    stats.put("patients", e.getValue());
                    return stats;
                })
                .collect(Collectors.toList());
    }

    private Function<PatientDTO, String> getAgeRange() {
        return p -> {
            int age = getAge(p.getBirthDate());
            String ageRange = "";
            if (age < 21) {
                ageRange = "Under 21";
            } else if (age <= 30) {
                ageRange = "21-30";
            } else if (age <= 40) {
                ageRange = "31-40";
            } else if (age <= 50) {
                ageRange = "41-50";
            } else if (age <= 60) {
                ageRange = "51-60";
            } else if (age <= 70) {
                ageRange = "61-70";
            } else if (age <= 80) {
                ageRange = "71-80";
            } else if (age > 80) {
                ageRange = "Over 80";
            }
            return ageRange;
        };
    }

    private int getAge(Date birthDate) {
        LocalDate now = LocalDate.now();
        LocalDate bdayLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return Period.between(bdayLocal, now).getYears();
    }
}