package com.example.uploadtugas.controller;

import com.example.uploadtugas.model.Profil;
import com.example.uploadtugas.model.Tugas;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        List<Tugas> daftar = UploadController.getDaftarTugas();
        model.addAttribute("daftarTugas", daftar);
        model.addAttribute("profil", ProfilController.getProfil());
        model.addAttribute("activePage", "home");
        return "index";
    }
}