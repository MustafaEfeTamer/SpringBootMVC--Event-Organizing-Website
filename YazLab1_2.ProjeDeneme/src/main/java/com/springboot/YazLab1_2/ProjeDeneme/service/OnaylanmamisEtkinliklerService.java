package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.entity.OnaylanmamisEtkinlikler;

import java.util.List;
import java.util.Optional;

public interface OnaylanmamisEtkinliklerService {
    OnaylanmamisEtkinlikler save(OnaylanmamisEtkinlikler onaylanmamisEtkinlikler);
    void delete(OnaylanmamisEtkinlikler onaylanmamisEtkinlikler);
    List<OnaylanmamisEtkinlikler> findAll();
    Optional<OnaylanmamisEtkinlikler> findById(Integer id);


}
