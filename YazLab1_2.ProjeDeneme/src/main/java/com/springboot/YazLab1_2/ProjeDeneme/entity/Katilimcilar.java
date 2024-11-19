package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "katılımcılar")
public class Katilimcilar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    @Column(name = "kullanıcı_id",nullable = false)
    private Long kullaniciId;
    @Column(name = "etkinlik_id",nullable = false)
    private Long etkinlikId;


    public Katilimcilar(){

    }

    public Katilimcilar(Long id, Long kullaniciId, Long etkinlikId) {
        this.id = id;
        this.kullaniciId = kullaniciId;
        this.etkinlikId = etkinlikId;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(Long kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public Long getEtkinlikId() {
        return etkinlikId;
    }

    public void setEtkinlikId(Long etkinlikId) {
        this.etkinlikId = etkinlikId;
    }

    @Override
    public String toString() {
        return "Katilimcilar{" +
                "id=" + id +
                ", kullaniciId=" + kullaniciId +
                ", etkinlikId=" + etkinlikId +
                '}';
    }
}