package com.example.tx1.Controller;

import com.example.tx1.DAO.ProductDAO;
import com.example.tx1.Entity.Product;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ProductFormController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField imageUrlField;
    @FXML private TextArea descriptionField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ImageView imageView;

    private Product product;
    private Runnable onSaveCallback;

    public void setProduct(Product product) {
        this.product = product;

        if (product != null) {
            // Set dữ liệu cũ vào form nếu đang sửa
            nameField.setText(product.getName());
            priceField.setText(product.getPrice());
            imageUrlField.setText(product.getImageUrl());
            descriptionField.setText(product.getDescription());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String price = priceField.getText();
        String imageUrl = imageUrlField.getText();
        String description = descriptionField.getText();

        if (name.isEmpty() || price.isEmpty()) {
            showAlert("Tên và giá không được để trống.");
            return;
        }

        if (product == null) {
            // Thêm mới
            Product newProduct = new Product(name, price, imageUrl, description);
            ProductDAO.insertProduct(newProduct);
        } else {
            // Cập nhật
            product.setName(name);
            product.setPrice(price);
            product.setImageUrl(imageUrl);
            product.setDescription(description);
            ProductDAO.updateProduct(product);
        }

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        // Đóng cửa sổ
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    private void handleImagePreview() {
        String url = imageUrlField.getText();
        if (url != null && !url.isEmpty()) {
            try {
                imageView.setImage(new Image(url, true));
            } catch (Exception e) {
                // Hiển thị ảnh mặc định hoặc xóa ảnh nếu lỗi
                imageView.setImage(null);
            }
        } else {
            imageView.setImage(null);
        }
    }
}