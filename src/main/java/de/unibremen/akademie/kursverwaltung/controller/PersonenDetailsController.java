package de.unibremen.akademie.kursverwaltung.controller;

import de.unibremen.akademie.kursverwaltung.domain.Kurs;
import de.unibremen.akademie.kursverwaltung.domain.Meldung;
import de.unibremen.akademie.kursverwaltung.domain.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.text.SimpleDateFormat;
import java.util.Date;

import static de.unibremen.akademie.kursverwaltung.domain.AnwendungsModel.kvModel;

public class PersonenDetailsController {
    @FXML
    public ChoiceBox choiceAnrede;
    @FXML
    public TextField txInpTitel;
    @FXML
    public TextField txInpVorname;
    @FXML
    public TextField txInpNachname;
    @FXML
    public TextField txInpStrasse;
    @FXML
    public TextField txInpPlz;
    @FXML
    public TextField txInpEmail;
    @FXML
    public TextField txInpTelefon;
    @FXML
    public TextField txInpOrt;
    @FXML
    public Button btnSavePersonDetails;
    @FXML
    public Button btnCancelPersonDetails;
    @FXML
    public Button btnInteressentZuTeilnehmer;
    @FXML
    public Button btnTeilnehmerZuInteressent;
    @FXML
    public Button btnInteressentKursRaus;
    @FXML
    public Button btnInteressentKursRein;
    @FXML
    public Button btnTeilnehmerKursRaus;
    @FXML
    public Button btnTeilnehmerKursRein;
    @FXML
    public Tab tabPersonenDetails;
    @FXML
    public TableView tblKurse;
    @FXML
    public TableView tblTeilnahmeKurse;
    @FXML
    public TableView tblInteresseKurse;
    @FXML
    public TableColumn colKurseKursname;
    @FXML
    public TableColumn colKurseStartDate;
    @FXML
    public TableColumn colTeilnahmeKurseKursname;
    @FXML
    public TableColumn colInteresseKurseKursname;

    private MainController mainCtrl;
    private Object selectedItem;
    public ObservableList<String> choiceListAnrede = FXCollections.observableArrayList();
    static public final ObservableList<Person> listKursTeilnehmer = FXCollections.observableArrayList();
    static public final ObservableList<Person> listKursInteressent = FXCollections.observableArrayList();
    static public boolean zurueckPersonenliste = false;
    static public boolean bearbeiten = false;

    public void init(MainController mainController) {
        mainCtrl = mainController;
    }

    public void initialize() {
        choiceListAnrede.add("");
        choiceListAnrede.add("Herr");
        choiceListAnrede.add("Frau");
        choiceListAnrede.add("Divers");
        choiceAnrede.setItems(choiceListAnrede);
        choiceAnrede.getSelectionModel().selectFirst();
        colKurseKursname.setCellValueFactory(new PropertyValueFactory<Kurs, String>("name"));
        colKurseKursname.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());
        colKurseStartDate.setCellValueFactory(new PropertyValueFactory<Kurs, Date>("startDatum"));
        //colKurseStartDate.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());
        colKurseStartDate.setCellFactory(column -> {
            TableCell<Kurs, Date> cell = new TableCell<Kurs, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };

            return cell;
        });

        TableView.TableViewSelectionModel<Kurs> selectionModel =
                tblKurse.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        // TODO Kurs vom Teilnehmer in TeilnahmeKurse anzeigen!!
        colTeilnahmeKurseKursname.setCellValueFactory(new PropertyValueFactory<Kurs, String>("name"));
        colTeilnahmeKurseKursname.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());

        // TODO Kurs vom Interessenter in InteresseKurse anzeigen!!
        colInteresseKurseKursname.setCellValueFactory(new PropertyValueFactory<Kurs, String>("name"));
        colInteresseKurseKursname.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());


        tblKurse.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkKursTeilnehmerButton());
        tblTeilnahmeKurse.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkKursAusTeilnehmerButton());


        tblInteresseKurse.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkKursInteressentenButton());

        tblKurse.setItems(kvModel.getKurse().getKursListe());
        checkKursTeilnehmerButton();
        checkKursAusTeilnehmerButton();
        checkKursInteressentenButton();
    }

    private void checkKursTeilnehmerButton() {
        selectedItem = tblKurse.getSelectionModel().getSelectedItem();
        boolean disable = tblTeilnahmeKurse.getItems().contains(selectedItem) || tblInteresseKurse.getItems().contains(selectedItem);
        btnTeilnehmerKursRein.setDisable(selectedItem == null || disable);
        btnInteressentKursRein.setDisable(selectedItem == null || disable);

    }

    private void checkKursAusTeilnehmerButton() {
        selectedItem = tblTeilnahmeKurse.getSelectionModel().getSelectedItem();
        btnTeilnehmerKursRaus.setDisable(selectedItem == null);
        btnTeilnehmerZuInteressent.setDisable(selectedItem == null);

    }

    private void checkKursInteressentenButton() {
        selectedItem = tblInteresseKurse.getSelectionModel().getSelectedItem();
        btnInteressentZuTeilnehmer.setDisable(selectedItem == null);
        btnInteressentKursRaus.setDisable(selectedItem == null);


    }

    @FXML
    public void onClickSavePerson() {
        Person person = null;
        // Update einer bestehenden Person
        if (kvModel.aktuellePerson != null) {
            try {
                kvModel.aktuellePerson.updatePerson(choiceAnrede.getValue().toString(), txInpTitel.getText(), txInpVorname.getText(),
                        txInpNachname.getText(), txInpStrasse.getText(), txInpPlz.getText(), txInpOrt.getText(), txInpEmail.getText(), txInpTelefon.getText());
                // kvModel.getPkListe().removeAllKurseAlsTeilnehmer(kvModel.aktuellePerson);
                //kvModel.getPkListe().removeAllKurseAlsInteressent(kvModel.aktuellePerson);

                kvModel.getPkListe().addKurseAlsTeilnehmer(kvModel.aktuellePerson, this.tblTeilnahmeKurse.getItems());
                kvModel.getPkListe().addKurseAlsInteressent(kvModel.aktuellePerson, this.tblInteresseKurse.getItems());
            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
                return;
            }
            felderLeeren();
            btnSavePersonDetails.setText("Speichern");
        } else {
            // Neue Person hinzufuegen
            int aktuelleAnzPersonen = kvModel.getPersonen().getPersonenListe().size();
            try {
                person = kvModel.getPersonen().addNewPerson(choiceAnrede.getValue().toString(), txInpTitel.getText(), txInpVorname.getText(),
                        txInpNachname.getText(), txInpStrasse.getText(), txInpPlz.getText(), txInpOrt.getText(), txInpEmail.getText(), txInpTelefon.getText());
                if (person == null) {
                    Meldung.eingabeFehler("Person schon existiert!!");
                    return;
                }
                kvModel.getPkListe().addKurseAlsTeilnehmer(person, this.tblTeilnahmeKurse.getItems());
                kvModel.getPkListe().addKurseAlsInteressent(person, this.tblInteresseKurse.getItems());
            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
                return;
            }
            if (kvModel.getPersonen().getPersonenListe().size() > aktuelleAnzPersonen) {
                felderLeeren();
            }
        }
        kvModel.aktuellePerson = null;
        Tab plTab = mainCtrl.fxmlPersonenListeController.tabPersonenListe;
        //plTab.getTabPane().getSelectionModel().select(plTab);
        mainCtrl.fxmlPersonenListeController.tblPersonenListe.refresh();
        mainCtrl.fxmlKurseListeController.tblKurseListe.refresh();

        if (PersonenDetailsController.zurueckPersonenliste) {
            plTab.getTabPane().getSelectionModel().select(plTab);
            mainCtrl.fxmlPersonenListeController.tblPersonenListe.getSelectionModel().clearSelection();
            mainCtrl.fxmlPersonenListeController.tblPersonenListe.getSelectionModel().select(person);
        }
    }

    @FXML
    public void updateEintraegePersonUndListen(Person person) {
        if (person != null) {
            String anrede = person.getAnrede();
            for (int i = 0; i < choiceListAnrede.size(); i++) {
                if (choiceListAnrede.get(i).equals(anrede)) {
                    this.choiceAnrede.getSelectionModel().select(i);
                }
            }
            this.txInpTitel.setText(person.getTitel());
            this.txInpVorname.setText(person.getVorname());
            this.txInpNachname.setText(person.getNachname());
            this.txInpStrasse.setText(person.getStrasse());
            this.txInpPlz.setText(person.getPlz());
            this.txInpOrt.setText(person.getOrt());
            this.txInpEmail.setText(person.getEmail());
            this.txInpTelefon.setText(person.getTelefon());
            tblTeilnahmeKurse.getItems().clear();
            tblTeilnahmeKurse.getItems().addAll(kvModel.getPkListe().getKurse(person, true));
            tblInteresseKurse.getItems().clear();
            tblInteresseKurse.getItems().addAll(kvModel.getPkListe().getKurse(person, false));

        }
    }

    @FXML
    public void onClickAbbrechenPerson(ActionEvent event) {
        felderLeeren();
        /////////// TODO MD
        kvModel.aktuellePerson = null;

        if (zurueckPersonenliste) {
            Tab plTab = mainCtrl.fxmlPersonenListeController.tabPersonenListe;
            plTab.getTabPane().getSelectionModel().select(plTab);
            zurueckPersonenliste = false;

        }

    }

    public void felderLeeren() {
        choiceAnrede.getSelectionModel().selectFirst();
        txInpTitel.clear();
        txInpVorname.clear();
        txInpNachname.clear();
        txInpStrasse.clear();
        txInpPlz.clear();
        txInpOrt.clear();
        txInpEmail.clear();
        txInpTelefon.clear();
        tblTeilnahmeKurse.getItems().clear();
        tblInteresseKurse.getItems().clear();
        btnSavePersonDetails.setText("Speichern");
    }

    public void onClickTeilnehmerZuInteressent(ActionEvent actionEvent) {

        //System.out.println("Teilnehmer zu Interessent!");
        tblInteresseKurse.getItems().add(tblTeilnahmeKurse.getSelectionModel().getSelectedItem());
        tblTeilnahmeKurse.getItems().removeAll(tblTeilnahmeKurse.getSelectionModel().getSelectedItems());
        tblTeilnahmeKurse.getSelectionModel().clearSelection();

    }

    public void onClickInteressentZuTeilnehmer(ActionEvent actionEvent) {

        tblTeilnahmeKurse.getItems().add(tblInteresseKurse.getSelectionModel().getSelectedItem());
        tblInteresseKurse.getItems().removeAll(tblInteresseKurse.getSelectionModel().getSelectedItems());
        tblInteresseKurse.getSelectionModel().clearSelection();

    }

    public void onClickKursRausAusInteressent(ActionEvent actionEvent) {
        tblInteresseKurse.getItems().removeAll(tblInteresseKurse.getSelectionModel().getSelectedItem());
        tblInteresseKurse.getSelectionModel().clearSelection();

    }

    public void onClickKursRausAusTeilnehmer(ActionEvent actionEvent) {
        tblTeilnahmeKurse.getItems().remove(tblTeilnahmeKurse.getSelectionModel().getSelectedItem());
        tblTeilnahmeKurse.getSelectionModel().clearSelection();
    }

    public void onClickKursZuTeilnehmer(ActionEvent actionEvent) {

        tblTeilnahmeKurse.getItems().add(tblKurse.getSelectionModel().getSelectedItem());
        tblKurse.getSelectionModel().clearSelection();
    }

    public void onClickKursZuInteressent(ActionEvent actionEvent) {

        tblInteresseKurse.getItems().add(tblKurse.getSelectionModel().getSelectedItem());
        tblKurse.getSelectionModel().clearSelection();

    }
}
