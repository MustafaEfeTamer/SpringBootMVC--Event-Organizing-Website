package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;


import java.util.List;

public interface KatilimcilarService {
    List<Katilimcilar> findByKullaniciId(Integer kullaniciId);
    Katilimcilar save(Katilimcilar theKatilimci);
}
