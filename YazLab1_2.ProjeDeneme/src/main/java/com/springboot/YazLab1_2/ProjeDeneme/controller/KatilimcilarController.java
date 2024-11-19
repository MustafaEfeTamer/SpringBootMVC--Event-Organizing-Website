package com.springboot.YazLab1_2.ProjeDeneme.controller;


import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import com.springboot.YazLab1_2.ProjeDeneme.service.EtkinliklerService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KatilimcilarService;
import com.springboot.YazLab1_2.ProjeDeneme.service.KullanicilarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/katilimcilar")
public class KatilimcilarController {


    private KullanicilarService kullanicilarService;
    private EtkinliklerService etkinliklerService;
    private KatilimcilarService katilimcilarService;

    public KatilimcilarController(KatilimcilarService katilimcilarService, KullanicilarService kullanicilarService, EtkinliklerService etkinliklerService) {
        this.katilimcilarService = katilimcilarService;
        this.kullanicilarService = kullanicilarService;
        this.etkinliklerService = etkinliklerService;
    }


    @GetMapping("/joinEvent/{id}")
    public String joinEvent(@PathVariable("id") Integer id, Model model) {

        Optional<Etkinlikler> etkinliklerOptional = etkinliklerService.findById(id);

        if (etkinliklerOptional.isPresent()) {
            Etkinlikler etkinlik = etkinliklerOptional.get();

            List<Katilimcilar> katilimlar = katilimcilarService.findByKullaniciId(KullanicilarController.kullaniciIdOlusturanIcin);


            for (Katilimcilar katilim : katilimlar) {
                Optional<Etkinlikler> eskiEtkinlikOptional = etkinliklerService.findByIdLong(katilim.getEtkinlikId());

                Etkinlikler eskiEtkinlik = eskiEtkinlikOptional.get();


                // Etkinlik tarih ve saatlerinin çakışıp çakışmadığını kontrol ediyoruz
                if (etkinlik.getTarih().equals(eskiEtkinlik.getTarih()) && etkinlik.getSaat().equals(eskiEtkinlik.getSaat())) {
                    if(etkinlik == eskiEtkinlik){
                        model.addAttribute("error2", "Bu Etkinliğe Zaten Katıldınız");
                        model.addAttribute("etkinlik", etkinlik);
                        return "event-page";
                    }
                    // Çakışma varsa, hata mesajı ekliyoruz
                    model.addAttribute("error", "Bu Etkinliğe Katılamazsınız. Tarih ve Saat Çakışıyor.");
                    model.addAttribute("etkinlik", etkinlik);
                    return "event-page"; // Etkinlik detay sayfasına geri dönüyoruz
                }
            }


            // Çakışma yoksa katılım kaydını oluşturuyoruz
            Katilimcilar yeniKatilimci = new Katilimcilar();
            yeniKatilimci.setKullaniciId((long) KullanicilarController.kullaniciIdOlusturanIcin);
            yeniKatilimci.setEtkinlikId(etkinlik.getId().longValue());
            katilimcilarService.save(yeniKatilimci);

            model.addAttribute("success", "Etkinliğe Başarıyla Katıldınız!");
            model.addAttribute("etkinlik", etkinlik);
            return "event-page"; // Katılım başarılı, ilgili sayfaya yönlendiriyoruz
        } else {
            return "error"; // Etkinlik bulunamazsa hata sayfasına yönlendiriyoruz
        }
    }
}
