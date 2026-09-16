package com.example.uploadtugas.controller;

import com.example.uploadtugas.model.Profil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/profil")
public class ProfilController {

    private static final Profil profil = new Profil();

    @Value("${upload.dir}")
    private String uploadDir;

    public static Profil getProfil() {
        return profil;
    }

    // Halaman profil — form edit
    @GetMapping
    public String halamanProfil(Model model) {
        model.addAttribute("profil", profil);
        model.addAttribute("activePage", "profil");
        return "profil";
    }

    // Update nama & NIM
    @PostMapping("/update")
    public String updateProfil(
            @RequestParam("namaDepan") String namaDepan,
            @RequestParam("namaBelakang") String namaBelakang,
            @RequestParam("nim") String nim,
            Model model) {
        profil.setNamaDepan(namaDepan);
        profil.setNamaBelakang(namaBelakang);
        profil.setNim(nim);
        model.addAttribute("profil", profil);
        model.addAttribute("sukses", "Profil berhasil diperbarui!");
        model.addAttribute("activePage", "profil");
        return "profil";
    }

    // Upload foto profil
    @PostMapping("/upload-foto")
    public String uploadFoto(@RequestParam("foto") MultipartFile file, Model model) {
        try {
            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "File harus berupa gambar!");
                } else {
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                    String original = file.getOriginalFilename();
                    String ext = original != null && original.contains(".")
                            ? original.substring(original.lastIndexOf(".")) : ".jpg";
                    String namaUnik = "profil_" + UUID.randomUUID() + ext;
                    Files.copy(file.getInputStream(),
                            uploadPath.resolve(namaUnik),
                            StandardCopyOption.REPLACE_EXISTING);

                    profil.setFotoUrl("/uploads/" + namaUnik);
                    model.addAttribute("sukses", "Foto profil berhasil diperbarui!");
                }
            }
        } catch (IOException e) {
            model.addAttribute("error", "Gagal upload: " + e.getMessage());
        }
        model.addAttribute("profil", profil);
        model.addAttribute("activePage", "profil");
        return "profil";
    }

    // Halaman CV
    @GetMapping("/cv")
    public String halamanCv(Model model) {
        model.addAttribute("profil", profil);
        model.addAttribute("activePage", "cv");
        return "cv";
    }
}