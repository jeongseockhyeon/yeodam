/*
package com.hifive.yeodam.reservationTest;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.reservation.dto.ReservationReqDto;
import com.hifive.yeodam.reservation.dto.ReservationResDto;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.reservation.repository.ReservationRepository;
import com.hifive.yeodam.reservation.service.ReservationService;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private GuideRepository guideRepository;

    @Mock
    private SellerService sellerService;

    @Test
    @DisplayName("예약 정보 추가 테스트")
    public void addReservationTest() {

        //given
        Auth auth = mock(Auth.class);
        User user = mock(User.class);

        when(userRepository.findByAuthId(auth.getId())).thenReturn(Optional.of(user));

        Long guideId = 1L;
        Guide guide = mock(Guide.class);
        when(guideRepository.findById(guideId)).thenReturn(Optional.of(guide));

        Long itemId = 1L;
        Item item = mock(Item.class);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Long reservationId = 1L;


        Reservation reservation = Reservation.builder()
                .guide(guide)
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationReqDto reqDto = mock(ReservationReqDto.class);
        when(reqDto.getGuideId()).thenReturn(guideId);
        when(reqDto.getItemId()).thenReturn(itemId);

        //when
        Long resultId = reservationService.addReservation(reqDto,auth);

        //then
        assertNotNull(resultId);
        assertEquals(reservationId,resultId);
        verify(userRepository, times(1)).findByAuthId(auth.getId());
        verify(guideRepository, times(1)).findById(guideId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(reservationRepository, times(1)).save(any(Reservation.class));

    }

    @Test
    @DisplayName("예약 정보 삭제")
    public void deleteReservationTest() {
        //when
        Long reservationId = 1L;

        Reservation reservation = mock(Reservation.class);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        doNothing().when(reservationRepository).delete(reservation);

        //when
        reservationService.deleteReservation(reservationId);

        //then
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    @DisplayName("업체별 예약 정보 조회")
    public void getReservationsBySellerTest(){
        //given
        Auth auth = mock(Auth.class);

        Seller seller = mock(Seller.class);
        when(sellerService.getSellerByAuth(auth)).thenReturn(seller);

        List<Reservation> reservations = new ArrayList<>();

        when(reservationRepository.findReservationBySeller(seller)).thenReturn(reservations);

        //when
        List<ReservationResDto> result = reservationService.getReservationsBySeller(auth);

        assertNotNull(result);
        assertEquals(reservations.size(),result.size());
        verify(sellerService, times(1)).getSellerByAuth(auth);
        verify(reservationRepository, times(1)).findReservationBySeller(seller);
    }

    @Test
    @DisplayName("유저별 예약 정보 조회")
    public void getReservationsByUserTest(){
        //given
        Auth auth = mock(Auth.class);

        User user = mock(User.class);
        when(userRepository.findByAuthId(auth.getId())).thenReturn(Optional.of(user));

        List<Reservation> reservations = new ArrayList<>();

        when(reservationRepository.findReservationByUser(user)).thenReturn(reservations);

        //when
        List<ReservationResDto> result = reservationService.getReservationsByUser(auth);

        assertNotNull(result);
        assertEquals(reservations.size(),result.size());
        verify(userRepository, times(1)).findByAuthId(auth.getId());
        verify(reservationRepository, times(1)).findReservationByUser(user);
    }

    @Test
    @DisplayName("예약 정보 단일 조회")
    public void getReservationsByReservationIdTest(){
        //given
        Long reservationId = 1L;

        User user = mock(User.class);
        when(user.getName()).thenReturn("test user");

        Seller seller = mock(Seller.class);
        when(seller.getCompanyName()).thenReturn("test company");

        Item item = mock(Item.class);
        when(item.getItemName()).thenReturn("test item");
        when(item.getSeller()).thenReturn(seller);

        Guide guide = mock(Guide.class);
        when(guide.getName()).thenReturn("test guide");

        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(reservationId);
        when(reservation.getGuide()).thenReturn(guide);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        //when
        ReservationResDto result = reservationService.getReservation(reservationId);

        //then
        assertNotNull(result);
        assertEquals(reservationId,result.getReservationId());
        verify(reservationRepository, times(1)).findById(reservationId);
    }

    @Test
    @DisplayName("d-day 계산기")
    public void dDayCalculateTest(){
        //given
        Long reservationId = 1L;

        LocalDate today = LocalDate.now();
        LocalDate testStartDay = today.plusDays(2);

        long  mockDay = ChronoUnit.DAYS.between(today, testStartDay);

        String mockDDay = "D-" + mockDay;
        Reservation reservation = mock(Reservation.class);
        when(reservation.getReservationStartDate()).thenReturn(testStartDay);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        //when
        String result = reservationService.dDayCalculate(reservationId);

        //then
        assertNotNull(result);
        assertEquals(mockDDay,result);
        verify(reservationRepository, times(1)).findById(reservationId);

    }
}
*/
