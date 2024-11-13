package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "kullanıcılar")
public class Kullanicilar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "kullanıcı_adi", nullable = false, length = 50)
    private String kullaniciAdi;

    @Column(name = "sifre", nullable = false, length = 100)
    private String sifre;

    @Column(name = "e_posta", nullable = false, unique = true, length = 100)
    private String ePosta;

    private String konum;

    private String ilgiAlanlari;

    @Column(nullable = false, length = 50)
    private String ad;

    @Column(nullable = false, length = 50)
    private String soyad;

    @Column(name = "dogum_tarihi")
    @Temporal(TemporalType.DATE)
    private Date dogumTarihi;

    @Column(nullable = false, length = 10)
    private String cinsiyet;

    @Column(length = 20)
    private String telefonNumarasi;

    private String profilFotografi;

   /* @OneToMany(mappedBy = "gonderici")
    private List<Mesajlar> gonderilenMesajlar;

    @OneToMany(mappedBy = "alici")
    private List<Mesajlar> alinanMesajlar;*/

    public Kullanicilar(){

    }

    public Kullanicilar(String kullaniciAdi, String sifre, String ePosta, String konum, String ilgiAlanlari, String ad, String soyad, Date dogumTarihi, String cinsiyet, String telefonNumarasi, String profilFotografi) {
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.ePosta = ePosta;
        this.konum = konum;
        this.ilgiAlanlari = ilgiAlanlari;
        this.ad = ad;
        this.soyad = soyad;
        this.dogumTarihi = dogumTarihi;
        this.cinsiyet = cinsiyet;
        this.telefonNumarasi = telefonNumarasi;
        this.profilFotografi = profilFotografi;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getePosta() {
        return ePosta;
    }

    public void setePosta(String ePosta) {
        this.ePosta = ePosta;
    }

    public String getKonum() {
        return konum;
    }

    public void setKonum(String konum) {
        this.konum = konum;
    }

    public String getIlgiAlanlari() {
        return ilgiAlanlari;
    }

    public void setIlgiAlanlari(String ilgiAlanlari) {
        this.ilgiAlanlari = ilgiAlanlari;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public Date getDogumTarihi() {
        return dogumTarihi;
    }

    public void setDogumTarihi(Date dogumTarihi) {
        this.dogumTarihi = dogumTarihi;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getTelefonNumarasi() {
        return telefonNumarasi;
    }

    public void setTelefonNumarasi(String telefonNumarasi) {
        this.telefonNumarasi = telefonNumarasi;
    }

    public String getProfilFotografi() {
        return profilFotografi;
    }

    public void setProfilFotografi(String profilFotografi) {
        this.profilFotografi = profilFotografi;
    }

   /* @Override
    public String toString() {
        return "Kullanicilar{" +
                "id=" + id +
                ", kullaniciAdi='" + kullaniciAdi + '\'' +
                ", sifre='" + sifre + '\'' +
                ", ePosta='" + ePosta + '\'' +
                ", konum='" + konum + '\'' +
                ", ilgiAlanlari='" + ilgiAlanlari + '\'' +
                ", ad='" + ad + '\'' +
                ", soyad='" + soyad + '\'' +
                ", dogumTarihi=" + dogumTarihi +
                ", cinsiyet=" + cinsiyet +
                ", telefonNumarasi='" + telefonNumarasi + '\'' +
                ", profilFotografi='" + profilFotografi + '\'' +
                ", gonderilenMesajlar=" + gonderilenMesajlar +
                ", alinanMesajlar=" + alinanMesajlar +
                '}';
    }*/

    @Override
    public String toString() {
        return "Kullanicilar{" +
                "id=" + id +
                ", kullaniciAdi='" + kullaniciAdi + '\'' +
                ", sifre='" + sifre + '\'' +
                ", ePosta='" + ePosta + '\'' +
                ", konum='" + konum + '\'' +
                ", ilgiAlanlari='" + ilgiAlanlari + '\'' +
                ", ad='" + ad + '\'' +
                ", soyad='" + soyad + '\'' +
                ", cinsiyet=" + cinsiyet +
                ", telefonNumarasi='" + telefonNumarasi + '\'' +
                ", profilFotografi='" + profilFotografi + '\'' +
                '}';
    }
}
