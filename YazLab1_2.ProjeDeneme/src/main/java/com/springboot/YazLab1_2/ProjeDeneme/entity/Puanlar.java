package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Puanlar")
public class Puanlar {

    @EmbeddedId
    private PuanId id;

    @ManyToOne
    @MapsId("kullaniciId")
    @JoinColumn(name = "Kullanıcı_ID")
    private Kullanicilar kullanici;

    @Column(name = "Puan", nullable = false)
    private Integer puan;

    @Temporal(TemporalType.DATE)
    @Column(name = "Kazanilan_Tarih", insertable = false, updatable = false)
    private Date kazanilanTarih;

    public Puanlar(){

    }

    public Puanlar(PuanId id, Kullanicilar kullanici, Integer puan, Date kazanilanTarih) {
        this.id = id;
        this.kullanici = kullanici;
        this.puan = puan;
        this.kazanilanTarih = kazanilanTarih;
    }

    public PuanId getId() {
        return id;
    }

    public void setId(PuanId id) {
        this.id = id;
    }

    public Kullanicilar getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanicilar kullanici) {
        this.kullanici = kullanici;
    }

    public Integer getPuan() {
        return puan;
    }

    public void setPuan(Integer puan) {
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
                ", kullanici=" + kullanici +
                ", puan=" + puan +
                ", kazanilanTarih=" + kazanilanTarih +
                '}';
    }
}