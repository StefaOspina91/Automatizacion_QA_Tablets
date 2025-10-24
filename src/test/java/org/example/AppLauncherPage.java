package org.example;


import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AppLauncherPage {
    private final AndroidDriver driver;
    private final WebDriverWait wait;

    // Botón "Quality Control" → es un ImageView en posición 1
    private final By qualityControlModule = AppiumBy.androidUIAutomator(
            "new UiSelector().textContains(\"android.widget.ImageView\").instance(1)"
    );

    public AppLauncherPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void selectQualityControlModule() {
        try {
            WebElement moduleBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(qualityControlModule)
            );
            moduleBtn.click();
            System.out.println(" Módulo 'Quality Control' seleccionado.");
        } catch (Exception e) {
            System.err.println(" Error al seleccionar módulo Quality Control: " + e.getMessage());
            throw e;
        }
    }
}
