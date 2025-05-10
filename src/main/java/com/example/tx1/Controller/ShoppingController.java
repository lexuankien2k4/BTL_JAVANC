package com.example.tx1.Controller;

import com.example.tx1.DAO.OrderDAO;
import com.example.tx1.DAO.OrderItemDAO;
import com.example.tx1.DAO.PaymentDAO;
import com.example.tx1.DAO.ProductDAO;
import com.example.tx1.Entity.*; // Product, CartItem, Order, OrderItem, Payment
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.text.ParseException; // Cần cho việc parse số có thể phức tạp hơn
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
// import java.sql.Timestamp; // Không cần trực tiếp trong controller nếu CartItem tự quản lý

public class ShoppingController {

    @FXML private ScrollPane productsScrollPane;
    @FXML private FlowPane productsFlowPane;
    @FXML private ListView<CartItem> cartListView; // Dùng ListView<CartItem> với CartItem bạn cung cấp
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField searchField;
    @FXML private Button checkoutButton;

    private List<Product> allProductsMasterList = new ArrayList<>();
    private ObservableList<Product> displayedProductList = FXCollections.observableArrayList();
    private Map<Integer, CartItem> cartMap = new HashMap<>(); // Key là ProductID
    private ObservableList<CartItem> observableCartItems = FXCollections.observableArrayList();

    private int currentUserId = 1;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    // Pattern để trích xuất số từ chuỗi giá (ví dụ: "123.456 ₫" -> 123456)
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\d+([.,]\\d+)*");


    @FXML
    public void initialize() {
        setupProductsGrid();
        setupCartListView();
        setupSortComboBox();
        loadProducts();
    }

    private void setupProductsGrid() {
        productsFlowPane.setHgap(20);
        productsFlowPane.setVgap(20);
        productsFlowPane.setPadding(new Insets(15));
        productsFlowPane.setAlignment(Pos.TOP_LEFT);
    }

    // Helper method để parse chuỗi giá từ VARCHAR
    private double parsePriceString(String priceString) throws NumberFormatException {
        if (priceString == null || priceString.trim().isEmpty()) {
            return 0.0;
        }
        // Loại bỏ ký tự tiền tệ và khoảng trắng, thay dấu phẩy bằng dấu chấm nếu cần (tùy định dạng)
        // Ví dụ: "150.000 ₫" -> "150000"
        String cleanedString = priceString.replaceAll("[^\\d.,]", ""); // Giữ lại số, dấu chấm, dấu phẩy

        // Xử lý trường hợp dấu chấm là phân cách hàng nghìn và không có phần thập phân
        // hoặc dấu phẩy là dấu thập phân
        // Ví dụ: "150.000" -> 150000.0
        // Ví dụ: "150,50" -> 150.50 (nếu dấu phẩy là thập phân)
        // Logic này cần rất cẩn thận tùy theo định dạng giá trên web hoặc trong DB VARCHAR
        // Giả định đơn giản nhất: chỉ có số, loại bỏ hết dấu chấm phân cách hàng nghìn.
        cleanedString = cleanedString.replace(".", ""); // Bỏ dấu chấm (nếu là hàng nghìn)
        cleanedString = cleanedString.replace(",", "."); // Đổi dấu phẩy (nếu là thập phân) thành chấm

        if (cleanedString.isEmpty()) return 0.0;

        return Double.parseDouble(cleanedString);
    }


    private void loadProducts() {
        try {
            allProductsMasterList.clear();
            // ProductDAO.getAllProducts() trả về List<Product> với Product.getPrice() là String
            allProductsMasterList.addAll(ProductDAO.getAllProducts());
            displayedProductList.setAll(allProductsMasterList);
            displayProducts();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi Tải Sản Phẩm", "Không thể tải danh sách sản phẩm.");
        }
        if (allProductsMasterList.isEmpty()) {
            System.out.println("Không có sản phẩm nào từ CSDL.");
        }
    }

    private void displayProducts() {
        productsFlowPane.getChildren().clear();
        for (Product product : displayedProductList) {
            VBox productCard = createProductCard(product);
            productsFlowPane.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Product product) {
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(product.getImageUrl(), true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh sản phẩm: " + product.getImageUrl() + " - " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/tx1/images/default-product.png")));
            } catch (Exception ex) {
                System.err.println("Lỗi tải ảnh mặc định: " + ex.getMessage());
            }
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setMaxWidth(150);
        nameLabel.setWrapText(true);

        // product.getPrice() trả về String, cần parse sang double
        double priceValue = 0.0;
        try {
            priceValue = parsePriceString(product.getPrice());
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse giá sản phẩm '" + product.getName() + "': chuỗi giá là '" + product.getPrice() + "'");
        }
        Label priceLabel = new Label(formatPrice(priceValue));
        priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Button addButton = new Button("Thêm vào giỏ");
        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        addButton.setOnAction(e -> addProductToCart(product));

        VBox card = new VBox(10, imageView, nameLabel, priceLabel, addButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 0);");
        card.setPrefWidth(200);
        return card;
    }

    private void setupCartListView() {
        cartListView.setItems(observableCartItems);
        cartListView.setCellFactory(listView -> new ListCell<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // CartItem không có productName, phải tra cứu từ allProductsMasterList
                    Product productDetails = allProductsMasterList.stream()
                            .filter(p -> p.getId() == item.getProductId())
                            .findFirst().orElse(null);
                    String productNameToDisplay = (productDetails != null) ? productDetails.getName() : "Sản phẩm ID: " + item.getProductId();

                    // item.getPrice() trả về int. Chuyển sang double để tính subtotal.
                    double itemSubtotal = (double) item.getPrice() * item.getQuantity();
                    setText(String.format("%s x%d - %s",
                            productNameToDisplay,
                            item.getQuantity(),
                            formatPrice(itemSubtotal)
                    ));
                    // TODO: Có thể thêm ImageView cho sản phẩm trong giỏ hàng ở đây (setGraphic)
                    // nếu lấy được imageUrl từ productDetails
                }
            }
        });
        updateTotalLabel(0.0);
    }

    private void setupSortComboBox() {
        sortComboBox.getItems().addAll("Mặc định", "Giá tăng dần", "Giá giảm dần", "Tên A-Z", "Tên Z-A");
        sortComboBox.getSelectionModel().selectFirst();
        sortComboBox.setOnAction(e -> sortProducts(sortComboBox.getSelectionModel().getSelectedItem()));
    }

    private void sortProducts(String sortOption) {
        List<Product> productsToSort = new ArrayList<>(displayedProductList);
        Comparator<Product> priceComparator = Comparator.comparingDouble(p -> {
            try {
                return parsePriceString(p.getPrice());
            } catch (NumberFormatException e) {
                // Xử lý lỗi parse, ví dụ trả về 0 hoặc giá trị lớn để đẩy lỗi xuống cuối
                return sortOption.equals("Giá tăng dần") ? Double.MAX_VALUE : Double.MIN_VALUE;
            }
        });

        switch (sortOption) {
            case "Giá tăng dần":
                productsToSort.sort(priceComparator);
                break;
            case "Giá giảm dần":
                productsToSort.sort(priceComparator.reversed());
                break;
            case "Tên A-Z":
                productsToSort.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Tên Z-A":
                productsToSort.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            default:
                String currentSearchKeyword = searchField.getText().trim();
                if (currentSearchKeyword.isEmpty()) {
                    productsToSort = new ArrayList<>(allProductsMasterList);
                }
                productsToSort.sort(Comparator.comparingInt(Product::getId));
                break;
        }
        displayedProductList.setAll(productsToSort);
        displayProducts();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (!keyword.isEmpty()) {
            List<Product> filteredList = allProductsMasterList.stream()
                    .filter(p -> p.getName().toLowerCase().contains(keyword) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
            displayedProductList.setAll(filteredList);
        } else {
            displayedProductList.setAll(allProductsMasterList);
        }
        sortComboBox.getSelectionModel().selectFirst();
        displayProducts();
    }

    private void addProductToCart(Product product) {
        CartItem cartItemInMap = cartMap.get(product.getId());

        if (cartItemInMap != null) {
            cartItemInMap.setQuantity(cartItemInMap.getQuantity() + 1);
            // cartItemInMap.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // Nếu cần
        } else {
            // product.getPrice() là String, parse sang double
            double productPriceDouble = 0.0;
            try {
                productPriceDouble = parsePriceString(product.getPrice());
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse giá khi thêm vào giỏ cho sản phẩm '" + product.getName() + "': " + product.getPrice());
                showAlert("Lỗi Giá Sản Phẩm", "Không thể thêm sản phẩm '" + product.getName() + "' vào giỏ do lỗi định dạng giá.");
                return; // Không thêm sản phẩm lỗi giá
            }

            // Chuyển đổi giá double sang int cho CartItem.price (MẤT MÁT ĐỘ CHÍNH XÁC)
            int cartItemPriceInt = (int) Math.round(productPriceDouble);
            // Hoặc nếu bạn muốn giá theo đơn vị nhỏ nhất (ví dụ xu, nếu giá là 100.50 thì lưu 10050)
            // thì logic ở đây và khi lấy ra phải rất cẩn thận. Hiện tại làm tròn.

            // Sử dụng constructor của CartItem bạn đã cung cấp
            cartItemInMap = new CartItem(
                    currentUserId,
                    product.getId(),
                    1, // quantity
                    cartItemPriceInt // price là int
            );
            // Nếu bạn muốn tạo id cho cartItem ngay hoặc dùng constructor đầy đủ:
            // cartItemInMap = new CartItem(0, currentUserId, product.getId(), 1, cartItemPriceInt,
            //                              new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
            cartMap.put(product.getId(), cartItemInMap);
        }
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        List<CartItem> currentCartList = new ArrayList<>(cartMap.values());
        // Sắp xếp theo thời gian tạo (nếu CartItem có createdAt và bạn muốn)
        // currentCartList.sort(Comparator.comparing(CartItem::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
        observableCartItems.setAll(currentCartList);

        double total = calculateCartTotal();
        updateTotalLabel(total);
        checkFreeShipping(total);
    }

    @FXML
    private void handleRemoveFromCart() {
        CartItem selectedCartItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedCartItem != null) {
            int productId = selectedCartItem.getProductId();
            CartItem itemInMap = cartMap.get(productId);

            if (itemInMap != null) {
                if (itemInMap.getQuantity() > 1) {
                    itemInMap.setQuantity(itemInMap.getQuantity() - 1);
                    // itemInMap.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                } else {
                    cartMap.remove(productId);
                }
                updateCartDisplay();
            }
        } else {
            showAlert("Chưa Chọn Sản Phẩm", "Vui lòng chọn một sản phẩm trong giỏ hàng để xóa.");
        }
    }

    @FXML
    private void handleCheckout() {
        if (cartMap.isEmpty()) {
            showAlert("Giỏ Hàng Trống", "Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác Nhận Đơn Hàng");
        confirmation.setHeaderText("Bạn có chắc chắn muốn đặt hàng với tổng số tiền là " + totalLabel.getText() + "?");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean orderPlacedSuccessfully = processOrderPlacement();
            if (orderPlacedSuccessfully) {
                cartMap.clear();
                updateCartDisplay();
                showAlert("Đặt Hàng Thành Công", "Cảm ơn bạn đã mua sắm!");
            } else {
                showAlert("Lỗi Đặt Hàng", "Không thể xử lý đơn hàng của bạn.");
            }
        }
    }

    private boolean processOrderPlacement() {
        if (cartMap.isEmpty()) return false;
        double totalAmount = calculateCartTotal(); // totalAmount là double
        Date currentDate = new Date();

        Payment payment = new Payment(0, currentUserId, currentDate, "cash", totalAmount, "completed");
        int paymentId = PaymentDAO.insertPayment(payment); // PaymentDAO.amount là double, DB là DECIMAL
        if (paymentId <= 0) {
            System.err.println("Lỗi: Không thể tạo Payment.");
            return false;
        }

        Order order = new Order(0, currentUserId, paymentId, currentDate, "pending", totalAmount);
        int orderId = OrderDAO.insertOrder(order); // OrderDAO.totalPrice là double, DB là DECIMAL. Nhớ sửa OrderDAO.setTimestamp
        if (orderId <= 0) {
            System.err.println("Lỗi: Không thể tạo Order. Payment ID: " + paymentId);
            return false;
        }

        for (CartItem cartItem : cartMap.values()) {
            // cartItem.getPrice() trả về int. OrderItem.price trong DB là DECIMAL, OrderItemDAO.setDouble()
            // Chuyển int sang double cho OrderItem
            double orderItemPrice = (double) cartItem.getPrice();
            OrderItem orderItem = new OrderItem(
                    0, orderId, cartItem.getProductId(),
                    cartItem.getQuantity(), orderItemPrice
            );
            int orderItemId = OrderItemDAO.insertOrderItem(orderItem);
            if (orderItemId <= 0) {
                System.err.println("Lỗi: Không thể tạo OrderItem cho Product ID: " + cartItem.getProductId());
                return false;
            }
        }
        System.out.println("Đơn hàng đã được tạo thành công. Order ID: " + orderId);
        return true;
    }

    private double calculateCartTotal() {
        return cartMap.values().stream()
                // cartItem.getPrice() trả về int, chuyển sang double để tính toán
                .mapToDouble(item -> (double) item.getPrice() * item.getQuantity())
                .sum();
    }

    private String formatPrice(double priceValue) {
        return currencyFormatter.format(priceValue);
    }

    private void updateTotalLabel(double total) {
        totalLabel.setText("Tổng tiền: " + formatPrice(total));
    }

    private void checkFreeShipping(double total) {
        final double FREE_SHIPPING_THRESHOLD = 300000;
        if (total >= FREE_SHIPPING_THRESHOLD) {
            checkoutButton.setText("Thanh toán (FREESHIP)");
            checkoutButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        } else {
            checkoutButton.setText("Thanh toán");
            checkoutButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (title.toLowerCase().contains("lỗi")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}