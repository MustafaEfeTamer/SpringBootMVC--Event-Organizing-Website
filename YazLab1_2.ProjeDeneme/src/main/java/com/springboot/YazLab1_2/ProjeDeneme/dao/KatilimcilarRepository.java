package com.springboot.YazLab1_2.ProjeDeneme.dao;


import com.springboot.YazLab1_2.ProjeDeneme.entity.KatilimciId;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KatilimcilarRepository extends JpaRepository<Katilimcilar, KatilimciId> {
}
