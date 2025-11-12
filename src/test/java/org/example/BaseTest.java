package org.example;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import org.openqa.selenium.WebElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class BaseTest {
    protected AndroidDriver driver;

    public void setUp() {
        DesiredCapabilities caps = new DesiredCapabilities();

        // ==== PARCHE MÍNIMO ====
        // Appium 2 / java-client 9 usan prefijo "appium:" en las capabilities
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:deviceName", "emulator-5554");
        caps.setCapability("appium:udid", "emulator-5554");
        caps.setCapability("appium:noReset", true);
        caps.setCapability("appium:newCommandTimeout", 300);

        // Usa el case EXACTO confirmado por adb:
        caps.setCapability("appium:appPackage", "com.ght.QualityManagementApp.test");
        caps.setCapability("appium:appActivity", "com.ght.QualityManagementApp.test.MainActivity");
        // Espera por posibles pantallas iniciales (splash, main, etc.)
        caps.setCapability("appium:appWaitActivity", "*.MainActivity,*.Splash*,*.*");
        // ======================

        try {
            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), caps);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private boolean cerrarAlFinal = false;

    public void tearDown() {
        if (driver != null && cerrarAlFinal) {
            driver.quit();
        }
    }
}
