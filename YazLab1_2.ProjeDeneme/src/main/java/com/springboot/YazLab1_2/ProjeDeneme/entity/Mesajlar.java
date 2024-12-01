package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Mesajlar")
public class Mesajlar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mesajId;

    @Column(name = "Gönderici_ID", nullable = false)
    private Integer gonderici;

    @Column(name = "Alıcı_ID", nullable = false)
    private Integer alici;

    @Column(name = "Mesaj_Metni")
    private String mesajMetni;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Gönderim_Zamanı")
    private Date gonderimZamani;

    public Mesajlar(){

    }

    public Mesajlar(Integer mesajId, Integer gonderici, Integer alici, String mesajMetni, Date gonderimZamani) {
        this.mesajId = mesajId;
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

    public Integer getGonderici() {
        return gonderici;
    }

    public void setGonderici(Integer gonderici) {
        this.gonderici = gonderici;
    }

    public Integer getAlici() {
        return alici;
    }

    public void setAlici(Integer alici) {
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
