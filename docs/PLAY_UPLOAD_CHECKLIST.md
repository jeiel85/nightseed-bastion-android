# Play Console 업로드 체크리스트 — Nightseed Bastion

이 문서는 사용자가 직접 수행해야 하는 단계입니다 (Play Console 계정 권한 필요).

## 사전 확인

- [ ] Play Console 계정: **pedaiah85@gmail.com** (jeiel85 아님)
- [ ] 바탕화면에 두 파일 모두 있는지:
  - `NightseedBastion-v1.0.1-vc2.aab`
  - `NightseedBastion-v1.0.1-vc2-release-notes.txt`
- [ ] 패키지명이 `com.aistudio.nightseedbastion.gamedef` 그대로인지 (AI Studio 프로젝트 표준 패키지)
- [ ] versionCode가 **2** (Play 최신 vc1 이상)

## 업로드 절차

1. https://play.google.com/console 에서 **pedaiah85** 계정 선택
2. **Nightseed Bastion** 앱 → 좌측 메뉴 **테스트 → 내부 테스트** 진입
3. **새 릴리스 만들기** 버튼
4. App Bundle 영역에 `NightseedBastion-v1.0.1-vc2.aab` 드래그
5. **출시 노트** 입력란에 `NightseedBastion-v1.0.1-vc2-release-notes.txt` 내용을 그대로 붙여넣기 (꺾쇠 태그 그대로)
6. 저장 → 검토 → **출시 시작**

## 출시 후 확인

- [ ] Play Console **출시 개요**에서 vc2가 활성화됐는지
- [ ] 내부 테스트 트랙 링크로 자기 디바이스에서 업데이트 받기
- [ ] 업데이트 후 첫 실행에서 데이터(Room DB account_state, active_run) 그대로인지 — 대장간 씨앗 재(Seed Ash) 및 영구 해금 내역 기준
- [ ] 도감(Codex Archive) 및 계정 경험치(Exp) 정상 작동
- [ ] 황혼의 전조(Omen) 및 황혼의 계약(Bargain) 정상 작동
