package kelas.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kelas.model.Produk;

public class DataProduk {

    private final ObservableList<Produk> daftar = FXCollections.observableArrayList();
    private int urutan = 1;

    public ObservableList<Produk> getDaftar() {
        return FXCollections.unmodifiableObservableList(daftar);
    }

    public Produk tambah(String nama, String kategori, long harga, String uri) {
        // PR%03d Bagian ini digunakan untuk membuat kode produk otomatis, misalnya PR001, PR002, dan seterusnya.
        Produk p = new Produk(String.format("PR%03d", urutan), nama, kategori, harga, uri);
        daftar.add(p);
        urutan++;
        return p;
    }

    public void hapus(Produk p) {
        daftar.remove(p);
    }
}
