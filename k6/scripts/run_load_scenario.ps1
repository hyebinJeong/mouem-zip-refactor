k6 run `
  -e BASE_URL=http://localhost:8080 `
  -e USER_ID=1 `
  -e WARMUP_CREATE=0 `
  -e CHECKLIST_POST_RATE=0.01 `
  C:/Users/seung/Desktop/Project/mouem-zip-refactor/k6/scenarios/load-scenario.js


#  .\run_load_scenario.ps1 명령어로 실행
