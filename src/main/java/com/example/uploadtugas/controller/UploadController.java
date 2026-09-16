package com.example.uploadtugas.controller;

import com.example.uploadtugas.model.Tugas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class UploadController {

    @Value("${upload.dir}")
    private String uploadDir;

    private static final List<Tugas> daftarTugas = new ArrayList<>();

    public static List<Tugas> getDaftarTugas() {
        return daftarTugas;
    }

    @GetMapping("/upload")
    public String formUpload(Model model) {
        model.addAttribute("daftarTugas", daftarTugas);
        model.addAttribute("profil", ProfilController.getProfil());
        model.addAttribute("activePage", "upload");
        return "upload";
    }

    @PostMapping("/upload")
    public String prosesUpload(
            @RequestParam("jenis") String jenis,
            @RequestParam("judul") String judul,
            @RequestParam(value = "deskripsi", required = false) String deskripsi,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "link", required = false) String link,
            Model model) {

        try {
            String fileUrl = null;
            String originalName = null;
            long size = 0;

            if ("link".equals(jenis)) {
                if (link == null || link.isBlank()) {
                    model.addAttribute("error", "URL link wajib diisi!");
                    return reload(model);
                }
                fileUrl = link;
                originalName = link;
            } else {
                if (file == null || file.isEmpty()) {
                    model.addAttribute("error", "File wajib diupload!");
                    return reload(model);
                }
                String contentType = file.getContentType();
                if ("gambar".equals(jenis) && (contentType == null || !contentType.startsWith("image/"))) {
                    model.addAttribute("error", "File harus berupa gambar!");
                    return reload(model);
                }
                if ("video".equals(jenis) && (contentType == null || !contentType.startsWith("video/"))) {
                    model.addAttribute("error", "File harus berupa video!");
                    return reload(model);
                }
                if ("dokumen".equals(jenis)) {
                    String name = file.getOriginalFilename();
                    if (name == null || !(name.toLowerCase().endsWith(".pdf")
                            || name.toLowerCase().endsWith(".doc")
                            || name.toLowerCase().endsWith(".docx")
                            || name.toLowerCase().endsWith(".ppt")
                            || name.toLowerCase().endsWith(".pptx")
                            || name.toLowerCase().endsWith(".xls")
                            || name.toLowerCase().endsWith(".xlsx")
                            || name.toLowerCase().endsWith(".txt"))) {
                        model.addAttribute("error", "File harus berupa dokumen (PDF, DOC, PPT, XLS, TXT)!");
                        return reload(model);
                    }
                }

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                originalName = file.getOriginalFilename();
                String ext = originalName != null && originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf(".")) : "";
                String namaUnik = UUID.randomUUID() + ext;
                Files.copy(file.getInputStream(),
                        uploadPath.resolve(namaUnik),
                        StandardCopyOption.REPLACE_EXISTING);

                fileUrl = "/uploads/" + namaUnik;
                size = file.getSize();
            }

            Tugas tugas = new Tugas(UUID.randomUUID().toString(), jenis, judul,
                    deskripsi == null ? "" : deskripsi,
                    fileUrl, originalName, size);
            daftarTugas.add(tugas);

            model.addAttribute("sukses", "Tugas berhasil diupload!");
        } catch (IOException e) {
            model.addAttribute("error", "Gagal upload: " + e.getMessage());
        }

        return reload(model);
    }

    // Hapus tugas
    @PostMapping("/upload/hapus/{id}")
    public String hapusTugas(@PathVariable String id, Model model) {
        Tugas target = null;
        for (Tugas t : daftarTugas) {
            if (t.getId().equals(id)) { target = t; break; }
        }
        if (target != null) {
            // Hapus file fisik kalau bukan link
            if (!"link".equals(target.getJenis()) && target.getFileUrl() != null) {
                try {
                    String namaFile = target.getFileUrl().replace("/uploads/", "");
                    Path p = Paths.get(uploadDir).resolve(namaFile);
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {}
            }
            daftarTugas.remove(target);
            model.addAttribute("sukses", "Tugas berhasil dihapus!");
        }
        return reload(model);
    }

    private String reload(Model model) {
        model.addAttribute("daftarTugas", daftarTugas);
        model.addAttribute("profil", ProfilController.getProfil());
        model.addAttribute("activePage", "upload");
        return "upload";
    }
}