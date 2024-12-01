package com.springboot.YazLab1_2.ProjeDeneme.dao;


import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KatilimcilarRepository extends JpaRepository<Katilimcilar, Long> {
    List<Katilimcilar> findByKullaniciId(Integer kullaniciId);
    void deleteByKullaniciIdAndEtkinlikId(Long kullaniciId, Long etkinlikId);
    List<Katilimcilar> findByEtkinlikId(Long etkinlikId);

}
