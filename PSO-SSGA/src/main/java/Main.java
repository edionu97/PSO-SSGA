import Controller.WindowController;
import MethodImplementation.SSGAFct;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends  Application  {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/window.fxml"));

        Parent parent = loader.load();

        ((WindowController)loader.getController()).setStage(primaryStage);
        Scene scene = new Scene(parent);

        scene.getStylesheets().add("/style.css");

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String ...args){
        launch(args);
    }
}
