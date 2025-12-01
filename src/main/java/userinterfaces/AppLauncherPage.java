package userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import io.appium.java_client.MobileBy;

public class AppLauncherPage {

    /**
     * Módulo "Quality Control"
     * Equivalente al locator original: instance(1) en ImageView
     */
    public static final Target QUALITY_CONTROL_MODULE = Target.the("módulo Quality Control")
            .located(MobileBy.AndroidUIAutomator(
                    "new UiSelector().className(\"android.widget.ImageView\").instance(1)"
            ));
}
