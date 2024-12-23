package com.hifive.yeodam.user.controller;

import com.google.gson.*;
import com.hifive.yeodam.advice.GlobalExceptionHandler;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.service.AuthService;
import com.hifive.yeodam.user.dto.JoinRequest;
import com.hifive.yeodam.user.dto.UserResponse;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserViewControllerTest {

    @InjectMocks
    private UserViewController target;

    @Mock
    Model model;

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
        gsonBuilder.registerTypeAdapter(LocalDate.class, new UserViewControllerTest.LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new UserViewControllerTest.LocalDateDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @Test
    public void 회원가입뷰페이지로드() throws Exception{
        //given

        //when
        String result = target.userJoinView(model);

        //then
        assertThat(result).isEqualTo("user/join");
    }

    /*@Test
    public void 회원수정뷰렌더링() throws Exception{
        //given
        Long id = -1L;

        User user = User.builder().id(id).build();
        UserResponse userResponse = new UserResponse(user);

        doReturn(userResponse).when(userService).getUser(id);
        doReturn(any(Auth.class)).when(authService).getAuth(userResponse.getId());

        //when
        String result = target.userEditView(id, model);

        //then
        assertThat(result).isEqualTo("user/edit");

        //verify
        verify(userService, times(1)).getUser(id);
        verify(authService, times(1)).getAuth(id);
    }*/

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