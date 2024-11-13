package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.EtkinliklerRepository;
import com.springboot.YazLab1_2.ProjeDeneme.dao.KullanicilarRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Etkinlikler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
