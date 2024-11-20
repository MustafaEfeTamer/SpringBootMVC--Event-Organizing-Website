package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Puanlar")
public class Puanlar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    @Column(name = "Kullanıcı_ID",nullable = false)
    private int kullaniciId;
    @Column(name = "Puan",nullable = false)
    private Long puan;
    @Column(name = "Kazanilan_Tarih", nullable = false)
    private Date kazanilanTarih;


    public Puanlar(){

    }

    public Puanlar(Long id, int kullaniciId, Long puan, Date kazanilanTarih) {
        this.id = id;
        this.kullaniciId = kullaniciId;
        this.puan = puan;
        this.kazanilanTarih = kazanilanTarih;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(int kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public Long getPuan() {
        return puan;
    }

    public void setPuan(Long puan) {
        this.puan = puan;
    }

    public Date getKazanilanTarih() {
        return kazanilanTarih;
    }

    public void setKazanilanTarih(Date kazanilanTarih) {
        this.kazanilanTarih = kazanilanTarih;
    }

    @Override
    public String toString() {
        return "Puanlar{" +
                "id=" + id +
                ", kullaniciId=" + kullaniciId +
                ", puan=" + puan +
                ", kazanilanTarih=" + kazanilanTarih +
                '}';
    }
}