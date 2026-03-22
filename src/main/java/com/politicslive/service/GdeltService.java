package com.politicslive.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.politicslive.dto.GdeltEventDto;
import com.politicslive.mapper.GdeltEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GdeltService {

    private final GdeltEventMapper gdeltEventMapper;
    private final RestTemplate restTemplate = new RestTemplate();  // 간단 다운로드용

    public void fetchAndParseLatestGdelt() {
        try {
            // 1. lastupdate.txt 다운로드 → 최신 파일 URL 추출 (첫 줄)
            String lastUpdateUrl = "http://data.gdeltproject.org/gdeltv2/lastupdate.txt";
            String content = restTemplate.getForObject(lastUpdateUrl, String.class);
            if (content == null || content.isEmpty()) {
                log.error("GDELT lastupdate.txt 빈 내용");
                return;
            }
            
            String latestFileUrl = content.lines().findFirst().orElse("").split(" ")[2];  // 세 번째 필드 = URL

            if (latestFileUrl.isEmpty()) {
                log.error("최신 GDELT 파일 URL 추출 실패");
                return;
            }

            log.info("최신 GDELT 파일 다운로드 시작: {}", latestFileUrl);

            // 2. ZIP 파일 다운로드 → 스트림으로 파싱 (메모리 절약)
            try (InputStream in = new URL(latestFileUrl).openStream();
                ZipInputStream zis = new ZipInputStream(in);
                InputStreamReader isr = new InputStreamReader(zis);
                CSVReader csvReader = new CSVReaderBuilder(isr).withSkipLines(0).withCSVParser(new com.opencsv.CSVParserBuilder().withSeparator('\t').build()).build()) {

                zis.getNextEntry();  // ZIP 안에 하나의 TSV 파일

                String[] line;
                int count = 0;
                int batchSize = 500;
                List<GdeltEventDto> batchList = new ArrayList<>(batchSize);

                while ((line = csvReader.readNext()) != null) {
                    if (line.length < 58 || line[57].trim().isEmpty() || line[58].trim().isEmpty()) continue;

                    String latStr = line[57].trim();
                    String lngStr = line[58].trim();
                    if (latStr.isEmpty() || lngStr.isEmpty()) continue;  // 위치 없으면 스킵

                    GdeltEventDto dto = new GdeltEventDto();
                    dto.setEventDate(java.time.LocalDate.now());  // 간단히 오늘 날짜
                    dto.setLat(Double.parseDouble(latStr));
                    dto.setLng(Double.parseDouble(lngStr));

                    // 톤 + 강도 → intensity 계산 (예: |Goldstein| + |Tone| 조합)
                    double tone = line[31].isEmpty() ? 0 : Double.parseDouble(line[31]);
                    double goldstein = line[28].isEmpty() ? 0 : Double.parseDouble(line[28]);
                    int intensity = (int) Math.abs(goldstein * 2 + tone);  // 예시 공식 (조정 가능)

                    dto.setAvgTone(tone);
                    dto.setIntensity(intensity > 0 ? intensity : 5);  // 최소 강도
                    dto.setSourceUrl(latestFileUrl);

                    gdeltEventMapper.insertEvent(dto);

                    if (++count % 1000 == 0) {
                        log.info("파싱 중: {}개 이벤트 처리", count);
                    }

                    if (count > 5000) break;  // 테스트용 제한 (전체 파일은 수만 건 – 나중에 제거)
                }

                log.info("GDELT 파싱 완료: {}개 이벤트 저장", count);

            }
        } catch (Exception e) {
            log.error("GDELT fetch/parse 실패", e);
        }
    }

    // 히트맵용 데이터 조회 (Controller에서 호출)
    public List<double[]> getRecentHeatPoints() {
        return gdeltEventMapper.selectRecentHeatPoints().stream()
                .map(map -> new double[]{
                        (Double) map.get("lat"),
                        (Double) map.get("lng"),
                        ((Number) map.get("intensity")).doubleValue()
                })
                .toList();
    }
}