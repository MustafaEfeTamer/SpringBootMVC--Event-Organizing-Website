package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.service.EtkinliklerService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KullanicilarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Date;
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
            yeniEtkinlik.setOlusturan(KullanicilarController.kullaniciIdOlusturanIcin);

            etkinliklerService.save(yeniEtkinlik);

            model.addAttribute("registrationSuccess", true);
            return "redirect:/kullanicilar/user";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Geçersiz değer.");
            return "redirect:/kullanicilar/user";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateEventPage(@PathVariable("id") Integer id, Model model) {
        Optional<Etkinlikler> etkinlikOptional = etkinliklerService.findById(id);
        if (etkinlikOptional.isPresent()) {
            model.addAttribute("etkinlik", etkinlikOptional.get());
            return "event-update-page"; // Güncelleme sayfasını render eder
        }
        model.addAttribute("error", "Etkinlik bulunamadı!");
        return "redirect:/kullanicilar/user";
    }


    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable("id") Integer id,
                              @RequestParam("etkinlikAdi") String etkinlikAdi,
                              @RequestParam("aciklama") String aciklama,
                              @RequestParam("etkinlikSuresi") Integer etkinlikSuresi,
                              @RequestParam("konum") String konum,
                              @RequestParam("kategori") String kategori,
                              @RequestParam("saat") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime saat,
                              @RequestParam("tarih") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tarih,
                              @RequestParam("resimUrl") String resimUrl) {
        Optional<Etkinlikler> etkinlikOptional = etkinliklerService.findById(id);
        if (etkinlikOptional.isPresent()) {
            Etkinlikler etkinlik = etkinlikOptional.get();
            etkinlik.setEtkinlikAdi(etkinlikAdi);
            etkinlik.setAciklama(aciklama);
            etkinlik.setEtkinlikSuresi(etkinlikSuresi);
            etkinlik.setKonum(konum);
            etkinlik.setKategori(kategori);
            etkinlik.setSaat(saat);
            etkinlik.setTarih(tarih);
            etkinlik.setResimUrl(resimUrl);

            etkinliklerService.save(etkinlik);
            return "redirect:/kullanicilar/user"; // Güncelleme sonrası kullanıcı sayfasına dön
        }
        return "redirect:/kullanicilar/user"; // Hata durumunda kullanıcı sayfasına dön
    }


    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Integer id, Model model) {
        // Etkinliği sil
        etkinliklerService.deleteById(id);
        return "user";
    }

    @GetMapping("/showEvent/{id}")
    public String showEventPage(@PathVariable("id") Integer id, Model model) {
        Optional<Etkinlikler> etkinlikOptional = etkinliklerService.findById(id);
        if (etkinlikOptional.isPresent()) {
            model.addAttribute("etkinlik", etkinlikOptional.get());
            return "event-page"; // Güncelleme sayfasını render eder
        }
        model.addAttribute("error", "Etkinlik bulunamadı!");
        return "redirect:/kullanicilar/user";
    }
}
