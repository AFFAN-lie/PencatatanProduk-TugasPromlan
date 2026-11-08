package kelas;

import javafx.application.Application;
import javafx.stage.Stage;
import kelas.data.DataProduk;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new Navigator(stage, new DataProduk()).bukaForm();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
