package org.example;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By usernameField = AppiumBy.xpath("//android.widget.EditText[@text='User']");
    private final By passwordField = AppiumBy.xpath("//android.widget.EditText[@text='Password']");
    private final By loginButton = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Iniciar Sesión\")");
    private final By permissionButton = AppiumBy.xpath("//android.widget.Button[@text='While using the app']");

    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void login(String username, String password) {
        try {
            //  1. Usuario y contraseña
            WebElement userInput = wait.until(ExpectedConditions.presenceOfElementLocated(usernameField));
            WebElement passInput = driver.findElement(passwordField);
            WebElement loginBtn = driver.findElement(loginButton);

            userInput.sendKeys(username);
            passInput.sendKeys(password);
            loginBtn.click();
            System.out.println(" Login realizado correctamente.");

            //  2. Permiso
            WebElement allowPermission = wait.until(
                    ExpectedConditions.presenceOfElementLocated(permissionButton)
            );
            allowPermission.click();
            System.out.println(" Permiso aceptado correctamente.");

        } catch (Exception e) {
            System.err.println(" Error en login: " + e.getMessage());
            throw e;
        }
    }
}
