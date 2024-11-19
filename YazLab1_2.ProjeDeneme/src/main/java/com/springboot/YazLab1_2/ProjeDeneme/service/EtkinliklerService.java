package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;

import java.util.List;
import java.util.Optional;

public interface EtkinliklerService {
    Etkinlikler save(Etkinlikler theEtkinlikler);
    List<Etkinlikler> findByOlusturan(Integer olusturan);
    List<Etkinlikler> findAll();
    Optional<Etkinlikler> findById(Integer id);
    Optional<Etkinlikler> findByIdLong(Long id);
    void deleteById(Integer id);
}
