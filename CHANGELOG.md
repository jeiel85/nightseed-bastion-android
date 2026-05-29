# CHANGELOG.md

## Unreleased

### Added
- `New Run` 버튼에서 기본 Moonwell Bastion 런을 시작하고 mock `GameRoot`로 진입
- P0-003용 `GameRoot` mock 화면, 상태 디버그 오버레이, 수동 phase 진행 버튼
- Night mock 결과를 승리/패배로 처리하는 RunStateMachine 연결
- 프로토타입 이미지 자산 9종을 `assets/art/` 하위 폴더로 정리
- `assets/art/README.md`에 자산 출처, 용도, 크기, 사용 주의사항 기록

### Documentation
- P0-003 백로그 완료 상태, 작업 이력, mock GameRoot 결정 기록
- `docs/08_ART_AUDIO_GUIDE.md`에 프로토타입 이미지 자산 인벤토리 안내 추가

### Verification
- `godot --headless --path . --quit` 실행 시도 실패: 현재 환경에서 `godot` 명령을 찾을 수 없음

## v0.1.1 - 2026-05-17

### Added
- 공개용 README 재작성 (키 비주얼, 배지, 코어 루프 다이어그램, 문서 인덱스)
- GitHub Pages 사이트 (`docs/index.md` + `docs/_config.yml` + cayman 테마)
- Godot 4.x 프로젝트 셸 (`project.godot`, autoload 7종 등록)
- Autoload 스크립트 스텁: AppConfig · SceneRouter · SaveManager · DataRegistry · LocalizationService · AudioBus · InputService · RunManager
- 코어 스크립트: EventBus, RunStateMachine, Boot, CombatResolver
- 플레이스홀더 씬: `Boot.tscn`, `MainMenu.tscn`
- 부트 스모크 테스트 체크리스트 (`tests/manual/smoke_boot.md`)

### Documentation
- `docs/20_DECISION_LOG.md`에 Pages 호스팅 및 Godot 셸 결정 기록

### Verification
- 파일 구조 및 스크립트 컴파일은 Godot 실 엔진에서 아직 검증되지 않음 (후속 작업)

## v0.1.0 - 2026-05-17

### Added
- Nightseed Bastion 전용 제품 방향, 게임 설계서, 시스템 설계서, 기술 설계서 초안 추가
- Godot 4.x Android-first 개발을 위한 저장소 스켈레톤 정의
- 낮/황혼/밤/새벽 루프, 성채 건설, 영웅 조작, 웨이브 방어, Dusk Bargain 시스템 정의
- 구현 가능한 JSON 데이터 샘플과 저장 데이터 버전 정책 추가
- GitHub 이슈/PR 템플릿, 릴리즈 노트, Play Store 초안 추가
- 생성형 콘셉트 아트 `assets/concept/main_menu_concept_generated.png` 추가

### Documentation
- `README.md`, `AGENTS.md`, `HISTORY.md`, `docs/*` 초기 구성

### Verification
- 문서 패키지 생성 및 ZIP 구조 확인
- 실제 Godot 빌드/테스트는 아직 실행하지 않음
