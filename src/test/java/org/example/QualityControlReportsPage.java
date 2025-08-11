package org.example;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class QualityControlReportsPage {

    private AndroidDriver driver;

    public QualityControlReportsPage(AndroidDriver driver) {
        this.driver = driver;
    }

    // El localizador ahora es AppiumBy.id
    private final AppiumBy companySelector = AppiumBy.id("com.ght.QualityManagementApp:id/compania");

    public void selectCompany(String companyName) {
        try {
            // Se usa el driver directamente sin conversiones
            WebElement selectorElement = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(companySelector));
            selectorElement.click();
            System.out.println("✅ Selector de compañía clickeado.");

            AppiumBy companyNameLocator = AppiumBy.xpath("//android.widget.TextView[@text=\"" + companyName + "\"]");
            WebElement targetCompany = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(companyNameLocator));
            targetCompany.click();
            System.out.println("✅ Compañía '" + companyName + "' seleccionada con éxito.");

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar la compañía: " + e.getMessage());
            throw e;
        }
    }
}