package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Etkinlikler")
public class Etkinlikler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "etkinlik_adi", nullable = false, length = 100)
    private String etkinlikAdi;

    @Column(name= "aciklama")
    private String aciklama;

    @Column(name = "tarih")
    @Temporal(TemporalType.DATE)
    private Date tarih;

    @Column(name = "saat")
    @Temporal(TemporalType.TIME)
    private LocalTime saat;

    @Column(name = "etkinlik_suresi")
    private Integer etkinlikSuresi;

    private String konum;

    private String kategori;

    @Column(name = "resim")
    private String resimUrl;

    @OneToMany(mappedBy = "etkinlikId")
    private List<Katilimcilar> katilimcilar;


    public Etkinlikler(){

    }

    public Etkinlikler(String etkinlikAdi, String aciklama, Date tarih, LocalTime saat, Integer etkinlikSuresi, String konum, String kategori, String resimUrl) {
        this.etkinlikAdi = etkinlikAdi;
        this.aciklama = aciklama;
        this.tarih = tarih;
        this.saat = saat;
        this.etkinlikSuresi = etkinlikSuresi;
        this.konum = konum;
        this.kategori = kategori;
        this.resimUrl = resimUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEtkinlikAdi() {
        return etkinlikAdi;
    }

    public void setEtkinlikAdi(String etkinlikAdi) {
        this.etkinlikAdi = etkinlikAdi;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public Date getTarih() {
        return tarih;
    }

    public void setTarih(Date tarih) {
        this.tarih = tarih;
    }

    public LocalTime getSaat() {
        return saat;
    }

    public void setSaat(LocalTime saat) {
        this.saat = saat;
    }

    public Integer getEtkinlikSuresi() {
        return etkinlikSuresi;
    }

    public void setEtkinlikSuresi(Integer etkinlikSuresi) {
        this.etkinlikSuresi = etkinlikSuresi;
    }

    public String getKonum() {
        return konum;
    }

    public void setKonum(String konum) {
        this.konum = konum;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getResimUrl() {
        return resimUrl;
    }

    public void setResimUrl(String resimUrl) {
        this.resimUrl = resimUrl;
    }

    @Override
    public String toString() {
        return "Etkinlikler{" +
                "id=" + id +
                ", etkinlikAdi='" + etkinlikAdi + '\'' +
                ", aciklama='" + aciklama + '\'' +
                ", tarih=" + tarih +
                ", saat=" + saat +
                ", etkinlikSuresi=" + etkinlikSuresi +
                ", konum='" + konum + '\'' +
                ", kategori='" + kategori + '\'' +
                ", resimUrl='" + resimUrl + '\'' +
                ", katilimcilar=" + katilimcilar +
                '}';
    }
}

