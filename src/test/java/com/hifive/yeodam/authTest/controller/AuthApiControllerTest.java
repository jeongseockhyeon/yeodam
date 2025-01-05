package com.hifive.yeodam.authTest.controller;

import com.google.gson.*;
import com.hifive.yeodam.auth.controller.AuthApiController;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.global.exception.CustomExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthApiControllerTest {

    public static final String email = "emailName@domain.com";

    @InjectMocks
    private AuthApiController target;

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
        gsonBuilder.registerTypeAdapter(LocalDate.class, new AuthApiControllerTest.LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new AuthApiControllerTest.LocalDateDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @Test
    public void 중복이메일체크_중복존재() throws Exception{
        //given
        String url = "/api/auth/email-check";

        doReturn(true).when(authService).checkEmail(email);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(email)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 중복이메일체크_중복없음() throws Exception{
        //given
        String url = "/api/auth/email-check";

        doReturn(false).when(authService).checkEmail(email);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(email)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 만료날짜설정실패_AuthService에서에러Throw() throws Exception{
        //given
        String url = "/api/auth/expiration";

        LocalDate localDate = LocalDate.now();

        doThrow(new CustomException(CustomErrorCode.AUTH_NOT_FOUND))
                .when(authService).updateExpiration(any(Auth.class), any(LocalDate.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(localDate))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void 만료날짜설정성공() throws Exception{
        //given
        String url = "/api/auth/expiration";

        LocalDate localDate = LocalDate.now();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(localDate))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 만료날짜삭제성공() throws Exception{
        //given
        String url = "/api/auth/expiration";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put(url)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 날짜조회성공() throws Exception{
        //given
        String url = "/api/auth/get-date";

        LocalDate now = LocalDate.now();
        LocalDate deleteDate = now.plusDays(30);

        int expectedYear = deleteDate.getYear();
        int expectedMonth = deleteDate.getMonthValue();
        int expectedDay = deleteDate.getDayOfMonth();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(expectedYear))
                .andExpect(jsonPath("$.month").value(expectedMonth))
                .andExpect(jsonPath("$.day").value(expectedDay));
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
