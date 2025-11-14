#  .\run_cache_scenario.ps1 명령어로 실행

<# 캐시 적용 전 - Cold #>
# k6 run `
#   -e BASE_URL=http://localhost:8080 `
#   -e USER_ID=1 `
#   -e CHECKLIST_POST_RATE=0.1 `
#   -e MODE=cold `
#   -e LABEL=cold `
#   C:/Users/seung/Desktop/Project/mouem-zip-refactor/k6/scenarios/cache-scenario.js


<# 캐시 적용 후 - Hot #>
<# redis-cli FLUSHALL #>
k6 run `
  -e BASE_URL=http://localhost:8080 `
  -e USER_ID=1 `
  -e CHECKLIST_POST_RATE=0.1 `
  -e MODE=hot `
  -e LABEL=hot `
  C:/Users/seung/Desktop/Project/mouem-zip-refactor/k6/scenarios/cache-scenario.js

