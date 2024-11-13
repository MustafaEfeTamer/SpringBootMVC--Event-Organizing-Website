package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import com.springboot.YazLab1_2.ProjeDeneme.service.KullanicilarService;
import jakarta.persistence.Column;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("/kullanicilar")
public class KullanicilarController {

    private KullanicilarService kullanicilarService;

    public KullanicilarController(KullanicilarService kullanicilarService) {
        this.kullanicilarService = kullanicilarService;
    }

    @GetMapping("/login")   // tarayıcıdan url girerek alının isteklerin hepsi 'get' tir
    public String showLoginPage() {
        return "login"; // `login.html` sayfasını gösterir
    }

    @PostMapping("/login")
    public String login(@RequestParam("kullaniciAdi") String kullaniciAdi,
                        @RequestParam("sifre") String sifre,
                        Model model) throws InterruptedException {

        // Eğer kullanıcı adı ve şifre admin ise admin sayfasına yönlendir
        if (kullaniciAdi.equals("admin") && sifre.equals("admin123")) {
            model.addAttribute("success", true);  // Başarılı giriş mesajı
            //Thread.sleep(1000);
            return "admin"; // Admin sayfasına yönlendirir
        }

        try {
            Kullanicilar kullanici = kullanicilarService.findByKullaniciAdiveSifre(kullaniciAdi, sifre);
            model.addAttribute("success", true);  // Başarılı giriş mesajı
            //Thread.sleep(1000);
            return "user"; // Aynı sayfada başarılı mesajını gösterir
        } catch (RuntimeException e) {
            model.addAttribute("error", true);  // Hatalı giriş mesajı
            return "login"; // Aynı sayfada hatalı giriş mesajını gösterir
        }
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
    public String showUserProfile(){
        return "user-profile";
    }

    @PostMapping("/userProfile")
    public String processUserProfile(){
        return "user-profile";
    }

    @GetMapping("/userNotifications")
    public String showUserNotifications(){
        return "user-notifications";
    }

    @PostMapping("/userNotifications")
    public String processUserNotifications(){
        return "user-notifications";
    }

}
