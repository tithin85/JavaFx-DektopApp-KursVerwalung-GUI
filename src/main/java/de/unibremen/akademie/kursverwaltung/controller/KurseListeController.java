

package de.unibremen.akademie.kursverwaltung.controller;


import de.unibremen.akademie.kursverwaltung.application.DatumFormatieren;
import de.unibremen.akademie.kursverwaltung.domain.Kurs;
import de.unibremen.akademie.kursverwaltung.domain.KursListe;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.unibremen.akademie.kursverwaltung.domain.AnwendungsModel.kvModel;

public class KurseListeController {

    @FXML
    private Label lblBisDatum;
    @FXML
    private TextField txInpSuche;
    @FXML
    private ComboBox comboKurseListeStatusSuche;
    @FXML
    private DatePicker pickAbDatum;
    @FXML
    private DatePicker pickBisDatum;
    @FXML
    private DatePicker pickDate;
    @FXML
    public Button btnResetAction;
    @FXML
    private Button btnBearbeiten;
    @FXML
    private Button btnEntfernen;
    @FXML
    private Button btnHinzufuegen;
    @FXML
    public TableView<Kurs> tblKurseListe;
    @FXML
    public TableColumn<Kurs, String> colKurseListeKursname;
    @FXML
    public TableColumn<Kurs, Date> colKurseListeStartDatum;
    @FXML
    public TableColumn<Kurs, Date> colKurseListeEndDatum;
    @FXML
    public TableColumn<Kurs, Integer> colKurseListeFreiePlaetze;
    @FXML
    public TableColumn<Kurs, Integer> colKurseListeAnzahlTeilnehmer;
    @FXML
    public TableColumn<Kurs, String> colKurseListeStatus;
    @FXML
    public TableColumn<Kurs, String> colKurseListeTeilnehmerAnKurs;
    @FXML
    public TableColumn<Kurs, String> colKurseListeInteressentenAnKurs;
    @FXML
    public Tab tabKurseListe;
    private MainController mainCtrl;
    ObservableList<Kurs> kurseListe = FXCollections.observableArrayList();
    //private FilteredList<Kurs> filteredData;
    public void init(MainController mainController) {
        mainCtrl = mainController;
    }

    public TableView<Kurs> getTblKurseListe() {
        return tblKurseListe;
    }

    public void initialize() {
        DatumFormatieren.datumFormatieren(pickAbDatum);
        DatumFormatieren.datumFormatieren(pickBisDatum);
        pickAbDatum.setPromptText("Startdatum eingeben");
        pickBisDatum.setPromptText("Enddatum eingeben");
        tblKurseListe.setEditable(false);
        tblKurseListe.setPlaceholder(
                new Label("Keine Einträge zum Anzeigen vorhanden"));
        colKurseListeKursname.setCellValueFactory(new PropertyValueFactory<Kurs, String>("name"));
        colKurseListeKursname.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());
        colKurseListeStatus.setCellValueFactory(new PropertyValueFactory<Kurs, String>("status"));
        colKurseListeStatus.setCellFactory(TextFieldTableCell.<Kurs>forTableColumn());
        colKurseListeFreiePlaetze.setCellValueFactory(new PropertyValueFactory<Kurs, Integer>("freiePlaetze"));
        colKurseListeFreiePlaetze.setCellFactory(ComboBoxTableCell.<Kurs, Integer>forTableColumn());
        colKurseListeAnzahlTeilnehmer.setCellValueFactory(new PropertyValueFactory<Kurs, Integer>("aktuelleTnZahl"));
        colKurseListeAnzahlTeilnehmer.setCellFactory(ComboBoxTableCell.<Kurs, Integer>forTableColumn());
        colKurseListeStartDatum.setCellValueFactory(new PropertyValueFactory<Kurs, Date>("startDatum"));
        //colStart_Datum.setCellFactory(ComboBoxTableCell.<Kurs, String>forTableColumn());
        colKurseListeStartDatum.setCellFactory(column -> {
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
        colKurseListeEndDatum.setCellValueFactory(new PropertyValueFactory<Kurs, Date>("endeDatum"));
        //colEnd_Datum.setCellFactory(ComboBoxTableCell.<Kurs, String>forTableColumn());
        colKurseListeEndDatum.setCellFactory(column -> {
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
        colKurseListeTeilnehmerAnKurs.setCellValueFactory(kurs -> new ReadOnlyStringWrapper(kvModel.getPkListe().getPersonNameAlsTeilnehmer(kurs.getValue()).toString()));
        colKurseListeInteressentenAnKurs.setCellValueFactory(kurs -> new ReadOnlyStringWrapper(kvModel.getPkListe().getPersonNameAlsInteressent(kurs.getValue()).toString()));
        tblKurseListe.setItems(kvModel.getKurse().getKursListe());
        TableView.TableViewSelectionModel<Kurs> selectionModel =
                tblKurseListe.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        tblKurseListe.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Kurs> change) -> {
            kurseListe = tblKurseListe.getSelectionModel().getSelectedItems();
            btnBearbeiten.setDisable(kurseListe != null && kurseListe.size() > 1);
        });
        FilteredList<Kurs> filteredData = new FilteredList<>(kvModel.getKurse().getKursListe(), kurs -> true);
        txInpSuche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(kurs -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (kurs.getName().toLowerCase().startsWith(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        comboKurseListeStatusSuche.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(kurs -> {
                if (newValue == null || newValue.toString().isEmpty() || newValue.toString().isBlank() || newValue.toString().equals("Alle")) {
                    return true;
                }
                String lowerCaseFilter = newValue.toString().toLowerCase();
                if (kurs.getStatus().toLowerCase().startsWith(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
//////////////////////////////////////////// TODO //////////////////////////////////////////
        KursListe dateFilter = new KursListe();
        pickAbDatum.valueProperty().addListener((observable, oldValue, newValue) -> {
            dateFilter.setVonDatum(newValue);
            filteredData.setPredicate(kurs -> dateFilter.isBetween(kurs.getStartDatum()));
        });
        pickBisDatum.valueProperty().addListener((observable, oldValue, newValue) -> {
            dateFilter.setBisDatum(newValue);
            dateFilter.alertDatum(dateFilter.getVonDatum(), dateFilter.getBisDatum());
            filteredData.setPredicate(kurs -> dateFilter.isBetween(kurs.getStartDatum()));
        });
        SortedList<Kurs> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblKurseListe.comparatorProperty());
        tblKurseListe.setItems(sortedData);
    }

    @FXML
    void onClickHinzufügenButton(ActionEvent event) {
        kvModel.aktuellerKurs = null;
        mainCtrl.fxmlKurseDetailsController.onClickAbbrechenKurs(event);
        for (Tab tabPaneKursAnlegen : tabKurseListe.getTabPane().getTabs()) {
            if (tabPaneKursAnlegen.getText().equals("Kurse-Details")) {
                tabPaneKursAnlegen.getTabPane().getSelectionModel().select(tabPaneKursAnlegen);
            }
        }
    }

    @FXML
    void onClickEntfernenButton(ActionEvent event) {
        //ObservableList<Person> allPerson = kvModel.getPersonen().getPersonenListe();
        List<Kurs> selectedKursCopy = new ArrayList<>(tblKurseListe.getSelectionModel().getSelectedItems());
        selectedKursCopy.forEach(kvModel::removeKurse); // ist das Gleiche wie die folgende Zeile
        // for (Person p : selectedPersonCopy) { kvModel.removePerson(p);}
    }
       /* tableKurseListe.setItems(kvModel.getKurse().getKursListe());
        ObservableList<Kurs> kurse = tableKurseListe.getItems();
        List<Kurs> selectedCoursesCopy = new ArrayList<>(tableKurseListe.getSelectionModel().getSelectedItems());
        selectedCoursesCopy.forEach(kurse::remove);*/

    @FXML
    void abDatselectDate(ActionEvent event) {
        pickAbDatum.getValue();
    }

    @FXML
    void bisDatSelectDate(ActionEvent event) {
        pickBisDatum.getValue();
    }

    @FXML
    void onClickcomboStatusKurseListeSelect(ActionEvent event) {
        comboKurseListeStatusSuche.getValue();
    }

    @FXML
    void onClickBearbeitenButton(ActionEvent event) {
        // tableKurseListe.setItems(kvModel.getKurse().getKursListe());
        if (!tblKurseListe.getSelectionModel().isEmpty() && tblKurseListe.getSelectionModel().getSelectedItems().size() < 2) {
            kvModel.aktuellerKurs = tblKurseListe.getSelectionModel().getSelectedItem();
            //KvModel.aktuellerKurs = tableView.getSelectionModel().;
            mainCtrl.fxmlKurseDetailsController.anzeigeZumAendernKurs(kvModel.aktuellerKurs);
            mainCtrl.fxmlKurseDetailsController.show();
        }
    }

    public void resetButtonAction(ActionEvent actionEvent) {
        txInpSuche.clear();
        pickAbDatum.setValue(null);
        pickBisDatum.setValue(null);
        comboKurseListeStatusSuche.setValue("Alle");
        // tableKurseListe.getItems();
    }
}
