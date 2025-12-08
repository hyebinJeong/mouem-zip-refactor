#  .\run_cache_scenario.ps1 명령어로 실행

###############################################
#   1) 캐시 적용 전 - COLD (Cache OFF or Empty)
###############################################
# k6 run `
#   -e BASE_URL=http://localhost:8080 `
#   -e USER_ID=1 `
#   -e CHECKLIST_POST_RATE=0.1 `
#   -e MODE=cold `
#   -e LABEL=cold `
#   C:/Users/seung/Desktop/Project/mouem-zip-refactor/k6/scenarios/cache-scenario.js

###################################################
#   2) 캐시 WARM-UP (Cache 채우기 전용 - Cache ON)
###################################################
# Redis 캐시 비우기
# redis-cli FLUSHALL

# warm-up 실행 (짧고 가볍게 돌려서 캐시 채우기)
# k6 run `
#   -e BASE_URL=http://localhost:8080 `
#   -e USER_ID=1 `
#   -e CHECKLIST_POST_RATE=0 `
#   -e MODE=warm `
#   -e LABEL=warmup `
#   C:/Users/seung/Desktop/Project/mouem-zip-refactor/k6/scenarios/cache-scenario.js




##########################################
#   3) 캐시 적용 후 - HOT (Cache Fully ON)
##########################################
<# redis-cli FLUSHALL #>
k6 run `
  -e BASE_URL=http://localhost:8080 `
  -e USER_ID=1 `
  -e CHECKLIST_POST_RATE=0.1 `
  -e MODE=hot `
  -e LABEL=hot `
  C:/Users/seung/Desktop/Project/mouem-zip-refactor/k6/scenarios/cache-scenario.js

