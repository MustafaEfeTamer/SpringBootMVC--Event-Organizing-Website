package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Date;
import java.util.Objects;

@Embeddable
public class PuanId implements java.io.Serializable {
    private Integer kullaniciId;
    @Column(name = "Kazanilan_Tarih")
    private Date kazanilanTarih;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PuanId)) return false;
        PuanId puanId = (PuanId) o;
        return Objects.equals(kullaniciId, puanId.kullaniciId) &&
                Objects.equals(kazanilanTarih, puanId.kazanilanTarih);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kullaniciId, kazanilanTarih);
    }}
