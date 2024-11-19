package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.EtkinliklerRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtkinliklerServiceImpl implements EtkinliklerService{

    private EtkinliklerRepository etkinliklerRepository;

    @Autowired // bir tane constructor varsa isteğe bağlıdır
    public EtkinliklerServiceImpl(EtkinliklerRepository etkinliklerRepository) {
        this.etkinliklerRepository = etkinliklerRepository;
    }

    @Override
    public Etkinlikler save(Etkinlikler theEtkinlikler) {
        return etkinliklerRepository.save(theEtkinlikler);
    }

    @Override
    public List<Etkinlikler> findByOlusturan(Integer olusturan) {
        return etkinliklerRepository.findByOlusturan(olusturan);
    }

    @Override
    public List<Etkinlikler> findAll() {
        return etkinliklerRepository.findAll();
    }

    @Override
    public Optional<Etkinlikler> findById(Integer id) {
        return etkinliklerRepository.findById(id);
    }

    @Override
    public Optional<Etkinlikler> findByIdLong(Long id) {
        return etkinliklerRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        etkinliklerRepository.deleteById(id);
    }

}
