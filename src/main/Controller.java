package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML public TableView<Account> accountTable;
    @FXML public Button copyPasswordButton;
    @FXML public Button copyNameButton;
    @FXML private TableColumn<Account, String> accName;
    @FXML private TableColumn<Account, String> password;
    @FXML private TableColumn<Account, String> ingameName;
    @FXML private TableColumn<Account, String> level;
    @FXML private TableColumn<Account, String> rank;
    @FXML private TableColumn<Account, String> champPool;

    public void copyButtonClicked(MouseEvent mouseEvent) {
        String response = mouseEvent.getSource().equals(copyNameButton) ? accountTable.getSelectionModel().getSelectedItem().accName : accountTable.getSelectionModel().getSelectedItem().password;
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        new StringSelection(response),
                        null
                );
    }

    public void addButtonClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        ingameName.setCellValueFactory(new PropertyValueFactory<>("ingameName"));
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        champPool.setCellValueFactory(new PropertyValueFactory<>("champPool"));

        List<Account> accountList = makeAccountList();
        if (!accountList.isEmpty()) {
            accountTable.getItems().setAll(accountList);
        }
    }

    private List<Account> makeAccountList () {
        List<Account> accountList = new ArrayList<>();
        Account acc1 = new Account("Lemonshmunter", "CENSORED!","LemonHunter","90","gold 2","all!");
        accountList.add(acc1);
        return accountList;
    }
}
