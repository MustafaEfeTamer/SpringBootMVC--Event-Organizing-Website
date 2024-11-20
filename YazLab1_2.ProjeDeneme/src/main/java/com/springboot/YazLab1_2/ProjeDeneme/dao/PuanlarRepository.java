package com.springboot.YazLab1_2.ProjeDeneme.dao;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Puanlar;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PuanlarRepository extends JpaRepository<Puanlar, Long> {
    Optional<Puanlar> findByKullaniciId(Integer kullaniciId);

}
