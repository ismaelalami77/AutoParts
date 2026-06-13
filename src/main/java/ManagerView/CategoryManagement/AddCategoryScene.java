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

public class AddCategoryScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text addCategoryText;

    private TextField categoryNameField;
    private TextField descriptionField;

    private Button addCategoryButton;
    private Button cancelButton;

    private final ViewCategories viewCategories;

    public AddCategoryScene(ViewCategories viewCategories) {
        this.viewCategories = viewCategories;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        addCategoryText = UIHelperC.createTitleText("Add Category");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        categoryNameField = UIHelperC.createStyledTextField("Category Name");
        descriptionField = UIHelperC.createStyledTextField("Description");

        grid.add(UIHelperC.createInfoText("Category Name:"), 0, 0);
        grid.add(categoryNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        addCategoryButton = UIHelperC.createStyledButton("Add");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(addCategoryButton, cancelButton);

        centerVbox.getChildren().addAll(addCategoryText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 400);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Add Category");

        cancelButton.setOnAction(e -> stage.close());
        addCategoryButton.setOnAction(e -> addCategory());

        categoryNameField.setOnAction(e -> descriptionField.requestFocus());
        descriptionField.setOnAction(e -> addCategory());
    }

    private void addCategory() {
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

        Category category = new Category(
                0,
                categoryName,
                description
        );

        boolean inserted = CategoryDAO.insertCategory(category);

        if (inserted) {
            viewCategories.loadCategories();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Category added successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Category could not be added! Category name may already exist.");
        }
    }

    public void showStage() {
        stage.show();
    }
}