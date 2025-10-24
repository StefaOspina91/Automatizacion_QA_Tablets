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
        caps.setCapability("platformName", "Android");
        caps.setCapability("deviceName", "emulator-5554");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("appPackage", "com.ght.QualityManagementApp");
        caps.setCapability("appActivity", "com.ght.QualityManagementApp.MainActivity");

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
