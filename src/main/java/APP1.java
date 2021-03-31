import javafx.application.Application;
import javafx.stage.Stage;


public class APP1 extends Application
{
    @Override
    public void start(Stage stage) {
        gui.app1.Gui guiApp1 = new gui.app1.Gui();
        guiApp1.start(stage);

    }
    public static void main(String[] args) {
        launch();
    }
}