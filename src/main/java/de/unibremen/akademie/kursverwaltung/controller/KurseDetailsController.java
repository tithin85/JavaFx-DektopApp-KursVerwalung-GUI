package de.unibremen.akademie.kursverwaltung.controller;

import de.unibremen.akademie.kursverwaltung.application.CreatePdf;
import de.unibremen.akademie.kursverwaltung.application.DatumFormatieren;
import de.unibremen.akademie.kursverwaltung.domain.Kurs;
import de.unibremen.akademie.kursverwaltung.domain.Meldung;
import de.unibremen.akademie.kursverwaltung.domain.Person;
import de.unibremen.akademie.kursverwaltung.domain.PersonKurs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static de.unibremen.akademie.kursverwaltung.domain.AnwendungsModel.kvModel;
import static java.lang.Double.parseDouble;

// TODO: Datumsänderung wird nicht aktualaiesiert.

public class KurseDetailsController {

    // Zeile 293 ist auskommentiert, jetzt muss erstmal der Pfad nicht angepasst werden
    private final String pdfReader = "C:/Program Files/PDF24/pdf24-Reader.exe"; // Anpassen an den jeweiligen PC !!
    private final String pdfSpeicherPfad = "src/main/resources/de/unibremen/akademie/kursverwaltung/pdf/";

    @FXML
    private ComboBox comboStatus;
    @FXML
    public TextField txInpMwsProzent;
    @FXML
    private TextField txInpKursname;
    @FXML
    private TextField txInpAnzahlTage;
    @FXML
    private TextField txInpZyklus;
    @FXML
    private TextField txInpMinTnZahl;
    @FXML
    private TextField txInpMaxTnZahl;
    @FXML
    private TextField txInpAktuelleTnZahl;
    @FXML
    private TextField txInpFreiePlaetze;
    @FXML
    private TextField txInpGebuehrBrutto;
    @FXML
    private TextField txInpGebuehrNetto;
    @FXML
    private TextField txInpMwsEuro;
    @FXML
    private TextArea txAreaKursBeschreibung;
    @FXML
    public DatePicker pickAnwesenheitsDatum;
    @FXML
    private DatePicker pickStartDatum;
    @FXML
    private DatePicker pickEndDatum;
    @FXML
    public HBox hbxPrintAnwesenheitsliste;
    @FXML
    public HBox hbxCsvTeilnehmerliste;
    @FXML
    public Button btnKursSpeichern;
    @FXML
    public Button btnPersonAlsTeilnehmer;
    @FXML
    public Button btnTeilnehmerZuPerson;
    @FXML
    public Button btnPersonAlsInteressent;
    @FXML
    public Button btnInteressentenZuPerson;
    @FXML
    public Button btnInteressentZuTeilnehmer;
    @FXML
    public Button btnTeilnehmerZuInteressent;
    @FXML
    public TableView tblPerson;
    @FXML
    public TableView tblTeilnehmerPerson;
    @FXML
    public TableView tblInteressentenPerson;
    @FXML
    public TableColumn colPersonPersonName;
    @FXML
    public TableColumn colPersonPersonNachName;

    @FXML
    public TableColumn colTeilnahmeKursePersonName;
    @FXML
    public TableColumn colTeilnahmeKursePersonNachName;
    @FXML
    public TableColumn colInteresseKursePersonName;
    @FXML
    public TableColumn colInteresseKursePersonNachName;
    @FXML
    private Tab tabKurseDetails;
    private MainController mainCtrl;
    private Object selectedItem;

    public static boolean checkIsDouble(String wert) {
        return wert.matches("\\d+(\\.|,\\d+)?");
    }

    @FXML
    public void initialize() {
        // Anzeige im deutschen Format, nutzt Klasse DatumFormatieren im Application-Ordner
        DatumFormatieren.datumFormatieren(pickAnwesenheitsDatum);
        DatumFormatieren.datumFormatieren(pickStartDatum);
        DatumFormatieren.datumFormatieren(pickEndDatum);
        pickStartDatum.setPromptText("01.01.1970");
        pickEndDatum.setPromptText("Wird kalkuliert!");

        colPersonPersonName.setCellValueFactory(new PropertyValueFactory<Person, String>("vorname"));
        colPersonPersonName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());

        colPersonPersonNachName.setCellValueFactory(new PropertyValueFactory<Person, String>("nachname"));
        colPersonPersonNachName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());

        TableView.TableViewSelectionModel<Kurs> selectionModel =
                tblPerson.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);


        // TODO Kurs vom Teilnehmer in TeilnahmeKurse anzeigen!!
        colTeilnahmeKursePersonName.setCellValueFactory(new PropertyValueFactory<Person, String>("vorname"));
        colTeilnahmeKursePersonName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colTeilnahmeKursePersonNachName.setCellValueFactory(new PropertyValueFactory<Person, String>("nachname"));
        colTeilnahmeKursePersonNachName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());

        // TODO Kurs vom Interessenten in InteresseKurse anzeigen!!
        colInteresseKursePersonName.setCellValueFactory(new PropertyValueFactory<Person, String>("vorname"));
        colInteresseKursePersonName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        colInteresseKursePersonNachName.setCellValueFactory(new PropertyValueFactory<Person, String>("nachname"));
        colInteresseKursePersonNachName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());

        tblPerson.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkPersonTeilnehmerButton());
        tblTeilnehmerPerson.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkPersonAusTeilnehmerButton());
        tblInteressentenPerson.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkPersonInteressentenButton());

        tblPerson.setItems(kvModel.getPersonen().getPersonenListe());
        checkPersonTeilnehmerButton();
        checkPersonAusTeilnehmerButton();
        checkPersonInteressentenButton();

    }

    private void checkPersonTeilnehmerButton() {
        selectedItem = tblPerson.getSelectionModel().getSelectedItem();
        boolean disable = tblTeilnehmerPerson.getItems().contains(selectedItem) || tblInteressentenPerson.getItems().contains(selectedItem);
        btnPersonAlsTeilnehmer.setDisable(selectedItem == null || disable);
        btnPersonAlsInteressent.setDisable(selectedItem == null || disable);

    }

    private void checkPersonAusTeilnehmerButton() {
        selectedItem = tblTeilnehmerPerson.getSelectionModel().getSelectedItem();
        btnTeilnehmerZuPerson.setDisable(selectedItem == null);
        btnTeilnehmerZuInteressent.setDisable(selectedItem == null);

    }

    // special thanx to chatGPT ;)
    private void pickAnwesenheitsDatumSetzen(LocalDate startDatum, LocalDate endDatum) {
        LocalDate aktuellesDatum = LocalDate.now();
        LocalDate value = startDatum;
        if (aktuellesDatum.isAfter(startDatum)) {
            value = aktuellesDatum;
        }
        pickAnwesenheitsDatum.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(startDatum) || item.isAfter(endDatum)) {
                    setDisable(true);
                    getStyleClass().add("pickAnwesenheitsDatum");
                }
            }
        });
        pickAnwesenheitsDatum.setValue(value);
    }

    public void onClickAbbrechenKurs(ActionEvent actionEvent) {
        felderLeeren();
        /////////// TODO MD
        // umstellung auf StatusVariable?? (siehe personenDetails)
        //kvModel.aktuellerKurs = null;

        if (kvModel.aktuellerKurs != null) {
            Tab plTab = mainCtrl.fxmlKurseListeController.tabKurseListe;
            plTab.getTabPane().getSelectionModel().select(plTab);
        }
    }

    public void teilnehmerlist(ActionEvent actionEvent) {
        for (Tab tabPaneKursListe : tabKurseDetails.getTabPane().getTabs()) {
            if (tabPaneKursListe.getText().equals("Personen-Liste")) {
                tabPaneKursListe.getTabPane().getSelectionModel().select(tabPaneKursListe);
            }
        }
    }

    private void checkPersonInteressentenButton() {
        selectedItem = tblInteressentenPerson.getSelectionModel().getSelectedItem();
        btnInteressentZuTeilnehmer.setDisable(selectedItem == null);
        btnInteressentenZuPerson.setDisable(selectedItem == null);


    }

    public void interessentenlist(ActionEvent actionEvent) {
    }

    public void onDatePickerAction(ActionEvent actionEvent) {
    }

    public void show() {
        tabKurseDetails.getTabPane().getSelectionModel().select(tabKurseDetails);
    }

    public void init(MainController mainController) {
        mainCtrl = mainController;
    }

    public void anzeigeZumAendernKurs(Kurs kurs) {
        if (kurs != null) {
            txInpKursname.setText(kurs.getName());
            comboStatus.setValue(kurs.getStatus());
            txInpAnzahlTage.setText(String.valueOf(kurs.getAnzahlTage()));
            txInpZyklus.setText(String.valueOf(kurs.getZyklus()));
            LocalDate datetolocal = LocalDate.ofInstant(kurs.getStartDatum().toInstant(), ZoneId.of("CET"));
            pickStartDatum.setValue(datetolocal);
            txInpMinTnZahl.setText(String.valueOf(kurs.getMinTnZahl()));
            txInpMaxTnZahl.setText(String.valueOf(kurs.getMaxTnZahl()));
            txInpGebuehrBrutto.setText(String.valueOf(kurs.getGebuehrBrutto()));
            txInpMwsProzent.setText(String.valueOf(kurs.getMwstProzent()));
            //if(kurs.getKursBeschreibung()!=null)
            txAreaKursBeschreibung.setText(kurs.getKursBeschreibung());
            LocalDate datelocal = LocalDate.ofInstant(kurs.getEndeDatum().toInstant(), ZoneId.of("CET"));
            pickEndDatum.setValue(datelocal);
            txInpFreiePlaetze.setText(String.valueOf(kurs.getFreiePlaetze()));
            txInpAktuelleTnZahl.setText(String.valueOf(kurs.getAktuelleTnZahl()));
            txInpMwsEuro.setText(String.valueOf(kurs.getMwstEuro()));
            txInpGebuehrNetto.setText(String.valueOf(kurs.getGebuehrNetto()));
            btnKursSpeichern.setText("Update");
            //h-boxen für Pdf und Csv anzeigen, wenn der Kurs Teilnehmer hat
            if (hatKursTeilnehmer()) {
                // Auswahldatum auf die Dauer des Kurses einschränken
                pickAnwesenheitsDatumSetzen(pickStartDatum.getValue(), pickEndDatum.getValue());
                hbxPrintAnwesenheitsliste.setVisible(true);
                hbxCsvTeilnehmerliste.setVisible(true);
            } else {
                hbxPrintAnwesenheitsliste.setVisible(false);
                hbxCsvTeilnehmerliste.setVisible(false);
            }

            tblTeilnehmerPerson.getItems().clear();
            tblTeilnehmerPerson.getItems().addAll(kvModel.getPkListe().getPersonen(kurs, true));
            tblInteressentenPerson.getItems().clear();
            tblInteressentenPerson.getItems().addAll(kvModel.getPkListe().getPersonen(kurs, false));
        }
    }

    // checks fuer die Umwandlungen beim Auslesen und Zuweisen der GUI-Felder
    public static boolean checkIsInt(String wert) {
        return wert.matches("\\d+");
    }

    // FIXME: status leer gibt keinen Fehlermeldung
    public void onClickSaveKurs(ActionEvent actionEvent) {
        Kurs kurs = null;
        if (kvModel.aktuellerKurs != null) {
            // Bestehenden Kurs aendern
            try {
                kvModel.aktuellerKurs.setName(txInpKursname.getText());
                kvModel.aktuellerKurs.setAnzahlTage((Integer.parseInt(txInpAnzahlTage.getText())));
                kvModel.aktuellerKurs.setZyklus((Integer.parseInt(txInpZyklus.getText())));
                LocalDate localDate = pickStartDatum.getValue();
                kvModel.aktuellerKurs.setStartDatum(Date.from(localDate.atStartOfDay(ZoneId.of("CET")).toInstant()));
                kvModel.aktuellerKurs.setMinTnZahl((Integer.parseInt(txInpMinTnZahl.getText())));
                kvModel.aktuellerKurs.setMaxTnZahl((Integer.parseInt(txInpMaxTnZahl.getText())));
                double gebuehrBrutto = (txInpGebuehrBrutto.getText().contains(",")) ? parseDouble(txInpGebuehrBrutto.getText().replace(",", ".")) : parseDouble(txInpGebuehrBrutto.getText());
                double mwstProzent = (txInpMwsProzent.getText().contains((","))) ? parseDouble(txInpMwsProzent.getText().replace(",", ".")) : parseDouble(txInpMwsProzent.getText());
                kvModel.aktuellerKurs.setGebuehrBrutto(gebuehrBrutto);
                kvModel.aktuellerKurs.setMwstProzent(mwstProzent);
                kvModel.aktuellerKurs.setKursBeschreibung(txAreaKursBeschreibung.getText());
                kvModel.aktuellerKurs.setEndeDatum();
                kvModel.aktuellerKurs.setGebuehrNetto();
                kvModel.aktuellerKurs.setFreiePlaetze();
                kvModel.aktuellerKurs.setMwstEuro();
                kvModel.aktuellerKurs.setAktuelleTnZahl(kvModel.aktuellerKurs.getAktuelleTnZahl());
                kvModel.aktuellerKurs.setStatus(comboStatus.getValue().toString());
                //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                //kvModel.aktuellerKurs.setDisplaystartDate(dateFormat.format(kvModel.aktuellerKurs.getStartDatum()));
                //kvModel.aktuellerKurs.setDisplayEndeDate(dateFormat.format(kvModel.aktuellerKurs.getEndeDatum()));

                if (hatKursTeilnehmer()) {
                    hbxCsvTeilnehmerliste.setVisible(true);
                    hbxPrintAnwesenheitsliste.setVisible(true);
                }

                // TODO Mohammed 04.02

//                kvModel.getPkListe().removeAllKurseAlsTeilnehmer(kvModel.aktuellePerson);
//                kvModel.getPkListe().removeAllKurseAlsInteressent(kvModel.aktuellePerson);
//
//                kvModel.getPkListe().addTeilnehmerInKurs(kvModel.aktuellePerson, this.tableViewTeilnehmerZu.getItems());
//                kvModel.getPkListe().addInteressentInKurs(kvModel.aktuellePerson, this.tableViewInteressentenZu.getItems());

                //  TODO 04.02

                kvModel.getPkListe().addPersonAlsTeilNehmer(kvModel.aktuellerKurs, this.tblTeilnehmerPerson.getItems());
                kvModel.getPkListe().addPersonAlsInteressent(kvModel.aktuellerKurs, this.tblInteressentenPerson.getItems());

            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
                return;
            }
            btnKursSpeichern.setText("Speichern");
            mainCtrl.fxmlKurseListeController.tblKurseListe.refresh();
            mainCtrl.fxmlPersonenDetailsController.tblKurse.refresh();

        } else {
            int anzahlTage, zyklusTage, minTn, maxTn;
            double gebuehrBrutto, mwstProzent;
            LocalDate localDate;
            Date startDate = null;

            // Kurs kurs;

            String kursName = txInpKursname.getText();
            String kursBeschreibung = txAreaKursBeschreibung.getText();
            String kursStatus;

            try {
                if (comboStatus.getSelectionModel().getSelectedIndex() == -1) {
                    throw new IllegalArgumentException("Bitte einen Kurs-Status eingeben");
                } else {
                    kursStatus = comboStatus.getSelectionModel().getSelectedItem().toString();
                }
            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
                return;
            }

            try {
                if (!checkIsInt(txInpAnzahlTage.getText()) ||
                        !checkIsInt(txInpZyklus.getText()) ||
                        !checkIsInt(txInpMinTnZahl.getText()) ||
                        !checkIsInt(txInpMaxTnZahl.getText())) {
                    throw new IllegalArgumentException("Bitte nur ganze Zahlen (1) eingeben!");
                } else {
                    anzahlTage = Integer.parseInt(txInpAnzahlTage.getText());
                    zyklusTage = Integer.parseInt(txInpZyklus.getText());
                    minTn = Integer.parseInt(txInpMinTnZahl.getText());
                    maxTn = Integer.parseInt(txInpMaxTnZahl.getText());
                }

                if (!checkIsDouble(txInpGebuehrBrutto.getText()) ||
                        !checkIsDouble(txInpMwsProzent.getText())) {
                    throw new IllegalArgumentException("Bitte nur Zahlen eingeben!");
                } else {
                    gebuehrBrutto = (txInpGebuehrBrutto.getText().contains(",")) ? parseDouble(txInpGebuehrBrutto.getText().replace(",", ".")) : parseDouble(txInpGebuehrBrutto.getText());
                    mwstProzent = (txInpMwsProzent.getText().contains((","))) ? parseDouble(txInpMwsProzent.getText().replace(",", ".")) : parseDouble(txInpMwsProzent.getText());
                }

                if (!checkIsDate(String.valueOf(pickStartDatum.getValue()))) {
                    throw new IllegalArgumentException("Bitte Datum mit dem DatePicker wählen!");
                } else {
                    localDate = pickStartDatum.getValue();
                    startDate = Date.from(localDate.atStartOfDay(ZoneId.of("CET")).toInstant());
                }
            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
                return;
            }

            // try {
            kurs = kvModel.getKurse().addNewKurs(kursName, anzahlTage, zyklusTage, startDate, minTn, maxTn, gebuehrBrutto, mwstProzent, kursBeschreibung, kursStatus);
            //} catch (Exception e) {
            // Meldung.eingabeFehler(e.getMessage());
            //return;
            // }

            LocalDate datetolocal = LocalDate.ofInstant(kurs.getEndeDatum().toInstant(), ZoneId.of("CET"));
            pickEndDatum.setValue(datetolocal);
            txInpAktuelleTnZahl.setText(String.valueOf(kurs.getAktuelleTnZahl()));
            txInpFreiePlaetze.setText(String.valueOf(kurs.getFreiePlaetze()));
            txInpMwsEuro.setText(String.valueOf(kurs.getMwstEuro()));
            txInpGebuehrNetto.setText(String.valueOf(kurs.getGebuehrNetto()));
            kvModel.getPkListe().addPersonAlsTeilNehmer(kurs, this.tblTeilnehmerPerson.getItems());
            kvModel.getPkListe().addPersonAlsInteressent(kurs, this.tblInteressentenPerson.getItems());

        }
        kvModel.aktuellerKurs = null;

        Tab klTab = mainCtrl.fxmlKurseListeController.tabKurseListe;

        mainCtrl.fxmlKurseListeController.tblKurseListe.refresh();
        mainCtrl.fxmlPersonenListeController.tblPersonenListe.refresh();


        klTab.getTabPane().getSelectionModel().select(klTab);
        mainCtrl.fxmlKurseListeController.tblKurseListe.getSelectionModel().clearSelection();
        mainCtrl.fxmlKurseListeController.tblKurseListe.getSelectionModel().select(kurs);
    }

    public static boolean checkIsDate(String wert) {
        return wert.matches("^\s*((?:20)\\d{2})\\-(1[012]|0?[1-9])\\-(3[01]|[12][0-9]|0?[1-9])\s*$");
    }

    public void onClickPrintAnwesenheitsliste(ActionEvent actionEvent) {
        if (kvModel.aktuellerKurs != null) {
            try {
                LocalDate localDate = pickAnwesenheitsDatum.getValue();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String datumAnwesenheitsliste = localDate.format(formatter);
                new CreatePdf().createAnwesenheitslistePdf(kvModel.aktuellerKurs.getName(), datumAnwesenheitsliste);
                String erstelltesPdf = "Anwesenheitsliste_" + kvModel.aktuellerKurs.getName().replace(" ", "_") + "_" + datumAnwesenheitsliste + ".pdf";
                /*ProcessBuilder pb = new ProcessBuilder(pdfReader, pdfSpeicherPfad + erstelltesPdf);
                Thread.sleep(500); // 1,5 Sekunden warten
                pb.start();*/
                // Nach Druck zurück zur Liste
                Tab plTab = mainCtrl.fxmlKurseListeController.tabKurseListe;
                plTab.getTabPane().getSelectionModel().select(plTab);
            } catch (Exception e) {
                Meldung.eingabeFehler(e.getMessage());
            }
        }
    }

    public boolean hatKursTeilnehmer() {
        int teilnehmendePersonen = 0;
        for (PersonKurs personKurs : kvModel.getPkListe().personKursList) {
            if (personKurs.getKurs().getName().equals(kvModel.aktuellerKurs.getName()) && personKurs.istTeilnehmer()) {
                Person person = personKurs.getPerson();
                teilnehmendePersonen++;
            }
        }
        // todo: Abgleich mit MindestTeilnehmerAnzahl ??
        return teilnehmendePersonen > 0;
    }

    public void onClickCsvTeilnehmerliste(ActionEvent actionEvent) {
        if (hatKursTeilnehmer()) {
            hbxCsvTeilnehmerliste.setVisible(true);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Speichern unter");
            fileChooser.setInitialFileName(kvModel.aktuellerKurs.getName().replace(" ","_"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-Datei (*.csv)", "*.csv"));
            File file = fileChooser.showSaveDialog(mainCtrl.mainStage);
            List<Person> pkListe = kvModel.getPkListe().getPersonAlsTeilnehmer(kvModel.aktuellerKurs);
            if (file != null) {
                try {
                    FileWriter writer = new FileWriter(file);
                    String csvTrenner = pkListe.get(0).getCSVTRENNER();
                    writer.append("Anrede").append(csvTrenner)
                            .append("Titel").append(csvTrenner)
                            .append("Vorname").append(csvTrenner)
                            .append("Nachname").append(csvTrenner)
                            .append("Strasse").append(csvTrenner)
                            .append("PLZ").append(csvTrenner)
                            .append("Ort").append(csvTrenner)
                            .append("E-Mail").append(csvTrenner)
                            .append("telefon").append(csvTrenner)
                            .append(String.valueOf('\n'));
                    for (Person p : pkListe) {
                        writer.append(p.toCsv());
                        writer.append('\n');
                    }
                    writer.flush();
                    writer.close();
                    System.out.println("CSV-Datei wurde erfolgreich gespeichert.");
                } catch (Exception e) {
                    Meldung.eingabeFehler(("Fehler beim Speichern der CSV-Datei: " + e.getMessage()));
                }
            }
        }
    }

    // for test only
    MainController getMainCtrl() {
        return mainCtrl;
    }

    public void onClickPersonZuTeilnehmer(ActionEvent actionEvent) {
        tblTeilnehmerPerson.getItems().add(tblPerson.getSelectionModel().getSelectedItem());
        tblPerson.getSelectionModel().clearSelection();
    }

    public void onClickPersonZuInteressent(ActionEvent actionEvent) {

        tblInteressentenPerson.getItems().add(tblPerson.getSelectionModel().getSelectedItem());
        tblPerson.getSelectionModel().clearSelection();

    }

    public void onClickTeilnehmerZuInteressent(ActionEvent actionEvent) {
        //System.out.println("Teilnehmer zu Interessent!");
        tblInteressentenPerson.getItems().add(tblTeilnehmerPerson.getSelectionModel().getSelectedItem());
        tblTeilnehmerPerson.getItems().removeAll(tblTeilnehmerPerson.getSelectionModel().getSelectedItems());
        tblTeilnehmerPerson.getSelectionModel().clearSelection();


    }

    public void onClickInteressentZuTeilnehmer(ActionEvent actionEvent) {
        tblTeilnehmerPerson.getItems().add(tblInteressentenPerson.getSelectionModel().getSelectedItem());
        tblInteressentenPerson.getItems().removeAll(tblInteressentenPerson.getSelectionModel().getSelectedItems());
        tblInteressentenPerson.getSelectionModel().clearSelection();

    }

    public void onClickPersonRausAusInteressent(ActionEvent actionEvent) {
        tblInteressentenPerson.getItems().removeAll(tblInteressentenPerson.getSelectionModel().getSelectedItem());
        tblInteressentenPerson.getSelectionModel().clearSelection();

    }

    public void onClickPersonRausAusTeilnehmer(ActionEvent actionEvent) {
        tblTeilnehmerPerson.getItems().remove(tblTeilnehmerPerson.getSelectionModel().getSelectedItem());
        tblTeilnehmerPerson.getSelectionModel().clearSelection();
    }

    public void felderLeeren () {
        txInpKursname.clear();
        comboStatus.setValue(comboStatus.getPromptText());
        txInpAnzahlTage.clear();
        txInpZyklus.clear();
        pickStartDatum.setValue(null);
        txInpMinTnZahl.clear();
        txInpMaxTnZahl.clear();
        txInpGebuehrBrutto.clear();
        txInpMwsProzent.clear();
        txAreaKursBeschreibung.clear();
        pickEndDatum.setValue(null);
        txInpFreiePlaetze.clear();
        txInpAktuelleTnZahl.clear();
        txInpMwsEuro.clear();
        txInpGebuehrNetto.clear();
        hbxPrintAnwesenheitsliste.setVisible(false);
        hbxCsvTeilnehmerliste.setVisible(false);
        btnKursSpeichern.setText("Speichern");
    }
}
