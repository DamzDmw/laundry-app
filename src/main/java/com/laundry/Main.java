package com.laundry;

import com.laundry.ui.LoginScene;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Titik masuk utama aplikasi LaundryApp.
 * Menjalankan JavaFX dan menampilkan halaman Login pertama kali.
 *
 * Cara menjalankan:
 *   mvn javafx:run
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("LaundryApp - Login");
        stage.setScene(LoginScene.create(stage));
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
