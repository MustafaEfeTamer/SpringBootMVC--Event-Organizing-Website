package com.springboot.YazLab1_2.ProjeDeneme.dao;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KullanicilarRepository extends JpaRepository<Kullanicilar, Integer> {
    Optional<Kullanicilar> findByKullaniciAdiAndSifre(String kullaniciAdi, String sifre);
    Optional<Kullanicilar> findByEPosta(String ePosta);
    Optional<Kullanicilar> findByKullaniciAdi(String kullaniciAdi);
}
