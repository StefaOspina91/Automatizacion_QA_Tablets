package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginTest extends BaseTest {

    // Timeouts cortos y claros
    private static final Duration T_SHORT = Duration.ofSeconds(5);
    private static final Duration T_LONG  = Duration.ofSeconds(10);

    private WebDriverWait waitShort;
    private WebDriverWait waitLong;

    @BeforeEach
    public void setupTest() {
        setUp();
        waitShort = new WebDriverWait(driver, T_SHORT);
        waitLong  = new WebDriverWait(driver, T_LONG);
    }

    @AfterEach
    public void tearDownTest() { tearDown(); }

    @Test
    public void testFlujoCompleto() {
        try {
            // Page Objects existentes
            LoginPage loginPage = new LoginPage(driver);
            QualityControlReportsPage reportsPage = new QualityControlReportsPage(driver);

            // 1) Abrir módulo Quality Control
            abrirModuloQualityControl();

            // 2) Login (y permisos del SO si aparecen)
            loginPage.login("StefaOspina", "sospina");
            aceptarPermisosSiAparecen();

            // 3) Seleccionar compañía
            reportsPage.selectCompany("BQC");

            // 4) Traer USDA, escribir y buscar
            String usda = new org.example.infra.db.DbConnectionTest().obtenerUnaUsda();
            if (usda == null || usda.isEmpty()) throw new RuntimeException("USDA vacío desde BD");
            escribirUsda(usda);
            clicBuscar();

            System.out.println("✅ Flujo completo OK");

        } catch (Exception e) {
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail("Error en el flujo de prueba: " + e.getMessage(), e);
        }
    }

    // -------------------- Pasos compactos --------------------

    private void abrirModuloQualityControl() {
        // Pantalla bienvenida (si aparece)
        try { waitShort.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[contains(@text,'Bienvenido')]"))); } catch (Exception ignored) {}

        WebElement card = localizarCardQualityControl();
        clickSmart(card);
        System.out.println("Módulo Quality Control abierto");
    }

    private void escribirUsda(String usda) {
        WebElement input = localizarInputUsda();
        input.click();
        try { input.clear(); } catch (Exception ignored) {}
        input.sendKeys(usda);
        System.out.println("USDA escrito: " + usda);
    }

    private void clicBuscar() {
        System.out.println("Intentando hacer clic en botón 'Buscar'...");

        try {
            // --- Espera a que el botón Buscar esté visible y clickable ---
            By buscarButton = AppiumBy.androidUIAutomator(
                    "new UiSelector().textContains(\"Buscar\")"); // más flexible que text()

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement boton = wait.until(ExpectedConditions.elementToBeClickable(buscarButton));

            boton.click();
            System.out.println("✅ Clic en botón 'Buscar' ejecutado correctamente.");

        } catch (Exception e1) {
            System.err.println("⚠️ No se pudo hacer clic en botón 'Buscar' con UiSelector. Intentando fallback...");

            // Fallback usando XPath del inspector (por si el TextView no es clickable)
            try {
                By xpathBuscar = AppiumBy.xpath("(//android.widget.TextView[@text='Buscar'])[2]");
                WebElement label = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(xpathBuscar));

                // Si el TextView no tiene clickable, subimos al padre
                WebElement botonPadre = null;
                try {
                    botonPadre = label.findElement(AppiumBy.xpath("./ancestor::*[@clickable='true'][1]"));
                } catch (Exception ignored) {}

                WebElement target = (botonPadre != null) ? botonPadre : label;
                target.click();

                System.out.println("✅ Clic en botón 'Buscar' (fallback XPath) ejecutado.");
            } catch (Exception e2) {
                System.err.println("❌ No se logró hacer clic en 'Buscar': " + e2.getMessage());
                tapCentroFallback(); // último recurso
            }
        }
    }
    private void tapCentroFallback() {
        try {
            WebElement posible = driver.findElement(
                    AppiumBy.xpath("(//android.widget.TextView[@text='Buscar'])[2]"));
            Rectangle r = posible.getRect();
            int x = r.x + r.width / 2;
            int y = r.y + r.height / 2;

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(List.of(tap));

            System.out.println("✅ Tap directo en el centro de 'Buscar'.");
        } catch (Exception e) {
            System.err.println("❌ TapFallback también falló: " + e.getMessage());
        }
    }



    // -------------------- Localizadores simples --------------------

    private WebElement localizarCardQualityControl() {
        // 1) por texto “Quality” y “Control” y su ancestro clickable
        List<WebElement> labels = driver.findElements(
                AppiumBy.xpath("//*[contains(@text,'Quality') and contains(@text,'Control')]"));
        for (WebElement l : labels) {
            if (isVisible(l)) {
                WebElement clickable = findAncestorClickable(l);
                if (clickable != null) return clickable;
                return l;
            }
        }
        // 2) fallback: segundo ImageView (card central)
        List<WebElement> imgs = driver.findElements(AppiumBy.className("android.widget.ImageView"));
        return imgs.size() >= 2 ? imgs.get(1) : waitLong.until(d -> null); // fuerza Timeout si no hay
    }

    private WebElement localizarInputUsda() {
        // a) por hint
        WebElement e = firstVisible(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().className(\"android.widget.EditText\").textContains(\"Ingrese USDA\")")
        );
        if (e != null) return e;

        // b) por content-desc cercano
        e = firstVisible(AppiumBy.xpath(
                "//*[contains(@content-desc,'Ingrese USDA') or contains(@content-desc,'USDA')]" +
                        "//following::*[self::android.widget.EditText or .//android.widget.EditText][1]"));
        if (e != null) return e;

        // c) primer EditText visible
        List<WebElement> all = driver.findElements(AppiumBy.className("android.widget.EditText"));
        for (WebElement el : all) if (isVisible(el)) return el;

        return waitLong.until(d -> null); // Timeout -> falla clara
    }

    // -------------------- Helpers mínimos reutilizables --------------------

    /** Devuelve el primer elemento visible que matchee alguno de los locators (o null). */
    private WebElement firstVisible(By... locators) {
        for (By by : locators) {
            for (WebElement el : driver.findElements(by)) {
                if (isVisible(el)) return el;
            }
        }
        return null;
    }

    private WebElement findAncestorClickable(WebElement el) {
        try { return el.findElement(AppiumBy.xpath("./ancestor::*[@clickable='true'][1]")); }
        catch (Exception ignored) { return null; }
    }

    /** Intenta click normal; si falla, hace tap al centro (corrige “Origin of move must be set”). */
    private void clickSmart(WebElement el) {
        if (el == null) throw new IllegalArgumentException("Elemento null en clickSmart");
        try {
            waitShort.until(ExpectedConditions.elementToBeClickable(el)).click();
        } catch (Exception ignored) {
            tapCenter(el);
        }
    }

    private void tapCenter(WebElement el) {
        Rectangle r = el.getRect();
        int x = r.x + r.width / 2;
        int y = r.y + r.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }

    private boolean isVisible(WebElement el) {
        try { return el != null && el.isDisplayed(); } catch (Exception ignored) { return false; }
    }

    // -------------------- Permisos SO (compacto) --------------------

    private void aceptarPermisosSiAparecen() {
        // Hasta 3 diálogos en cadena
        for (int i = 0; i < 3; i++) {
            if (clickIfPresent(By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"))) continue;
            if (clickIfPresent(By.id("com.android.permissioncontroller:id/permission_allow_one_time_button")))       continue;
            if (clickIfPresent(By.id("com.android.permissioncontroller:id/permission_allow_button")))                continue;

            if (clickIfPresent(AppiumBy.xpath("//android.widget.Button[" +
                    "contains(@text,'While using the app') or contains(@text,'Only this time') or contains(@text,'Allow') or " +
                    "contains(@text,'Mientras la app') or contains(@text,'Solo esta vez') or contains(@text,'Permitir')]")))
                continue;

            break; // no hay permiso que aceptar
        }
    }

    private boolean clickIfPresent(By by) {
        try {
            WebElement el = waitShort.until(ExpectedConditions.presenceOfElementLocated(by));
            el.click();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
