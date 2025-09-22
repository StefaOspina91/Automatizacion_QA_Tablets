package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseTest {

    @BeforeEach
    public void setupTest() {
        setUp();
    }

    @Test
    public void testFlujoCompleto() {
        try {
            // 👉 Page Objects
            AppLauncherPage launcherPage = new AppLauncherPage(driver);
            LoginPage loginPage = new LoginPage(driver);
            QualityControlReportsPage reportsPage = new QualityControlReportsPage(driver);

            // 👉 1. Seleccionar módulo Quality Control
            launcherPage.selectQualityControlModule();

            // 👉 2. Login + permisos
            loginPage.login("StefaOspina", "sospina");

            // 👉 3. Selección de compañía
            reportsPage.selectCompany("BQC");

        } catch (Exception e) {
            System.err.println("❌ Error en el flujo de prueba: " + e.getMessage());
        }
    }

   @AfterEach
    public void tearDownTest() {
        tearDown();
    }
}
