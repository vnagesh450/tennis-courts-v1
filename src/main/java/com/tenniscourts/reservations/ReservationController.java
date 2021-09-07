package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class ReservationController extends BaseRestController {

    private final ReservationService reservationService;

    @ApiOperation(value = "Create reservation on a schedule")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Reservation created successfully")
    })
    @PostMapping("/reservations")
    public ResponseEntity<Void> bookReservation(@RequestBody CreateReservationRequestDTO createReservationRequestDTO) {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @ApiOperation(value = "Get reservation by reservation id", response = ReservationDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Reservation retrieved successfully"),
            @ApiResponse(code = 404, message = "Reservation id not found")
    })
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationDTO> findReservation(@ApiParam(value = "Reservation id") @PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }

    @ApiOperation(value = "Cancel reservation by reservation id", response = ReservationDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Reservation cancelled successfully"),
            @ApiResponse(code = 404, message = "Reservation id not found")
    })
    @PutMapping("/reservations/cancel/{reservationId}")
    public ResponseEntity<ReservationDTO> cancelReservation(@ApiParam(value = "Reservation id") @PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @ApiOperation(value = "Reschedule reservation by reservation id and schedule id", response = ReservationDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Reservation rescheduled successfully"),
            @ApiResponse(code = 404, message = "Reservation id or schedule id not found")
    })
    @PutMapping("/reservations/reschedule/{reservationId}/{scheduleId}")
    public ResponseEntity<ReservationDTO> rescheduleReservation(
            @ApiParam(value = "Reservation id") @PathVariable Long reservationId,
            @ApiParam(value = "Schedule id") @PathVariable Long scheduleId) {
        return ResponseEntity.ok(reservationService.rescheduleReservation(reservationId, scheduleId));
    }
}
