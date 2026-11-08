package kelas.controller;

import java.io.IOException;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import kelas.Navigator;
import kelas.data.DataProduk;
import kelas.model.Produk;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableCell;

public class DaftarController {

    @FXML
    private TableView<Produk> tabel;
    @FXML
    private TableColumn<Produk, String> kodeCol;
    @FXML
    private TableColumn<Produk, String> namaCol;
    @FXML
    private TableColumn<Produk, String> kategoriCol;
    @FXML
    private TableColumn<Produk, Long> hargaCol;
    @FXML
    private TableColumn<Produk, String> uriCol;
    @FXML
    private TextField cariField;
    @FXML
    private ComboBox<String> filterKategoriBox;
    @FXML
    private CheckBox hargaCheck;
    @FXML
    private Label pesanLabel;

    private Navigator navigator;
    private DataProduk data;
    private final ObservableList<Produk> dataTampilan
            = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // KOLOM KODE
        PropertyValueFactory<Produk, String> sumberKode
                = new PropertyValueFactory<Produk, String>("kode");
        kodeCol.setCellValueFactory(sumberKode);

        // KOLOM NAMA PRODUK
        PropertyValueFactory<Produk, String> sumberNama
                = new PropertyValueFactory<Produk, String>("nama");
        namaCol.setCellValueFactory(sumberNama);

        // KOLOM KATEGORI PRODUK
        PropertyValueFactory<Produk, String> sumberKategori
                = new PropertyValueFactory<Produk, String>("kategori");
        kategoriCol.setCellValueFactory(sumberKategori);

        // KOLOM HARGA
        PropertyValueFactory<Produk, Long> sumberHarga
                = new PropertyValueFactory<Produk, Long>("harga");
        hargaCol.setCellValueFactory(sumberHarga);
        
        // KOLOM GAMBAR (URI)
        PropertyValueFactory<Produk, String> sumberUri
                = new PropertyValueFactory<Produk, String>("uri");
        uriCol.setCellValueFactory(sumberUri);

        // Mengubah teks URI menjadi tampilan Gambar
        uriCol.setCellFactory(column -> new TableCell<Produk, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String uri, boolean empty) {
                super.updateItem(uri, empty);
                if (empty || uri == null || uri.isEmpty()) {
                    setGraphic(null);
                } else {
                    Image image = new Image(uri, 250, 250, true, true);
                    imageView.setImage(image);
                    imageView.fitWidthProperty().bind(column.widthProperty().subtract(10));
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);
                }
                setText(null);
            }
        });

        filterKategoriBox.getItems().add("Semua");
        filterKategoriBox.getItems().add("Makanan");
        filterKategoriBox.getItems().add("Minuman");
        filterKategoriBox.setValue("Semua");

        tabel.setItems(dataTampilan);
        tabel.setFixedCellSize(100);
        tabel.setPlaceholder(new Label("Belum ada data produk yang sesuai."));
        ubahTampilanHarga();
    }

    public void setKonteks(Navigator navigator, DataProduk data) {
        this.navigator = navigator;
        this.data = data;
        cari();
    }

    @FXML
    private void cari() {
        if (data == null) {
            return;
        }
        dataTampilan.clear();
        String kata = cariField.getText().trim().toLowerCase(Locale.ROOT);
        String kategoriDipilih = filterKategoriBox.getValue();

        for (Produk produk : data.getDaftar()) {
            String nama = produk.getNama().toLowerCase(Locale.ROOT);
            boolean cocokKata = false;
            boolean cocokKategori = false;

            if (nama.contains(kata)) {
                cocokKata = true;
            }
            if ("Semua".equals(kategoriDipilih)) {
                cocokKategori = true;
            } else if (produk.getKategori().equals(kategoriDipilih)) {
                cocokKategori = true;
            }
            if (cocokKata && cocokKategori) {
                dataTampilan.add(produk);
            }
        }
        tabel.sort();
        pesanLabel.setText("Jumlah produk yang tampil: " + dataTampilan.size());
    }

    @FXML
    private void resetPencarian() {
        cariField.clear();
        filterKategoriBox.setValue("Semua");
        cari();
    }

    @FXML
    private void ubahTampilanHarga() {
        if (hargaCheck.isSelected()) {
            hargaCol.setVisible(true);
        } else {
            hargaCol.setVisible(false);
        }
    }

    @FXML
    private void tambah() {
        try {
            navigator.bukaForm();
        } catch (IOException e) {
            pesanLabel.setText("Form gagal dibuka.");
            e.printStackTrace();
        }
    }

    @FXML
    private void detail() {
        Produk produk = tabel.getSelectionModel().getSelectedItem();
        if (produk == null) {
            pesanLabel.setText("Pilih satu baris produk dahulu.");
            return;
        }
        try {
            navigator.bukaDetail(produk);
        } catch (IOException e) {
            pesanLabel.setText("Detail gagal dibuka.");
            e.printStackTrace();
        }
    }

    @FXML
    private void hapus() {
        Produk produk = tabel.getSelectionModel().getSelectedItem();
        if (produk == null) {
            pesanLabel.setText("Pilih satu baris produk dahulu.");
            return;
        }
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus produk " + produk.getKode() + " (" + produk.getNama() + ")?",
                ButtonType.YES, ButtonType.NO);
        dialog.initOwner(tabel.getScene().getWindow());
        ButtonType jawaban = dialog.showAndWait().orElse(ButtonType.NO);
        if (jawaban == ButtonType.YES) {
            data.hapus(produk);
            cari();
        }
    }
}
