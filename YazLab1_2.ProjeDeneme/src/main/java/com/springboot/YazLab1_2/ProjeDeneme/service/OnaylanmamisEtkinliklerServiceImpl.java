package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.OnaylanmamisEtkinliklerRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.OnaylanmamisEtkinlikler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OnaylanmamisEtkinliklerServiceImpl implements OnaylanmamisEtkinliklerService{
    private OnaylanmamisEtkinliklerRepository onaylanmamisEtkinliklerRepository;

    @Autowired
    public OnaylanmamisEtkinliklerServiceImpl(OnaylanmamisEtkinliklerRepository onaylanmamisEtkinliklerRepository) {
        this.onaylanmamisEtkinliklerRepository = onaylanmamisEtkinliklerRepository;
    }

    @Override
    public OnaylanmamisEtkinlikler save(OnaylanmamisEtkinlikler onaylanmamisEtkinlikler) {
        return onaylanmamisEtkinliklerRepository.save(onaylanmamisEtkinlikler);
    }

    @Override
    public void delete(OnaylanmamisEtkinlikler onaylanmamisEtkinlikler) {
        onaylanmamisEtkinliklerRepository.delete(onaylanmamisEtkinlikler);
    }

    @Override
    public List<OnaylanmamisEtkinlikler> findAll() {
        return onaylanmamisEtkinliklerRepository.findAll();
    }

    @Override
    public Optional<OnaylanmamisEtkinlikler> findById(Integer id) {
        return onaylanmamisEtkinliklerRepository.findById(id);
    }

}
