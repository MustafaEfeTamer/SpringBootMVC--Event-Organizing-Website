package com.springboot.YazLab1_2.ProjeDeneme.controller;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Mesajlar;
import com.springboot.YazLab1_2.ProjeDeneme.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class MesajlarController {

    private KullanicilarService kullanicilarService;
    private EtkinliklerService etkinliklerService;
    private PuanlarService puanlarService;
    private KatilimcilarService katilimcilarService;
    private OnaylanmamisEtkinliklerService onaylanmamisEtkinliklerService;
    private MesajlarService mesajlarService;

    public MesajlarController(KullanicilarService kullanicilarService, EtkinliklerService etkinliklerService, PuanlarService puanlarService, KatilimcilarService katilimcilarService, OnaylanmamisEtkinliklerService onaylanmamisEtkinliklerService, MesajlarService mesajlarService) {
        this.kullanicilarService = kullanicilarService;
        this.etkinliklerService = etkinliklerService;
        this.puanlarService = puanlarService;
        this.katilimcilarService = katilimcilarService;
        this.onaylanmamisEtkinliklerService = onaylanmamisEtkinliklerService;
        this.mesajlarService = mesajlarService;
    }

    @GetMapping("/etkinlikler/showEventMessage/{id}")
    public String eventMessage(@PathVariable("id") Integer id, Model model) {
        List<Mesajlar> messageList = mesajlarService.findByAliciId(id);
        Optional<Etkinlikler> event = etkinliklerService.findById(id);
        model.addAttribute("etkinlik", event);
        model.addAttribute("mesajlar", messageList);
        return "event-page";
    }


    @PostMapping("/etkinlikler/showEventMessage/{id}")
    public String mesajGonder(@PathVariable("id") Integer id, HttpSession session, @RequestParam("messageText") String messageText, Model model) {
        boolean katildiMi = false;
        String kullaniciAdi = (String) session.getAttribute("kullaniciAdi");
        Kullanicilar kullanici = kullanicilarService.findByKullaniciAdi(kullaniciAdi);

        Optional<Etkinlikler> etkinlik = etkinliklerService.findById(id);
        List<Katilimcilar> katilimcilar = katilimcilarService.findByKullaniciId(kullanici.getId());
        for(Katilimcilar katilimci : katilimcilar){
            if(katilimci.getKullaniciId() == kullanici.getId().longValue()){
                katildiMi = true;
            }
        }
        model.addAttribute("kullanıcı",kullanici);
        if(katildiMi==true){
            Mesajlar mesaj = new Mesajlar();
            mesaj.setMesajMetni(messageText);
            mesaj.setGonderimZamani(new Date());
            mesaj.setGonderici(kullanici.getId());
            mesaj.setAlici(etkinlik.get().getId());
            mesajlarService.save(mesaj);
        }
        if (etkinlik.isPresent()) {
            model.addAttribute("etkinlik", etkinlik.get());
        }
        return "event-page";
    }

    @GetMapping("/etkinlikler/showEvent/mesajlar/kullanici/{id}")
    @ResponseBody
    public Kullanicilar fetchUser(@PathVariable("id") Integer id) {
        return kullanicilarService.findByKullaniciId(id);
    }

    @GetMapping("/etkinlikler/showEvent/mesajlar/{id}")
    @ResponseBody
    public List<Mesajlar> fetchMessage(@PathVariable("id") Integer id) {
        return mesajlarService.findByAliciId(id);
    }
}
