package com.springboot.YazLab1_2.ProjeDeneme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class KatilimciId implements java.io.Serializable {
    private Integer kullaniciId;
    @Column(name = "Etkinlik_ID")
    private Integer etkinlikId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KatilimciId)) return false;
        KatilimciId katilimciId = (KatilimciId) o;
        return Objects.equals(kullaniciId, katilimciId.kullaniciId) &&
                Objects.equals(etkinlikId, katilimciId.etkinlikId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kullaniciId, etkinlikId);
    }
}
