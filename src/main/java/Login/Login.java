package Login;

import Connection.UserDAO;
import EmployeeView.EmployeeView;
import ManagerView.ManagerView;
import com.example.autoparts.UIHelperC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Login {
    private BorderPane root;
    private Stage stage;
    private Scene scene;

    private VBox container;

    private Text loginText;
    private TextField userField;
    private PasswordField passField;
    private Button loginButton;

    public Login() {
        root = new BorderPane();
        root.getStyleClass().add("login-root");

        container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(16);
        container.setPadding(new Insets(34, 40, 34, 40));
        container.getStyleClass().add("login-card");

        Label brandLabel = new Label("AutoParts");
        brandLabel.getStyleClass().add("login-brand");

        loginText = UIHelperC.createTitleText("Login");


        userField = UIHelperC.createStyledTextField("Username");
        passField = UIHelperC.createStyledPassField("Password");

        loginButton = UIHelperC.createStyledButton("Login");

        container.getChildren().addAll(brandLabel, loginText, userField, passField, loginButton);

        root.setCenter(container);

        stage = new Stage();
        scene = new Scene(root, 430, 500);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.setResizable(false);

        loginButton.setOnAction(e -> LoginHandle());
        userField.setOnAction(e -> passField.requestFocus());
        passField.setOnAction(e -> LoginHandle());
    }

    private void LoginHandle() {
        UserDAO dao = new UserDAO();
        User u = dao.authenticate(userField.getText().trim(), passField.getText());

        if (u == null) {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Wrong username or password");
            return;
        }

        if ("MANAGER".equalsIgnoreCase(u.getRole())) {
            new ManagerView(u).showStage();
            stage.close();
        } else if ("EMPLOYEE".equalsIgnoreCase(u.getRole())) {
            new EmployeeView(u).showStage();
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Unknown user role");
        }
    }
    public void showStage() {
        stage.show();
    }
}
