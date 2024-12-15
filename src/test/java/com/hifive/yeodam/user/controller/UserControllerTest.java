package com.hifive.yeodam.user.controller;

import com.google.gson.*;
import com.hifive.yeodam.advice.GlobalExceptionHandler;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.exception.UserErrorResult;
import com.hifive.yeodam.user.exception.UserException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private static final String email = "emailName@domain.com";
    private static final String password = "1234";
    private static final String name = "son";
    private static final String nickname = "sonny";
    private static final String phone = "01011112222";
    private static final LocalDate birthDate = LocalDate.of(2000, 3, 16);

    @InjectMocks
    private UserController target;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Gson 이 LocalDate 를 인식할 수 있게 해줌
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @ParameterizedTest
    @MethodSource("invalidJoinRequestParameter")
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
        doThrow(new AuthException(AuthErrorResult.DUPLICATED_AUTH_JOIN))
                .when(authService).addAuth(any(JoinRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(joinRequest(email, password, name,
                                nickname, phone, birthDate, "M")))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 유저등록성공() throws Exception{
        //given
        String url = "/api/users";

        User user = User.builder()
                .id(-1L).name(name).nickname(nickname).birthDate(birthDate).gender("M")
                .build();

        Auth auth = Auth.builder()
                .id(-1L).email(email).password(password).phone(phone)
                .build();

        doReturn(auth).when(authService).addAuth(any(JoinRequest.class));
        doReturn(user).when(userService).addUser(any(JoinRequest.class), any(Auth.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(joinRequest(email, password, name, nickname, phone, birthDate, "M")))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    public void 회원목록조회성공() throws Exception{
        //given
        String url = "/api/users";

        doReturn(Arrays.asList(
                User.builder().build(),
                User.builder().build(),
                User.builder().build()
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
        doThrow(new UserException(UserErrorResult.USER_NOT_FOUND))
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

        doReturn(User.builder().build())
                .when(userService).getUser(-1L);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    // validation, 유효하지 않은 파라미터들
    private static Stream<Arguments> invalidJoinRequestParameter() {
        return Stream.of(
                //null 을 포함하는 경우
                Arguments.of(null, password, name, nickname, phone, birthDate, "M"), //이메일이 Null
                Arguments.of("username", password, name, nickname, phone, birthDate, "M"), //이메일의 형식이 아닌 경우
                Arguments.of("username@", password, name, nickname, phone, birthDate, "M"), //이메일의 형식이 잘못된 경우
                Arguments.of(email, null, name, nickname, phone, birthDate, "M"), //비밀번호가 Null
                Arguments.of(email, password, null, nickname, phone, birthDate, "M"), //이름이 Null
                Arguments.of(email, password, name, null, phone, birthDate, "M"), //닉네임이 Null
                Arguments.of(email, password, name, nickname, null, birthDate, "M"), //전화번호가 Null
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
