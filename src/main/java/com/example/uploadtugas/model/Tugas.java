package com.example.uploadtugas.model;

public class Tugas {
    private String id;
    private String jenis;      // gambar | video | link | dokumen
    private String judul;
    private String deskripsi;
    private String fileUrl;
    private String originalName;
    private long size;

    public Tugas() {}

    public Tugas(String id, String jenis, String judul, String deskripsi,
                 String fileUrl, String originalName, long size) {
        this.id = id;
        this.jenis = jenis;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.fileUrl = fileUrl;
        this.originalName = originalName;
        this.size = size;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
}