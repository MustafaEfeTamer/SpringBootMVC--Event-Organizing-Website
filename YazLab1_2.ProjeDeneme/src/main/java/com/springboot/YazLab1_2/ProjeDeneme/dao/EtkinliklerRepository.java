package com.springboot.YazLab1_2.ProjeDeneme.dao;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EtkinliklerRepository extends JpaRepository<Etkinlikler, Integer> {
    List<Etkinlikler> findByOlusturan(Integer olusturan);
}
