package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "onaylanmamıs_etkinlikler")  // Tablo adı
public class OnaylanmamisEtkinlikler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "etkinlik_adi", nullable = false, length = 100)
    private String etkinlikAdi;

    @Column(name = "aciklama", columnDefinition = "TEXT")
    private String aciklama;

    @Column(name = "tarih", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date tarih;

    @Column(name = "saat", nullable = false)
    @Temporal(TemporalType.TIME)
    private LocalTime saat;

    @Column(name = "etkinlik_suresi", nullable = false)
    private Integer etkinlikSuresi;

    @Column(name = "konum", nullable = false, length = 100)
    private String konum;

    @Column(name = "kategori", nullable = false, length = 50)
    private String kategori;

    @Column(name = "resim", nullable = true, length = 255)
    private String resim;

    @Column(name = "olusturan", nullable = false)
    private Integer olusturan;  // Etkinliği oluşturan kullanıcının ID'si


    public OnaylanmamisEtkinlikler(){

    }

    public OnaylanmamisEtkinlikler(Integer id, String etkinlikAdi, String aciklama, Date tarih, LocalTime saat, Integer etkinlikSuresi, String konum, String kategori, String resim, Integer olusturan) {
        this.id = id;
        this.etkinlikAdi = etkinlikAdi;
        this.aciklama = aciklama;
        this.tarih = tarih;
        this.saat = saat;
        this.etkinlikSuresi = etkinlikSuresi;
        this.konum = konum;
        this.kategori = kategori;
        this.resim = resim;
        this.olusturan = olusturan;
    }

    // Getter ve Setter metodları
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

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public Integer getOlusturan() {
        return olusturan;
    }

    public void setOlusturan(Integer olusturan) {
        this.olusturan = olusturan;
    }

    @Override
    public String toString() {
        return "OnaylanmamisEtkinlikler{" +
                "id=" + id +
                ", etkinlikAdi='" + etkinlikAdi + '\'' +
                ", aciklama='" + aciklama + '\'' +
                ", tarih=" + tarih +
                ", saat=" + saat +
                ", etkinlikSuresi=" + etkinlikSuresi +
                ", konum='" + konum + '\'' +
                ", kategori='" + kategori + '\'' +
                ", resim='" + resim + '\'' +
                ", olusturan=" + olusturan +
                '}';
    }
}
