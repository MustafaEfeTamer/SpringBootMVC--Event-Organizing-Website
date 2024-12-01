package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Mesajlar;

import java.util.List;

public interface MesajlarService {
    List<Mesajlar> findByAliciId(Integer id);
    Mesajlar save(Mesajlar theMesaj);
}
