package zerobase.weather.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping("/create/diary")
    @ApiOperation(value = "Using diary texts and weather, it saves to DB", notes = "")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "Weather Format: yyyy-MM-dd") LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    @GetMapping("/read/diary")
    @ApiOperation(value = "Fetch the selected date diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "Weather Format: yyyy-MM-dd")LocalDate date) {
        return diaryService.readDiary(date);
    }

    @GetMapping("/read/diaries")
    @ApiOperation(value = "Fetch selected diatires given the dates")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "Weather Format: yyyy-MM-dd")LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "Weather Format: yyyy-MM-dd")LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @PutMapping("/update/diary")
    @ApiOperation(value = "Update a diary given the date", notes = "If they are multiple, fetch only one and update")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @DeleteMapping("/delete/diary")
    @ApiOperation(value = "Delete a diary given the date")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "Weather Format: yyyy-MM-dd")LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
