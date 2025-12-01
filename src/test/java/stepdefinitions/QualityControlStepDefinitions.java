package stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import org.example.tasks.SelectQualityControlModule;
import utils.DriverFactory;

public class QualityControlStepDefinitions {

    @Before
    public void setTheStage() {
        // Inicializa el sistema de actores de Screenplay
        OnStage.setTheStage(new OnlineCast());
    }

    @Dado("que {string} accede a la aplicación de Quality Management en la tablet")
    public void queElUsuarioAccedeALaAplicacion(String usuario) {

        // Creamos el actor y le damos la habilidad de usar el driver
        OnStage.theActorCalled(usuario)
                .can(BrowseTheWeb.with(DriverFactory.create()));
    }

    @Dado("la aplicación se ha iniciado correctamente")
    public void appIniciadaCorrectamente() {
        // Aquí normalmente podríamos hacer una validación.
        // Por ahora lo dejamos vacío.
    }

    @Cuando("ingresa al módulo de Quality Control desde el launcher")
    public void ingresarAlModuloQualityControl() {

        // El actor en foco ejecuta la Task
        OnStage.theActorInTheSpotlight().attemptsTo(
                SelectQualityControlModule.open()
        );
    }

    @Entonces("debería visualizar la pantalla principal del módulo de Quality Control")
    public void validarPantallaPrincipalQC() {
        // Más adelante agregamos una Question.
        System.out.println("Validación pendiente: pantalla QC");
    }
}
