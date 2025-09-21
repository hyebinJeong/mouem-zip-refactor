k6 run `
  -e BASE_URL=http://localhost:8080 `
  -e USER_ID=1 `
  -e WARMUP_CREATE=1 `
  -e WARMUP_LIMIT=5 `
  -e CHECKLIST_POST_RATE=0.1 `
  C:/Users/seung/Desktop/Project/mouem-zip-refactor/k6/cache-scenario.js


#  .\run-k6.ps1 명령어로 실행
