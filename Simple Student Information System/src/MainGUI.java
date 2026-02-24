import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.List;

public class MainGUI {

    private StudentManager studentManager;
    private ProgramManager programManager;
    private CollegeManager collegeManager;

    private TableView<Student> studentTable;
    private TableView<Program> programTable;
    private TableView<College> collegeTable;

    private ComboBox<String> studentProgramBox;
    private ComboBox<String> programCollegeBox;

    private ObservableList<Student> studentData = FXCollections.observableArrayList();
    private ObservableList<Program> programData = FXCollections.observableArrayList();
    private ObservableList<College> collegeData = FXCollections.observableArrayList();

    public void start(Stage stage) {

        List<Student> students = csvReader.loadStudent();
        List<Program> programs = csvReader.loadProgram();
        List<College> colleges = csvReader.loadCollege();

        collegeManager = new CollegeManager(colleges);
        programManager = new ProgramManager(programs);
        studentManager = new StudentManager(students);

        TabPane tabPane = new TabPane();

        Tab studentTab = new Tab("Students");
        studentTab.setContent(createStudentPane());
        studentTab.setClosable(false);

        Tab programTab = new Tab("Programs");
        programTab.setContent(createProgramPane());
        programTab.setClosable(false);

        Tab collegeTab = new Tab("Colleges");
        collegeTab.setContent(createCollegePane());
        collegeTab.setClosable(false);

        tabPane.getTabs().addAll(studentTab, programTab, collegeTab);

        //------------------dark mode--------------------

        ToggleButton darkModeToggle = new ToggleButton("🌙 Dark Mode");
        darkModeToggle.setOnAction(e -> {
            Scene scene = darkModeToggle.getScene();
            if (scene != null) {
                if (darkModeToggle.isSelected()) {
                    darkModeToggle.setText("☀️ Light Mode");
                    scene.getRoot().setStyle("-fx-base: #2B2B2B;");
                } else {
                    darkModeToggle.setText("🌙 Dark Mode");
                    scene.getRoot().setStyle("");
                }
            }
        });

        HBox topBar = new HBox(darkModeToggle);
        topBar.setStyle("-fx-padding: 10; -fx-alignment: center-right;");

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1050, 600);
        stage.setTitle("Student Management System");
        stage.setScene(scene);
        stage.show();
    }

    //-------------------------student---------------------------
    private Pane createStudentPane() {

        studentTable = new TableView<>();
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));

        TableColumn<Student, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));

        TableColumn<Student, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));

        TableColumn<Student, String> progCol = new TableColumn<>("Program");
        progCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProgramCode()));

        TableColumn<Student, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getYear()));

        TableColumn<Student, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSex()));

        studentTable.getColumns().addAll(idCol, firstCol, lastCol, progCol, yearCol, genderCol);
        refreshStudents();

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search Students (ID, Name, Program)...");

        FilteredList<Student> filteredData = new FilteredList<>(studentData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (student.getId().toLowerCase().contains(lowerCaseFilter)) return true;
                if (student.getFirstName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (student.getLastName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (student.getProgramCode().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);

        TextField yearField = new TextField();
        yearField.setPromptText("YYYY");

        TextField numberField = new TextField();
        numberField.setPromptText("NNNN");

        yearField.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,4}") ? c : null));
        numberField.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,4}") ? c : null));

        TextField firstField = new TextField();
        firstField.setPromptText("First Name");

        TextField lastField = new TextField();
        lastField.setPromptText("Last Name");

        TextField yearLevelField = new TextField();
        yearLevelField.setPromptText("Year Level");
        yearLevelField.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,1}") ? c : null));

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female");

        studentProgramBox = new ComboBox<>();
        refreshStudentProgramBox();

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button saveBtn = new Button("Add Student");
        Button clearBtn = new Button("Clear Form");
        Button deleteBtn = new Button("Delete Selected");

        // double click to edit
        studentTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && studentTable.getSelectionModel().getSelectedItem() != null) {
                Student s = studentTable.getSelectionModel().getSelectedItem();
                String[] idParts = s.getId().split("-");
                if (idParts.length == 2) {
                    yearField.setText(idParts[0]);
                    numberField.setText(idParts[1]);
                }
                firstField.setText(s.getFirstName());
                lastField.setText(s.getLastName());
                studentProgramBox.setValue(s.getProgramCode());
                yearLevelField.setText(String.valueOf(s.getYear()));

                String gender = s.getSex();
                if (gender != null && !gender.isEmpty()) {
                    genderBox.setValue(gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase());
                }

                // Lock the ID fields and change button to Update
                yearField.setEditable(false);
                numberField.setEditable(false);
                yearField.setStyle("-fx-background-color: #e0e0e0;");
                numberField.setStyle("-fx-background-color: #e0e0e0;");
                saveBtn.setText("Update Student");
            }
        });

        // clear button
        clearBtn.setOnAction(e -> {
            clear(yearField, numberField, firstField, lastField, yearLevelField);
            studentProgramBox.setValue(null);
            genderBox.setValue(null);
            yearField.setEditable(true);
            numberField.setEditable(true);
            yearField.setStyle("");
            numberField.setStyle("");
            saveBtn.setText("Add Student");
            studentTable.getSelectionModel().clearSelection();
            errorLabel.setText("");
        });

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");
            if (yearField.getText().length() != 4 || numberField.getText().length() != 4) {
                errorLabel.setText("ID must be YYYY-NNNN.");
                return;
            }
            if (studentProgramBox.getValue() == null) {
                errorLabel.setText("Select a program.");
                return;
            }
            try {
                String fullId = yearField.getText() + "-" + numberField.getText();
                Student s = new Student(fullId, firstField.getText(), lastField.getText(), studentProgramBox.getValue(), Integer.parseInt(yearLevelField.getText()), genderBox.getValue());

                String error = studentManager.addStudent(s, programManager);

                if (error != null) {
                    errorLabel.setText(error);
                } else {
                    refreshStudents();
                    clearBtn.fire(); // Trigger clear to reset form out of edit mode
                }
            } catch (Exception ex) {
                errorLabel.setText("Complete all fields.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Student selected = studentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = studentManager.deleteStudent(selected.getId()); //
                if (success) {
                    refreshStudents();
                    clearBtn.fire(); // Reset form if deleted while editing
                } else {
                    errorLabel.setText("Failed to delete student.");
                }
            }
        });

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setMinWidth(250);

        form.add(new Label("Year:"), 0, 0);
        form.add(yearField, 1, 0);
        form.add(new Label("Number:"), 0, 1);
        form.add(numberField, 1, 1);
        form.add(new Label("First Name:"), 0, 2);
        form.add(firstField, 1, 2);
        form.add(new Label("Last Name:"), 0, 3);
        form.add(lastField, 1, 3);
        form.add(new Label("Program:"), 0, 4);
        form.add(studentProgramBox, 1, 4);
        form.add(new Label("Year Level:"), 0, 5);
        form.add(yearLevelField, 1, 5);
        form.add(new Label("Gender:"), 0, 6);
        form.add(genderBox, 1, 6);

        HBox buttonBox1 = new HBox(10, saveBtn, clearBtn);
        form.add(buttonBox1, 1, 7);
        form.add(deleteBtn, 1, 8);
        form.add(errorLabel, 1, 9);

        VBox tableContainer = new VBox(10, searchField, studentTable);
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox layout = new HBox(30, tableContainer, form);
        layout.setStyle("-fx-padding: 20;");

        return layout;
    }

    //--------------------program---------------------
    private Pane createProgramPane() {

        programTable = new TableView<>();
        programTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Program, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));

        TableColumn<Program, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Program, String> collegeCol = new TableColumn<>("College");
        collegeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCollege()));

        programTable.getColumns().addAll(codeCol, nameCol, collegeCol);
        refreshPrograms();

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search Programs (Code, Name, College)...");

        FilteredList<Program> filteredData = new FilteredList<>(programData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(program -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (program.getCode().toLowerCase().contains(lowerCaseFilter)) return true;
                if (program.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (program.getCollege().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<Program> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(programTable.comparatorProperty());
        programTable.setItems(sortedData);

        TextField codeField = new TextField();
        codeField.setPromptText("Program Code (e.g., BSCS)");

        TextField nameField = new TextField();
        nameField.setPromptText("Program Name");

        programCollegeBox = new ComboBox<>();
        programCollegeBox.setPromptText("Select College");
        refreshProgramCollegeBox();

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button saveBtn = new Button("Add Program");
        Button clearBtn = new Button("Clear Form");
        Button deleteBtn = new Button("Delete Selected");

        // double click to edit
        programTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && programTable.getSelectionModel().getSelectedItem() != null) {
                Program p = programTable.getSelectionModel().getSelectedItem();
                codeField.setText(p.getCode());
                nameField.setText(p.getName());
                programCollegeBox.setValue(p.getCollege());

                // Lock Code field
                codeField.setEditable(false);
                codeField.setStyle("-fx-background-color: #e0e0e0;");
                saveBtn.setText("Update Program");
            }
        });

        //  clear button
        clearBtn.setOnAction(e -> {
            clear(codeField, nameField);
            programCollegeBox.setValue(null);
            codeField.setEditable(true);
            codeField.setStyle("");
            saveBtn.setText("Add Program");
            programTable.getSelectionModel().clearSelection();
            errorLabel.setText("");
        });

        saveBtn.setOnAction(e -> {
            Program p = new Program(codeField.getText().toUpperCase(), nameField.getText(), programCollegeBox.getValue());
            String error = programManager.addProgram(p, collegeManager);

            if (error != null) {
                errorLabel.setText(error);
            } else {
                refreshPrograms();
                refreshStudentProgramBox();
                clearBtn.fire(); // Reset form out of edit mode
            }
        });

        // captures boolean and displays error
        deleteBtn.setOnAction(e -> {
            Program selected = programTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = programManager.deleteProgram(selected.getCode(), studentManager); //

                if (!success) {
                    errorLabel.setText("Cannot delete: Students are enrolled in this program.");
                } else {
                    refreshPrograms();
                    refreshStudentProgramBox();
                    clearBtn.fire();
                }
            }
        });

        HBox actionButtons = new HBox(10, saveBtn, clearBtn);
        VBox form = new VBox(10, codeField, nameField, programCollegeBox, actionButtons, deleteBtn, errorLabel);
        form.setMinWidth(250);

        VBox tableContainer = new VBox(10, searchField, programTable);
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox layout = new HBox(30, tableContainer, form);
        layout.setStyle("-fx-padding: 20;");

        return layout;
    }

    //------------------------COLLEGE TAB---------------------------

    private Pane createCollegePane() {

        collegeTable = new TableView<>();
        collegeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<College, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));

        TableColumn<College, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        collegeTable.getColumns().addAll(codeCol, nameCol);
        refreshColleges();

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search Colleges (Code, Name)...");

        FilteredList<College> filteredData = new FilteredList<>(collegeData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(college -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (college.getCode().toLowerCase().contains(lowerCaseFilter)) return true;
                if (college.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<College> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(collegeTable.comparatorProperty());
        collegeTable.setItems(sortedData);

        TextField codeField = new TextField();
        codeField.setPromptText("College Code (e.g., CCS)");

        TextField nameField = new TextField();
        nameField.setPromptText("College Name");

        codeField.textProperty().addListener((obs, o, n) -> codeField.setText(n.toUpperCase()));

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button saveBtn = new Button("Add College");
        Button clearBtn = new Button("Clear Form");
        Button deleteBtn = new Button("Delete Selected");

        // double click to edit
        collegeTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && collegeTable.getSelectionModel().getSelectedItem() != null) {
                College c = collegeTable.getSelectionModel().getSelectedItem();
                codeField.setText(c.getCode());
                nameField.setText(c.getName());

                // Lock Code field
                codeField.setEditable(false);
                codeField.setStyle("-fx-background-color: #e0e0e0;");
                saveBtn.setText("Update College");
            }
        });

        // clear button
        clearBtn.setOnAction(e -> {
            clear(codeField, nameField);
            codeField.setEditable(true);
            codeField.setStyle("");
            saveBtn.setText("Add College");
            collegeTable.getSelectionModel().clearSelection();
            errorLabel.setText("");
        });

        saveBtn.setOnAction(e -> {
            College c = new College(codeField.getText(), nameField.getText());
            String error = collegeManager.addCollege(c);

            if (error != null) {
                errorLabel.setText(error);
            } else {
                refreshColleges();
                refreshProgramCollegeBox();
                clearBtn.fire(); // Reset out of edit mode
            }
        });

        // captures boolean and shows error
        deleteBtn.setOnAction(e -> {
            College selected = collegeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = collegeManager.deleteCollege(selected.getCode(), programManager); //

                if (!success) {
                    errorLabel.setText("Cannot delete: Programs belong to this college.");
                } else {
                    refreshColleges();
                    refreshProgramCollegeBox();
                    clearBtn.fire();
                }
            }
        });

        HBox actionButtons = new HBox(10, saveBtn, clearBtn);
        VBox form = new VBox(10, codeField, nameField, actionButtons, deleteBtn, errorLabel);
        form.setMinWidth(250);

        VBox tableContainer = new VBox(10, searchField, collegeTable);
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        HBox layout = new HBox(30, tableContainer, form);
        layout.setStyle("-fx-padding: 20;");

        return layout;
    }

    //------------------------refresh methods---------------------------
    private void refreshStudents() {
        studentData.setAll(studentManager.getStudents());
    }

    private void refreshPrograms() {
        programData.setAll(programManager.getPrograms());
    }

    private void refreshColleges() {
        collegeData.setAll(collegeManager.getColleges());
    }

    private void refreshStudentProgramBox() {
        if (studentProgramBox != null) {
            studentProgramBox.getItems().setAll(
                    programManager.getPrograms()
                            .stream()
                            .map(Program::getCode)
                            .toList()
            );
        }
    }

    private void refreshProgramCollegeBox() {
        if (programCollegeBox != null) {
            programCollegeBox.getItems().setAll(
                    collegeManager.getColleges()
                            .stream()
                            .map(College::getCode)
                            .toList()
            );
        }
    }

    private void clear(TextField... fields) {
        for (TextField f : fields) f.clear();
    }
}