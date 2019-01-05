package sample;

import JDBC.Client;
import JDBC.Pool;
import JDBC.Reservation;
import JDBC.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;


/*
TabPane ma wyświetlać tabele dla danych basenów:
po kliknięciu w dany basen z listy po lewej,
aplikacja usuwa zawartość tabel i wczytuje nowe elo
 */
public class MarketingController implements Initializable {

    @FXML
    private ListView<String> poolList;

    @FXML
    private TableView<MarketingClient> clientsTable;
    @FXML
    private TableColumn<MarketingClient, String> clientID;
    @FXML
    private TableColumn<MarketingClient, String> clientFName;
    @FXML
    private TableColumn<MarketingClient, String> clientLName;
    @FXML
    private TableColumn<MarketingClient, String> clientPosition;

    @FXML
    private TableView<MarketingTopClient> topClientsTable;
    @FXML
    private TableColumn<MarketingTopClient, String> topClientNumber;
    @FXML
    private TableColumn<MarketingTopClient, String> topClientFName;
    @FXML
    private TableColumn<MarketingTopClient, String> topClientLName;
    @FXML
    private TableColumn<MarketingTopClient, String> topClientTransactionsNumber;

    @FXML
    private TableView<MarketingReservation> reservationsTable;
    @FXML
    private TableColumn<MarketingReservation, String> reservationDate;
    @FXML
    private TableColumn<MarketingReservation, String> reservationType;
    @FXML
    private TableColumn<MarketingReservation, String> reservationFName;
    @FXML
    private TableColumn<MarketingReservation, String> reservationLName;

    @FXML
    private TableView<MarketingTransaction> transactionsTable;
    @FXML
    private TableColumn<MarketingTransaction, String> transactionDate;
    @FXML
    private TableColumn<MarketingTransaction, String> transactionValue;
    @FXML
    private TableColumn<MarketingTransaction, String> transactionFName;
    @FXML
    private TableColumn<MarketingTransaction, String> transactionLName;

    @FXML
    private Text usedReservationsText;

    public MarketingController() throws SQLException {
    }

    private void clearTables(){
        reservationsTable.getItems().clear();
        transactionsTable.getItems().clear();
    }

    private ObservableList<String> getPoolNames(Connection conn) throws SQLException {
        int noPools;
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Baseny");
        ResultSet rSet = stmt.executeQuery();

        if(rSet.next()) noPools = rSet.getInt(1);
        else noPools = 0;

        rSet.close();
        stmt.close();

        ObservableList<String> list = FXCollections.observableArrayList();
        for(int i = 0; i < noPools; i++){
            String temp = Pool.getPool(conn, i+1);
            if(temp != null) list.add(temp);
        }
        return list;
    }

    private ObservableList<MarketingClient> getClients(Connection conn) throws SQLException {
        int noClients;
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Klienci ");
        ResultSet rSet = stmt.executeQuery();

        if(rSet.next()) noClients = rSet.getInt(1);
        else noClients = 0;

        rSet.close();
        stmt.close();

        ObservableList<MarketingClient> list = FXCollections.observableArrayList();
        for(int i = 0; i < noClients; i++){
            MarketingClient temp = Client.getMarketingClient(conn, i+1);
            if(temp != null) list.add(temp);
        }
        return list;
    }

    private ObservableList<MarketingTopClient> getTopClients(Connection conn) throws SQLException {
        int noClients;
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Klienci ");
        ResultSet rSet = stmt.executeQuery();

        if(rSet.next()) noClients = rSet.getInt(1);
        else noClients = 0;

        rSet.close();
        stmt.close();

        ObservableList<MarketingTopClient> list = FXCollections.observableArrayList();
        for(int i = 0; i < noClients; i++){
            MarketingTopClient temp = Client.getMarketingTopClient(conn, i+1);
            if(temp != null) list.add(temp);
        }
        return list;
    }

    private ObservableList<MarketingReservation> getReservations(Connection conn, String poolItem) throws SQLException {
        int noReservations;
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Rezerwacje_Toru");
        ResultSet rSet = stmt.executeQuery();

        if(rSet.next()) noReservations = rSet.getInt(1);
        else noReservations = 0;

        rSet.close();
        stmt.close();

        ObservableList<MarketingReservation> list = FXCollections.observableArrayList();
        for(int i = 0; i < noReservations; i++){
            MarketingReservation temp = Reservation.getMarketingReservation(conn, i+1, poolItem);
            if(temp != null) list.add(temp);
        }
        return list;
    }

    private ObservableList<MarketingTransaction> getTransactions(Connection conn, String poolItem) throws SQLException {
        int noTransactions;
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Transakcje");
        ResultSet rSet = stmt.executeQuery();

        if(rSet.next()) noTransactions = rSet.getInt(1);
        else noTransactions = 0;

        rSet.close();
        stmt.close();

        ObservableList<MarketingTransaction> list = FXCollections.observableArrayList();
        for(int i = 0; i < noTransactions; i++){
            MarketingTransaction temp = Transaction.getMarketingTransaction(conn, i+1, poolItem);
            if(temp != null) list.add(temp);
        }
        return list;
    }

    private ObservableList<MarketingClient> clients = getClients(Main.jdbc.getConn());

    private ObservableList<MarketingTopClient> topClients = getTopClients(Main.jdbc.getConn());

    private ObservableList<MarketingReservation> reservations = FXCollections.observableArrayList();

    private ObservableList<MarketingTransaction> transactions = FXCollections.observableArrayList();

    private void initializeClients(){
        clientID.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientFName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientLName.setCellValueFactory(new PropertyValueFactory<>("surname"));
        clientPosition.setCellValueFactory(new PropertyValueFactory<>("position"));

        clientsTable.getItems().addAll(clients);
    }

    private void initializeTopClients(){
        topClientNumber.setCellValueFactory(new PropertyValueFactory<>("id"));
        topClientFName.setCellValueFactory(new PropertyValueFactory<>("name"));
        topClientLName.setCellValueFactory(new PropertyValueFactory<>("surname"));
        topClientTransactionsNumber.setCellValueFactory(new PropertyValueFactory<>("noTransactions"));

        topClientsTable.getItems().addAll(topClients);
    }

    private void initializeReservations(){
        reservationDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        reservationType.setCellValueFactory(new PropertyValueFactory<>("type"));
        reservationFName.setCellValueFactory(new PropertyValueFactory<>("name"));
        reservationLName.setCellValueFactory(new PropertyValueFactory<>("surname"));

        reservationsTable.getItems().addAll(reservations);
    }

    private void initializeTransactions(){
        transactionDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        transactionValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        transactionFName.setCellValueFactory(new PropertyValueFactory<>("name"));
        transactionLName.setCellValueFactory(new PropertyValueFactory<>("surname"));

        transactionsTable.getItems().addAll(transactions);
    }

    private void initializePoolList() throws SQLException {
        ObservableList<String> items = getPoolNames(Main.jdbc.getConn());
        poolList.setItems(items);
    }

    private void initializeUsedReservations(){
        usedReservationsText.setText("0.005%");
    }

    private void changeTables(String poolItem) throws SQLException {
        clearTables();
        reservations = getReservations(Main.jdbc.getConn(), poolItem);
        transactions = getTransactions(Main.jdbc.getConn(), poolItem);
        initializeReservations();
        initializeTransactions();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initializePoolList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initializeClients();
        initializeTopClients();
        initializeReservations();
        initializeTransactions();
        initializeUsedReservations();
    }


    @FXML
    public void changeReservationsPeriod(){

    }
    @FXML
    public void changeTransactionsPeriod(){

    }
    @FXML
    public void poolItemClicked() throws SQLException {

        String poolItem = poolList.getSelectionModel().getSelectedItem();

        if(poolItem == null) return;

        String[] poolName = poolItem.split(",");
        System.out.println("clicked on " + poolName[0]);   //debug

        changeTables(poolName[0]);

    }

    public void logOutButtonPushed(ActionEvent event) throws IOException {

        Parent tableViewParent = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene tableViewScene = new Scene(tableViewParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(tableViewScene);
        window.show();
    }
}