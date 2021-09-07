package com.tenniscourts.reservations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleMapper;
import com.tenniscourts.schedules.ScheduleService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ReservationService.class)
public class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    ScheduleService scheduleService;

    @Autowired
    ScheduleMapper scheduleMapper;

    @Test
    public void getRefundValueFullRefund() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal(10), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test
    public void getRefundValueNoRefund() {
        Schedule schedule = new Schedule();

        schedule.setStartDateTime(LocalDateTime.now().minusDays(2));

        Assert.assertEquals(new BigDecimal(0), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test
    public void getRefundValuePartialRefund() {
        Schedule schedule = new Schedule();

        schedule.setStartDateTime(LocalDateTime.now().plusMinutes(2));

        Assert.assertEquals(new BigDecimal("2.50"),
                reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));

        schedule.setStartDateTime(LocalDateTime.now().plusHours(3));

        Assert.assertEquals(new BigDecimal("5.0"),
                reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));

        schedule.setStartDateTime(LocalDateTime.now().plusHours(15));

        Assert.assertEquals(new BigDecimal("7.50"),
                reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleReservationThrowsExceptionWhenSameSlot() {
        LocalDateTime now = LocalDateTime.now();
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(10L);
        scheduleDTO.setStartDateTime(now.plusHours(30));
        scheduleDTO.setEndDateTime(now.plusHours(31));

        Schedule schedule = Schedule.builder().startDateTime(now.plusHours(30)).endDateTime(now.plusHours(31)).build();

        Reservation reservation = Reservation.builder()
                .value(new BigDecimal(10L))
                .schedule(schedule)
                .reservationStatus(ReservationStatus.READY_TO_PLAY).build();
        Reservation canceledReservation = Reservation.builder()
                .value(new BigDecimal(0))
                .refundValue(new BigDecimal(10L))
                .schedule(schedule)
                .reservationStatus(ReservationStatus.CANCELLED).build();

        Mockito.when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        Mockito.when(reservationRepository.save(Mockito.any())).thenReturn(canceledReservation);
        Mockito.when(scheduleService.findSchedule(10L)).thenReturn(scheduleDTO);

        reservationService.rescheduleReservation(1L, 10L);
    }
}