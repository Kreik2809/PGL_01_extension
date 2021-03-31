import javafx.application.Application;
import javafx.stage.Stage;


public class APP2 extends Application
{
    @Override
    public void start(Stage stage) {
        gui.app2.Gui guiApp2 = new gui.app2.Gui();
        guiApp2.start(stage);

    }

    public static void main(String[] args) {
        launch();
    }
} 