package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.dao.PuanlarRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.*;
import com.springboot.YazLab1_2.ProjeDeneme.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/kullanicilar")
public class KullanicilarController {

    private KullanicilarService kullanicilarService;
    private EtkinliklerService etkinliklerService;
    private PuanlarService puanlarService;
    private KatilimcilarService katilimcilarService;
    private OnaylanmamisEtkinliklerService onaylanmamisEtkinliklerService;

    public static int kullaniciIdOlusturanIcin;
    public static int userId;



    public KullanicilarController(KullanicilarService kullanicilarService, EtkinliklerService etkinliklerService, PuanlarService puanlarService, KatilimcilarService katilimcilarService, OnaylanmamisEtkinliklerService onaylanmamisEtkinliklerService) {
        this.kullanicilarService = kullanicilarService;
        this.etkinliklerService = etkinliklerService;
        this.puanlarService = puanlarService;
        this.katilimcilarService = katilimcilarService;
        this.onaylanmamisEtkinliklerService = onaylanmamisEtkinliklerService;
    }


    @GetMapping("/login")   // tarayıcıdan url girerek alınan isteklerin hepsi 'get' tir
    public String showLoginPage() {
        return "login"; // `login.html` sayfasını gösterir
    }

    @PostMapping("/login")
    public String login(@RequestParam("kullaniciAdi") String kullaniciAdi,
                        @RequestParam("sifre") String sifre,
                        HttpSession session,
                        Model model) throws InterruptedException {

        if (kullaniciAdi.equals("admin") && sifre.equals("admin")) {
            model.addAttribute("success", true);  // Başarılı giriş mesajı

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


            return "admin"; // Admin sayfasına yönlendirir
        }
        try {
            Kullanicilar kullanici = kullanicilarService.findByKullaniciAdiveSifre(kullaniciAdi, sifre);
            kullaniciIdOlusturanIcin = kullanici.getId(); // user ekranındaki olusturan id için
            model.addAttribute("success", true);  // Başarılı giriş mesajı
            model.addAttribute("profileImageUrl", kullanici.getProfilFotografi()); // Kullanıcı resim URL'sini modele ekliyoruz

            // Kullanıcıya ait etkinlikleri al
            List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(kullanici.getId());
            model.addAttribute("userEvents", userEvents);


            Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            if(puanlarOptional.isPresent()){
                model.addAttribute("userPoints", puanlarOptional.get().getPuan());
            }else{
                model.addAttribute("userPoints", 0);
            }

            session.setAttribute("kullaniciAdi", kullaniciAdi);

            // Tüm etkinlikleri al
            List<Etkinlikler> allEvents = etkinliklerService.findAll();

            // Öneri kuralları
            List<Etkinlikler> recommendedEvents = new ArrayList<>();

        // İlgi Alanı Uyum Kuralı
        String[] ilgiAlanlari = kullanici.getIlgiAlanlari().split("[, ]+"); // İlgi alanları virgülle ayrılmış olarak varsayılmıştır
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
            String[] kullaniciKonumu = kullanici.getKonum().split("[, ]+");
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

            return "user"; // Aynı sayfada başarılı mesajını gösterir
        } catch (RuntimeException e) {
            model.addAttribute("error", true);  // Hatalı giriş mesajı
            return "login"; // Aynı sayfada hatalı giriş mesajını gösterir
        }
    }

    @GetMapping("/userPage")
    public String showUserPage(Model model){

        Kullanicilar kullanici = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

        model.addAttribute("profileImageUrl", kullanici.getProfilFotografi()); // Kullanıcı resim URL'sini modele ekliyoruz

        // Kullanıcıya ait etkinlikleri al
        List<Etkinlikler> userEvents = etkinliklerService.findByOlusturan(kullanici.getId());
        model.addAttribute("userEvents", userEvents);


        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
        if(puanlarOptional.isPresent()){
            model.addAttribute("userPoints", puanlarOptional.get().getPuan());
        }else{
            model.addAttribute("userPoints", 0);
        }


        // Tüm etkinlikleri al
        List<Etkinlikler> allEvents = etkinliklerService.findAll();

        // Öneri kuralları
        List<Etkinlikler> recommendedEvents = new ArrayList<>();

        // İlgi Alanı Uyum Kuralı
        String[] ilgiAlanlari = kullanici.getIlgiAlanlari().split("[, ]+"); // İlgi alanları virgülle ayrılmış olarak varsayılmıştır
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
                    break;
                }
            }
        }


        List<String> istisnaKelimeler = Arrays.asList("Türkiye", "Mah.", "Cad.", "Sok.", "Sit.", "Blok", "Daire");

        // Coğrafi Konum Kuralı
        String[] kullaniciKonumu = kullanici.getKonum().split("[, ]+");
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

        return "user";
    }

    @GetMapping("/adminPage")
    public String showAdminPage(Model model){
        // Onay Bekleyen Etkinlikleri Al
        List<OnaylanmamisEtkinlikler> unapprovedEvents = onaylanmamisEtkinliklerService.findAll();
        model.addAttribute("unapprovedEvents", unapprovedEvents);

        // Kullanıcıya ait etkinlikleri al
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


    // Kayıt sayfasını göstermek için GetMapping
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // `register.html` adında kayıt sayfasını döner
    }

    // Kayıt formundan aldığımız verileri veritabanına işlemek için
    @PostMapping("/register")
    public String registerUser(@RequestParam("kullaniciAdi") String kullaniciAdi,
                               @RequestParam("sifre") String sifre,
                               @RequestParam("ePosta") String ePosta,
                               @RequestParam("konum") String konum,
                               @RequestParam("ilgiAlanlari") String ilgiAlanlari,
                               @RequestParam("ad") String ad,
                               @RequestParam("soyad") String soyad,
                               @RequestParam("dogumTarihi") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dogumTarihi,
                               @RequestParam("cinsiyet") String cinsiyet,
                               @RequestParam("telefonNumarasi") String telefonNumarasi,
                               @RequestParam("profilFotografi") String profilFotografi,
                               Model model) {
        try {
            Kullanicilar yeniKullanici = new Kullanicilar();
            yeniKullanici.setKullaniciAdi(kullaniciAdi);
            yeniKullanici.setSifre(sifre);
            yeniKullanici.setePosta(ePosta);
            yeniKullanici.setKonum(konum);
            yeniKullanici.setIlgiAlanlari(ilgiAlanlari);
            yeniKullanici.setAd(ad);
            yeniKullanici.setSoyad(soyad);
            yeniKullanici.setDogumTarihi(dogumTarihi);
            yeniKullanici.setCinsiyet(cinsiyet);
            yeniKullanici.setTelefonNumarasi(telefonNumarasi);
            yeniKullanici.setProfilFotografi(profilFotografi);

            kullanicilarService.save(yeniKullanici);

            model.addAttribute("registrationSuccess", true);
            return "redirect:/kullanicilar/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Geçersiz değer.");
            return "register";
        }
    }



    // Şifre yenileme sayfasını göstermek için GetMapping
    @GetMapping("/forgetPassword")
    public String showForgetPasswordPage(Model model) {
        model.addAttribute("showEmailForm", true);
        model.addAttribute("showPasswordForm", false);
        return "forget-password";
    }

    // E-posta doğrulaması yapılması için PostMapping
    @PostMapping("/forgetPassword")
    public String processForgetPassword(@RequestParam("email") String email, Model model) {
        if (kullanicilarService.existsByEmail(email)) {
            model.addAttribute("showEmailForm", false);
            model.addAttribute("showPasswordForm", true);
            model.addAttribute("email", email);
        } else {
            model.addAttribute("showEmailForm", true);
            model.addAttribute("showPasswordForm", false);
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Bu e-posta adresi kayıtlı değil.");
        }
        return "forget-password";
    }

    // Şifre yenileme işlemi için PostMapping
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Şifreler eşleşmiyor.");
            model.addAttribute("showPasswordForm", true);
            model.addAttribute("email", email);
            return "forget-password";
        }

        boolean result = kullanicilarService.resetPassword(email, newPassword);
        if (result) {
            model.addAttribute("success", true);
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Şifre yenileme işlemi başarısız.");
            model.addAttribute("showPasswordForm", true);
            model.addAttribute("email", email);
        }
        return "forget-password";
    }


    @GetMapping("/user")
    public String showUser(){
        return "user";
    }

    @PostMapping("/user")
    public String processUser(){
        return "user";
    }

    @GetMapping("/userProfile")
    public String showUserProfile(Model model){
        Kullanicilar kullanici = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

        model.addAttribute("profileImageUrl", kullanici.getProfilFotografi()); // Kullanıcı resim URL'sini modele ekliyoruz


        List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
        List<Etkinlikler> etkinliklerList = new ArrayList<>();

        for(Katilimcilar katilim : katilimlar){
            Optional<Etkinlikler> etkinlik = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
            if(etkinlik.isPresent()){
                etkinliklerList.add(etkinlik.get());
            }
        }

        Kullanicilar user = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
        model.addAttribute("user", user);
        model.addAttribute("pastEvents", etkinliklerList);

        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
        if(puanlarOptional.isPresent()){
            model.addAttribute("userPoints", puanlarOptional.get().getPuan());
        }else{
            model.addAttribute("userPoints", 0);
        }
        return "user-profile";
    }


    // Kayıt formundan aldığımız verileri veritabanına işlemek için
    @PostMapping("/userProfile")
    public String processUserProfile(
                             @RequestParam("kullaniciAdi") String kullaniciAdi,
                             @RequestParam("sifre") String sifre,
                             @RequestParam("ePosta") String ePosta,
                             @RequestParam("konum") String konum,
                             @RequestParam("ilgiAlanlari") String ilgiAlanlari,
                             @RequestParam("ad") String ad,
                             @RequestParam("soyad") String soyad,
                             @RequestParam("dogumTarihi") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dogumTarihi,
                             @RequestParam("cinsiyet") String cinsiyet,
                             @RequestParam("telefonNumarasi") String telefonNumarasi,
                             @RequestParam("profilFotografi") String profilFotografi,
                             Model model) {
        try {
            Kullanicilar kullanici = kullanicilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            kullanici.setKullaniciAdi(kullaniciAdi);
            kullanici.setSifre(sifre);
            kullanici.setePosta(ePosta);
            kullanici.setKonum(konum);
            kullanici.setIlgiAlanlari(ilgiAlanlari);
            kullanici.setAd(ad);
            kullanici.setSoyad(soyad);
            kullanici.setDogumTarihi(dogumTarihi);
            kullanici.setCinsiyet(cinsiyet);
            kullanici.setTelefonNumarasi(telefonNumarasi);
            kullanici.setProfilFotografi(profilFotografi);

            kullanicilarService.save(kullanici);

            List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            List<Etkinlikler> etkinliklerList = new ArrayList<>();

            for(Katilimcilar katilim : katilimlar){
                Optional<Etkinlikler> etkinlik = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
                if(etkinlik.isPresent()){
                    etkinliklerList.add(etkinlik.get());
                }
            }

            model.addAttribute("registrationSuccess", true);
            model.addAttribute("user",kullanici);
            model.addAttribute("pastEvents", etkinliklerList);

            Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            if(puanlarOptional.isPresent()){
                model.addAttribute("userPoints", puanlarOptional.get().getPuan());
            }else{
                model.addAttribute("userPoints", 0);
            }

            return "user-profile";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Geçersiz değer.");
            return "user-profile";
        }
    }

    @GetMapping("/deleteUserAdmin/{id}")
    public String deleteUserAdmin(@PathVariable("id") Integer id, Model model) {
        // kullanıcıyı silmek için
        kullanicilarService.deleteById(id);

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

    @GetMapping("/userNotifications")
    public String showUserNotifications(){
        return "user-notifications";
    }

    @PostMapping("/userNotifications")
    public String processUserNotifications(){
        return "user-notifications";
    }

    @GetMapping("/userProfileAdmin/{id}")
    public String showUserProfileAdmin(@PathVariable("id") Integer id, Model model){
        userId = id;
        Kullanicilar kullanici = kullanicilarService.findByKullaniciId(userId);

        model.addAttribute("profileImageUrl", kullanici.getProfilFotografi()); // Kullanıcı resim URL'sini modele ekliyoruz


        List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(userId);
        List<Etkinlikler> etkinliklerList = new ArrayList<>();

        for(Katilimcilar katilim : katilimlar){
            Optional<Etkinlikler> etkinlik = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
            if(etkinlik.isPresent()){
                etkinliklerList.add(etkinlik.get());
            }
        }


        Kullanicilar user = kullanicilarService.findByKullaniciId(userId);
        model.addAttribute("user", user);
        model.addAttribute("pastEvents", etkinliklerList);

        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(userId);
        if(puanlarOptional.isPresent()){
            model.addAttribute("userPoints", puanlarOptional.get().getPuan());
        }else{
            model.addAttribute("userPoints", 0);
        }
        return "user-profile-admin";
    }



    @PostMapping("/userProfileAdmin")
    public String processUserProfileAdmin(
            @RequestParam("kullaniciAdi") String kullaniciAdi,
            @RequestParam("sifre") String sifre,
            @RequestParam("ePosta") String ePosta,
            @RequestParam("konum") String konum,
            @RequestParam("ilgiAlanlari") String ilgiAlanlari,
            @RequestParam("ad") String ad,
            @RequestParam("soyad") String soyad,
            @RequestParam("dogumTarihi") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dogumTarihi,
            @RequestParam("cinsiyet") String cinsiyet,
            @RequestParam("telefonNumarasi") String telefonNumarasi,
            @RequestParam("profilFotografi") String profilFotografi,
            Model model) {
        try {
            Kullanicilar kullanici = kullanicilarService.findByKullaniciId(userId);
            kullanici.setKullaniciAdi(kullaniciAdi);
            kullanici.setSifre(sifre);
            kullanici.setePosta(ePosta);
            kullanici.setKonum(konum);
            kullanici.setIlgiAlanlari(ilgiAlanlari);
            kullanici.setAd(ad);
            kullanici.setSoyad(soyad);
            kullanici.setDogumTarihi(dogumTarihi);
            kullanici.setCinsiyet(cinsiyet);
            kullanici.setTelefonNumarasi(telefonNumarasi);
            kullanici.setProfilFotografi(profilFotografi);

            kullanicilarService.save(kullanici);


            // sayfayı yeniden oluşturmak için gerekli düzenlemeler
            List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(userId);
            List<Etkinlikler> etkinliklerList = new ArrayList<>();

            for(Katilimcilar katilim : katilimlar){
                Optional<Etkinlikler> etkinlik = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
                if(etkinlik.isPresent()){
                    etkinliklerList.add(etkinlik.get());
                }
            }

            model.addAttribute("registrationSuccess", true);
            model.addAttribute("user",kullanici);
            model.addAttribute("pastEvents", etkinliklerList);

            Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(userId);
            if(puanlarOptional.isPresent()){
                model.addAttribute("userPoints", puanlarOptional.get().getPuan());
            }else{
                model.addAttribute("userPoints", 0);
            }

            return "user-profile-admin";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Geçersiz değer.");
            return "user-profile-admin";
        }
    }


    @GetMapping("/showRegisterAdmin")
    public String showRegisterAdmin(){
        return "register-user-admin";
    }

    @PostMapping("/registerUserAdmin")
    public String registerUserAdmin(@RequestParam("kullaniciAdi") String kullaniciAdi,
                               @RequestParam("sifre") String sifre,
                               @RequestParam("ePosta") String ePosta,
                               @RequestParam("konum") String konum,
                               @RequestParam("ilgiAlanlari") String ilgiAlanlari,
                               @RequestParam("ad") String ad,
                               @RequestParam("soyad") String soyad,
                               @RequestParam("dogumTarihi") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dogumTarihi,
                               @RequestParam("cinsiyet") String cinsiyet,
                               @RequestParam("telefonNumarasi") String telefonNumarasi,
                               @RequestParam("profilFotografi") String profilFotografi,
                               Model model) {
        try {
            Kullanicilar yeniKullanici = new Kullanicilar();
            yeniKullanici.setKullaniciAdi(kullaniciAdi);
            yeniKullanici.setSifre(sifre);
            yeniKullanici.setePosta(ePosta);
            yeniKullanici.setKonum(konum);
            yeniKullanici.setIlgiAlanlari(ilgiAlanlari);
            yeniKullanici.setAd(ad);
            yeniKullanici.setSoyad(soyad);
            yeniKullanici.setDogumTarihi(dogumTarihi);
            yeniKullanici.setCinsiyet(cinsiyet);
            yeniKullanici.setTelefonNumarasi(telefonNumarasi);
            yeniKullanici.setProfilFotografi(profilFotografi);

            kullanicilarService.save(yeniKullanici);

            model.addAttribute("registrationSuccess", true);
            return "register-user-admin";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Geçersiz değer.");
            return "register-user-admin";
        }
    }


    @GetMapping("/search")
    public String searchUser(@RequestParam("query") String query, Model model) {
        List<Kullanicilar> kullanicilarList = new ArrayList<>();

        for(Kullanicilar kullanici : kullanicilarService.findAll()){
            if(kullanici.getKullaniciAdi().toLowerCase().contains(query.toLowerCase())){
                kullanicilarList.add(kullanici);
            }
        }

        // Filtrelenmiş kullanıcılar
        model.addAttribute("allUsers", kullanicilarList);

        // Onay Bekleyen Etkinlikleri Al
        List<OnaylanmamisEtkinlikler> unapprovedEvents = onaylanmamisEtkinliklerService.findAll();
        model.addAttribute("unapprovedEvents", unapprovedEvents);

        //  Tüm etkinlikleri al
        List<Etkinlikler> allEvents = etkinliklerService.findAll();
        model.addAttribute("allEvents", allEvents);

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
}
