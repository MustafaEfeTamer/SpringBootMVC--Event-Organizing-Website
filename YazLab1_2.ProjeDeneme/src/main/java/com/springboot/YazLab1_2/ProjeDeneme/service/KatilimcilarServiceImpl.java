package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.KatilimcilarRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Katilimcilar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class KatilimcilarServiceImpl implements KatilimcilarService{

    private KatilimcilarRepository katilimcilarRepository;

    @Autowired
    public KatilimcilarServiceImpl(KatilimcilarRepository katilimcilarRepository) {
        this.katilimcilarRepository = katilimcilarRepository;
    }


    @Override
    public List<Katilimcilar> findByKullaniciId(Integer kullaniciId) {
        return katilimcilarRepository.findByKullaniciId(kullaniciId);
    }

    @Override
    public Katilimcilar save(Katilimcilar theKatilimci) {
        return katilimcilarRepository.save(theKatilimci);
    }

    @Override
    public void deleteEventByUserIdEventId(Integer userId, Integer eventId) {
        katilimcilarRepository.deleteByKullaniciIdAndEtkinlikId(userId.longValue(), eventId.longValue());
    }

    @Override
    public List<Katilimcilar> findByEtkinlikId(Long id) {
        return katilimcilarRepository.findByEtkinlikId(id);
    }
}
