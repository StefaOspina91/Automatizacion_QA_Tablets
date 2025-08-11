package org.example;

import io.appium.java_client.MobileElement;
import io.appium.java_client.MobileBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginTest extends BaseTest {

    @BeforeEach
    public void setupTest() {
        setUp(); // Inicia Appium y el driver
    }

    @Test
    public void testLoginFlujoCompleto() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 20);

            // 👉 Paso 1: Clic en el botón "Quality Control"
            MobileElement qualityControlBtn = (MobileElement) wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            MobileBy.AndroidUIAutomator(
                                    "new UiSelector().className(\"android.widget.ImageView\").instance(1)"
                            )
                    )
            );
            qualityControlBtn.click();

            // 👉 Paso 2: Ingresar usuario y contraseña
            MobileElement username = (MobileElement) wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            MobileBy.xpath("//android.widget.EditText[@text='User']")
                    )
            );
            MobileElement password = driver.findElementByXPath("//android.widget.EditText[@text='Password']");
            MobileElement loginBtn = driver.findElementByAndroidUIAutomator(
                    "new UiSelector().textContains(\"Iniciar Sesión\")"
            );

            username.sendKeys("StefaOspina");
            password.sendKeys("sospina");
            loginBtn.click();

            System.out.println("✔ Credenciales ingresadas y login ejecutado");

            // 👉 Paso 3: Aceptar permisos si aparecen
            try {
                MobileElement permisoBtn = (MobileElement) wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                MobileBy.AndroidUIAutomator("new UiSelector().text(\"While using the app\")")
                        )
                );
                permisoBtn.click();
                System.out.println("✔ Permiso concedido");
            } catch (Exception ex) {
                System.out.println("ℹ No se mostró solicitud de permisos");
            }

        } catch (Exception e) {
            System.err.println(" Error en el flujo de login: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDownTest() {
        // tearDown(); // Descomentar si quieres cerrar la app al final
    }
}
