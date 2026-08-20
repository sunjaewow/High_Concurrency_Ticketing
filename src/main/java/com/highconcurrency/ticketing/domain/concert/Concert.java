package com.highconcurrency.ticketing.domain.concert;

import com.highconcurrency.ticketing.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "concerts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Concert extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int totalSeatCount;

    public static Concert create(String title, int totalSeatCount) {
        return Concert.builder()
                .title(title)
                .totalSeatCount(totalSeatCount)
                .build();
    }
}
