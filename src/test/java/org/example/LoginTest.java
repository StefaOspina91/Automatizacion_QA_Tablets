package org.example;

import org.openqa.selenium.WebElement;
import io.appium.java_client.MobileBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class LoginTest extends BaseTest {

    @BeforeEach
    public void setupTest() {
        setUp(); // Inicia Appium y el driver
    }

    @Test
    public void testLoginFlujoCompleto() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // 👉 1. Clic en módulo "Quality Control"
            WebElement  qualityControlBtn = (WebElement ) wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            MobileBy.AndroidUIAutomator(
                                    "new UiSelector().className(\"android.widget.ImageView\").instance(1)"
                            )
                    )
            );
            qualityControlBtn.click();

            //  2. Llenar usuario y contraseña
            WebElement  username = (WebElement ) wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            MobileBy.xpath("//android.widget.EditText[@text='User']")
                    )
            );
            WebElement  password = (WebElement ) driver.findElement(
                    MobileBy.xpath("//android.widget.EditText[@text='Password']")
            );
            WebElement  loginBtn = (WebElement ) driver.findElement(
                    MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"Iniciar Sesión\")")
            );

            username.sendKeys("StefaOspina");
            password.sendKeys("sospina");
            loginBtn.click();
            System.out.println("✅ Login realizado correctamente");

            // 👉 3. Manejar permiso (seleccionar "While using the app")
            WebElement  allowPermission = (WebElement ) wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            MobileBy.xpath("//android.widget.Button[@text='While using the app']")
                    )
            );
            allowPermission.click();
            System.out.println("✅ Permiso aceptado correctamente");

            // Aquí podrías seguir con el flujo llamando otras clases
          new QualityControlReportsPage(driver).selectCompany("BQC");

        } catch (Exception e) {
            System.err.println("❌ Error en el flujo de login: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDownTest() {
        // No cerrar aún si deseas seguir navegando por otras pantallas
        // tearDown();
    }
}
