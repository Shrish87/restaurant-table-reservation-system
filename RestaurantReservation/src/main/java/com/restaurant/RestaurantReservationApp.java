package com.restaurant;

import com.restaurant.dao.ReservationDAO;
import com.restaurant.model.Customer;
import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RestaurantReservationApp extends Application {

    private ReservationDAO dao = new ReservationDAO();
    private TableView<Reservation> reservationTable;
    private ObservableList<Reservation> reservationData;

    private TextField nameField, emailField, phoneField;
    private ComboBox<RestaurantTable> tableComboBox;
    private DatePicker datePicker;
    private ComboBox<String> timeSlotComboBox;
    private Spinner<Integer> guestSpinner;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Restaurant Table Reservation System");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(15));

        VBox bookingForm = createBookingForm();
        VBox tableView = createReservationTableView();

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(bookingForm, tableView);
        splitPane.setDividerPositions(0.4);

        mainLayout.setCenter(splitPane);
        mainLayout.setTop(createMenuBar());

        Scene scene = new Scene(mainLayout, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshReservationTable();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(e -> refreshReservationTable());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), exitItem);

        Menu viewMenu = new Menu("View");
        MenuItem todayItem = new MenuItem("Today's Reservations");
        todayItem.setOnAction(e -> showTodayReservations());
        MenuItem allItem = new MenuItem("All Reservations");
        allItem.setOnAction(e -> refreshReservationTable());
        viewMenu.getItems().addAll(todayItem, allItem);

        menuBar.getMenus().addAll(fileMenu, viewMenu);
        return menuBar;
    }

    private VBox createBookingForm() {
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 10;");

        Label titleLabel = new Label("New Reservation");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label customerLabel = new Label("Customer Information");
        customerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        nameField = new TextField();
        nameField.setPromptText("Full Name");

        emailField = new TextField();
        emailField.setPromptText("Email Address");

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        Label reservationLabel = new Label("Reservation Details");
        reservationLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        tableComboBox = new ComboBox<>();
        tableComboBox.setPromptText("Select Table");
        tableComboBox.setPrefWidth(300);
        loadTables();

        datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        datePicker.setPromptText("Select Date");
        datePicker.setPrefWidth(300);

        timeSlotComboBox = new ComboBox<>();
        timeSlotComboBox.getItems().addAll(
                "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
                "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
                "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM",
                "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM"
        );
        timeSlotComboBox.setPromptText("Select Time Slot");
        timeSlotComboBox.setPrefWidth(300);

        guestSpinner = new Spinner<>(1, 20, 2);
        guestSpinner.setEditable(true);
        guestSpinner.setPrefWidth(300);

        Label guestLabel = new Label("Number of Guests:");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button bookButton = new Button("Book Reservation");
        bookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        bookButton.setOnAction(e -> bookReservation());

        Button clearButton = new Button("Clear Form");
        clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        clearButton.setOnAction(e -> clearForm());

        Button checkButton = new Button("Check Availability");
        checkButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        checkButton.setOnAction(e -> checkAvailability());

        buttonBox.getChildren().addAll(bookButton, checkButton, clearButton);

        formBox.getChildren().addAll(
                titleLabel,
                new Separator(),
                customerLabel,
                new Label("Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Phone:"), phoneField,
                reservationLabel,
                new Label("Table:"), tableComboBox,
                new Label("Date:"), datePicker,
                new Label("Time Slot:"), timeSlotComboBox,
                guestLabel, guestSpinner,
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox container = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return container;
    }

    private VBox createReservationTableView() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Current Reservations");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        reservationTable = new TableView<>();
        reservationData = FXCollections.observableArrayList();
        reservationTable.setItems(reservationData);

        TableColumn<Reservation, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        idCol.setPrefWidth(50);

        TableColumn<Reservation, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCustomer().getName()
                )
        );
        customerCol.setPrefWidth(120);

        TableColumn<Reservation, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCustomer().getEmail()
                )
        );
        emailCol.setPrefWidth(150);

        TableColumn<Reservation, String> tableCol = new TableColumn<>("Table");
        tableCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        "Table " + cellData.getValue().getTable().getTableNumber()
                )
        );
        tableCol.setPrefWidth(70);

        TableColumn<Reservation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getReservationDate().format(
                                DateTimeFormatter.ofPattern("MMM dd, yyyy")
                        )
                )
        );
        dateCol.setPrefWidth(100);

        TableColumn<Reservation, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        timeCol.setPrefWidth(90);

        TableColumn<Reservation, Integer> guestsCol = new TableColumn<>("Guests");
        guestsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        guestsCol.setPrefWidth(70);

        TableColumn<Reservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<Reservation, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(80);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");

            {
                cancelBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
                cancelBtn.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    if (reservation.getStatus().equals("CONFIRMED")) {
                        cancelReservation(reservation);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    if ("CONFIRMED".equals(reservation.getStatus())) {
                        setGraphic(cancelBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        reservationTable.getColumns().addAll(
                idCol, customerCol, emailCol, tableCol,
                dateCol, timeCol, guestsCol, statusCol, actionCol
        );

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshReservationTable());
        buttonBox.getChildren().add(refreshBtn);

        tableBox.getChildren().addAll(titleLabel, new Separator(), reservationTable, buttonBox);
        VBox.setVgrow(reservationTable, Priority.ALWAYS);

        return tableBox;
    }

    private void loadTables() {
        try {
            List<RestaurantTable> tables = dao.getAvailableTables();
            tableComboBox.setItems(FXCollections.observableArrayList(tables));
        } catch (Exception e) {
            showAlert("Error", "Failed to load tables: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void checkAvailability() {
        RestaurantTable selectedTable = tableComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeSlotComboBox.getValue();

        if (selectedTable == null || selectedDate == null || selectedTime == null) {
            showAlert("Validation Error", "Please select table, date, and time slot to check availability.", Alert.AlertType.WARNING);
            return;
        }

        try {
            boolean available = dao.isTableAvailable(selectedTable.getTableId(), selectedDate, selectedTime);
            if (available) {
                showAlert("Available",
                        "Table " + selectedTable.getTableNumber() + " is available for " +
                                selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                                " at " + selectedTime, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Not Available",
                        "Table " + selectedTable.getTableNumber() + " is already booked for " +
                                selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                                " at " + selectedTime, Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to check availability: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void bookReservation() {
        if (!validateInputs()) {
            return;
        }

        try {
            Customer customer = new Customer(
                    nameField.getText().trim(),
                    emailField.getText().trim().toLowerCase(),
                    phoneField.getText().trim()
            );

            customer = dao.saveCustomer(customer);

            Reservation reservation = new Reservation(
                    customer,
                    tableComboBox.getValue(),
                    datePicker.getValue(),
                    timeSlotComboBox.getValue(),
                    guestSpinner.getValue()
            );

            dao.createReservation(reservation);

            showAlert("Success",
                    "Reservation confirmed for " + customer.getName() +
                            "\nTable: " + tableComboBox.getValue().getTableNumber() +
                            "\nDate: " + datePicker.getValue().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                            "\nTime: " + timeSlotComboBox.getValue(),
                    Alert.AlertType.INFORMATION);

            clearForm();
            refreshReservationTable();

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter customer name", Alert.AlertType.WARNING);
            return false;
        }

        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showAlert("Validation Error", "Please enter a valid email address", Alert.AlertType.WARNING);
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter phone number", Alert.AlertType.WARNING);
            return false;
        }

        if (tableComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a table", Alert.AlertType.WARNING);
            return false;
        }

        if (datePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a date", Alert.AlertType.WARNING);
            return false;
        }

        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showAlert("Validation Error", "Cannot book reservations for past dates", Alert.AlertType.WARNING);
            return false;
        }

        if (timeSlotComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a time slot", Alert.AlertType.WARNING);
            return false;
        }

        if (guestSpinner.getValue() > tableComboBox.getValue().getCapacity()) {
            showAlert("Validation Error",
                    "Number of guests (" + guestSpinner.getValue() +
                            ") exceeds table capacity (" + tableComboBox.getValue().getCapacity() + ")",
                    Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        tableComboBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        timeSlotComboBox.setValue(null);
        guestSpinner.getValueFactory().setValue(2);
    }

    private void refreshReservationTable() {
        try {
            List<Reservation> reservations = dao.getAllReservations();
            reservationData.clear();
            reservationData.addAll(reservations);
        } catch (Exception e) {
            showAlert("Error", "Failed to load reservations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showTodayReservations() {
        try {
            List<Reservation> reservations = dao.getReservationsByDate(LocalDate.now());
            reservationData.clear();
            reservationData.addAll(reservations);
        } catch (Exception e) {
            showAlert("Error", "Failed to load today's reservations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cancelReservation(Reservation reservation) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Reservation");
        confirmAlert.setContentText("Are you sure you want to cancel this reservation?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.cancelReservation(reservation.getReservationId());
                    showAlert("Success", "Reservation cancelled successfully", Alert.AlertType.INFORMATION);
                    refreshReservationTable();
                } catch (Exception e) {
                    showAlert("Error", "Failed to cancel reservation: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        com.restaurant.util.HibernateUtil.shutdown();
    }
}