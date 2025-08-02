package org.scoula.finalreport.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.finalreport.mapper.FinalReportMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class FinalReportScheduler {

    private final FinalReportMapper finalReportMapper;

    // 테스트용 (1분마다 돌아가게하기)
   //  @Scheduled(cron = "0 */1 * * * *")
     @Scheduled(cron = "0 0 3 * * *") // 사용자, 개발자 모두 잘 접속하지 않는 새벽 3시로 설정
    public void expireFinalReports() {
        int updated = finalReportMapper.expireOldReports();
        log.info("리포트 " + updated + "건이 자동 만료되었습니다.");
    }
}
