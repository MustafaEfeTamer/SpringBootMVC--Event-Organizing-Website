package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.MesajlarRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Mesajlar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesajlarServiceImpl implements MesajlarService{
    MesajlarRepository mesajlarRepository;

    @Autowired
    public MesajlarServiceImpl(MesajlarRepository mesajlarRepository) {
        this.mesajlarRepository = mesajlarRepository;
    }

    @Override
    public List<Mesajlar> findByAliciId(Integer id) {
        return mesajlarRepository.findByAlici(id);
    }

    @Override
    public Mesajlar save(Mesajlar theMesaj) {
        return mesajlarRepository.save(theMesaj);
    }
}
