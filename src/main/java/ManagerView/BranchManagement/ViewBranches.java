package ManagerView.BranchManagement;

import Connection.BranchDAO;
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

public class ViewBranches {

    public static ArrayList<Branch> branches = new ArrayList<>();
    public static ObservableList<Branch> observableBranches = FXCollections.observableArrayList();

    private BorderPane root;
    private TableView<Branch> branchesTable;

    private VBox leftVBox;
    private VBox centerVBox;

    private Text manageBranchesText;
    private TextField searchTextField;

    private Button addBtn;
    private Button updateBtn;

    private AddBranchScene addBranchScene;
    private UpdateBranchScene updateBranchScene;

    public ViewBranches() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        createCenterContent();
        createLeftButtons();

        root.setLeft(leftVBox);
        root.setCenter(centerVBox);

        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        loadBranches();
    }

    private void createCenterContent() {
        centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(25));
        centerVBox.getStyleClass().add("employee-content");

        manageBranchesText = new Text("Manage Branches");
        manageBranchesText.getStyleClass().add("page-title");

        branchesTable = new TableView<>();
        branchesTable.getStyleClass().add("employees-table");

        TableColumn<Branch, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        TableColumn<Branch, String> branchNameCol = new TableColumn<>("Branch Name");
        branchNameCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<Branch, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Branch, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Branch, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        branchesTable.getColumns().addAll(
                idCol,
                branchNameCol,
                cityCol,
                addressCol,
                phoneCol
        );

        branchesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        branchesTable.setPrefHeight(420);
        branchesTable.setItems(observableBranches);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search branch by name, city, address, or phone...");
        searchTextField.getStyleClass().add("employee-search-field");

        searchTextField.setMaxWidth(Double.MAX_VALUE);
        searchTextField.prefWidthProperty().bind(branchesTable.widthProperty());

        centerVBox.getChildren().addAll(
                manageBranchesText,
                branchesTable,
                searchTextField
        );
    }

    private void createLeftButtons() {
        leftVBox = new VBox(18);
        leftVBox.setAlignment(Pos.CENTER);
        leftVBox.setPadding(new Insets(25));
        leftVBox.getStyleClass().add("employee-side-actions");

        addBtn = new Button("Add Branch");
        updateBtn = new Button("Update Branch");

        addBtn.getStyleClass().add("modern-action-button");
        updateBtn.getStyleClass().add("modern-action-button");

        leftVBox.getChildren().addAll(addBtn, updateBtn);

        addBtn.setOnAction(e -> {
            addBranchScene = new AddBranchScene(this);
            addBranchScene.showStage();
        });

        updateBtn.setOnAction(e -> updateAction());
    }

    public void loadBranches() {
        branches.clear();
        observableBranches.clear();

        branches.addAll(BranchDAO.getAllBranches());
        observableBranches.addAll(branches);
    }

    private void updateAction() {
        Branch selectedBranch = branchesTable.getSelectionModel().getSelectedItem();

        if (selectedBranch == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select a branch to update");
            return;
        }

        updateBranchScene = new UpdateBranchScene(this, selectedBranch);
        updateBranchScene.showStage();
    }

    private void filterTable(String text) {
        if (text == null || text.trim().isEmpty()) {
            observableBranches.setAll(branches);
            return;
        }

        String query = text.trim().toLowerCase();
        ArrayList<Branch> filtered = new ArrayList<>();

        for (Branch branch : branches) {
            String branchName = branch.getBranchName() == null ? "" : branch.getBranchName().toLowerCase();
            String city = branch.getCity() == null ? "" : branch.getCity().toLowerCase();
            String address = branch.getAddress() == null ? "" : branch.getAddress().toLowerCase();
            String phone = branch.getPhone() == null ? "" : branch.getPhone().toLowerCase();

            if (branchName.contains(query)
                    || city.contains(query)
                    || address.contains(query)
                    || phone.contains(query)) {
                filtered.add(branch);
            }
        }

        observableBranches.setAll(filtered);
    }

    public BorderPane getRoot() {
        return root;
    }
}
