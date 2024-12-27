package com.hifive.yeodam.reservation.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@RestController
public class ReservationApiController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> addReservation(@RequestBody ReservationReqDto reservationReqDto, @AuthenticationPrincipal Auth auth) {
        return ResponseEntity.ok(reservationService.addReservation(reservationReqDto, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable("id") Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResDto> getReservation(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @GetMapping("/user-reservation")
    public ResponseEntity<List<ReservationResDto>> getReservationByUser(@AuthenticationPrincipal Auth auth) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(auth));
    }

    @GetMapping("/seller-reservation")
    public ResponseEntity<List<ReservationResDto>> getReservationBySeller(@AuthenticationPrincipal Auth auth) {
        return ResponseEntity.ok(reservationService.getReservationsBySeller(auth));
    }

    @GetMapping("/{id}/d-day")
    public ResponseEntity<String> getReservationByDay(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.dDayCalculate(id));
    }
}
