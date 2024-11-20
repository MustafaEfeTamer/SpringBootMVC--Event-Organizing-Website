package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Puanlar;
import com.springboot.YazLab1_2.ProjeDeneme.service.EtkinliklerService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KatilimcilarService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KullanicilarService;
import com.springboot.YazLab1_2.ProjeDeneme.service.PuanlarService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/puanlar")
public class PuanlarController {

    private KullanicilarService kullanicilarService;
    private EtkinliklerService etkinliklerService;
    private static KatilimcilarService katilimcilarService;
    private static PuanlarService puanlarService;

    public PuanlarController(KullanicilarService kullanicilarService, EtkinliklerService etkinliklerService, KatilimcilarService katilimcilarService, PuanlarService puanlarService) {
        this.kullanicilarService = kullanicilarService;
        this.etkinliklerService = etkinliklerService;
        this.katilimcilarService = katilimcilarService;
        this.puanlarService = puanlarService;
    }

    // etkinlik katılımına özel puan kazanmak için
    public static void updatePoint(){
        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

        Puanlar puanlar;

        List<Katilimcilar> katilimcilarList = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

        if(katilimcilarList.isEmpty() && !(puanlarOptional.isPresent())){
            // Eğer kullanıcı daha önce bir etkinliğe katılmadıysa ve puanı yoksa, yeni bir puan kaydı oluştur
            puanlar = new Puanlar();
            puanlar.setKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            puanlar.setPuan(20L); // İlk katılım bonusu
            puanlar.setKazanilanTarih(new Date()); // Şu anki tarih
        }else if(katilimcilarList.isEmpty() == true && puanlarOptional.isPresent() == true) {
            // Eğer kullanıcı puanı varsa, mevcut puanı güncelle
            puanlar = puanlarOptional.get();
            puanlar.setPuan(puanlar.getPuan() + 20L);
        }else{
            // Eğer kullanıcı puanı varsa, mevcut puanı güncelle
            puanlar = puanlarOptional.get();
            puanlar.setPuan(puanlar.getPuan() + 10L);
        }
        // Puan kaydını veritabanına kaydet
        puanlarService.save(puanlar);
    }

    public static void updatePointForRegister(){
        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

        Puanlar puanlar;

        if(puanlarOptional.isPresent()){
            // Eğer kullanıcı puanı varsa, mevcut puanı güncelle
            puanlar = puanlarOptional.get();
            puanlar.setPuan(puanlar.getPuan() + 15L);
        }else{
            // Eğer kullanıcı puanı yoksa, yeni bir puan kaydı oluştur
            puanlar = new Puanlar();
            puanlar.setKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);
            puanlar.setPuan(15L);
            puanlar.setKazanilanTarih(new Date()); // Şu anki tarih
        }

        // Puan kaydını veritabanına kaydet
        puanlarService.save(puanlar);
    }
}
