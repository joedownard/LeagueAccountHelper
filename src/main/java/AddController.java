import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AddController {

    @FXML public TextField accNameField;
    @FXML public TextField passField;
    @FXML public TextField ingameNameField;
    @FXML public TextField levelField;
    @FXML public TextField rankField;
    @FXML public TextField champPoolField;
    @FXML public Button addAccountButton;

    public void addAccountClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();

        Account newAccount = new Account(
                accNameField.getText(),
                passField.getText(),
                ingameNameField.getText(),
                levelField.getText(),
                rankField.getText(),
                champPoolField.getText()
        );

        MainController mainController =  loader.getController();
        mainController.addToTable(newAccount);

        Stage stage = (Stage) addAccountButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 630, 400));
    }

    public void editAccount(Account account) throws IOException {
        accNameField.setText(account.accName);
        passField.setText(account.password);
        ingameNameField.setText(account.ingameName);
        levelField.setText(account.level);
        rankField.setText(account.rank);
        champPoolField.setText(account.champPool);
    }


}
