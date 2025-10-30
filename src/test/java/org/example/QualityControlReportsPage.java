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

    // Selectores conocidos
    private final By companyDropdown = AppiumBy.xpath("(//android.view.ViewGroup[@content-desc='Compañía'])[2]/android.widget.ImageView");

    public QualityControlReportsPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void selectCompany(String companyName) {
        try {
            System.out.println("Intentando abrir dropdown de Compañía...");

            // Esperar que el dropdown esté clickable y abrirlo
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(companyDropdown));
            dropdown.click();
            System.out.println(" Dropdown 'Compañía' abierto correctamente.");

            // Esperar que aparezcan las opciones dentro del ScrollView
            By optionLocator = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='" + companyName + "']");
            WebElement targetCompany = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));

            // Click sobre la compañía deseada
            targetCompany.click();
            System.out.println(" Compañía '" + companyName + "' seleccionada correctamente.");

        } catch (Exception e) {
            System.err.println(" Error al seleccionar compañía: " + e.getMessage());
            throw e;
        }
    }
}
