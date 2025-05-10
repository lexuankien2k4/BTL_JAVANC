package com.example.tx1.Controller;

import com.example.tx1.Entity.Product;
import com.example.tx1.DAO.ProductDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.List;
import java.util.Optional;

public class ProductController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> priceColumn;
    @FXML private TableColumn<Product, String> imageUrlColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private TextField searchField;
    @FXML private TableColumn<Product, String> imageColumn;

    @FXML private Pagination pagination;
    private int itemsPerPage = 10; // Số sản phẩm trên mỗi trang
    private ObservableList<Product> allProducts = FXCollections.observableArrayList(); // Danh sách tất cả sản phẩm

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        imageUrlColumn.setCellValueFactory(cellData -> cellData.getValue().imageUrlProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        imageColumn.setCellValueFactory(cellData -> cellData.getValue().imageUrlProperty());

        imageColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        imageView.setImage(new Image(url, true));
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        loadAllProducts();
        initPagination();
    }

    private void loadAllProducts() {
        allProducts.setAll(ProductDAO.getAllProducts());
    }

    private void initPagination() {
        int totalProducts = allProducts.size();
        int pageCount = (int) Math.ceil((double) totalProducts / itemsPerPage);
        pagination.setPageCount(Math.max(1, pageCount)); // Đảm bảo có ít nhất 1 trang
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            changePage(newIndex.intValue());
        });
        changePage(0); // Hiển thị trang đầu tiên khi khởi tạo
    }

    private void changePage(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, allProducts.size());
        ObservableList<Product> currentPageData = FXCollections.observableArrayList();
        if (fromIndex < toIndex) {
            currentPageData.addAll(allProducts.subList(fromIndex, toIndex));
        }
        productTable.setItems(currentPageData);
    }

    @FXML
    public void handleAdd() {
        showProductForm(null);
    }

    @FXML
    public void handleEdit() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showProductForm(selected);
        } else {
            showAlert("Vui lòng chọn sản phẩm để sửa.");
        }
    }

    @FXML
    public void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(AlertType.CONFIRMATION, "Bạn có chắc muốn xóa sản phẩm này?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                ProductDAO.deleteProduct(selected.getId());
                loadAllProducts(); // Tải lại toàn bộ sản phẩm
                initPagination(); // Khởi tạo lại pagination
            }
        } else {
            showAlert("Vui lòng chọn sản phẩm để xóa.");
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        List<Product> results = ProductDAO.searchProducts(keyword);
        allProducts.setAll(results);
        initPagination(); // Khởi tạo lại pagination với kết quả tìm kiếm
    }

    @FXML
    public void handleRefresh() {
        loadAllProducts();
        initPagination(); // Khởi tạo lại pagination với dữ liệu mới
        searchField.clear();
    }

    private void showProductForm(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ProductView/product_form.fxml"));
            Parent root = loader.load();

            ProductFormController controller = loader.getController();
            controller.setProduct(product);
            controller.setOnSaveCallback(() -> {
                handleRefresh();
            });

            Stage stage = new Stage();
            stage.setTitle(product == null ? "Thêm sản phẩm" : "Sửa sản phẩm");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi mở cửa sổ nhập liệu.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}