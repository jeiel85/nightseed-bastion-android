# Nightseed Bastion · 나이트시드 바스티온

<p align="center">
  <img src="key-visual/Nightseed%20Bastion.png" alt="Nightseed Bastion Key Visual" width="640" />
</p>

<p align="center">
  <a href="https://jeiel85.github.io/nightseed-bastion-preproduction/">🌐 Landing Site</a> ·
  <a href="https://play.google.com/store/apps/details?id=com.jeiel85.nightseedbastion">▶️ Play Store</a> ·
  <a href="docs/00_START_HERE.md">🚀 Start Here</a> ·
  <a href="docs/02_GAME_DESIGN_DOCUMENT.md">📘 Game Design Doc</a> ·
  <a href="docs/privacy.html">🔒 Privacy</a> ·
  <a href="CHANGELOG.md">📝 Changelog</a>
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.jeiel85.nightseedbastion">
    <img src="https://img.shields.io/badge/Google%20Play-Internal%20Testing-3DDC84?logo=google-play&logoColor=white" alt="Google Play status" />
  </a>
  <img src="https://img.shields.io/badge/build-Kotlin%20%2B%20Compose-7F52FF?logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/design-Godot%204.x-478CBF?logo=godot-engine&logoColor=white" />
  <img src="https://img.shields.io/badge/platform-Android%20first-3DDC84?logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/orientation-Portrait%209%3A16-7C3AED" />
  <img src="https://img.shields.io/badge/locale-ko%20%2F%20en-orange" />
  <img src="https://img.shields.io/badge/monetization-free%20%C2%B7%20no%20ads-success" />
</p>

<p align="center">
  <a href="docs/screenshots/phone-01.png"><img src="docs/screenshots/phone-01.png" width="160" alt="Main menu" /></a>
  <a href="docs/screenshots/phone-02.png"><img src="docs/screenshots/phone-02.png" width="160" alt="Day phase build" /></a>
  <a href="docs/screenshots/phone-03.png"><img src="docs/screenshots/phone-03.png" width="160" alt="Deploy defenses" /></a>
  <a href="docs/screenshots/phone-04.png"><img src="docs/screenshots/phone-04.png" width="160" alt="Dusk omens & bargains" /></a>
  <a href="docs/screenshots/phone-05.png"><img src="docs/screenshots/phone-05.png" width="160" alt="Night combat" /></a>
</p>
<p align="center"><sub>Main menu · Day build · Deploy menu · Dusk bargain · Night combat</sub></p>

---

> ### 📌 현재 상태 / Current status (2026-05)
> 이 저장소의 **실제 제품 구현은 Android 네이티브 앱**입니다 — Kotlin + Jetpack Compose, 소스는 [`app/`](app/), 빌드는 Gradle. Play Console Internal Testing에 **v1.0.6 (vc9)** 까지 올라가 있습니다.
>
> 함께 보존된 **Godot 4.x 프로젝트**([`project.godot`](project.godot), [`scenes/`](scenes), [`scripts/*.gd`](scripts))와 설계 문서([`docs/00_START_HERE.md`](docs/00_START_HERE.md) ~ `docs/20_*`)는 **초기 프리프로덕션 계획 자료**입니다. 과거엔 Godot 구현으로 계획했으나 **현재 빌드·출시는 Android 네이티브 기준**이며, 설계 의도를 남겨두기 위해 함께 둡니다.
>
> *This repo's shipping product is now the **Android-native app** (Kotlin + Jetpack Compose under [`app/`](app/)). The Godot 4.x project and design docs are kept as historical preproduction planning.*

## 한눈에 보기 / At a Glance

**Nightseed Bastion**(나이트시드 바스티온)은 세로 모바일 화면에 최적화된 **요새 생존 전략 액션** 게임입니다. 플레이어는 저주받은 달빛 성채의 마지막 지배자로서 *낮 → 황혼 → 밤 → 새벽*을 반복하며, 성채 한가운데 박힌 **나이트시드(Nightseed)** 를 정화할 때까지 버텨내야 합니다.

> **Build a cursed moonlit bastion by day. Read the enemy omen at dusk.  
> Defend the walls at night. Survive enough nights to cleanse the Nightseed at the heart of the fortress.**

| 항목 | 값 |
| --- | --- |
| 프로젝트 코드 | `NSB` |
| 패키지 ID | `com.jeiel85.nightseedbastion` |
| 엔진 | **Android 네이티브 (Kotlin + Jetpack Compose)** · 설계 프로토타입은 Godot 4.x |
| 1차 플랫폼 | Android (세로 9:16) |
| 2차 플랫폼 | iOS (안드로이드 파이프라인 안정화 후) |
| 1차 언어 | 한국어 |
| 2차 언어 | English |
| 현재 단계 | Android 네이티브 구현 진행 중 · Play Console Internal Testing (v1.0.6 / vc9) |

---

## ✨ 핵심 차별화 / Pillars

1. **세로 한 손 전략 (Vertical One-Thumb Strategy)** — PC RTS를 그대로 옮긴 UI 대신, 엄지 하나로 끝낼 수 있도록 처음부터 9:16 모바일 레이아웃으로 설계되었습니다.
2. **황혼 오멘 (Dusk Omen)** — 전투 시작 전, 어느 레인에 어떤 적이 올지 미리 읽고 대비할 수 있는 정보 단계가 있습니다.
3. **황혼 거래 (Dusk Bargain)** — 더 강한 보상을 위해 저주받은 거래를 받아들이는 위험-보상 선택지가 매 밤마다 등장합니다.
4. **영웅 × 요새 하이브리드** — 영웅은 직접 조작하고, 타워·벽·함정은 자동으로 작동합니다. 직접 영향력과 시스템 방어가 동시에.
5. **런 기반 성채 진화** — 한 판마다 임시 성채가 만들어지고, 메타 진척으로 영구 해금이 풀립니다. *no gacha, no pay-to-win*.

### 세션 길이 / Target Session Lengths

| 모드 | 목표 시간 |
| --- | ---: |
| 한 번의 밤 | 90–150초 |
| 풀 런 (승리) | 12–18분 |
| 실패한 런 | 4–12분 |

---

## 🌗 코어 루프 / Core Loop

```text
        ┌────────────────────────────────────────────────┐
        │                                                │
        ▼                                                │
 ┌──────────┐   ┌──────────┐   ┌────────────┐   ┌──────────────┐
 │ 새벽 보상 │ → │ 낮 건설  │ → │ 황혼 오멘  │ → │ 황혼 거래    │
 │  Dawn    │   │  Day     │   │  Dusk Omen │   │  Dusk Bargain│
 └──────────┘   └──────────┘   └────────────┘   └──────┬───────┘
        ▲                                                 │
        │                                                 ▼
        │                                          ┌──────────┐
        │                                          │ 밤 전투  │
        │                                          │  Night   │
        │                                          └────┬─────┘
        │                                               │
        │   ┌─────────────┐    승리/패배 판정             │
        └───┤ 밤 결산     │◀──────────────────────────────┘
            │ Resolution  │
            └─────────────┘
```

자세한 상태 머신은 [docs/03_CORE_LOOP_AND_STATE_MACHINE.md](docs/03_CORE_LOOP_AND_STATE_MACHINE.md)를 참고하세요.

---

## 🏰 첫 수직 슬라이스 콘텐츠 / Vertical Slice Content

첫 플레이 가능 빌드는 단순 MVP가 아니라 **프로덕션 품질의 수직 슬라이스**입니다.

| 카테고리 | 콘텐츠 |
| --- | --- |
| 맵 | `moonwell_bastion` — 3 레인 / 7박 / 보스 밤 1회 |
| 영웅 | Vagrant Warden (방랑 파수꾼) — 근접 / 마크 / 라스트 랜턴 패시브 |
| 건물 6종 | Bastion Core · Moonwell · Watchtower · Ember Brazier · Thorn Wall · Grave Snare |
| 적 5종 + 보스 | Huskling · Bone Runner · Lantern Eater · Grave Brute · Hex Archer · **Nightseed Herald** (보스) |
| 자원 | Moonshards · Ember · Seed Ash (메타) · Hope |
| 결과물 | 안드로이드 디버그 APK · 내부 테스트용 AAB |

상세 정의는 [`data/*.json`](data) 및 [docs/02_GAME_DESIGN_DOCUMENT.md](docs/02_GAME_DESIGN_DOCUMENT.md), [docs/05_LEVEL_AND_CONTENT_DESIGN.md](docs/05_LEVEL_AND_CONTENT_DESIGN.md)에서 관리됩니다.

---

## 🗺️ 폴더 구조 / Repository Map

```text
nightseed-bastion-android/
│
│  ── 실제 제품 / Shipping product (Android 네이티브) ──
├── app/                 Kotlin + Jetpack Compose — 모든 게임플레이 코드
│                        (data/ · game/GameViewModel · ui/screens + BattleSprites)
├── build.gradle.kts · settings.gradle.kts · gradle/   Gradle 빌드
├── scripts/             export-play-store-release.ps1 (AAB 내보내기·검증)
├── store_assets/        Play Store 아이콘 · 피처 그래픽
│
│  ── 초기 프리프로덕션 계획 자료 / Historical preproduction (빌드 제외) ──
├── assets/              아트, 오디오, 폰트, 콘셉트 레퍼런스
│   ├── art/             buildings · characters · enemies · ui
│   ├── audio/           bgm · sfx
│   └── concept/         AI/수작업 콘셉트 이미지
├── data/                JSON 게임 데이터 (밸런스의 단일 출처)
│   ├── bargains.json    황혼 거래 카드
│   ├── buildings.json   건물 정의
│   ├── enemies.json     적 정의
│   ├── heroes.json      영웅 정의
│   ├── maps.json        맵 / 레인 / 건설 슬롯
│   ├── rewards.json     새벽 보상 풀
│   └── waves.json       밤별 웨이브 스폰 스크립트
├── docs/                설계 / 기술 / 출시 / QA 문서
├── key-visual/          외부 공개용 키 비주얼
├── localization/        strings_ko.csv · strings_en.csv
├── play_store/          스토어 카피, 릴리즈 노트
├── scenes/              Godot 씬 타깃 (boot / game / menu / ui)
├── scripts/             GDScript 구현 타깃 (autoload / core / game / ui)
├── tests/               수동 / 자동 테스트
├── tools/               JSON 검증 등 콘텐츠 툴링
├── project.godot        Godot 4.x 프로젝트 파일
├── AGENTS.md            AI 코딩 에이전트 작업 규칙
├── CHANGELOG.md         공개용 변경 요약
├── HISTORY.md           내부 작업 이력
└── MANIFEST.txt         설계 패키지 파일 목록
```

---

## 🧱 기술 골격 / Tech Foundation

- **Stack:** Android 네이티브 · Kotlin · Jetpack Compose (Material 3)
- **Architecture:** `GameViewModel` 단일 상태 머신(StateFlow) + Compose 화면 + Room 영속화
- **Rendering:** 전투 화면은 Compose `Canvas`에 절차적으로 그림 — 외부 스프라이트 없이 적/영웅/건물을 도형으로 렌더 ([BattleSprites.kt](app/src/main/java/com/jeiel85/nightseedbastion/ui/screens/BattleSprites.kt))
- **Save:** Room DB(`account_state`, `active_run`) + Moshi JSON 직렬화
- **Performance:** 고빈도 상태를 draw 단계로 deferred read 하여 프레임당 recomposition 회피

> 참고: 아래 `docs/09_TECHNICAL_ARCHITECTURE.md` 등 설계 문서는 Godot 시절 기준이라 현재 Android 구현과 다를 수 있습니다.

---

## 🚀 시작하기 / Getting Started

### 1. 필수 도구

- [Android Studio](https://developer.android.com/studio) (최신) + Android SDK
- JDK 17

### 2. 빌드 & 실행

```bash
git clone https://github.com/jeiel85/nightseed-bastion-android.git
cd nightseed-bastion-android
./gradlew :app:installDebug      # 연결된 기기/에뮬에 설치
# 또는 Android Studio에서 app 모듈 실행
```

### 3. 릴리스 번들 & 스토어 내보내기

```powershell
./gradlew clean :app:bundleRelease                 # 서명된 AAB 생성
./scripts/export-play-store-release.ps1            # 노트 500자 검증 + Build\로 복사
```

> 릴리스 서명은 루트의 `keystore.properties`(커밋 제외)로 설정합니다. 없으면 디버그 키로 폴백합니다.

> 아래 *프리프로덕션 설계 자료*(Godot 프로젝트, `docs/00~20`)는 과거 계획 기록으로만 보존되며 현재 빌드와 무관합니다.

---

## 📚 설계 문서 인덱스 / Design Docs Index

| # | 문서 | 한 줄 요약 |
| ---: | --- | --- |
| 00 | [START_HERE](docs/00_START_HERE.md) | 구현 진입점 · 첫 10개 작업 |
| 01 | [PRODUCT_BRIEF](docs/01_PRODUCT_BRIEF.md) | 제품 정체성 · 타깃 · 수익화 |
| 02 | [GAME_DESIGN_DOCUMENT](docs/02_GAME_DESIGN_DOCUMENT.md) | 게임 디자인 본편 |
| 03 | [CORE_LOOP_AND_STATE_MACHINE](docs/03_CORE_LOOP_AND_STATE_MACHINE.md) | 페이즈 상태 머신 |
| 04 | [GAME_SYSTEMS_IMPLEMENTATION](docs/04_GAME_SYSTEMS_IMPLEMENTATION.md) | 시스템 구현 설계 |
| 05 | [LEVEL_AND_CONTENT_DESIGN](docs/05_LEVEL_AND_CONTENT_DESIGN.md) | 레벨/콘텐츠 |
| 06 | [BALANCE_MODEL](docs/06_BALANCE_MODEL.md) | 밸런스 모델 |
| 07 | [MOBILE_UX_GUIDE](docs/07_MOBILE_UX_GUIDE.md) | 모바일 UX 가이드 |
| 08 | [ART_AUDIO_GUIDE](docs/08_ART_AUDIO_GUIDE.md) | 아트 & 오디오 가이드 |
| 09 | [TECHNICAL_ARCHITECTURE](docs/09_TECHNICAL_ARCHITECTURE.md) | 기술 아키텍처 |
| 10 | [SCENE_SCRIPT_CONTRACTS](docs/10_SCENE_SCRIPT_CONTRACTS.md) | 씬/스크립트 계약 |
| 11 | [DATA_SCHEMA](docs/11_DATA_SCHEMA.md) | JSON / 세이브 스키마 |
| 12 | [SAVE_AND_MIGRATION](docs/12_SAVE_AND_MIGRATION.md) | 세이브 마이그레이션 |
| 13 | [BACKLOG](docs/13_BACKLOG.md) | P0 / P1 / P2 백로그 |
| 14 | [ROADMAP](docs/14_ROADMAP.md) | 출시 로드맵 |
| 15 | [VIBE_CODING_PLAYBOOK](docs/15_VIBE_CODING_PLAYBOOK.md) | AI 페어 프로그래밍 가이드 |
| 16 | [TESTING_QA_RELEASE](docs/16_TESTING_QA_RELEASE.md) | 테스트 & QA |
| 17 | [STORE_LAUNCH_PLAN](docs/17_STORE_LAUNCH_PLAN.md) | 스토어 출시 계획 |
| 18 | [IP_AND_CLONE_GUARDRAILS](docs/18_IP_AND_CLONE_GUARDRAILS.md) | IP 가드레일 |
| 19 | [AI_ASSET_PIPELINE](docs/19_AI_ASSET_PIPELINE.md) | AI 에셋 파이프라인 |
| 20 | [DECISION_LOG](docs/20_DECISION_LOG.md) | 의사결정 로그 |
| — | [MASTER_DESIGN_BUNDLE](docs/MASTER_DESIGN_BUNDLE.md) | 단일 파일 묶음 (참고용) |

---

## 🛡️ Non-Goals / IP 가드레일

- 기존 게임의 화면 구성, 이름, 아이콘, 건물 구성, 색 정체성, UI 카피, 특유의 연출을 복제하지 **않습니다**.
- 가차, 강제 광고, P2W 업그레이드를 **넣지 않습니다**.

자세한 사항은 [docs/18_IP_AND_CLONE_GUARDRAILS.md](docs/18_IP_AND_CLONE_GUARDRAILS.md) 참고.

---

## 🤝 기여 / Contributing

본 저장소는 현재 1인 + AI 페어 개발 단계입니다. 외부 PR을 받지 않더라도, 다음 규칙은 따라 주세요.

- 작업 전: `AGENTS.md` → `docs/00_START_HERE.md` → 해당 백로그 항목 순서로 읽기
- 커밋 메시지 prefix: `feat` `fix` `docs` `refactor` `test` `chore` `balance` `art`
- 의미 있는 결정은 [`docs/20_DECISION_LOG.md`](docs/20_DECISION_LOG.md)에 기록

---

## 📜 라이선스 / License

미정(TBD). 외부 배포 시점 전까지 모든 권리는 저작자에게 귀속됩니다. © 2026 jeiel85.

