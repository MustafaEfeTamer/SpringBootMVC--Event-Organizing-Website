package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;

import java.util.List;

public interface KullanicilarService {
    List<Kullanicilar> findAll();
    Kullanicilar findByKullaniciAdiveSifre(String kullaniciAdi, String sifre);
    Kullanicilar save(Kullanicilar theKullanici);
    boolean existsByEmail(String ePosta); // E-posta doğrulama metodu
    boolean resetPassword(String ePosta, String sifre); // Şifre güncelleme metodu
    Kullanicilar findByKullaniciAdi(String kullaniciAdi);
    Kullanicilar findByKullaniciId(Integer id);
    void deleteById(Integer id);
}
