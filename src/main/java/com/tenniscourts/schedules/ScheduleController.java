package com.tenniscourts.schedules;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @ApiOperation(value = "Add schedule for a tennis court")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Schedule created successfully")
    })
    @PostMapping("/schedules")
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO).getId())).build();
    }

    @ApiOperation(value = "Get all schedules withing a date range", response = ScheduleDTO.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List of schedules retrieved successfully")
    })
    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(
            @ApiParam(value = "Start date in 'yyyy-MM-dd' format") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "End date in 'yyyy-MM-dd' format") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(23, 59))));
    }

    @ApiOperation(value = "Get schedule by schedule id", response = ScheduleDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Schedule retrieved successfully"),
            @ApiResponse(code = 404, message = "Schedule id not found")
    })
    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@ApiParam(value = "Schedule id") @PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }
}
