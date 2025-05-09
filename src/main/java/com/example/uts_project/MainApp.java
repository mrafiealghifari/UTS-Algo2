package com.example.uts_project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class MainApp extends Application {

    private final List<Siswa> daftarSiswa = new ArrayList<>();
    private final ListView<String> listView = new ListView<>();
    private final TextField inputCari = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        bacaDataSiswa();
        tampilkanDaftar();

        Button btnUrut = new Button("Urutkan Nilai");
        Button btnCari = new Button("Cari Nama");
        Button btnRata = new Button("Rata-rata & Jumlah");
        Button btnEdit = new Button("Ganti Nama");
        Button btnHapus = new Button("Hapus Data");
        Button btnTambah = new Button("Tambah Siswa");

        inputCari.setPromptText("Nama siswa");

        btnUrut.setOnAction(_ -> {
            daftarSiswa.sort((a, b) -> Double.compare(b.getNilaiAkhir(), a.getNilaiAkhir()));
            tampilkanDaftar();
        });

        btnCari.setOnAction(_ -> {
            daftarSiswa.sort(Comparator.comparing(Siswa::getNama));
            String cari = inputCari.getText().trim();
            int index = binarySearch(daftarSiswa, cari);
            if (index >= 0) {
                listView.getItems().clear();
                listView.getItems().add(daftarSiswa.get(index).toString());
            } else {
                showAlert("Siswa tidak ditemukan");
            }
        });

        btnRata.setOnAction(_ -> {
            double total = totalNilaiRekursif(daftarSiswa, 0);
            double rata = daftarSiswa.isEmpty() ? 0 : total / daftarSiswa.size();
            showAlert("Total Nilai: " + total + "\nRata-rata: " + rata);
        });

        btnEdit.setOnAction(_ -> {
            daftarSiswa.sort(Comparator.comparing(Siswa::getNama));
            String cari = inputCari.getText().trim();
            int index = binarySearch(daftarSiswa, cari);
            if (index >= 0) {
                TextInputDialog dialog = new TextInputDialog(daftarSiswa.get(index).getNama());
                dialog.setHeaderText(null);
                dialog.setContentText("Nama Baru:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(namaBaru -> {
                    daftarSiswa.get(index).setNama(namaBaru);
                    tampilkanDaftar();
                    simpanDataKeFile();
                });
            } else {
                showAlert("Siswa tidak ditemukan");
            }
        });

        btnHapus.setOnAction(_ -> {
            daftarSiswa.sort(Comparator.comparing(Siswa::getNama));
            String cari = inputCari.getText().trim();
            int index = binarySearch(daftarSiswa, cari);
            if (index >= 0) {
                daftarSiswa.remove(index);
                tampilkanDaftar();
                simpanDataKeFile();
                showAlert("Data siswa berhasil dihapus");
            } else {
                showAlert("Siswa tidak ditemukan");
            }
        });

        btnTambah.setOnAction(_ -> {
            Dialog<Siswa> dialog = new Dialog<>();
            dialog.setTitle("Tambah Siswa Baru");

            TextField inputNama = new TextField();
            TextField inputNIM = new TextField();
            TextField inputMat = new TextField();
            TextField inputIPA = new TextField();
            TextField inputBhs = new TextField();

            inputNama.setPromptText("Nama");
            inputNIM.setPromptText("NIM");
            inputMat.setPromptText("Nilai Matematika");
            inputIPA.setPromptText("Nilai IPA");
            inputBhs.setPromptText("Nilai Bahasa");

            VBox form = new VBox(5, new Label("Nama:"), inputNama, new Label("NIM:"), inputNIM,
                    new Label("Matematika:"), inputMat, new Label("IPA:"), inputIPA, new Label("Bahasa:"), inputBhs);
            dialog.getDialogPane().setContent(form);

            ButtonType tambahButtonType = new ButtonType("Tambah", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(tambahButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == tambahButtonType) {
                    try {
                        String nama = inputNama.getText().trim();
                        String nim = inputNIM.getText().trim();
                        double mat = Double.parseDouble(inputMat.getText().trim());
                        double ipa = Double.parseDouble(inputIPA.getText().trim());
                        double bhs = Double.parseDouble(inputBhs.getText().trim());
                        return new Siswa(nama, nim, mat, ipa, bhs);
                    } catch (NumberFormatException e) {
                        showAlert("Input nilai harus berupa angka.");
                    }
                }
                return null;
            });

            Optional<Siswa> result = dialog.showAndWait();
            result.ifPresent(siswaBaru -> {
                daftarSiswa.add(siswaBaru);
                tampilkanDaftar();
                simpanDataKeFile();
            });
        });

        VBox root = new VBox(10, inputCari, listView, btnUrut, btnCari, btnRata, btnEdit, btnHapus, btnTambah);
        root.setStyle("-fx-padding: 10;");
        stage.setScene(new Scene(root, 600, 550));
        stage.setTitle("Aplikasi Nilai Siswa");
        stage.show();
    }

    private void bacaDataSiswa() {
        File file = new File("siswa.txt");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length == 5) {
                    String nama = d[0], nim = d[1];
                    double matematika = Double.parseDouble(d[2]);
                    double ipa = Double.parseDouble(d[3]);
                    double bhs = Double.parseDouble(d[4]);
                    daftarSiswa.add(new Siswa(nama, nim, matematika, ipa, bhs));
                }
            }
        } catch (IOException e) {
            showAlert("Gagal membaca file: " + e.getMessage());
        }
    }

    private void simpanDataKeFile() {
        try (PrintWriter writer = new PrintWriter("siswa.txt")) {
            for (Siswa s : daftarSiswa) {
                writer.println(s.getNama() + "," + s.getNim() + "," + s.getMatematika() + "," + s.getIpa() + "," + s.getBahasa());
            }
        } catch (IOException e) {
            showAlert("Gagal menyimpan ke file: " + e.getMessage());
        }
    }

    private void tampilkanDaftar() {
        listView.getItems().clear();
        if (daftarSiswa.isEmpty()) {
            listView.getItems().add("Tidak ada data siswa.");
        } else {
            for (Siswa s : daftarSiswa) {
                listView.getItems().add(s.toString());
            }
        }
    }

    private int binarySearch(List<Siswa> list, String target) {
        int kiri = 0, kanan = list.size() - 1;
        while (kiri <= kanan) {
            int mid = (kiri + kanan) / 2;
            int cmp = list.get(mid).getNama().compareToIgnoreCase(target);
            if (cmp == 0) return mid;
            else if (cmp < 0) kiri = mid + 1;
            else kanan = mid - 1;
        }
        return -1;
    }

    private double totalNilaiRekursif(List<Siswa> list, int i) {
        if (i >= list.size()) return 0;
        return list.get(i).getNilaiAkhir() + totalNilaiRekursif(list, i + 1);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class Siswa {
        private String nama;
        private final String nim;
        private final double matematika;
        private final double ipa;
        private final double bahasa;

        public Siswa(String nama, String nim, double matematika, double ipa, double bahasa) {
            this.nama = nama;
            this.nim = nim;
            this.matematika = matematika;
            this.ipa = ipa;
            this.bahasa = bahasa;
        }

        public String getNama() { return nama; }
        public void setNama(String nama) { this.nama = nama; }
        public String getNim() { return nim; }
        public double getMatematika() { return matematika; }
        public double getIpa() { return ipa; }
        public double getBahasa() { return bahasa; }
        public double getNilaiAkhir() {
            return (matematika + ipa + bahasa) / 3;
        }

        @Override
        public String toString() {
            return nama + " | " + nim + " | Mat: " + matematika + ", IPA: " + ipa + ", Bhs: " + bahasa +
                    " | Nilai Akhir: " + String.format("%.2f", getNilaiAkhir());
        }
    }
}