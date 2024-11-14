package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Employee;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;

public interface KullanicilarService {
    Kullanicilar findByKullaniciAdiveSifre(String kullaniciAdi, String sifre);
    Kullanicilar save(Kullanicilar theKullanici);
    boolean existsByEmail(String ePosta); // E-posta doğrulama metodu
    boolean resetPassword(String ePosta, String sifre); // Şifre güncelleme metodu
    Kullanicilar findByKullaniciAdi(String kullaniciAdi);
}
