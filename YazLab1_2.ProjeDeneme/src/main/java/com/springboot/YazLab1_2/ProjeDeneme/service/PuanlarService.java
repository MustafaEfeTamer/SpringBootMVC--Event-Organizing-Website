package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Puanlar;

import java.util.List;
import java.util.Optional;

public interface PuanlarService {
    Optional<Puanlar> findByKullaniciId(Integer kullaniciId);

    Puanlar save(Puanlar thePuanlar);

}
