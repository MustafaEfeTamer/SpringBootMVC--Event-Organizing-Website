package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.PuanlarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PuanlarServiceImpl implements PuanlarService{
        PuanlarRepository puanlarRepository;
        @Autowired
        public PuanlarServiceImpl(PuanlarRepository puanlarRepository) {
            this.puanlarRepository = puanlarRepository;
        }
}
