package com.example.uploadtugas.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.uploadtugas.model.Profil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/profil")
public class ProfilController {

    private static final Profil profil = new Profil();

    @Autowired
    private Cloudinary cloudinary;

    public static Profil getProfil() {
        return profil;
    }

    @GetMapping
    public String halamanProfil(Model model) {
        model.addAttribute("profil", profil);
        model.addAttribute("activePage", "profil");
        return "profil";
    }

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

    @PostMapping("/upload-foto")
    public String uploadFoto(@RequestParam("foto") MultipartFile file, Model model) {
        try {
            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "File harus berupa gambar!");
                } else {
                    Map uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder", "web-upload-tugas/profil",
                                    "resource_type", "image"
                            )
                    );

                    String secureUrl = (String) uploadResult.get("secure_url");
                    profil.setFotoUrl(secureUrl);
                    model.addAttribute("sukses", "Foto profil berhasil diperbarui!");
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Gagal upload: " + e.getMessage());
        }
        model.addAttribute("profil", profil);
        model.addAttribute("activePage", "profil");
        return "profil";
    }

    @GetMapping("/cv")
    public String halamanCv(Model model) {
        model.addAttribute("profil", profil);
        model.addAttribute("activePage", "cv");
        return "cv";
    }
}