package com.politicslive.mapper;

import com.politicslive.dto.GdeltEventDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper  // MyBatis가 자동으로 빈 등록
public interface GdeltEventMapper {

    void insertEvent(GdeltEventDto dto);

    List<Map<String, Object>> selectRecentHeatPoints();
}