package ManagerView.CategoryManagement;

import Connection.CategoryDAO;
import com.example.autoparts.UIHelperC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UpdateCategoryScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text updateCategoryText;

    private TextField categoryNameField;
    private TextField descriptionField;

    private Button updateCategoryButton;
    private Button cancelButton;

    private final ViewCategories viewCategories;
    private final Category category;

    public UpdateCategoryScene(ViewCategories viewCategories, Category category) {
        this.viewCategories = viewCategories;
        this.category = category;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        updateCategoryText = UIHelperC.createTitleText("Update Category");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        categoryNameField = UIHelperC.createStyledTextField("Category Name");
        descriptionField = UIHelperC.createStyledTextField("Description");

        fillFields();

        grid.add(UIHelperC.createInfoText("Category Name:"), 0, 0);
        grid.add(categoryNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        updateCategoryButton = UIHelperC.createStyledButton("Update");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(updateCategoryButton, cancelButton);

        centerVbox.getChildren().addAll(updateCategoryText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 400);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Update Category");

        cancelButton.setOnAction(e -> stage.close());
        updateCategoryButton.setOnAction(e -> updateCategory());

        categoryNameField.setOnAction(e -> descriptionField.requestFocus());
        descriptionField.setOnAction(e -> updateCategory());
    }

    private void fillFields() {
        categoryNameField.setText(category.getCategoryName());
        descriptionField.setText(category.getDescription());
    }

    private void updateCategory() {
        String categoryName = categoryNameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (categoryName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter category name!");
            return;
        }

        if (description.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter description!");
            return;
        }

        Category updatedCategory = new Category(
                category.getCategoryId(),
                categoryName,
                description
        );

        boolean updated = CategoryDAO.updateCategory(updatedCategory);

        if (updated) {
            viewCategories.loadCategories();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Category updated successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Category could not be updated!");
        }
    }

    public void showStage() {
        stage.show();
    }
}