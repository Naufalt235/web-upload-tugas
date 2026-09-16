package com.example.uploadtugas.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.uploadtugas.model.Tugas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UploadController {

    @Autowired
    private Cloudinary cloudinary;

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
            String publicId = null;
            String originalName = null;
            long size = 0;

            // ===== JENIS: LINK =====
            if ("link".equals(jenis)) {
                if (link == null || link.isBlank()) {
                    model.addAttribute("error", "URL link wajib diisi!");
                    return reload(model);
                }
                fileUrl = link;
                originalName = link;
            } else {
                // ===== JENIS: FILE (gambar, video, dokumen, audio) =====
                if (file == null || file.isEmpty()) {
                    model.addAttribute("error", "File wajib diupload!");
                    return reload(model);
                }

                String contentType = file.getContentType();
                String name = file.getOriginalFilename();
                String lowerName = name != null ? name.toLowerCase() : "";

                // Validasi tipe file
                if ("gambar".equals(jenis) && (contentType == null || !contentType.startsWith("image/"))) {
                    model.addAttribute("error", "File harus berupa gambar!");
                    return reload(model);
                }
                if ("video".equals(jenis) && (contentType == null || !contentType.startsWith("video/"))) {
                    model.addAttribute("error", "File harus berupa video!");
                    return reload(model);
                }
                if ("audio".equals(jenis) && (contentType == null || !contentType.startsWith("audio/"))) {
                    model.addAttribute("error", "File harus berupa audio!");
                    return reload(model);
                }
                if ("dokumen".equals(jenis)) {
                    if (!(lowerName.endsWith(".pdf")
                            || lowerName.endsWith(".doc")
                            || lowerName.endsWith(".docx")
                            || lowerName.endsWith(".ppt")
                            || lowerName.endsWith(".pptx")
                            || lowerName.endsWith(".xls")
                            || lowerName.endsWith(".xlsx")
                            || lowerName.endsWith(".txt"))) {
                        model.addAttribute("error", "File harus berupa dokumen (PDF, DOC, PPT, XLS, TXT)!");
                        return reload(model);
                    }
                }

                // ===== TENTUKAN resource_type & format untuk Cloudinary =====
                String resourceType = "auto";
                String format = null;

                if ("gambar".equals(jenis)) {
                    resourceType = "image";
                } else if ("video".equals(jenis)) {
                    resourceType = "video";
                } else if ("audio".equals(jenis)) {
                    resourceType = "video"; // Cloudinary handle audio sebagai video
                } else if ("dokumen".equals(jenis)) {
                    if (lowerName.endsWith(".pdf")) {
                        resourceType = "image"; // PDF → image (biar ada .pdf)
                    } else {
                        resourceType = "raw";
                        // Ambil ekstensi untuk parameter format
                        if (lowerName.contains(".")) {
                            format = lowerName.substring(lowerName.lastIndexOf(".") + 1);
                        }
                    }
                }

                // ===== UPLOAD KE CLOUDINARY =====
                Map<String, Object> options = new HashMap<>();
                options.put("folder", "web-upload-tugas/tugas");
                options.put("resource_type", resourceType);
                options.put("use_filename", true);
                options.put("unique_filename", true);

                // Tambahkan format (untuk raw dokumen: txt, docx, pptx, xlsx)
                if (format != null) {
                    options.put("format", format);
                }

                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

                fileUrl = (String) uploadResult.get("secure_url");
                publicId = (String) uploadResult.get("public_id");
                originalName = file.getOriginalFilename();
                size = file.getSize();
            }

            Tugas tugas = new Tugas(UUID.randomUUID().toString(), jenis, judul,
                    deskripsi == null ? "" : deskripsi,
                    fileUrl, originalName, size);
            tugas.setPublicId(publicId);
            daftarTugas.add(tugas);

            model.addAttribute("sukses", "Tugas berhasil diupload!");
        } catch (Exception e) {
            model.addAttribute("error", "Gagal upload: " + e.getMessage());
        }

        return reload(model);
    }

    // ===== HAPUS TUGAS =====
    @PostMapping("/upload/hapus/{id}")
    public String hapusTugas(@PathVariable String id, Model model) {
        Tugas target = null;
        for (Tugas t : daftarTugas) {
            if (t.getId().equals(id)) { target = t; break; }
        }
        if (target != null) {
            if (!"link".equals(target.getJenis()) && target.getPublicId() != null) {
                try {
                    String resourceType = "auto";
                    String jenis = target.getJenis();

                    if ("gambar".equals(jenis)) {
                        resourceType = "image";
                    } else if ("video".equals(jenis)) {
                        resourceType = "video";
                    } else if ("audio".equals(jenis)) {
                        resourceType = "video";
                    } else if ("dokumen".equals(jenis)) {
                        String name = target.getOriginalName();
                        String lowerName = name != null ? name.toLowerCase() : "";
                        if (lowerName.endsWith(".pdf")) {
                            resourceType = "image";
                        } else {
                            resourceType = "raw";
                        }
                    }

                    cloudinary.uploader().destroy(
                            target.getPublicId(),
                            ObjectUtils.asMap("resource_type", resourceType)
                    );
                } catch (Exception ignored) {}
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