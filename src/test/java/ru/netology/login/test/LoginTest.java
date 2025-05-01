package ru.netology.login.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.login.data.DataHelper;
import ru.netology.login.data.SQLHelper;
import ru.netology.login.pages.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.login.data.SQLHelper.cleanAuthCodes;
import static ru.netology.login.data.SQLHelper.cleanDatabase;

public class LoginTest {
    LoginPage loginPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }

    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    void shouldSuccessfulLogin() {
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    void shouldGetErrorNotificationWhenLoginRandomUserNotAddedToBase() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.login(authInfo);
        loginPage.verifyErrorNotification("Ошибка! неверно указан логин или пароль");
    }

    @Test
    void shouldGetErrorNotificationWhenLoginWithRandomVerificationCode() {
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    void shouldBlockTheUser() {
        loginPage.loginWithInvalidPasswordEntryThreeTimes();
        loginPage.verifyErrorNotification("Пароль трижды введён неверно. Пользователь заблокирован.");
    }
}
