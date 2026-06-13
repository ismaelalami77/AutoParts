package ManagerView.CategoryManagement;

import Connection.CategoryDAO;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
public class ViewCategories {

    public static ArrayList<Category> categories = new ArrayList<>();
    public static ObservableList<Category> observableCategories = FXCollections.observableArrayList();

    private BorderPane root;
    private TableView<Category> categoriesTable;

    private VBox leftVBox;
    private VBox centerVBox;

    private Text manageCategoriesText;
    private TextField searchTextField;

    private Button addBtn;
    private Button updateBtn;

    private AddCategoryScene addCategoryScene;
    private UpdateCategoryScene updateCategoryScene;

    public ViewCategories() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        createCenterContent();
        createLeftButtons();

        root.setLeft(leftVBox);
        root.setCenter(centerVBox);

        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        loadCategories();
    }

    private void createCenterContent() {
        centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(25));
        centerVBox.getStyleClass().add("employee-content");

        manageCategoriesText = new Text("Manage Categories");
        manageCategoriesText.getStyleClass().add("page-title");

        categoriesTable = new TableView<>();
        categoriesTable.getStyleClass().add("employees-table");

        TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("categoryId"));

        TableColumn<Category, String> categoryNameCol = new TableColumn<>("Category Name");
        categoryNameCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<Category, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        categoriesTable.getColumns().addAll(
                idCol,
                categoryNameCol,
                descriptionCol
        );

        categoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        categoriesTable.setPrefHeight(420);
        categoriesTable.setItems(observableCategories);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search category by name or description...");
        searchTextField.getStyleClass().add("employee-search-field");

        searchTextField.setMaxWidth(Double.MAX_VALUE);
        searchTextField.prefWidthProperty().bind(categoriesTable.widthProperty());

        centerVBox.getChildren().addAll(
                manageCategoriesText,
                categoriesTable,
                searchTextField
        );
    }

    private void createLeftButtons() {
        leftVBox = new VBox(18);
        leftVBox.setAlignment(Pos.CENTER);
        leftVBox.setPadding(new Insets(25));
        leftVBox.getStyleClass().add("employee-side-actions");

        addBtn = new Button("Add Category");
        updateBtn = new Button("Update Category");

        addBtn.getStyleClass().add("modern-action-button");
        updateBtn.getStyleClass().add("modern-action-button");

        leftVBox.getChildren().addAll(addBtn, updateBtn);

        addBtn.setOnAction(e -> {
            addCategoryScene = new AddCategoryScene(this);
            addCategoryScene.showStage();
        });

        updateBtn.setOnAction(e -> updateAction());
    }

    public void loadCategories() {
        categories.clear();
        observableCategories.clear();

        categories.addAll(CategoryDAO.getAllCategories());
        observableCategories.addAll(categories);
    }

    private void updateAction() {
        Category selectedCategory = categoriesTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select a category to update");
            return;
        }

        updateCategoryScene = new UpdateCategoryScene(this, selectedCategory);
        updateCategoryScene.showStage();
    }

    private void filterTable(String text) {
        if (text == null || text.trim().isEmpty()) {
            observableCategories.setAll(categories);
            return;
        }

        String query = text.trim().toLowerCase();
        ArrayList<Category> filtered = new ArrayList<>();

        for (Category category : categories) {
            String categoryName = category.getCategoryName() == null ? "" : category.getCategoryName().toLowerCase();
            String description = category.getDescription() == null ? "" : category.getDescription().toLowerCase();

            if (categoryName.contains(query) || description.contains(query)) {
                filtered.add(category);
            }
        }

        observableCategories.setAll(filtered);
    }

    public BorderPane getRoot() {
        return root;
    }
}
