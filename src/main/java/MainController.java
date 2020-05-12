import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML public TableView<Account> accountTable;

    @FXML public Button copyPasswordButton;
    @FXML public Button copyNameButton;

    @FXML public Button addButton;
    @FXML public Button deleteButton;
    @FXML public Button updateButton;
    @FXML public TextField apiKeyBox;
    @FXML public Button editButton;

    @FXML private TableColumn<Account, String> accName;
    @FXML private TableColumn<Account, String> password;
    @FXML private TableColumn<Account, String> ingameName;
    @FXML private TableColumn<Account, String> level;
    @FXML private TableColumn<Account, String> rank;
    @FXML private TableColumn<Account, String> champPool;

    List<Account> accountList = new ArrayList<>();
    String apikey;

    public void copyButtonClicked(MouseEvent mouseEvent) {
        Account account = findAccount(accountTable.getSelectionModel().getSelectedItem().accName);
        String response = mouseEvent.getSource().equals(copyNameButton) ? account.getAccName() : account.getPassword();
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        new StringSelection(response),
                        null
                );
    }

    public void addButtonClicked(MouseEvent mouseEvent) throws IOException {
        Parent addGUI = FXMLLoader.load(getClass().getResource("addAccount.fxml"));
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(addGUI, 555, 400));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            readFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        accName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        password.setCellValueFactory(new PropertyValueFactory<>("censoredPassword"));
        ingameName.setCellValueFactory(new PropertyValueFactory<>("ingameName"));
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        champPool.setCellValueFactory(new PropertyValueFactory<>("champPool"));

        if (!accountList.isEmpty()) {
            accountTable.getItems().setAll(accountList);
        }
    }

    public void deleteButtonClicked(MouseEvent mouseEvent) throws IOException {
        Account toRemove = findAccount(accountTable.getSelectionModel().getSelectedItem().accName);
        accountList.remove(toRemove);
        updateTable();
        writeToFile();
    }

    public void updateButtonClicked(MouseEvent mouseEvent) throws IOException {
        Orianna.setRiotAPIKey(apiKeyBox.getText());
        Orianna.setDefaultRegion(Region.EUROPE_WEST);

        for (Account acc : accountList) {
            String summonerName = acc.ingameName;
            Summoner summoner = Summoner.named(summonerName).withRegion(Region.EUROPE_WEST).get();

            LeagueEntry le = summoner.getLeaguePosition(Queue.RANKED_SOLO);
            String rank = "Unranked";

            if (le != null) {
                String division = le.getDivision().toString();
                String tier = le.getTier().toString();
                String tierCaseFixed = tier.substring(0, 1) + tier.substring(1).toLowerCase();
                int lp = le.getLeaguePoints();

                rank = tierCaseFixed + " " + division + " " + lp + "LP ";
            }

            String level = String.valueOf(summoner.getLevel());
            acc.rank = rank;
            acc.level = level;
        }
        updateTable();
        writeToFile();
    }

    public void editButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addAccount.fxml"));
        Parent addGUI = loader.load();

        Account toRemove = findAccount(accountTable.getSelectionModel().getSelectedItem().accName);
        accountList.remove(toRemove);

        AddController addController =  loader.getController();
        addController.editAccount(toRemove);

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(addGUI, 370, 400));

        updateTable();
        writeToFile();
    }


    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.UP)) {
            int fixedIndex = accountTable.getSelectionModel().getFocusedIndex() + 1;
            Account temp = accountList.get(fixedIndex);
            accountList.set(fixedIndex, accountList.get(fixedIndex - 1));
            accountList.set(fixedIndex - 1, temp);
            accountTable.getSelectionModel().select(fixedIndex);

        } else if (keyEvent.getCode().equals(KeyCode.DOWN)) {
            int fixedIndex = accountTable.getSelectionModel().getFocusedIndex() - 1;
            Account temp = accountList.get(fixedIndex);
            accountList.set(fixedIndex, accountList.get(fixedIndex + 1));
            accountList.set(fixedIndex + 1, temp);
            accountTable.getSelectionModel().select(fixedIndex);
        }

        updateTable();

        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile () throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        apikey = apiKeyBox.getText();

        File directory = new File(System.getProperty("user.home") + "\\LAH");
        if (!directory.exists()) directory.mkdir();

        objectMapper.writeValue(new File(System.getProperty("user.home") + "\\LAH\\apikey.json"), apikey);
        objectMapper.writeValue(new File(System.getProperty("user.home") + "\\LAH\\accountList.json"), accountList);
    }

    private void readFromFile () throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        apikey =  objectMapper.readValue(new File(System.getProperty("user.home") + "\\LAH\\apikey.json"), new TypeReference<String>(){});
        accountList = objectMapper.readValue(new File(System.getProperty("user.home") + "\\LAH\\accountList.json"), new TypeReference<List<Account>>(){});
        apiKeyBox.setText(apikey);
    }

    public void addToTable (Account account) throws IOException {
        accountList.add(account);
        updateTable();
        writeToFile();
    }

    public void updateTable () {
        if (!accountList.isEmpty()) {
            accountTable.getItems().setAll(accountList);
        } else {
            accountTable.getItems().clear();
        }
        accountTable.refresh();
    }

    public Account findAccount (String accountName) {
        for (Account acc : accountList) {
            if (acc.getAccName().equals(accountName)) return acc;
        }
        return null;
    }
}
