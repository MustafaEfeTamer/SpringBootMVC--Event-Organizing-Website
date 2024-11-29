package com.springboot.YazLab1_2.ProjeDeneme.controller;


import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Puanlar;
import com.springboot.YazLab1_2.ProjeDeneme.service.EtkinliklerService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KatilimcilarService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KullanicilarService;
import com.springboot.YazLab1_2.ProjeDeneme.service.PuanlarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/katilimcilar")
public class KatilimcilarController {

    private KullanicilarService kullanicilarService;
    private EtkinliklerService etkinliklerService;
    private KatilimcilarService katilimcilarService;
    private PuanlarService puanlarService;

    public KatilimcilarController(KullanicilarService kullanicilarService, EtkinliklerService etkinliklerService, KatilimcilarService katilimcilarService, PuanlarService puanlarService) {
        this.kullanicilarService = kullanicilarService;
        this.etkinliklerService = etkinliklerService;
        this.katilimcilarService = katilimcilarService;
        this.puanlarService = puanlarService;
    }

    private static Optional<Etkinlikler> etkinliklerOptional;

    @GetMapping("/joinEvent/{id}")
    public String joinEvent(@PathVariable("id") Integer id, Model model) {

        etkinliklerOptional = etkinliklerService.findById(id);

        if (etkinliklerOptional.isPresent()) {
            Etkinlikler etkinlik = etkinliklerOptional.get();

            List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);


            for (Katilimcilar katilim : katilimlar) {
                Optional<Etkinlikler> eskiEtkinlikOptional = etkinliklerService.findByIdLong(katilim.getEtkinlikId());

                Etkinlikler eskiEtkinlik = eskiEtkinlikOptional.get();

                // Etkinlik tarih ve saatlerinin çakışıp çakışmadığını kontrol ediyoruz
                if (etkinlik.getTarih().equals(eskiEtkinlik.getTarih()) && etkinlik.getSaat().equals(eskiEtkinlik.getSaat())) {
                    if (etkinlik == eskiEtkinlik) {
                        model.addAttribute("error2", "BU ETKİNLİĞE ZATEN KATILDINIZ. UYGUN ETKİNLİKLER SAĞ TARAFTA LİSTELENMEKTEDİR");
                        model.addAttribute("etkinlik", etkinlik);
                        model.addAttribute("dropList",  uygunEtkinlikler());
                        return "event-page";
                    }
                    // Çakışma varsa, hata mesajı ekliyoruz
                    model.addAttribute("error", "BU ETKİNLİĞE KATILAMAZSINIZ. TARİH VE SAAT ÇAKIŞIYOR. UYGUN ETKİNLİKLER SAĞ TARAFTA LİSTELENMEKTEDİR.");
                    model.addAttribute("etkinlik", etkinlik);
                    model.addAttribute("dropList",  uygunEtkinlikler());
                    return "event-page"; // Etkinlik detay sayfasına geri dönüyoruz
                }
            }


            List<Katilimcilar> katilimcilarList = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);


            if (katilimcilarList.isEmpty()) {
                model.addAttribute("success", "Etkinliğe Başarıyla Katıldınız ve İLK ETKİNLİK KATILIMINA ÖZEL 20 Puan Kazandınız🥳🎉");

                // başarılı bir şekilde etkinliğe katılacağımız zaman bu methodu çalıştıracağız
                PuanlarController.updatePoint();
            } else {
                model.addAttribute("success", "Etkinliğe Başarıyla Katıldınız ve 10 Puan Kazandınız😀");

                // başarılı bir şekilde etkinliğe katılacağımız zaman bu methodu çalıştıracağız
                PuanlarController.updatePoint();
            }

            // Çakışma yoksa katılım kaydını oluşturuyoruz
            Katilimcilar yeniKatilimci = new Katilimcilar();
            yeniKatilimci.setKullaniciId((long) KullanicilarController.kullaniciIdOlusturanIcin);
            yeniKatilimci.setEtkinlikId(etkinlik.getId().longValue());
            katilimcilarService.save(yeniKatilimci);

            model.addAttribute("etkinlik", etkinlik);
            return "event-page"; // Katılım başarılı, ilgili sayfaya yönlendiriyoruz
        } else {
            return "error"; // Etkinlik bulunamazsa hata sayfasına yönlendiriyoruz
        }
    }


    public List<String> uygunEtkinlikler() {
        List<String> uygunEtkinlikler = new ArrayList<>();
        List<Etkinlikler> etkinliklerList = etkinliklerService.findAll();
        List<Katilimcilar> katilimcilarList = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);

        Iterator<Etkinlikler> iterator = etkinliklerList.iterator();
        while (iterator.hasNext()) {
            Etkinlikler etkinlik = iterator.next();
            for (Katilimcilar katilim : katilimcilarList) {
                if (etkinlik.getId() == katilim.getEtkinlikId().longValue()) {
                    iterator.remove(); // Eşleşen etkinliği listeden sil
                    break; // Çık çünkü bu etkinliği zaten silmek istiyoruz
                }

                Optional<Etkinlikler> etkinlik2 = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
                if(etkinlik.getTarih().equals(etkinlik2.get().getTarih()) && etkinlik.getSaat().equals(etkinlik2.get().getSaat())){
                    iterator.remove();
                    break;
                }
            }
        }

        // Katılmayan etkinlikleri listeye ekle
        for (Etkinlikler etkinlik : etkinliklerList) {
            uygunEtkinlikler.add(etkinlik.getEtkinlikAdi()); // Etkinlik adlarını listeye ekle
        }
        return uygunEtkinlikler;
    }



    @GetMapping("/deleteEventUserAttended/{id}")
    public String deleteEventUserAttended(@PathVariable("id") Integer id, Model model) {

        katilimcilarService.deleteEventByUserIdEventId(KullanicilarController.userId, id);


        Kullanicilar kullanici = kullanicilarService.findByKullaniciId(KullanicilarController.userId);

        model.addAttribute("profileImageUrl", kullanici.getProfilFotografi());


        // Kullanıcının geçmişte katıldığı etkinlikler
        List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.userId);
        List<Etkinlikler> etkinliklerList = new ArrayList<>();

        for(Katilimcilar katilim : katilimlar){
            Optional<Etkinlikler> etkinlik = etkinliklerService.findById(katilim.getEtkinlikId().intValue());
            if(etkinlik.isPresent()){
                etkinliklerList.add(etkinlik.get());
            }
        }


        model.addAttribute("user", kullanici);
        model.addAttribute("pastEvents", etkinliklerList);

        Optional<Puanlar> puanlarOptional = puanlarService.findByKullaniciId(KullanicilarController.userId);
        if(puanlarOptional.isPresent()){
            model.addAttribute("userPoints", puanlarOptional.get().getPuan());
        }else{
            model.addAttribute("userPoints", 0);
        }

        return "user-profile-admin";
    }
}
