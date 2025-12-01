package tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import org.example.userinterfaces.AppLauncherPage;

public class SelectQualityControlModule implements Task {

    public static SelectQualityControlModule open() {
        return Tasks.instrumented(SelectQualityControlModule.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(AppLauncherPage.QUALITY_CONTROL_MODULE)
        );
    }
}
