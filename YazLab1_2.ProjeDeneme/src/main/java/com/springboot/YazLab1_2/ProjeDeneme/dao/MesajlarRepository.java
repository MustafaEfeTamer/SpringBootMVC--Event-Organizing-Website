package com.springboot.YazLab1_2.ProjeDeneme.dao;

import com.springboot.YazLab1_2.ProjeDeneme.entity.Mesajlar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesajlarRepository extends JpaRepository<Mesajlar, Integer> {
    List<Mesajlar> findByAlici(Integer id);
}
