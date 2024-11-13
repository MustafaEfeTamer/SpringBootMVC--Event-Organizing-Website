package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Mesajlar")
public class Mesajlar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mesajId;

    @ManyToOne
    @JoinColumn(name = "Gönderici_ID", nullable = false)
    private Kullanicilar gonderici;

    @ManyToOne
    @JoinColumn(name = "Alıcı_ID", nullable = false)
    private Kullanicilar alici;

    @Column(name = "Mesaj_Metni")
    private String mesajMetni;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Gönderim_Zamanı")
    private Date gonderimZamani;

    public Mesajlar(){

    }

    public Mesajlar(Kullanicilar gonderici, Kullanicilar alici, String mesajMetni, Date gonderimZamani) {
        this.gonderici = gonderici;
        this.alici = alici;
        this.mesajMetni = mesajMetni;
        this.gonderimZamani = gonderimZamani;
    }

    public Integer getMesajId() {
        return mesajId;
    }

    public void setMesajId(Integer mesajId) {
        this.mesajId = mesajId;
    }

    public Kullanicilar getGonderici() {
        return gonderici;
    }

    public void setGonderici(Kullanicilar gonderici) {
        this.gonderici = gonderici;
    }

    public Kullanicilar getAlici() {
        return alici;
    }

    public void setAlici(Kullanicilar alici) {
        this.alici = alici;
    }

    public String getMesajMetni() {
        return mesajMetni;
    }

    public void setMesajMetni(String mesajMetni) {
        this.mesajMetni = mesajMetni;
    }

    public Date getGonderimZamani() {
        return gonderimZamani;
    }

    public void setGonderimZamani(Date gonderimZamani) {
        this.gonderimZamani = gonderimZamani;
    }

    @Override
    public String toString() {
        return "Mesajlar{" +
                "mesajId=" + mesajId +
                ", gonderici=" + gonderici +
                ", alici=" + alici +
                ", mesajMetni='" + mesajMetni + '\'' +
                ", gonderimZamani=" + gonderimZamani +
                '}';
    }
}
