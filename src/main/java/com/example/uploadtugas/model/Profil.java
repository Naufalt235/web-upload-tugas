package com.example.uploadtugas.model;

public class Profil {
    private String namaDepan = "Naufal";
    private String namaBelakang = "Ramadhan";
    private String nim = "1247050103";
    private String fotoUrl = "https://ui-avatars.com/api/?name=Naufal+Ramadhan&background=6366f1&color=fff&size=200";

    public Profil() {}

    public String getNamaDepan() { return namaDepan; }
    public void setNamaDepan(String namaDepan) { this.namaDepan = namaDepan; }

    public String getNamaBelakang() { return namaBelakang; }
    public void setNamaBelakang(String namaBelakang) { this.namaBelakang = namaBelakang; }

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getNamaLengkap() {
        return namaDepan + " " + namaBelakang;
    }
}