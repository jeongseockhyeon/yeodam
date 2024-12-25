package com.hifive.yeodam.reservation.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.reservation.dto.ReservationReqDto;
import com.hifive.yeodam.reservation.dto.ReservationResDto;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.reservation.repository.ReservationRepository;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.exception.UserErrorResult;
import com.hifive.yeodam.user.exception.UserException;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final GuideRepository guideRepository;
    private final ItemRepository itemRepository;
    private final SellerService sellerService;

    /*예약 정보 추가*/
    public Long addReservation(ReservationReqDto reservationReqDto, Auth auth) {

        /*유저 정보*/
        User user = userRepository.findByAuthId(auth.getId())
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        /*가이드 정보*/
        Guide guide = guideRepository.findById(reservationReqDto.getGuideId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.GUIDE_NOT_FOUND));

        /*상품 정보*/
        Item item = itemRepository.findById(reservationReqDto.getItemId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));

        Reservation reservation = Reservation.builder()
                .user(user)
                .guide(guide)
                .item(item)
                .reservationStartDate(reservationReqDto.getReservationStartDate())
                .reservationEndDate(reservationReqDto.getReservationEndDate())
                .build();

        return reservationRepository.save(reservation).getId();
    }

    /*예약 정보 삭제*/
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.RESERVATION_NOT_FOUND));
        reservationRepository.delete(reservation);
    }

    /*예약 정보 단일 조회*/
    public ReservationResDto getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RESERVATION_NOT_FOUND));
        return new ReservationResDto(reservation);
    }

    /*업체별 예약 정보 조회*/
    public List<ReservationResDto> getReservationsBySeller(Auth auth) {
        Seller seller = sellerService.getSellerByAuth(auth);
        List<Reservation> reservations = reservationRepository.findReservationBySeller(seller);
        return reservations.stream()
                .map(ReservationResDto::new)
                .toList();
    }

    /*유저별 예약 정보 조회*/
    public List<ReservationResDto> getReservationsByUser(Auth auth) {
        User user = userRepository.findByAuthId(auth.getId())
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
        List<Reservation> reservations = reservationRepository.findReservationByUser(user);
        return reservations.stream()
                .map(ReservationResDto::new)
                .toList();
    }

    /*예약일 디데이*/
    public String dDayCalculate(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RESERVATION_NOT_FOUND));
        LocalDate toDay = LocalDate.now();
        long dDay = ChronoUnit.DAYS.between(toDay, reservation.getReservationStartDate());
        if(dDay > 0){
            return "D-" + dDay;
        }
        return "완료";
    }
}
