// Actividad Integradora 2
//  A01771003 Giuliana Sofía Islas Carbajal
//  A01368579 Noreth Sofia Villalpando Saldaña
//  A00829553 Roberto Miguel Rodriguez Hermann
package helloapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;


public class HelloApplication extends Application {
    
    //Funcion de inicio para el programa
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Actividad Integradora 2");
        stage.setScene(scene);
        stage.show();

        HelloController controller = fxmlLoader.getController();

    }

    public static void main(String[] args) {
        
        launch(args);
    }
}