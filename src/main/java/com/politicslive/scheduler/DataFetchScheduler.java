package com.politicslive.scheduler;

import com.politicslive.service.GdeltService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling  // 메인 클래스에도 @EnableScheduling 추가 필요!
@RequiredArgsConstructor
public class DataFetchScheduler {

    private final GdeltService gdeltService;

    @Scheduled(cron = "0 */15 * * * *")  // 매 15분
    public void fetchGdelt() {
        log.info("GDELT 업데이트 스케줄 시작");
        gdeltService.fetchAndParseLatestGdelt();
    }
}