package com.politicslive.dto;

import lombok.Data;
import java.time.LocalDate;

@Data  // getter/setter/toString 자동 생성
public class GdeltEventDto {
    private LocalDate eventDate;
    private Double avgTone;      // 여론 톤 (부정적일수록 낮음)
    private Double lat;
    private Double lng;
    private Integer intensity;   // 히트맵 강도
    private String sourceUrl;
}