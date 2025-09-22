package org.example;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class QualityControlReportsPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    // Usar By directamente
    private final By companyDropdown = AppiumBy.xpath("//android.widget.TextView[@text='Compañía']");

    public QualityControlReportsPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(40));
    }

    public void selectCompany(String companyName) {
        try {
            // 👉 Esperar dropdown
            WebElement dropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(companyDropdown)
            );
            dropdown.click();
            System.out.println("✅ Dropdown 'Compañía' abierto.");

            // 👉 Seleccionar compañía
            WebElement targetCompany = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            AppiumBy.xpath("//android.view.ViewGroup[@content-desc='" + companyName + "']")
                    )
            );
            targetCompany.click();
            System.out.println("✅ Compañía '" + companyName + "' seleccionada.");
        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar compañía: " + e.getMessage());
            throw e;
        }
    }
}
