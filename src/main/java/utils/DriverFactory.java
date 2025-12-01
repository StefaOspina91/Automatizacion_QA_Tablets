package utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverFactory {

    public static AndroidDriver create() {
        DesiredCapabilities caps = new DesiredCapabilities();

        // ==== Capabilities IGUALES a tu BaseTest =====
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:deviceName", "emulator-5554");
        caps.setCapability("appium:udid", "emulator-5554");
        caps.setCapability("appium:noReset", true);
        caps.setCapability("appium:newCommandTimeout", 300);

        caps.setCapability("appium:appPackage", "com.ght.QualityManagementApp.test");
        caps.setCapability("appium:appActivity", "com.ght.QualityManagementApp.test.MainActivity");
        caps.setCapability("appium:appWaitActivity", "*.MainActivity,*.Splash*,*.*");
        // =============================================

        try {
            return new AndroidDriver(
                    new URL("http://127.0.0.1:4723/"),
                    caps
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL del servidor Appium incorrecta", e);
        }
    }
}
