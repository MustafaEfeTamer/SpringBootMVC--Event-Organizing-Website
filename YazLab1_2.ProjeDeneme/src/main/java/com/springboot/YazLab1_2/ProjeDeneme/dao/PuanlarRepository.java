package com.springboot.YazLab1_2.ProjeDeneme.dao;

import com.springboot.YazLab1_2.ProjeDeneme.entity.PuanId;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Puanlar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuanlarRepository extends JpaRepository<Puanlar, PuanId> {
}
