package com.hifive.yeodam.reservationTest;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.reservation.dto.ReservationReqDto;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.reservation.repository.ReservationRepository;
import com.hifive.yeodam.reservation.service.ReservationService;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                .id(reservationId)
                .user(user)
                .guide(guide)
                .item(item)
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
    public void getReservationTest() {
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
}
