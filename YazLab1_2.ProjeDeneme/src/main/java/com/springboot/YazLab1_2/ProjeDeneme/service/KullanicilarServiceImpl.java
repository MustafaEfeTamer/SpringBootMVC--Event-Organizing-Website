package com.springboot.YazLab1_2.ProjeDeneme.service;

import com.springboot.YazLab1_2.ProjeDeneme.dao.KullanicilarRepository;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Employee;
import com.springboot.YazLab1_2.ProjeDeneme.entity.Kullanicilar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KullanicilarServiceImpl implements KullanicilarService{
    private KullanicilarRepository kullanicilarRepository;

    @Autowired // bir tane constructor varsa isteğe bağlıdır
    public KullanicilarServiceImpl(KullanicilarRepository kullanicilarRepository) {
        this.kullanicilarRepository = kullanicilarRepository;
    }


    @Override
    public Kullanicilar findByKullaniciAdiveSifre(String kullaniciAdi, String sifre) {
        Optional<Kullanicilar> result = kullanicilarRepository.findByKullaniciAdiAndSifre(kullaniciAdi, sifre);

        Kullanicilar theKullanicilar = null;

        if(result.isPresent()){
            theKullanicilar = result.get();
        }else{
            throw new RuntimeException("Kullanıcı Adı veya Şifre Hatalı");
        }
        return theKullanicilar;
    }

    @Override
    public Kullanicilar save(Kullanicilar theKullanici) {
        return kullanicilarRepository.save(theKullanici);
    }

    @Override
    public boolean existsByEmail(String ePosta) {
        return kullanicilarRepository.findByEPosta(ePosta).isPresent();
    }

    @Override
    public boolean resetPassword(String ePosta, String sifre) {
        Optional<Kullanicilar> kullaniciOptional = kullanicilarRepository.findByEPosta(ePosta);
        if (kullaniciOptional.isPresent()) {
            Kullanicilar kullanici = kullaniciOptional.get();
            kullanici.setSifre(sifre);
            kullanicilarRepository.save(kullanici);
            return true;
        }
        return false;
    }

    @Override
    public Kullanicilar findByKullaniciAdi(String kullaniciAdi) {
        Optional<Kullanicilar> result = kullanicilarRepository.findByKullaniciAdi(kullaniciAdi);

        Kullanicilar theKullanicilar = null;

        if(result.isPresent()){
            theKullanicilar = result.get();
        }else{
            throw new RuntimeException();
        }
        return theKullanicilar;
    }
}
