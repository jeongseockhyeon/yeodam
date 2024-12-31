package com.hifive.yeodam.userTest.controller;

import com.google.gson.*;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.global.exception.CustomExceptionHandler;
import com.hifive.yeodam.user.controller.UserApiController;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.dto.UserUpdateRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserApiControllerTest {

    private static final String email = "emailName@domain.com";
    private static final String password = "Passw0rd!";
    private static final String name = "손홍인";
    private static final String nickname = "sonny";
    private static final String phone = "01011112222";
    private static final LocalDate birthDate = LocalDate.of(2000, 3, 16);

    @InjectMocks
    private UserApiController target;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();

        // Gson 이 LocalDate 를 인식할 수 있게 해줌
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @ParameterizedTest
    @MethodSource("invalidRequestParameter")
    public void 유저등록실패_잘못된파라미터(String email, String password, String name,
                                    String nickname, String phone, LocalDate birthDate, String gender) throws Exception{
        //given
        String url = "/api/users";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(joinRequest(email, password, name,
                                                        nickname, phone, birthDate, gender)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 유저등록실패_AuthService에서에러Throw() throws Exception{
        //given
        String url = "/api/users";
        JoinRequest request = joinRequest(email, password, name, nickname, phone, birthDate, "M");

        doThrow(new CustomException(CustomErrorCode.DUPLICATED_EMAIL_JOIN))
                .when(authService).addAuth(any(JoinRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 유저등록실패_UserService에서에러Throw() throws Exception{
        //given
        String url = "/api/users";
        JoinRequest request = joinRequest(email, password, name, nickname, phone, birthDate, "M");

        doReturn(Auth.builder().email(email).build())
                .when(authService).addAuth(any(JoinRequest.class));

        doThrow(new CustomException(CustomErrorCode.DUPLICATED_NICKNAME_JOIN))
                .when(userService).addUser(any(JoinRequest.class), any(Auth.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 유저등록성공() throws Exception{
        //given
        String url = "/api/users";
        JoinRequest request = joinRequest(email, password, name, nickname, phone, birthDate, "M");

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("invalidRequestParameter")
    public void 유저수정실패_잘못된파라미터(String email, String password, String name,
                               String nickname, String phone, LocalDate birthDate, String gender) throws Exception{
        //given
        String url = "/api/users";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put(url)
                        .content(gson.toJson(updateRequest(email, password, name, nickname, phone, birthDate, gender)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 유저수정성공() throws Exception{
        //given
        String url =  "/api/users";

        UserUpdateRequest request = updateRequest(email, password, name, "sonny1", phone, birthDate, "M");

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 회원목록조회성공() throws Exception{
        //given
        String url = "/api/users";

        doReturn(Arrays.asList(
                new UserResponse(User.builder().build()),
                new UserResponse(User.builder().build()),
                new UserResponse(User.builder().build())
        )).when(userService).getUserList();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 회원상세조회실패_회원이존재하지않음() throws Exception{
        //given
        String url = "/api/users/-1";
        doThrow(new CustomException(CustomErrorCode.USER_NOT_FOUND))
                .when(userService).getUser(-1L);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void 회원상세조회성공() throws Exception{
        //given
        String url = "/api/users/-1";

        doReturn(new UserResponse(User.builder().build()))
                .when(userService).getUser(-1L);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 회원삭제실패_회원존재하지않음() throws Exception{
        //given
        String url = "/api/users/-1";

        doThrow(new CustomException(CustomErrorCode.USER_NOT_FOUND))
                .when(userService)
                .deleteUser(-1L);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void 회원삭제성공() throws Exception{
        //given
        String url = "/api/users/-1";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );

        //then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    public void 닉네임중복체크_중복닉네임없음() throws Exception{
        //given
        String url = "/api/users/nickname-check";

        doReturn(false).when(userService).checkNickname(nickname);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(nickname)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 닉네임중복체크_중복닉네임존재() throws Exception{
        //given
        String url = "/api/users/nickname-check";

        doReturn(true).when(userService).checkNickname(nickname);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(nickname)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    // validation, 유효하지 않은 파라미터들
    private static Stream<Arguments> invalidRequestParameter() {
        return Stream.of(
                //null 을 포함하는 경우
                Arguments.of(null, password, name, nickname, phone, birthDate, "M"), //이메일이 Null
                Arguments.of("username", password, name, nickname, phone, birthDate, "M"), //이메일의 형식이 아닌 경우
                Arguments.of("username@", password, name, nickname, phone, birthDate, "M"), //이메일의 형식이 잘못된 경우
                Arguments.of("username@.", password, name, nickname, phone, birthDate, "M"), //이메일의 형식이 잘못된 경우
                Arguments.of(email, null, name, nickname, phone, birthDate, "M"), //비밀번호가 Null
                Arguments.of(email, "1111111", name, nickname, phone, birthDate, "M"), //비밀번호가 8자 미만인 경우
                Arguments.of(email, "1234dawdsd@!Adsws2", name, nickname, phone, birthDate, "M"), //비밀번호가 16자 초과인 경우
                Arguments.of(email, "passw0rd!", name, nickname, phone, birthDate, "M"), //비밀번호가 영대문자가 없는 경우
                Arguments.of(email, "PASSW0RD!", name, nickname, phone, birthDate, "M"), //비밀번호가 영소문자가 없는 경우
                Arguments.of(email, "Password!", name, nickname, phone, birthDate, "M"), //비밀번호가 숫자가 없는 경우
                Arguments.of(email, "Passw0rd1", name, nickname, phone, birthDate, "M"), //비밀번호가 특수문자가 없는 경우
                Arguments.of(email, password, null, nickname, phone, birthDate, "M"), //이름이 Null
                Arguments.of(email, password, "son", nickname, phone, birthDate, "M"), //이름에 한글만 유효
                Arguments.of(email, password, name, null, phone, birthDate, "M"), //닉네임이 Null
                Arguments.of(email, password, name, nickname, null, birthDate, "M"), //전화번호가 Null
                Arguments.of(email, password, name, nickname, "0101234567", birthDate, "M"), //전화번호 형식이 잘못된 경우 (10자리)
                Arguments.of(email, password, name, nickname, "010123456789", birthDate, "M"), //전화번호 형식이 잘못된 경우 (12자리)
                Arguments.of(email, password, name, nickname, phone, birthDate, null) //성별이 Null
        );
    }

    private JoinRequest joinRequest(String email, String password, String name,
                                    String nickname, String phone, LocalDate birthDate, String gender) {

        return JoinRequest.builder()
                .email(email).password(password).name(name).nickname(nickname)
                .phone(phone).birthDate(birthDate).gender(gender)
                .build();
    }

    private UserUpdateRequest updateRequest(String email, String password, String name,
                                    String nickname, String phone, LocalDate birthDate, String gender) {

        return UserUpdateRequest.builder()
                .email(email).password(password).name(name).nickname(nickname)
                .phone(phone).birthDate(birthDate).gender(gender)
                .build();
    }

    private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public JsonElement serialize(LocalDate localDate, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(localDate));
        }
    }

    private static class LocalDateDeserializer implements JsonDeserializer <LocalDate> {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDate.parse(json.getAsString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.KOREA));
        }
    }
}
