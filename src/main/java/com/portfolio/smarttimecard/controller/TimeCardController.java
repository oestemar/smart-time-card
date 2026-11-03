package com.portfolio.smarttimecard.controller;

import com.portfolio.smarttimecard.entity.TimeCard;
import com.portfolio.smarttimecard.entity.Settings;
import com.portfolio.smarttimecard.repository.TimeCardRepository;
import com.portfolio.smarttimecard.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Sort;

import java.sql.Time;
import java.time.*;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Controller
public class TimeCardController {
    private final TimeCardRepository timeCardRepository;
    private final SettingsRepository settingsRepository;

    public TimeCardController(
            TimeCardRepository timeCardRepository,
            SettingsRepository settingsRepository
    ){
        this.timeCardRepository = timeCardRepository;
        this.settingsRepository = settingsRepository;
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

         Settings settings =
                settingsRepository.findById(1L)
                        .orElse(new Settings());

        model.addAttribute(
            "settings",
            settings
        );

        return "index";
    }

    @PostMapping("/start")
    public String start_time(
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam String workType
            ){
        try {
            TimeCard record = new TimeCard();
            record.setWorkType(workType);
            record.setWorkDate(LocalDate.now());
            record.setStartTime(LocalTime.now()
                    .withSecond(0).withNano(0));

            LocalDate today = LocalDate.now();
            Optional<TimeCard> already =
                    timeCardRepository.findByWorkDate(today);

            if (already.isPresent()){
                redirectAttributes.addFlashAttribute(
                    "message",
                            "本日は既に出勤打刻されています"
                );
            } else {
                redirectAttributes.addFlashAttribute(
                        "message",
                        "出勤を登録しました"
                );
                timeCardRepository.save(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping("/end")
    public String end_time(
            RedirectAttributes redirectAttributes,
            Model model,
            @RequestParam String workType
            ){
        try {
            LocalDate today = LocalDate.now();
            Optional<TimeCard> record =
                    timeCardRepository.findByWorkDate(today);
            if (record.isPresent()){
                redirectAttributes.addFlashAttribute(
                        "message",
                        "退勤を登録しました"
                );
                TimeCard timeCard = record.get();
                timeCard.setEndTime(LocalTime.now());
                timeCardRepository.save(timeCard);
            } else {
                redirectAttributes.addFlashAttribute(
                        "message",
                        "本日は既に退勤打刻されています"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/display")
    public String display(
            @RequestParam(required = false)
            LocalDate startDate,
            @RequestParam(required = false)
            LocalDate endDate,
            Model model
    ){
        List<TimeCard> records;

        if (startDate != null && endDate != null){
            records =
                    timeCardRepository
                    .findByWorkDateBetweenOrderByWorkDateDesc(
                            startDate,
                            endDate
                    );
        } else {
            records =
                    timeCardRepository.findAll(
                            Sort.by("workDate").ascending()
                    );
        }

        Settings settings =
                settingsRepository.findById(1L)
                                .orElse(new Settings());

        model.addAttribute(
                "records",
                records
        );

        model.addAttribute(
            "startDate",
                startDate
        );

        model.addAttribute(
            "endDate",
                endDate
        );

        model.addAttribute(
                "settings",
                settings
        );

        return "display";
    }

    @GetMapping("/summary")
    public String summary(
            @RequestParam(required = false)
            Integer year,
            @RequestParam(required = false)
            Integer startMonth,
            @RequestParam(required = false)
            Integer endMonth,
            Model model
            ) {

        List<Integer> years =
                timeCardRepository.findDistinctYears();

        model.addAttribute(
                "years",
                years
        );

        if (year != null && startMonth != null && endMonth != null) {
            LocalDate startDate =
                    LocalDate.of(
                            year,
                            startMonth,
                            1
                    );

            LocalDate endDate =
                    LocalDate.of(
                            year,
                            endMonth,
                            1
                    ).withDayOfMonth(
                            LocalDate.of(year, endMonth, 1)
                                    .lengthOfMonth()
                    );

            List<TimeCard> records;
            records = timeCardRepository
                    .findByWorkDateBetweenOrderByWorkDateDesc(
                            startDate,
                            endDate
                    );

            Map<YearMonth, Long> hoursByMonth =
                    new HashMap<>();
            Map<YearMonth, Integer> workDaysByMonth =
                    new HashMap<>();
            Map<YearMonth, Long> latenessMinutesByMonth =
                    new HashMap<>();
            Map<YearMonth, Long> leavingearlyMinutesByMonth =
                    new HashMap<>();
            Map<YearMonth, Long> overtimeMinutesByMonth =
                    new HashMap<>();
            Settings settings =
                    settingsRepository.findById(1L)
                            .orElse(new Settings());
            int defaultBreakMinutes =
                    settings.getDefaultBreakMinutes() == null
                            ? 60
                            :settings.getDefaultBreakMinutes().intValue();

            for (TimeCard record : records) {
                if (record.getStartTime() != null
                        && record.getEndTime() != null) {

                    YearMonth month =
                            YearMonth.from(
                                    record.getWorkDate()
                            );

                    Duration duration =
                            Duration.between(
                                    record.getStartTime(),
                                    record.getEndTime()
                            );
                    if (record.getStartTime().isAfter(
                            settings.getStartScheduledTime()
                    )){
                        Duration duration_lateness =
                                Duration.between(
                                        settings.getStartScheduledTime(),
                                        record.getStartTime()
                                );
                        long lateness =
                                duration_lateness.toMinutes();
                        latenessMinutesByMonth.merge(
                                month,
                                lateness,
                                Long::sum
                        );
                    }

                    if (record.getEndTime().isBefore(
                            settings.getEndScheduledTime()
                    )){
                        Duration duration_leaveearly =
                                Duration.between(
                                        record.getEndTime(),
                                        settings.getEndScheduledTime()
                                );
                        long leavingearly =
                                duration_leaveearly.toMinutes();
                        leavingearlyMinutesByMonth.merge(
                                month,
                                leavingearly,
                                Long::sum
                        );
                    }

                    if (record.getEndTime().isAfter(
                            settings.getEndScheduledTime()
                    )){
                        Duration duration_overtime =
                                Duration.between(
                                        settings.getEndScheduledTime(),
                                        record.getEndTime()
                                );
                        long overtime =
                                duration_overtime.toMinutes();
                        overtimeMinutesByMonth.merge(
                                month,
                                overtime,
                                Long::sum
                        );
                    }


                    int breakMinutes =
                            record.getBreakTime() == null
                                    ? defaultBreakMinutes
                                    : record.getBreakTime();

                    long minutes =
                            duration.toMinutes()
                                    - breakMinutes;

                    hoursByMonth.merge(
                            month,
                            minutes,
                            Long::sum
                    );

                    workDaysByMonth.merge(
                            month,
                            1,
                            Integer::sum
                    );
                }
            }

            Map<YearMonth, String> workTimesByMonth =
                    new HashMap<>();

            for (Map.Entry<YearMonth, Long> entry
                    : hoursByMonth.entrySet()) {

                long minutes = entry.getValue();

                workTimesByMonth.put(
                        entry.getKey(),
                        String.format(
                                "%d:%02d",
                                minutes / 60,
                                minutes % 60
                        )
                );
            }

            Map<YearMonth, String> averageTimesByMonth =
                    new HashMap<>();

            for (Map.Entry<YearMonth, Long> entry
                    : hoursByMonth.entrySet()) {

                YearMonth month = entry.getKey();
                long totalMinutes = entry.getValue();
                int workDays =
                        workDaysByMonth.get(month);
                long averageMinutes =
                        totalMinutes / workDays;
                averageTimesByMonth.put(
                        month,
                        String.format(
                                "%d:%02d",
                                averageMinutes / 60,
                                averageMinutes % 60
                        )
                );
            }

            Map<YearMonth, String> latenessTotalByMonth =
                    new HashMap<>();

            for (Map.Entry<YearMonth, Long> entry
                    : latenessMinutesByMonth.entrySet()) {

                long minutes = entry.getValue();

                latenessTotalByMonth.put(
                        entry.getKey(),
                        String.format(
                                "%d:%02d",
                                minutes / 60,
                                minutes % 60
                        )
                );
            }

            Map<YearMonth, String> leavingearlyTotalByMonth =
                    new HashMap<>();

            for (Map.Entry<YearMonth, Long> entry
                    : leavingearlyMinutesByMonth.entrySet()) {

                long minutes = entry.getValue();

                leavingearlyTotalByMonth.put(
                        entry.getKey(),
                        String.format(
                                "%d:%02d",
                                minutes / 60,
                                minutes % 60
                        )
                );
            }

            Map<YearMonth, String> overtimeTotalByMonth =
                    new HashMap<>();

            for (Map.Entry<YearMonth, Long> entry
                    : overtimeMinutesByMonth.entrySet()) {

                long minutes = entry.getValue();

                overtimeTotalByMonth.put(
                        entry.getKey(),
                        String.format(
                                "%d:%02d",
                                minutes / 60,
                                minutes % 60
                        )
                );
            }

            model.addAttribute(
                    "hoursByMonth",
                    hoursByMonth
            );

            model.addAttribute(
                    "workDaysByMonth",
                    workDaysByMonth
            );

            model.addAttribute(
                    "workTimesByMonth",
                    workTimesByMonth
            );

            model.addAttribute(
                    "averageTimesByMonth",
                    averageTimesByMonth
            );

            model.addAttribute(
                    "year",
                    year
            );

            model.addAttribute(
                    "startMonth",
                    startMonth
            );

            model.addAttribute(
                    "endMonth",
                    endMonth
            );

            model.addAttribute(
                "latenessTotalByMonth",
                    latenessTotalByMonth
            );

            model.addAttribute(
                "leavingearlyTotalByMonth",
                leavingearlyTotalByMonth
            );

            model.addAttribute(
                "overtimeTotalByMonth",
                overtimeTotalByMonth
            );
        }

        List<Integer> months =
                List.of(1,2,3,4,5,6,7,8,9,10,11,12);

        model.addAttribute(
            "months",
                months
        );

        return "summary";
    }

    @GetMapping("/settings")
    public String Settings(
            Model model
    ){
        Optional<Settings> settings =
                settingsRepository.findById(1L);

        if (settings.isPresent()){
            model.addAttribute(
                "settings",
                    settings.get()
            );
        } else {
            model.addAttribute(
                    "settings",
                    new Settings()
            );
        }
        return "settings";
    }

    @PostMapping("/settings")
    public String Settings(
            RedirectAttributes redirectAttributes,

            @RequestParam Long defaultBreakMinutes,

            @RequestParam(required = false)
            Boolean startNoticeEnabled,
            @RequestParam(required = false)
            Boolean endNoticeEnabled,

            @RequestParam LocalTime startNoticeTime,
            @RequestParam LocalTime endNoticeTime,

            @RequestParam LocalTime startScheduledTime,
            @RequestParam LocalTime endScheduledTime
    ){
        Settings settings =
                settingsRepository.findById(1L)
                        .orElse(new Settings());

        settings.setDefaultBreakMinutes(
                defaultBreakMinutes
        );

        settings.setStartNoticeEnabled(
                startNoticeEnabled != null
        );

        settings.setEndNoticeEnabled(
                endNoticeEnabled != null
        );

        settings.setStartNoticeTime(
                startNoticeTime
        );

        settings.setEndNoticeTime(
                endNoticeTime
        );

        settings.setStartScheduledTime(
                startScheduledTime
        );

        settings.setEndScheduledTime(
                endScheduledTime
        );

        redirectAttributes.addFlashAttribute(
                    "message",
                    "設定を保存しました"
        );

        settingsRepository.save(settings);

        return "redirect:/settings";
    }

    @GetMapping("/edit")
    public String edit(
            @RequestParam Long id,
            Model model
    ){
        Optional<TimeCard> optionalRecord =
                timeCardRepository.findById(id);

        if (optionalRecord.isEmpty()){
            return "redirect:/display";
        }

        TimeCard record =
                optionalRecord.get();

        model.addAttribute(
                "record",
                record
        );
        return "edit";
    }

    @PostMapping("/edit")
    public String edit(
            RedirectAttributes redirectAttributes,
            @RequestParam Long id,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam String workType,
            @RequestParam Integer breakTime,
            Model model
    ){
        Optional<TimeCard> optionalRecord =
                timeCardRepository.findById(id);

        if(optionalRecord.isPresent()) {
            TimeCard record = optionalRecord.get();

            record.setStartTime(startTime);
            record.setEndTime(endTime);
            record.setWorkType(workType);
            record.setBreakTime(breakTime);

            timeCardRepository.save(record);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "修正しました"
            );

            model.addAttribute(
                    "startTime",
                    startTime
            );

            model.addAttribute(
                    "endTime",
                    endTime
            );

            model.addAttribute(
                    "breakTime",
                    breakTime
            );

            model.addAttribute(
                    "workType",
                    workType
            );

        }
        return "redirect:/display";
    }
}
