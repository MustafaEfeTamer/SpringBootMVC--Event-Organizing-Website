package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.entity.*;
import com.springboot.YazLab1_2.ProjeDeneme.service.*;
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
    private OnaylanmamisEtkinliklerService onaylanmamisEtkinliklerService;


    public EtkinliklerController(EtkinliklerService etkinliklerService, KullanicilarService kullanicilarService, PuanlarService puanlarService, KatilimcilarService katilimcilarService, OnaylanmamisEtkinliklerService onaylanmamisEtkinliklerService) {
        this.puanlarService = puanlarService;
        this.etkinliklerService = etkinliklerService;
        this.kullanicilarService = kullanicilarService;
        this.katilimcilarService = katilimcilarService;
        this.onaylanmamisEtkinliklerService = onaylanmamisEtkinliklerService;
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
            OnaylanmamisEtkinlikler yeniEtkinlik = new OnaylanmamisEtkinlikler();
            yeniEtkinlik.setEtkinlikAdi(etkinlikAdi);
            yeniEtkinlik.setAciklama(aciklama);
            yeniEtkinlik.setEtkinlikSuresi(etkinlikSuresi);
            yeniEtkinlik.setKonum(konum);
            yeniEtkinlik.setKategori(kategori);
            yeniEtkinlik.setSaat(saat);
            yeniEtkinlik.setTarih(tarih);
            yeniEtkinlik.setResim(resimUrl);
            yeniEtkinlik.setOlusturan(KullanicilarController.kullaniciIdOlusturanIcin);

            // Onay bekleyen etkinlikler tabloma kaydet
            onaylanmamisEtkinliklerService.save(yeniEtkinlik);

            // html sayfasını yeniden düzenliyoruz
            Kullanicilar kullanicilar = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin); // user ekranındaki olusturan id için
            // Kullanıcı resim URL'sini modele ekliyoruz
            model.addAttribute("profileImageUrl", kullanicilar.getProfilFotografi());

            // Kullanıcıya ait etkinlikleri al
            List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(kullanicilar.getId());
            model.addAttribute("userEvents", userEvents);


            // Önerilen etkinlikleri modele ekleyin
            model.addAttribute("recommendedEvents", recommendedEvents());


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

    List<Etkinlikler> recommendedEvents(){
        Kullanicilar kullanicilar = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin); // user ekranındaki olusturan id için

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
        return recommendedEvents;
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


            // Önerilen etkinlikleri modele ekleyin
            model.addAttribute("recommendedEvents", recommendedEvents());


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

        // Önerilen etkinlikleri modele ekleyin
        model.addAttribute("recommendedEvents", recommendedEvents());


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

        // Onay Bekleyen Etkinlikleri Al
        List<OnaylanmamisEtkinlikler> unapprovedEvents = onaylanmamisEtkinliklerService.findAll();
        model.addAttribute("unapprovedEvents", unapprovedEvents);

        // Tüm etkinlikleri al
        List<Etkinlikler> allEvents = etkinliklerService.findAll();
        model.addAttribute("allEvents", allEvents);


        // Tüm kullanıcıları al
        List<Kullanicilar> allUsers = kullanicilarService.findAll();
        model.addAttribute("allUsers", allUsers);

        // Etkinliği oluşturan kişiyi al
        List<String> olusturanAdlari = new ArrayList<>();
        for (Etkinlikler event : allEvents) {
            Kullanicilar olusturan = kullanicilarService.findByKullaniciId(event.getOlusturan());
            if (olusturan != null) { // Kullanıcı bulunduğundan emin olun
                olusturanAdlari.add(olusturan.getAd());
            } else {
                olusturanAdlari.add("Bilinmiyor"); // Kullanıcı bulunamazsa bir varsayılan değer ekleyin
            }
        }

        // Map'i oluştur ve model'e ekle
        Map<Etkinlikler, String> etkinlikMap = new LinkedHashMap<>();
        for (int i = 0; i < allEvents.size(); i++) {
            etkinlikMap.put(allEvents.get(i), olusturanAdlari.get(i));
        }
        model.addAttribute("etkinlikMap", etkinlikMap);

        return "admin";
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


    @GetMapping("/RegisterEventAdmin")
    public String showRegisterEvent(){
        return "register-event-admin";
    }


    @PostMapping("/RegisterEventAdmin")
    public String registerEventAdmin(@RequestParam("etkinlikAdi") String etkinlikAdi,
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
            yeniEtkinlik.setOlusturan(11);

            etkinliklerService.save(yeniEtkinlik);


            model.addAttribute("registrationSuccess", true);
            return "register-event-admin";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Geçersiz değer.");
            return "register-event-admin";
        }
    }

    @GetMapping("/approveEvent/{id}")
    public String approveEvent(@PathVariable("id") Integer id, Model model){
        Optional<OnaylanmamisEtkinlikler> onaylanmamisEtkinlik = onaylanmamisEtkinliklerService.findById(id);
        Etkinlikler etkinlik = new Etkinlikler();
        etkinlik.setEtkinlikAdi(onaylanmamisEtkinlik.get().getEtkinlikAdi());
        etkinlik.setAciklama(onaylanmamisEtkinlik.get().getAciklama());
        etkinlik.setTarih(onaylanmamisEtkinlik.get().getTarih());
        etkinlik.setSaat(onaylanmamisEtkinlik.get().getSaat());
        etkinlik.setEtkinlikSuresi(onaylanmamisEtkinlik.get().getEtkinlikSuresi());
        etkinlik.setKonum(onaylanmamisEtkinlik.get().getKonum());
        etkinlik.setKategori(onaylanmamisEtkinlik.get().getKategori());
        etkinlik.setResimUrl(onaylanmamisEtkinlik.get().getResim());
        etkinlik.setOlusturan(onaylanmamisEtkinlik.get().getOlusturan());

        // onaylanmamış etkinliği onaylanmış etkinlikler tabloma kaydediyorum
        etkinliklerService.save(etkinlik);
        // onaylanmamış etkinlik tablomdan etkinliği sil
        onaylanmamisEtkinliklerService.delete(onaylanmamisEtkinlik.get());

        // SAYFAYI YENİLEMEK İÇİN

        // Onay Bekleyen Etkinlikleri Al
        List<OnaylanmamisEtkinlikler> unapprovedEvents = onaylanmamisEtkinliklerService.findAll();
        model.addAttribute("unapprovedEvents", unapprovedEvents);

        //  Tüm etkinlikleri al
        List<Etkinlikler> allEvents = etkinliklerService.findAll();
        model.addAttribute("allEvents", allEvents);

        // Tüm kullanıcıları al
        List<Kullanicilar> allUsers = kullanicilarService.findAll();
        model.addAttribute("allUsers", allUsers);

        // Etkinliği oluşturan kişiyi al
        List<String> olusturanAdlari = new ArrayList<>();
        for (Etkinlikler event : allEvents) {
            Kullanicilar olusturan = kullanicilarService.findByKullaniciId(event.getOlusturan());
            if (olusturan != null) { // Kullanıcı bulunduğundan emin olun
                olusturanAdlari.add(olusturan.getAd());
            } else {
                olusturanAdlari.add("Bilinmiyor"); // Kullanıcı bulunamazsa bir varsayılan değer ekleyin
            }
        }

        // Map'i oluştur ve model'e ekle
        Map<Etkinlikler, String> etkinlikMap = new LinkedHashMap<>();
        for (int i = 0; i < allEvents.size(); i++) {
            etkinlikMap.put(allEvents.get(i), olusturanAdlari.get(i));
        }
        model.addAttribute("etkinlikMap", etkinlikMap);

        return "admin";
    }

    @GetMapping("/deleteUserAdmin/{id}")
    public String deleteUserAdmin(@PathVariable("id") Integer id, Model model){
        Optional<OnaylanmamisEtkinlikler> onaylanmamisEtkinlik = onaylanmamisEtkinliklerService.findById(id);
        // Etkinlik admin tarafından onaylanmadığı için veritabanından sil
        onaylanmamisEtkinliklerService.delete(onaylanmamisEtkinlik.get());
        // SAYFAYI YENİLEMEK İÇİN

        // Onay Bekleyen Etkinlikleri Al
        List<OnaylanmamisEtkinlikler> unapprovedEvents = onaylanmamisEtkinliklerService.findAll();
        model.addAttribute("unapprovedEvents", unapprovedEvents);

        //  Tüm etkinlikleri al
        List<Etkinlikler> allEvents = etkinliklerService.findAll();
        model.addAttribute("allEvents", allEvents);

        // Tüm kullanıcıları al
        List<Kullanicilar> allUsers = kullanicilarService.findAll();
        model.addAttribute("allUsers", allUsers);

        // Etkinliği oluşturan kişiyi al
        List<String> olusturanAdlari = new ArrayList<>();
        for (Etkinlikler event : allEvents) {
            Kullanicilar olusturan = kullanicilarService.findByKullaniciId(event.getOlusturan());
            if (olusturan != null) { // Kullanıcı bulunduğundan emin olun
                olusturanAdlari.add(olusturan.getAd());
            } else {
                olusturanAdlari.add("Bilinmiyor"); // Kullanıcı bulunamazsa bir varsayılan değer ekleyin
            }
        }

        // Map'i oluştur ve model'e ekle
        Map<Etkinlikler, String> etkinlikMap = new LinkedHashMap<>();
        for (int i = 0; i < allEvents.size(); i++) {
            etkinlikMap.put(allEvents.get(i), olusturanAdlari.get(i));
        }
        model.addAttribute("etkinlikMap", etkinlikMap);

        return "admin";
    }

    @GetMapping("/unapprovedDetails/{id}")
    public String showUnapprovedEvent(@PathVariable("id") Integer id, Model model) {
        Optional<OnaylanmamisEtkinlikler> etkinlikOptional = onaylanmamisEtkinliklerService.findById(id);
        if (etkinlikOptional.isPresent()) {
            model.addAttribute("etkinlik", etkinlikOptional.get());
            return "unapproved-event-details"; // Güncelleme sayfasını render eder
        }
        model.addAttribute("error", "Etkinlik bulunamadı!");
        return "admin";
    }

    @GetMapping("/search")
    public String searchEtkinlik(@RequestParam("query") String query, Model model) {
        List<Etkinlikler> etkinliklerList = new ArrayList<>();

        for(Etkinlikler etkinlik : etkinliklerService.findAll()){
            if(etkinlik.getEtkinlikAdi().toLowerCase().contains(query.toLowerCase())){
                etkinliklerList.add(etkinlik);
            }
        }

        // Tüm kullanıcıları al
        List<Kullanicilar> allUsers = kullanicilarService.findAll();
        model.addAttribute("allUsers", allUsers);

        // Onay Bekleyen Etkinlikleri Al
        List<OnaylanmamisEtkinlikler> unapprovedEvents = onaylanmamisEtkinliklerService.findAll();
        model.addAttribute("unapprovedEvents", unapprovedEvents);

        // filtrelenmiş etkinlikler
        model.addAttribute("allEvents", etkinliklerList);

        // Etkinliği oluşturan kişiyi al
        List<String> olusturanAdlari = new ArrayList<>();
        for (Etkinlikler event : etkinliklerList) {
            Kullanicilar olusturan = kullanicilarService.findByKullaniciId(event.getOlusturan());
            if (olusturan != null) { // Kullanıcı bulunduğundan emin olun
                olusturanAdlari.add(olusturan.getAd());
            } else {
                olusturanAdlari.add("Bilinmiyor"); // Kullanıcı bulunamazsa bir varsayılan değer ekleyin
            }
        }

        // Map'i oluştur ve model'e ekle
        Map<Etkinlikler, String> etkinlikMap = new LinkedHashMap<>();
        for (int i = 0; i < etkinliklerList.size(); i++) {
            etkinlikMap.put(etkinliklerList.get(i), olusturanAdlari.get(i));
        }
        model.addAttribute("etkinlikMap", etkinlikMap);
        return "admin";
    }

    @GetMapping("/search2")
    public String searchEtkinlik2(@RequestParam("query") String query, Model model) {
        List<Etkinlikler> etkinliklerList = new ArrayList<>();

        // Kullanıcıya ait etkinlikleri al
        List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(KullanicilarController.kullaniciIdOlusturanIcin);

        for(Etkinlikler etkinlik : userEvents){
            if(etkinlik.getEtkinlikAdi().toLowerCase().contains(query.toLowerCase())){
                etkinliklerList.add(etkinlik);
            }
        }
        model.addAttribute("profileImageUrl", kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin).getProfilFotografi()); // Kullanıcı resim URL'sini modele ekliyoruz

        model.addAttribute("userEvents", etkinliklerList);

        model.addAttribute("recommendedEvents", recommendedEvents());

        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
        if(puanlarOptional.isPresent()){
            model.addAttribute("userPoints", puanlarOptional.get().getPuan());
        }else{
            model.addAttribute("userPoints", 0);
        }

        return "user";
    }


    @GetMapping("/search3")
    public String searchEtkinlik3(@RequestParam("query") String query, Model model) {
        List<Etkinlikler> etkinliklerList = new ArrayList<>();

        for(Etkinlikler etkinlik : etkinliklerService.findAll()){
            if(etkinlik.getEtkinlikAdi().toLowerCase().contains(query.toLowerCase())){
                etkinliklerList.add(etkinlik);
            }
        }

        // Kullanıcıya ait etkinlikleri al
        List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(KullanicilarController.kullaniciIdOlusturanIcin);

        model.addAttribute("profileImageUrl", kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin).getProfilFotografi()); // Kullanıcı resim URL'sini modele ekliyoruz

        model.addAttribute("userEvents", userEvents);

        model.addAttribute("recommendedEvents", etkinliklerList);

        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
        if(puanlarOptional.isPresent()){
            model.addAttribute("userPoints", puanlarOptional.get().getPuan());
        }else{
            model.addAttribute("userPoints", 0);
        }
        return "user";
    }
}
