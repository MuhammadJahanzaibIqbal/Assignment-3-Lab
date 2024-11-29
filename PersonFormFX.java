import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class PersonFormFX extends Application {

    private TextField txtFullName, txtID;
    private ComboBox<String> comboGender, comboProvince;
    private DatePicker datePickerDOB;
    private ArrayList<Person> personList = new ArrayList<>();
    private int currentIndex = -1;
    private final String FILE_NAME = "person_data.ser";

    @Override
    public void start(Stage primaryStage) {
        GridPane formGrid = new GridPane();
        formGrid.setPadding(new Insets(10));
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        Label lblFullName = new Label("Full Name:");
        txtFullName = new TextField();
        formGrid.add(lblFullName, 0, 0);
        formGrid.add(txtFullName, 1, 0);
        Label lblID = new Label("ID:");
        txtID = new TextField();
        formGrid.add(lblID, 0, 1);
        formGrid.add(txtID, 1, 1);
        Label lblGender = new Label("Gender:");
        comboGender = new ComboBox<>();
        comboGender.getItems().addAll("Male", "Female", "Other");
        comboGender.setPromptText("Select Gender");
        formGrid.add(lblGender, 0, 2);
        formGrid.add(comboGender, 1, 2);
        Label lblHomeProvince = new Label("Home Province:");
        comboProvince = new ComboBox<>();
        comboProvince.getItems().addAll("Punjab", "Sindh", "KPK", "Balochistan");
        comboProvince.setPromptText("Select Province");
        formGrid.add(lblHomeProvince, 0, 3);
        formGrid.add(comboProvince, 1, 3);
        Label lblDOB = new Label("DOB:");
        datePickerDOB = new DatePicker();
        formGrid.add(lblDOB, 0, 4);
        formGrid.add(datePickerDOB, 1, 4);


        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnNew = new Button("New");
        Button btnDelete = new Button("Delete");
        Button btnRestore = new Button("Restore");
        Button btnFindPrev = new Button("Find Prev");
        Button btnFindNext = new Button("Find Next");
        Button btnFind = new Button("Find");
        Button btnClose = new Button("Close");

        buttonBox.getChildren().addAll(btnNew, btnDelete, btnRestore, btnFindPrev, btnFindNext, btnFind, btnClose);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(formGrid, buttonBox);

        btnNew.setOnAction(e -> createNewRecord());
        btnDelete.setOnAction(e -> deleteCurrentRecord());
        btnRestore.setOnAction(e -> restoreFields());
        btnFindPrev.setOnAction(e -> findPreviousRecord());
        btnFindNext.setOnAction(e -> findNextRecord());
        btnFind.setOnAction(e -> findRecord());
        btnClose.setOnAction(e -> saveAndClose());

        loadFromFile();

        Scene scene = new Scene(mainLayout, 550, 400);
        primaryStage.setTitle("Person Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createNewRecord() {
        String fullName = txtFullName.getText();
        String id = txtID.getText();
        String gender = comboGender.getValue();
        String homeProvince = comboProvince.getValue();
        String dob = datePickerDOB.getValue() != null ? datePickerDOB.getValue().toString() : "";

        if (fullName.isEmpty() || id.isEmpty() || gender == null || homeProvince == null || dob.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        personList.add(new Person(fullName, id, gender, homeProvince, dob));
        currentIndex = personList.size() - 1;
        showAlert("Success", "Record added!");
        clearFields();
    }

    private void deleteCurrentRecord() {
        if (currentIndex >= 0 && currentIndex < personList.size()) {
            personList.remove(currentIndex);
            currentIndex = -1;
            showAlert("Success", "Record deleted!");
            clearFields();
        } else {
            showAlert("Error", "No record selected!");
        }
    }

    private void restoreFields() {
        if (currentIndex >= 0 && currentIndex < personList.size()) {
            Person person = personList.get(currentIndex);
            txtFullName.setText(person.getFullName());
            txtID.setText(person.getId());
            comboGender.setValue(person.getGender());
            comboProvince.setValue(person.getHomeProvince());
            datePickerDOB.setValue(java.time.LocalDate.parse(person.getDob()));
        } else {
            showAlert("Error", "No record to restore!");
        }
    }

    private void findPreviousRecord() {
        if (currentIndex > 0) {
            currentIndex--;
            restoreFields();
        } else {
            showAlert("Error", "No previous record!");
        }
    }

    private void findNextRecord() {
        if (currentIndex < personList.size() - 1) {
            currentIndex++;
            restoreFields();
        } else {
            showAlert("Error", "No next record!");
        }
    }

    private void findRecord() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Record");
        dialog.setHeaderText("Find by ID or Full Name");
        dialog.setContentText("Enter ID or Full Name:");

        dialog.showAndWait().ifPresent(searchQuery -> {
            for (int i = 0; i < personList.size(); i++) {
                Person person = personList.get(i);
                if (person.getId().equalsIgnoreCase(searchQuery) || person.getFullName().equalsIgnoreCase(searchQuery)) {
                    currentIndex = i;
                    restoreFields();
                    showAlert("Success", "Record found!");
                    return;
                }
            }
            showAlert("Error", "Record not found!");
        });
    }

    private void saveAndClose() {
        saveToFile();
        showAlert("Success", "Data saved!");
        System.exit(0);
    }

    private void clearFields() {
        txtFullName.clear();
        txtID.clear();
        comboGender.setValue(null);
        comboProvince.setValue(null);
        datePickerDOB.setValue(null);
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(personList);
        } catch (IOException e) {
            showAlert("Error", "Error saving data!");
        }
    }

    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            personList = (ArrayList<Person>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            personList = new ArrayList<>();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
