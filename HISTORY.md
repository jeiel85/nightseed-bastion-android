# HISTORY.md

## 2026-05-17 (2)

- 작업: 저장소 초기 푸시 · README 강화 · GitHub Pages 사이트 · Godot 프로젝트 골격 생성
- 변경 파일:
  - `README.md`: 키 비주얼/배지/코어 루프 다이어그램 포함한 공개용 README 재작성
  - `docs/index.md`, `docs/_config.yml`, `docs/assets/img/key-visual.png`: GitHub Pages 사이트 (cayman 테마)
  - `project.godot`: Godot 4.x 세로 모바일 프로젝트 정의 + autoload 등록
  - `scripts/autoload/*.gd`: AppConfig · SceneRouter · SaveManager · DataRegistry · LocalizationService · AudioBus · InputService · RunManager 스텁
  - `scripts/core/EventBus.gd`, `scripts/core/RunStateMachine.gd`, `scripts/core/Boot.gd`
  - `scripts/game/CombatResolver.gd`, `scripts/ui/MainMenuController.gd`
  - `scenes/boot/Boot.tscn`, `scenes/menu/MainMenu.tscn`
  - `tests/manual/smoke_boot.md`: 부트 스모크 체크리스트
  - `docs/20_DECISION_LOG.md`: Pages 호스팅 · Godot 셸 도입 결정 기록
- 검증:
  - 로컬: 파일 구조 확인. Godot 헤드리스 실행은 아직 수행하지 않음.
  - CI: 미구성.
  - 생략한 검증: 실 Godot 에디터 부팅, JSON 검증 결과 시각 확인.
- 결과: P0-001 (Godot 프로젝트 셸) 및 P0-002 (오토로드 매니저) 초기 자산 마련. 푸시 후 P0-003부터 본격 구현 가능.
- 후속 작업:
  - GitHub Pages 활성화 (`main` 브랜치 `/docs` 폴더) 후 빌드 결과 확인
  - Godot 에디터로 실제 부팅하여 데이터 검증 메시지 점검
  - P0-003 RunStateMachine 디버그 오버레이 연결

## 2026-05-17

- 작업: Nightseed Bastion 전용 구현 설계 묶음 생성
- 변경 파일:
  - `README.md`: 프로젝트 개요 및 시작 경로 작성
  - `AGENTS.md`: Bastion 전용 AI 작업 규칙 작성
  - `CHANGELOG.md`: v0.1.0 초기 문서 변경 기록
  - `docs/*`: 제품, 게임, 시스템, 기술, UX, 아트, 출시, QA 문서 작성
  - `data/*.json`: 초기 구현용 샘플 데이터 작성
  - `.github/*`: 이슈 및 PR 템플릿 작성
  - `assets/concept/main_menu_concept_generated.png`: 메인화면 콘셉트 이미지 포함
- 검증:
  - ZIP 파일 구조 생성 확인
  - JSON 파일 파싱 가능 여부 확인 예정
- 결과: 설계 패키지 생성 완료
- 후속 작업:
  - GitHub 저장소 `nightseed-bastion` 생성
  - Godot 4.x 프로젝트 생성
  - P0-001부터 구현 시작
