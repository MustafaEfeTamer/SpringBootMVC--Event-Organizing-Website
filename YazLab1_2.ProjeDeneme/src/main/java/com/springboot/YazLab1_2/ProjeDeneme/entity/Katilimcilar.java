package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Katılımcılar")
public class Katilimcilar {
    @EmbeddedId
    private KatilimciId katilimciId;

    @ManyToOne
    @MapsId("kullaniciId")
    @JoinColumn(name = "Kullanıcı_ID")
    private Kullanicilar kullaniciId;

    @ManyToOne
    @MapsId("etkinlikId")
    @JoinColumn(name = "Etkinlik_ID")
    private Etkinlikler etkinlikId;


    public Katilimcilar(){

    }

    public Katilimcilar(KatilimciId katilimciId, Kullanicilar kullaniciId, Etkinlikler etkinlikId) {
        this.katilimciId = katilimciId;
        this.kullaniciId = kullaniciId;
        this.etkinlikId = etkinlikId;
    }

    public KatilimciId getKatilimciId() {
        return katilimciId;
    }

    public void setKatilimciId(KatilimciId katilimciId) {
        this.katilimciId = katilimciId;
    }

    public Kullanicilar getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(Kullanicilar kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public Etkinlikler getEtkinlikId() {
        return etkinlikId;
    }

    public void setEtkinlikId(Etkinlikler etkinlikId) {
        this.etkinlikId = etkinlikId;
    }

    @Override
    public String toString() {
        return "KatilimciId{" +
                "kullaniciId=" + kullaniciId +
                ", etkinlikId=" + etkinlikId +
                '}';
    }
}