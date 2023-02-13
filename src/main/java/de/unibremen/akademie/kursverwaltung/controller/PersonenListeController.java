package de.unibremen.akademie.kursverwaltung.controller;

import de.unibremen.akademie.kursverwaltung.domain.Person;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static de.unibremen.akademie.kursverwaltung.domain.AnwendungsModel.kvModel;

public class PersonenListeController implements Initializable {

    @FXML
    private TextField txInpPersonSuche;
    @FXML
    private Button btnAendernAnzeigen;
    @FXML
    private Button btnPersonAusListeLoeschen;
    @FXML
    private Button btnPersonAnlegenPersonenListe;
    @FXML
    private Button btnResetSuchfeld;
    @FXML
    public TableView<Person> tblPersonenListe;
    @FXML
    public TableColumn<Person, String> colPersonenListeTeilnahmeKurse;
    @FXML
    public TableColumn<Person, String> colPersonenListeInteressierteKurse;
    @FXML
    private TableColumn<Person, String> colPersonenListeAnrede;
    @FXML
    private TableColumn<Person, String> colPersonenListeVorname;
    @FXML
    private TableColumn<Person, String> colPersonenListeNachname;
    @FXML
    private TableColumn<Person, String> colPersonenListeTitel;
    @FXML
    private TableColumn<Person, String> colPersonenListeStrasse;
    @FXML
    private TableColumn<Person, String> colPersonenListePlz;
    @FXML
    private TableColumn<Person, String> colPersonenListeOrt;
    @FXML
    private TableColumn<Person, String> colPersonenListeEmail;
    @FXML
    private TableColumn<Person, String> colPersonenListeTelefon;
    @FXML
    public Tab tabPersonenListe;
    private MainController mainCtrl;
    private ObservableList<Person> personenListe = FXCollections.observableArrayList();
    private FilteredList<Person> filteredData;
    // TODO wird noch bearbeitet! Mohammed
    String listPersonDetails[] = {"titel", "vorname", "nachname", "strasse", "plz", "ort", "email", "telefon"};

    /* TODO: Nur nach nachfrage löschen!
       TODO: Runtime exception abfangen!
       TODO: bei mehreren zu löschenden Personen einzeln nachfragen
    */

    public void init(MainController mainController) {
        mainCtrl = mainController;
    }

    public void onClickPersonAusListeLoeschen(ActionEvent event) {
        //ObservableList<Person> allPerson = kvModel.getPersonen().getPersonenListe();
        List<Person> selectedPersonCopy = new ArrayList<>(tblPersonenListe.getSelectionModel().getSelectedItems());
        selectedPersonCopy.forEach(kvModel::removePerson); // ist das Gleiche wie die folgende Zeile
        // for (Person p : selectedPersonCopy) { kvModel.removePerson(p);}
    }

    @FXML
    public void onClickPersonAnlegenPersonenListe(ActionEvent event) {

        kvModel.aktuellePerson = null;
        mainCtrl.fxmlPersonenDetailsController.felderLeeren();
        PersonenDetailsController.zurueckPersonenliste = true;
        for (Tab tabPanePersonAnlegen : tabPersonenListe.getTabPane().getTabs()) {
            if (tabPanePersonAnlegen.getText().equals("Personen-Details")) {
                tabPanePersonAnlegen.getTabPane().getSelectionModel().select(tabPanePersonAnlegen);
            }
        }
    }

    @FXML
    public void onClickPersonAendernPersonenListe(ActionEvent event) {
        PersonenDetailsController.zurueckPersonenliste = true;

        if (!tblPersonenListe.getSelectionModel().isEmpty()) {
            mainCtrl.fxmlPersonenDetailsController.btnSavePersonDetails.setText("Update");

            kvModel.aktuellePerson = tblPersonenListe.getSelectionModel().getSelectedItem();

            mainCtrl.fxmlPersonenDetailsController.updateEintraegePersonUndListen(kvModel.aktuellePerson);

            for (Tab tabPanePersonAnlegen : tabPersonenListe.getTabPane().getTabs()) {
                if (tabPanePersonAnlegen.getText().equals("Personen-Details")) {
                    tabPanePersonAnlegen.getTabPane().getSelectionModel().select(tabPanePersonAnlegen);
                }
            }
        }
    }


    @FXML
    public void onClickResetSuchfeld(ActionEvent event) {
        txInpPersonSuche.clear();
        tblPersonenListe.getItems();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tblPersonenListe.setEditable(true);
        colPersonenListeAnrede.setCellValueFactory(new PropertyValueFactory<Person, String>("anrede"));
        colPersonenListeAnrede.setCellFactory(ComboBoxTableCell.<Person, String>forTableColumn("", "Herr", "Frau", "Divers"));
        colPersonenListeAnrede.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> t) {
                        ((Person) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setAnrede(t.getNewValue());
                    }
                }
        );
        colPersonenListeTitel.setCellValueFactory(new PropertyValueFactory<Person, String>("titel"));
        colPersonenListeTitel.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListeTitel.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setTitel(v.getNewValue());
                    }
                }
        );
        colPersonenListeVorname.setCellValueFactory(new PropertyValueFactory<Person, String>("vorname"));
        colPersonenListeVorname.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListeVorname.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setVorname(v.getNewValue());
                    }
                }
        );
        colPersonenListeNachname.setCellValueFactory(new PropertyValueFactory<Person, String>("nachname"));
        colPersonenListeNachname.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListeNachname.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setNachname(v.getNewValue());
                    }
                }
        );
        colPersonenListeStrasse.setCellValueFactory(new PropertyValueFactory<Person, String>("strasse"));
        colPersonenListeStrasse.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListeStrasse.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setStrasse(v.getNewValue());
                    }
                }
        );
        colPersonenListePlz.setCellValueFactory(new PropertyValueFactory<Person, String>("plz"));
        colPersonenListePlz.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListePlz.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setPlz(v.getNewValue());
                    }
                }
        );
        colPersonenListeOrt.setCellValueFactory(new PropertyValueFactory<Person, String>("ort"));
        colPersonenListeOrt.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListeOrt.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setOrt(v.getNewValue());
                    }
                }
        );
        colPersonenListeEmail.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
        colPersonenListeEmail.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListeEmail.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setTelefon(v.getNewValue());
                    }
                }

        );
        colPersonenListeTelefon.setCellValueFactory(new PropertyValueFactory<Person, String>("telefon"));
        colPersonenListeTelefon.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colPersonenListeTelefon.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> v) {
                        ((Person) v.getTableView().getItems().get(
                                v.getTablePosition().getRow())
                        ).setTelefon(v.getNewValue());
                    }
                }
        );
        colPersonenListeTeilnahmeKurse.setCellValueFactory(person -> new ReadOnlyStringWrapper(kvModel.getPkListe().getKurseAlsTeilnehmer(person.getValue()).toString()));
        colPersonenListeInteressierteKurse.setCellValueFactory(person -> new ReadOnlyStringWrapper(kvModel.getPkListe().getKurseAlsInteressent(person.getValue()).toString()));
        //tablePersonenListe.setItems(kvModel.getPersonen().getPersonenListe());
        TableView.TableViewSelectionModel<Person> selectionModel = tblPersonenListe.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        tblPersonenListe.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Person> change) -> {
            personenListe = tblPersonenListe.getSelectionModel().getSelectedItems();
            btnAendernAnzeigen.setDisable(personenListe != null && personenListe.size() > 1);
        });

        // [Filtering with suchTextField]
        //Wrap the ObserviableList in a FilteredList
        FilteredList<Person> filteredData = new FilteredList<>(kvModel.getPersonen().getPersonenListe(), person -> true);

        // set the filter Predicate whenever the filter changes
        txInpPersonSuche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                //if filter text is empty display all persons
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                //compare first name and last name...
                String lowerCaseFilter = newValue.toLowerCase();
                 if (person.getAnrede().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (person.getTitel().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (person.getVorname().toLowerCase().contains(lowerCaseFilter)) {
                     return true;
                 } else if (person.getNachname().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (person.getStrasse().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (person.getPlz().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (person.getOrt().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (person.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (person.getTelefon().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        //wrap the filterList in a sortedList
        SortedList<Person> sortedData = new SortedList<>(filteredData);

        //bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(tblPersonenListe.comparatorProperty());

        //add sorted and filtered data to the table
        tblPersonenListe.setItems(sortedData);
    }
}
