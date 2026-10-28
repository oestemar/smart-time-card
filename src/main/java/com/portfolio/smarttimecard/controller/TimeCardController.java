package com.portfolio.smarttimecard.controller;

import com.portfolio.smarttimecard.entity.TimeCard;
import com.portfolio.smarttimecard.repository.TimeCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;


@Controller
public class TimeCardController {
    private final TimeCardRepository timeCardRepository;

    public TimeCardController(TimeCardRepository timeCardRepository){
        this.timeCardRepository = timeCardRepository;
    }

    @GetMapping("/")
    public String index(Model model){
        Optional<TimeCard> latestRecord =
                timeCardRepository.findTopByOrderByIdDesc();

        if(latestRecord.isPresent()){
            model.addAttribute(
                "latestRecord",
                latestRecord.get()
            );
        }
        return "index";
    }

    @PostMapping("/start")
    public String start_time(
            Model model,
            @RequestParam String work_type
            ){
        try {
            TimeCard record = new TimeCard();
            record.setWorkType(work_type);
            record.setWorkDate(LocalDate.now());
            record.setStartTime(LocalTime.now());

            LocalDate today = LocalDate.now();
            Optional<TimeCard> already =
                    timeCardRepository.findByWorkDate(today);

            if (already.isPresent()){
                model.addAttribute(
                    "message",
                            "本日は既に出勤打刻されています"
                );
            } else {
                timeCardRepository.save(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping("/end")
    public String end_time(
            Model model,
            @RequestParam String work_type
            ){
        try {
            LocalDate today = LocalDate.now();
            Optional<TimeCard> record =
                    timeCardRepository.findByWorkDate(today);
            if (record.isPresent()){
                TimeCard timeCard = record.get();
                timeCard.setEndTime(LocalTime.now());
                timeCardRepository.save(timeCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }


}
