package org.example;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class QualityControlReportsPage {

    private final AndroidDriver driver;
    private final AppiumBy companySelector = (AppiumBy) AppiumBy.id("com.ght.QualityManagementApp:id/compania");

    public QualityControlReportsPage(AndroidDriver driver) {
        this.driver = driver;
    }

    public void selectCompany(String companyName) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 👉 Abrir desplegable
            WebElement selectorElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(companySelector)
            );
            selectorElement.click();
            System.out.println("✅ Selector de compañía clickeado.");

            // 👉 Seleccionar la compañía (ej. "BQC")
            AppiumBy companyOption = (AppiumBy) AppiumBy.xpath("//android.widget.TextView[@text='" + companyName + "']");
            WebElement targetCompany = wait.until(
                    ExpectedConditions.presenceOfElementLocated(companyOption)
            );
            targetCompany.click();
            System.out.println("✅ Compañía '" + companyName + "' seleccionada con éxito.");

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar la compañía: " + e.getMessage());
            throw e;
        }
    }
}
