package com.example.tx1.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

public class DashboardController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showProductView(); // Mặc định hiển thị danh sách sản phẩm
    }

    @FXML
    public void showProductView() {
        try {
            Pane view = FXMLLoader.load(getClass().getResource("/com/example/ProductView/product_view.fxml"));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            showError("Không thể tải giao diện sản phẩm.");
        }
    }


    @FXML
    public void handleExit() {
        Platform.exit();
    }

    private void showError(String msg) {
        Alert alert = new Alert(AlertType.ERROR, msg);
        alert.showAndWait();
    }
}
