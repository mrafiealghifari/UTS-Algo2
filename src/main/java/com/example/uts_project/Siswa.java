package com.example.uts_project;

public class Siswa {
    private String nama;
    private String nim;
    private double nilai1;
    private double nilai2;
    private double nilai3;

    public Siswa(String nama, String nim, double nilai1, double nilai2, double nilai3){
        this.nama = nama;
        this.nim = nim;
        this.nilai1 = nilai1;
        this.nilai2 = nilai2;
        this.nilai3 = nilai3;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public double getNilai1() {
        return nilai1;
    }

    public void setNilai1(double nilai1) {
        this.nilai1 = nilai1;
    }

    public double getNilai2() {
        return nilai2;
    }

    public void setNilai2(double nilai2) {
        this.nilai2 = nilai2;
    }

    public double getNilai3() {
        return nilai3;
    }

    public void setNilai3(double nilai3) {
        this.nilai3 = nilai3;
    }

    public double getNilaiAkhir(){
      return (nilai1 + nilai2 + nilai3) / 3.0;
    }

    public String toText() {
        return nama + "," + nim + "," + nilai1 + "," + nilai2 + "," + nilai3;
    }
}