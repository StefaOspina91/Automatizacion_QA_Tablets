package org.example;

import org.openqa.selenium.WebElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.appium.java_client.AppiumBy;
import java.time.Duration;

public class LoginTest extends BaseTest {

    @BeforeEach
    public void setupTest() {
        setUp();
    }

    @Test
    public void testLoginFlujoCompleto() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // 👉 Paso 1: Clic en el botón "Quality Control"
            WebElement qualityControlBtn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUiAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(1)")
                    )
            );
            qualityControlBtn.click();

            // 👉 Paso 2: Ingresar usuario y contraseña
            WebElement username = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.xpath("//android.widget.EditText[@text='User']")
                    )
            );
            WebElement password = driver.findElement(AppiumBy.xpath("//android.widget.EditText[@text='Password']"));
            WebElement loginBtn = driver.findElement(AppiumBy.androidUiAutomator("new UiSelector().textContains(\"Iniciar Sesión\")"));

            username.sendKeys("StefaOspina");
            password.sendKeys("sospina");
            loginBtn.click();

            System.out.println("✔ Credenciales ingresadas y login ejecutado");

            // 👉 Paso 3: Aceptar permisos si aparecen
            try {
                WebElement permisoBtn = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                AppiumBy.androidUiAutomator("new UiSelector().text(\"While using the app\")")
                        )
                );
                permisoBtn.click();
                System.out.println("✔ Permiso concedido");
            } catch (Exception ex) {
                System.out.println("ℹ No se mostró solicitud de permisos");
            }

        } catch (Exception e) {
            System.err.println("❌ Error en el flujo de login: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDownTest() {
        // tearDown();
    }
}