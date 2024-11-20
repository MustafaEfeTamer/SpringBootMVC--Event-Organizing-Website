package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.PuanlarRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Puanlar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PuanlarServiceImpl implements PuanlarService{
        PuanlarRepository puanlarRepository;
        @Autowired
        public PuanlarServiceImpl(PuanlarRepository puanlarRepository) {
            this.puanlarRepository = puanlarRepository;
        }

    @Override
    public Optional<Puanlar> findByKullaniciId(Integer kullaniciId) {
        return puanlarRepository.findByKullaniciId(kullaniciId);
    }

    @Override
    public Puanlar save(Puanlar thePuanlar) {
        return puanlarRepository.save(thePuanlar);
    }
}
