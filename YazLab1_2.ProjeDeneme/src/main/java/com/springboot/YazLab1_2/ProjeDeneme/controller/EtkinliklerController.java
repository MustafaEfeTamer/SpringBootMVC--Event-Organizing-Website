package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Puanlar;
import com.springboot.YazLab1_2.ProjeDeneme.service.EtkinliklerService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KatilimcilarService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KullanicilarService;
import com.springboot.YazLab1_2.ProjeDeneme.service.PuanlarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/etkinlikler")
public class EtkinliklerController {
    private EtkinliklerService etkinliklerService;
    private KullanicilarService kullanicilarService;
    private PuanlarService puanlarService;
    private KatilimcilarService katilimcilarService;

    public EtkinliklerController(EtkinliklerService etkinliklerService, KullanicilarService kullanicilarService, PuanlarService puanlarService, KatilimcilarService katilimcilarService) {
        this.puanlarService = puanlarService;
        this.etkinliklerService = etkinliklerService;
        this.kullanicilarService = kullanicilarService;
        this.katilimcilarService = katilimcilarService;
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

            // html sayfasını yeniden düzenliyoruz
            Kullanicilar kullanicilar = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin); // user ekranındaki olusturan id için
            // Kullanıcı resim URL'sini modele ekliyoruz
            model.addAttribute("profileImageUrl", kullanicilar.getProfilFotografi());

            // Kullanıcıya ait etkinlikleri al
            List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(kullanicilar.getId());
            model.addAttribute("userEvents", userEvents);


            // Tüm etkinlikleri al
            List<Etkinlikler> allEvents = etkinliklerService.findAll();

            // Öneri kuralları
            List<Etkinlikler> recommendedEvents = new ArrayList<>();

            // İlgi Alanı Uyum Kuralı
            String[] ilgiAlanlari = kullanicilar.getIlgiAlanlari().split("[, ]+"); // İlgi alanları virgülle ayrılmış olarak varsayılmıştır
            for (Etkinlikler etkinlik : allEvents) {
                String[] etkinlikAciklamasi = etkinlik.getAciklama().split("[, ]+");
                for (String ilgi : ilgiAlanlari) {
                    for(String aciklama2 : etkinlikAciklamasi){
                        if (aciklama2.toLowerCase().contains(ilgi.toLowerCase()) || ilgi.toLowerCase().contains(aciklama2.toLowerCase())) {
                            recommendedEvents.add(etkinlik);
                            break; // Aynı etkinliği birden fazla kez eklememek için
                        }
                    }
                }
            }

            // Katılım Geçmişi Uyum Kuralı
            List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            for(Etkinlikler etkinlikler : allEvents){
                // Katılım Geçmişi Kuralı
                for (Katilimcilar katilim : katilimlar) {
                    Optional<Etkinlikler> etkinlik = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
                    if (etkinlik.isPresent() && !recommendedEvents.contains(etkinlikler) && (etkinlik.get().getKategori().equalsIgnoreCase(etkinlikler.getKategori()))) {
                        recommendedEvents.add(etkinlikler);
                    }
                }
            }


            List<String> istisnaKelimeler = Arrays.asList("Türkiye", "Mah.", "Cad.", "Sok.", "Sit.", "Blok", "Daire");

            // Coğrafi Konum Kuralı
            String[] kullaniciKonumu = kullanicilar.getKonum().split("[, ]+");
            for (Etkinlikler etkinlik : allEvents) {
                String[] etkinlikKonumu = etkinlik.getKonum().split("[, ]+");
                for(String Kkonum : kullaniciKonumu){
                    for (String Ekonum : etkinlikKonumu){
                        if ((Kkonum.equalsIgnoreCase(Ekonum) || Ekonum.equalsIgnoreCase(Kkonum)) && !recommendedEvents.contains(etkinlik)) {
                            if(istisnaKelimeler.contains(Kkonum) || istisnaKelimeler.contains(Ekonum)){
                                continue;
                            }
                            recommendedEvents.add(etkinlik);
                            break;
                        }
                    }
                }
            }

            // Önerilen etkinlikleri modele ekleyin
            model.addAttribute("recommendedEvents", recommendedEvents);


            // etkinliği kaydetmeden önce kullanıcı puanını güncelliyor
            PuanlarController.updatePointForRegister();

            // ekrandaki puan alanına mevcut puanı yazmak için
            Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

            if(puanlarOptional.isPresent()){
                model.addAttribute("userPoints", puanlarOptional.get().getPuan());
            }else{
                model.addAttribute("userPoints", 0);
            }

            model.addAttribute("registrationSuccess", true);
            return "user";
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
                              @RequestParam("resimUrl") String resimUrl,
                              Model model) {
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

            // etkinliği güncellemek için
            etkinliklerService.save(etkinlik);

            // html sayfasını yeniden düzenliyoruz
            Kullanicilar kullanicilar = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin); // user ekranındaki olusturan id için
            // Kullanıcı resim URL'sini modele ekliyoruz
            model.addAttribute("profileImageUrl", kullanicilar.getProfilFotografi());

            // Kullanıcıya ait etkinlikleri al
            List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(kullanicilar.getId());
            model.addAttribute("userEvents", userEvents);


            // Tüm etkinlikleri al
            List<Etkinlikler> allEvents = etkinliklerService.findAll();

            // Öneri kuralları
            List<Etkinlikler> recommendedEvents = new ArrayList<>();

            // İlgi Alanı Uyum Kuralı
            String[] ilgiAlanlari = kullanicilar.getIlgiAlanlari().split("[, ]+"); // İlgi alanları virgülle ayrılmış olarak varsayılmıştır
            for (Etkinlikler etkinlik2 : allEvents) {
                String[] etkinlikAciklamasi = etkinlik2.getAciklama().split("[, ]+");
                for (String ilgi : ilgiAlanlari) {
                    for(String aciklama2 : etkinlikAciklamasi){
                        if (aciklama2.toLowerCase().contains(ilgi.toLowerCase()) || ilgi.toLowerCase().contains(aciklama2.toLowerCase())) {
                            recommendedEvents.add(etkinlik2);
                            break; // Aynı etkinliği birden fazla kez eklememek için
                        }
                    }
                }
            }

            // Katılım Geçmişi Uyum Kuralı
            List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            for(Etkinlikler etkinlikler : allEvents){
                // Katılım Geçmişi Kuralı
                for (Katilimcilar katilim : katilimlar) {
                    Optional<Etkinlikler> etkinlik2 = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
                    if (etkinlik2.isPresent() && !recommendedEvents.contains(etkinlikler) && (etkinlik2.get().getKategori().equalsIgnoreCase(etkinlikler.getKategori()))) {
                        recommendedEvents.add(etkinlikler);
                    }
                }
            }


            List<String> istisnaKelimeler = Arrays.asList("Türkiye", "Mah.", "Cad.", "Sok.", "Sit.", "Blok", "Daire");

            // Coğrafi Konum Kuralı
            String[] kullaniciKonumu = kullanicilar.getKonum().split("[, ]+");
            for (Etkinlikler etkinlik2 : allEvents) {
                String[] etkinlikKonumu = etkinlik2.getKonum().split("[, ]+");
                for(String Kkonum : kullaniciKonumu){
                    for (String Ekonum : etkinlikKonumu){
                        if ((Kkonum.equalsIgnoreCase(Ekonum) || Ekonum.equalsIgnoreCase(Kkonum)) && !recommendedEvents.contains(etkinlik2)) {
                            if(istisnaKelimeler.contains(Kkonum) || istisnaKelimeler.contains(Ekonum)){
                                continue;
                            }
                            recommendedEvents.add(etkinlik2);
                            break;
                        }
                    }
                }
            }

            // Önerilen etkinlikleri modele ekleyin
            model.addAttribute("recommendedEvents", recommendedEvents);


            // ekrandaki puan alanına mevcut puanı yazmak için
            Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

            if(puanlarOptional.isPresent()){
                model.addAttribute("userPoints", puanlarOptional.get().getPuan());
            }else{
                model.addAttribute("userPoints", 0);
            }

            return "user"; // Güncelleme sonrası kullanıcı sayfasına dön
        }
        return "redirect:/kullanicilar/user"; // Hata durumunda kullanıcı sayfasına dön
    }



    @PostMapping("/updateAdmin/{id}")
    public String updateEventAdmin(@PathVariable("id") Integer id,
                              @RequestParam("etkinlikAdi") String etkinlikAdi,
                              @RequestParam("aciklama") String aciklama,
                              @RequestParam("etkinlikSuresi") Integer etkinlikSuresi,
                              @RequestParam("konum") String konum,
                              @RequestParam("kategori") String kategori,
                              @RequestParam("saat") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime saat,
                              @RequestParam("tarih") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tarih,
                              @RequestParam("resimUrl") String resimUrl,
                              Model model) {
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

            // etkinliği güncellemek için
            etkinliklerService.save(etkinlik);

                model.addAttribute("etkinlik", etkinlik);

            return "event-page-admin"; // Güncelleme sonrası kullanıcı sayfasına dön
        }
        return "redirect:/kullanicilar/user"; // Hata durumunda kullanıcı sayfasına dön
    }


    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Integer id, Model model) {
        // etkinliği silmek için
        etkinliklerService.deleteById(id);

        // html sayfasını yeniden düzenliyoruz
        Kullanicilar kullanicilar = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin); // user ekranındaki olusturan id için
        // Kullanıcı resim URL'sini modele ekliyoruz
        model.addAttribute("profileImageUrl", kullanicilar.getProfilFotografi());

        // Kullanıcıya ait etkinlikleri al
        List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(kullanicilar.getId());
        model.addAttribute("userEvents", userEvents);



        // Tüm etkinlikleri al
        List<Etkinlikler> allEvents = etkinliklerService.findAll();

        // Öneri kuralları
        List<Etkinlikler> recommendedEvents = new ArrayList<>();

        // İlgi Alanı Uyum Kuralı
        String[] ilgiAlanlari = kullanicilar.getIlgiAlanlari().split("[, ]+"); // İlgi alanları virgülle ayrılmış olarak varsayılmıştır
        for (Etkinlikler etkinlik : allEvents) {
            String[] etkinlikAciklamasi = etkinlik.getAciklama().split("[, ]+");
            for (String ilgi : ilgiAlanlari) {
                for(String aciklama : etkinlikAciklamasi){
                    if (aciklama.toLowerCase().contains(ilgi.toLowerCase()) || ilgi.toLowerCase().contains(aciklama.toLowerCase())) {
                        recommendedEvents.add(etkinlik);
                        break; // Aynı etkinliği birden fazla kez eklememek için
                    }
                }
            }
        }

        // Katılım Geçmişi Uyum Kuralı
        List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
        for(Etkinlikler etkinlikler : allEvents){
            // Katılım Geçmişi Kuralı
            for (Katilimcilar katilim : katilimlar) {
                Optional<Etkinlikler> etkinlik = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
                if (etkinlik.isPresent() && !recommendedEvents.contains(etkinlikler) && (etkinlik.get().getKategori().equalsIgnoreCase(etkinlikler.getKategori()))) {
                    recommendedEvents.add(etkinlikler);
                }
            }
        }


        List<String> istisnaKelimeler = Arrays.asList("Türkiye", "Mah.", "Cad.", "Sok.", "Sit.", "Blok", "Daire");

        // Coğrafi Konum Kuralı
        String[] kullaniciKonumu = kullanicilar.getKonum().split("[, ]+");
        for (Etkinlikler etkinlik : allEvents) {
            String[] etkinlikKonumu = etkinlik.getKonum().split("[, ]+");
            for(String Kkonum : kullaniciKonumu){
                for (String Ekonum : etkinlikKonumu){
                    if ((Kkonum.equalsIgnoreCase(Ekonum) || Ekonum.equalsIgnoreCase(Kkonum)) && !recommendedEvents.contains(etkinlik)) {
                        if(istisnaKelimeler.contains(Kkonum) || istisnaKelimeler.contains(Ekonum)){
                            continue;
                        }
                        recommendedEvents.add(etkinlik);
                        break;
                    }
                }
            }
        }

        // Önerilen etkinlikleri modele ekleyin
        model.addAttribute("recommendedEvents", recommendedEvents);


        // ekrandaki puan alanına mevcut puanı yazmak için
        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

        if(puanlarOptional.isPresent()){
            model.addAttribute("userPoints", puanlarOptional.get().getPuan());
        }else{
            model.addAttribute("userPoints", 0);
        }

        return "user"; // Aynı sayfada başarılı mesajını gösterir
    }

    @GetMapping("/deleteAdmin/{id}")
    public String deleteEventAdmin(@PathVariable("id") Integer id, Model model) {
        // etkinliği silmek için
        etkinliklerService.deleteById(id);

        // Tüm etkinlikleri al
        List<Etkinlikler> allEvents = etkinliklerService.findAll();
        model.addAttribute("allEvents", allEvents);

        return "admin"; // Aynı sayfada başarılı mesajını gösterir
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

    @GetMapping("/showEventAdmin/{id}")
    public String showEventPageAdmin(@PathVariable("id") Integer id, Model model) {
        Optional<Etkinlikler> etkinlikOptional = etkinliklerService.findById(id);
        if (etkinlikOptional.isPresent()) {
            model.addAttribute("etkinlik", etkinlikOptional.get());
            return "event-page-admin"; // Güncelleme sayfasını render eder
        }
        model.addAttribute("error", "Etkinlik bulunamadı!");
        return "admin";
    }
}
