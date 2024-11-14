package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import com.springboot.YazLab1_2.ProjeDeneme.service.EtkinliklerService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KullanicilarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/etkinlikler")
public class EtkinliklerController {
    private EtkinliklerService etkinliklerService;
    private KullanicilarService kullanicilarService;

    public EtkinliklerController(EtkinliklerService etkinliklerService, KullanicilarService kullanicilarService) {
        this.etkinliklerService = etkinliklerService;
        this.kullanicilarService = kullanicilarService;
    }

    @PostMapping("/register")
    public String registerEvent(@RequestParam("etkinlikAdi") String etkinlikAdi,
                                @RequestParam("aciklama") String aciklama,
                                @RequestParam("etkinlikSuresi") Integer etkinlikSuresi,
                                @RequestParam("konum") String konum,
                                @RequestParam("kategori") String kategori,
                                @RequestParam("saat") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime saat,
                                @RequestParam("tarih") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tarih,
                                @RequestParam("resimUrl") String resimUrl,
                                Model model) {
        try {
            Etkinlikler yeniEtkinlik = new Etkinlikler();
            yeniEtkinlik.setEtkinlikAdi(etkinlikAdi);
            yeniEtkinlik.setAciklama(aciklama);
            yeniEtkinlik.setEtkinlikSuresi(etkinlikSuresi);
            yeniEtkinlik.setKonum(konum);
            yeniEtkinlik.setKategori(kategori);
            yeniEtkinlik.setSaat(saat);
            yeniEtkinlik.setTarih(tarih);
            yeniEtkinlik.setResimUrl(resimUrl);

            etkinliklerService.save(yeniEtkinlik);

            model.addAttribute("registrationSuccess", true);
            return "redirect:/kullanicilar/user";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Geçersiz değer.");
            return "redirect:/kullanicilar/user";
        }
    }

    @GetMapping("/details/{id}")
    public String showEventDetails(@PathVariable("id") Integer id, Model model) {
        Optional<Etkinlikler> etkinlikOptional = etkinliklerService.findById(id); // Servisten etkinlik detayını alın

        // Etkinlik varsa model'e ekleyin
        Etkinlikler etkinlik = etkinlikOptional.get();
        model.addAttribute("etkinlik", etkinlik);
        return "event-details"; // event-details.html sayfasını render eder
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Integer id, Model model) {
        // Etkinliği sil
        etkinliklerService.deleteById(id);
        return "user";
    }

/*    @CrossOrigin(origins = "http://localhost:2727")
    @DeleteMapping("/delete/{id}")
    @ResponseBody // JSON döndürmek için
    public ResponseEntity<String> deleteEvent(@PathVariable("id") Integer id) {
        System.out.println("efem");
        try {
            etkinliklerService.deleteById(id);
            return ResponseEntity.ok("Etkinlik başarıyla silindi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Etkinlik silinemedi.");
        }
    }*/
}
