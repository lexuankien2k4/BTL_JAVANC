package com.example.tx1;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load file Dashboard.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard/Dashboard.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Trang quản lý");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace(); // Giúp debug lỗi chi tiết
        }
    }
}
//@Override
//public void start(Stage primaryStage) throws Exception {
//    // Tải FXML
//    FXMLLoader loader = new FXMLLoader(getClass().getResource("/customer_shop.fxml"));
//
//    // Nạp Parent từ FXML
//    Parent root = loader.load();
//
//    // Thiết lập Scene và Stage
//    primaryStage.setTitle("Customer Shop");
//    primaryStage.setScene(new Scene(root));
//    primaryStage.show();
//}
//
//    public static void main(String[] args) {
//        // Khởi chạy ứng dụng JavaFX
//        launch(args);
//    }
//}
