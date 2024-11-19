package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.MesajlarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MesajlarServiceImpl implements MesajlarService{
    MesajlarRepository mesajlarRepository;

    @Autowired
    public MesajlarServiceImpl(MesajlarRepository mesajlarRepository) {
        this.mesajlarRepository = mesajlarRepository;
    }
}
