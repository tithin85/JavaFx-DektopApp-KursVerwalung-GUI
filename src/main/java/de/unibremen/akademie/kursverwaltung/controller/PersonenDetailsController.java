package de.unibremen.akademie.kursverwaltung.controller;

import de.unibremen.akademie.kursverwaltung.domain.Kurs;
import de.unibremen.akademie.kursverwaltung.domain.KvModel;
import de.unibremen.akademie.kursverwaltung.domain.Meldung;
import de.unibremen.akademie.kursverwaltung.domain.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class PersonenDetailsController {
    @FXML
    public ChoiceBox anrede;
    @FXML

    public static boolean zurueckwechseln;
    @FXML
    public TextField titel;
    @FXML
    public TextField vorname;
    @FXML
    public TextField nachname;
    @FXML
    public TextField strasse;
    @FXML
    public TextField plz;
    @FXML
    public TextField email;
    @FXML
    public TextField telefon;
    @FXML
    public TextField ort;
    @FXML
    public Button save;

    @FXML
    public Button abbrechen;

    public ObservableList<String> choiceListAnrede = FXCollections.observableArrayList();

    public Tab fxmlPersonenDetails;

    static public boolean zurueckPersonenliste = false;
    public TableView TableViewKurse;
    public Label kursliste;
    public TableColumn kursname;
    public TableColumn startDate;


    private MainController main;

    public void init(MainController mainController) {
        main = mainController;
    }


    public void initialize() {
        choiceListAnrede.add("");
        choiceListAnrede.add("Herr");
        choiceListAnrede.add("Frau");
        choiceListAnrede.add("Divers");
        anrede.setItems(choiceListAnrede);
        anrede.getSelectionModel().selectFirst();
        kursname.setCellValueFactory(new PropertyValueFactory<Kurs, String>("name"));
        kursname.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());
        startDate.setCellValueFactory(new PropertyValueFactory<Kurs, String>("displaystartDate"));
        startDate.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());


        TableViewKurse.setItems(KvModel.model.kursList);
        TableView.TableViewSelectionModel<Kurs> selectionModel =
                TableViewKurse.getSelectionModel();
        selectionModel.setSelectionMode(
                SelectionMode.SINGLE);

    }

    @FXML
    public void onsaveclick() {
        if (KvModel.aktuellePerson != null) {
            try {
                KvModel.aktuellePerson.updatePerson(anrede.getValue().toString(), titel.getText(), vorname.getText(), nachname.getText(), strasse.getText(), plz.getText(), ort.getText(), email.getText(), telefon.getText());
            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
            }
            felderLeeren();
            save.setText("speichern");
        } else {
            int aktuelleAnzPersonen = KvModel.personList.size();
            try {
                Person person = Person.addNewPerson(anrede.getValue().toString(), titel.getText(), vorname.getText(), nachname.getText(), strasse.getText(), plz.getText(), ort.getText(), email.getText(), telefon.getText());
            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
            }
            if (KvModel.personList.size() > aktuelleAnzPersonen) {
                felderLeeren();
            }
        }
        KvModel.aktuellePerson = null;
        Tab plTab = main.fxmlPersonenListeController.fxmlPersonenListe;
        main.fxmlPersonenListeController.table.refresh();
        if (PersonenDetailsController.zurueckPersonenliste) {
            plTab.getTabPane().getSelectionModel().select(plTab);
        }
    }

    @FXML
    public void update(Person person) {
        String anrede = person.getAnrede();
        String titel = person.getTitel();
        String vorname = person.getVorname();
        String nachname = person.getNachname();
        String strasse = person.getStrasse();
        String plz = person.getPlz();
        String ort = person.getOrt();
        String email = person.getEmail();
        String telefon = person.getTelefon();
        for (int i = 0; i < choiceListAnrede.size(); i++) {
            if (choiceListAnrede.get(i).equals(anrede)) {
                this.anrede.getSelectionModel().select(i);
            }
        }

        this.titel.setText(titel);
        this.vorname.setText(vorname);
        this.nachname.setText(nachname);
        this.strasse.setText(strasse);
        this.plz.setText(plz);
        this.ort.setText(ort);
        this.email.setText(email);
        this.telefon.setText(telefon);
    }

    @FXML
    public void onabbrechenclick(ActionEvent event) {
        felderLeeren();

        if (zurueckPersonenliste) {
            //System.out.println(zurueckPersonenliste);
            Tab plTab = main.fxmlPersonenListeController.fxmlPersonenListe;
            plTab.getTabPane().getSelectionModel().select(plTab);
            zurueckPersonenliste = false;
        }
    }


    public void felderLeeren() {
        anrede.getSelectionModel().selectFirst();
        titel.clear();
        vorname.clear();
        nachname.clear();
        strasse.clear();
        plz.clear();
        ort.clear();
        email.clear();
        telefon.clear();
    }
}
