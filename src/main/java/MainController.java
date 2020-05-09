import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML public ProgressBar updateProgressBar;

    @FXML private TableColumn<Account, String> accName;
    @FXML private TableColumn<Account, String> password;
    @FXML private TableColumn<Account, String> ingameName;
    @FXML private TableColumn<Account, String> level;
    @FXML private TableColumn<Account, String> rank;
    @FXML private TableColumn<Account, String> champPool;

    List<Account> accountList = new ArrayList<>();

    public void copyButtonClicked(MouseEvent mouseEvent) {
        String response = mouseEvent.getSource().equals(copyNameButton) ? accountTable.getSelectionModel().getSelectedItem().accName : accountTable.getSelectionModel().getSelectedItem().password;
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
        updateProgressBar.setVisible(false);
        try {
            readFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        accName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        ingameName.setCellValueFactory(new PropertyValueFactory<>("ingameName"));
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        champPool.setCellValueFactory(new PropertyValueFactory<>("champPool"));

        if (!accountList.isEmpty()) {
            accountTable.getItems().setAll(accountList);
        }
    }

    private void writeToFile () throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
        objectMapper.writeValue(new File(System.getProperty("user.home") + "\\accountList.json"), accountList);
    }

    private void readFromFile () throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        accountList = objectMapper.readValue(new File(System.getProperty("user.home") + "\\accountList.json"), new TypeReference<List<Account>>(){});
    }

    public void addToTable (Account account) throws IOException {
        accountList.add(account);
        updateTable();
        writeToFile();
    }

    public void deleteButtonClicked(MouseEvent mouseEvent) throws IOException {
        Account toRemove = new Account (
                accountTable.getSelectionModel().getSelectedItem().accName,
                accountTable.getSelectionModel().getSelectedItem().password,
                accountTable.getSelectionModel().getSelectedItem().ingameName,
                accountTable.getSelectionModel().getSelectedItem().level,
                accountTable.getSelectionModel().getSelectedItem().rank,
                accountTable.getSelectionModel().getSelectedItem().champPool
        );
        accountList.removeIf(acc -> acc.equals(toRemove));
        updateTable();
        writeToFile();
    }

    public void updateTable () {
        if (!accountList.isEmpty()) {
            accountTable.getItems().setAll(accountList);
        }
    }

    public void updateButtonClicked(MouseEvent mouseEvent) throws IOException {
        updateProgressBar.setVisible(true);
        Orianna.setRiotAPIKey("RGAPI-8e470e76-8828-44b7-b25a-54f6edbeb83d");
        Orianna.setDefaultRegion(Region.EUROPE_WEST);

        int listSize = accountList.size();
        int i = 0;
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
            updateProgressBar.setProgress((double)++i/(double)listSize);
        }
        updateProgressBar.setVisible(false);
        updateTable();
        writeToFile();
    }
}
