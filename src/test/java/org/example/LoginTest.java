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
            reportsPage.selectCompany("QU");

            // 4) Traer USDA, escribir y buscar
            String usda = new org.example.infra.db.DbConnectionTest().obtenerUnaUsda();
            if (usda == null || usda.isEmpty()) throw new RuntimeException("USDA vacío desde BD");
            escribirUsda(usda);
            clicBuscar();

            // Espera un poco a que se renderice la orden
            Thread.sleep(800);

            // Clic en pestaña Inspección
            clicInspeccion();


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

        // Asegura que los controles de abajo estén visibles (el de arriba se mueve)
        asegurarControlesInferiores();

        // Localiza el botón "Buscar" inferior (más abajo en Y)
        WebElement boton = null;
        try {
            // Espera a que al menos algún 'Buscar' exista
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(d -> !driver.findElements(
                            AppiumBy.androidUIAutomator("new UiSelector().text(\"Buscar\")")).isEmpty());
            boton = findBuscarInferior();
        } catch (Exception ignored) {}

        if (boton != null) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(boton));
            } catch (Exception ignored) { /* seguimos con clickSmart */ }

            clickSmart(boton);
            System.out.println("✅ Clic en botón 'Buscar' (inferior) ejecutado.");
            return;
        }

        // Fallback extremo si no lo encontró (coordenadas aproximadas abajo-derecha)
        try {
            org.openqa.selenium.Dimension win = driver.manage().window().getSize();
            int x = (int) (win.width * 0.90);
            int y = (int) (win.height * 0.92);

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(List.of(tap));

            System.out.println("✅ Tap por coordenadas en zona de botón 'Buscar' inferior.");
        } catch (Exception e) {
            System.err.println("❌ No se logró hacer clic en 'Buscar': " + e.getMessage());
        }
    }
    // === 1) Click robusto en "Inspección" ===
    private void clicInspeccion() {
        System.out.println("Intentando hacer clic en la pestaña 'Inspección'…");

        // Asegura que ya estás en detalle tras Buscar
        esperarVistaDetalle();

        // Opcional: sube al principio por si quedó scrolleado
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).setAsVerticalList().scrollToBeginning(2)"));
        } catch (Exception ignored) {}

        // 1) Localiza label "Inspección" con tolerancia a tildes/mayúsculas
        WebElement label = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(drv -> {
                    List<WebElement> c1 = drv.findElements(
                            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Inspecci\")"));
                    if (!c1.isEmpty()) return c1.get(0);
                    List<WebElement> c2 = drv.findElements(
                            AppiumBy.xpath("//*[contains(@text,'Inspección') or contains(@text,'Inspecci')]"));
                    return c2.isEmpty() ? null : c2.get(0);
                });

        // 2) Busca ancestro clickable (hasta 6 niveles)
        WebElement objetivo = label;
        try {
            WebElement n = label;
            for (int i = 0; i < 6; i++) {
                if (Boolean.parseBoolean(n.getAttribute("clickable"))) {
                    objetivo = n;
                    break;
                }
                n = n.findElement(AppiumBy.xpath(".."));
            }
        } catch (Exception ignored) {}

        // 3) Click; si no responde, tap con offset hacia arriba (hit-area suele ser del contenedor)
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(objetivo))
                    .click();
            System.out.println("➡️ Click sobre contenedor/label de 'Inspección'.");
        } catch (Exception e) {
            System.out.println("⚠️ Click falló; aplicando tap táctil con pequeño offset…");
            tapOffset(label, 0, -20); // 20 px hacia arriba respecto al label
        }

        // 4) Validación flexible del contenido de Inspección
        validarContenidoInspeccion();
        System.out.println("✅ Pestaña 'Inspección' activa/visible.");
    }

    private void validarContenidoInspeccion() {
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(8));

        // Primer intento: sin mover scroll
        boolean ok = w.until(drv ->
                !drv.findElements(AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"General\")")).isEmpty()
                        || !drv.findElements(AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"Reporte de Calidad\")")).isEmpty()
                        || !drv.findElements(AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"Producto\")")).isEmpty()
        );

        if (!ok) {
            // Sube por si los encabezados quedaron fuera de viewport y reintenta
            try {
                driver.findElement(AppiumBy.androidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true)).setAsVerticalList().scrollToBeginning(2)"));
            } catch (Exception ignored) {}

            new WebDriverWait(driver, Duration.ofSeconds(6)).until(drv ->
                    !drv.findElements(AppiumBy.androidUIAutomator(
                            "new UiSelector().textContains(\"General\")")).isEmpty()
                            || !drv.findElements(AppiumBy.androidUIAutomator(
                            "new UiSelector().textContains(\"Reporte de Calidad\")")).isEmpty()
                            || !drv.findElements(AppiumBy.androidUIAutomator(
                            "new UiSelector().textContains(\"Producto\")")).isEmpty()
            );
        }
    }

    // === 3) Tap utilitario con offset relativo al elemento ===
    private void tapOffset(WebElement el, int dx, int dy) {
        Rectangle r = el.getRect();
        int x = r.x + r.width / 2 + dx;
        int y = r.y + r.height / 2 + dy;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }


    // Espera a que cargue la vista de detalle de la orden tras pulsar Buscar
    private void esperarVistaDetalle() {
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Usa varios textos para ser tolerante a cambios/tildes
        w.until(d ->
                !driver.findElements(AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"Orden De Producción\")")).isEmpty()
                        || !driver.findElements(AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"Orden de Produccion\")")).isEmpty()  // sin tilde
                        || !driver.findElements(AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"Eventos USDA\")")).isEmpty()
        );
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
    /** Quita foco del buscador superior y fuerza a que aparezcan los controles inferiores. */
    private void asegurarControlesInferiores() {
        // Toca una zona segura en el centro para quitar foco del input superior
        tapZonaSegura();           // ya lo tienes; si no, te lo dejo abajo
        dormir(250);

        // Empuja un pelín hacia abajo para forzar re-layout si hiciera falta
        try {
            org.openqa.selenium.Dimension win = driver.manage().window().getSize();
            int cx = win.width / 2;
            int y1 = (int) (win.height * 0.45);
            int y2 = (int) (win.height * 0.55);

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "nudge");
            Sequence swipe = new Sequence(finger, 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, y1));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(200), PointerInput.Origin.viewport(), cx, y2));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(List.of(swipe));
        } catch (Exception ignored) {}

        dormir(200);
    }
    private void tapZonaSegura() {
        org.openqa.selenium.Dimension win = driver.manage().window().getSize();
        int x = (int) (win.width * 0.50);
        int y = (int) (win.height * 0.40);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }
    private void dormir(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }


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
    /** Devuelve el elemento 'Buscar' que está más abajo (botón inferior), evitando el de la barra superior. */
    private WebElement findBuscarInferior() {
        List<WebElement> todos = driver.findElements(
                AppiumBy.androidUIAutomator("new UiSelector().text(\"Buscar\")"));

        if (todos.isEmpty()) return null;

        // Elegimos el que tenga mayor coordenada Y (más cerca de la parte inferior)
        WebElement elegido = todos.get(0);
        int maxY = elegido.getRect().y;

        for (WebElement e : todos) {
            int y = e.getRect().y;
            if (y > maxY) {
                maxY = y;
                elegido = e;
            }
        }
        return elegido;
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
