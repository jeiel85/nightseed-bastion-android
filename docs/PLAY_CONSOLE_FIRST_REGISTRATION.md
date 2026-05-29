# Play Console — Nightseed Bastion 최초 등록 마스터 양식

> 본 문서는 https://play.google.com/console 에서 **Nightseed Bastion** 앱 신규 등록 시 모든 입력값의 “정답지”입니다. 자동화로 채울 수 없는 텍스트 필드/설문은 본 문서를 보면서 복사·붙여넣기로 마무리합니다.
>
> - **사용 계정**: `pedaiah85@gmail.com` (Sitdory 개발자 계정)
> - **개발자 계정 ID**: `6375329746023339599`
> - **앱 ID** (활성, 사용): `4976211695174558334`
> - **앱 패키지** (영구 확정): `com.jeiel85.nightseedbastion`
> - **현재 버전**: versionName `1.0.1`, versionCode `4`
> - **업로드 키스토어**: `.keystore/nightseed-release.jks` (BACKUP.md 참조)
> - **앱 콘솔 URL**: https://play.google.com/console/u/1/developers/6375329746023339599/app/4976211695174558334/app-dashboard
> - **개인정보처리방침**: https://jeiel85.github.io/nightseed-bastion-privacy/ (Repo: https://github.com/jeiel85/nightseed-bastion-privacy)
>
> **참고**: 이전에 `com.aistudio.nightseedbastion.gamedef` 패키지로 만든 앱(App ID `4973117804781565500`)은 미게시 상태로 잔존. Play Console에서 직접 삭제하거나 무시 가능. 동일 패키지명은 360일간 reserved.

## ⚙️ 자동화 진행 결과 (2026-05-24)

| 단계 | 자동화 결과 |
|---|---|
| ① 앱 신규 생성 (앱 이름·패키지·언어·게임/무료·정책 동의) | ✅ Chrome MCP로 완료 |
| ② 스토어 등록정보 텍스트 (앱이름/짧은설명/자세한설명, 임시저장) | ✅ `execCommand('insertText')` 우회로 완료 |
| ③ 그래픽 자산 업로드 (아이콘/피처/스크린샷) | ⏳ 자산 미준비 — 사용자가 준비 후 업로드 |
| ④ 카테고리(게임/전략) / 연락처(이메일) | ✅ 완료 |
| ⑤ 가격(무료) | ✅ 기본값 유지 |
| ⑥ 앱 콘텐츠 12개 선언 | ✅ 모두 완료 (광고·앱 액세스·콘텐츠 등급·타겟층·데이터 보안·광고 ID·정부 앱·금융·건강·개인정보처리방침 포함) |
| ⑦ 개인정보처리방침 호스팅 | ✅ GitHub: `https://jeiel85.github.io/nightseed-bastion-privacy/` |
| ⑧ 첫 내부 테스트 트랙 릴리즈 (AAB 업로드) | ⏳ `app/build/outputs/bundle/release/app-release.aab` 이미 준비됨, 사용자 업로드 필요 |

---

## 0. 사전 준비물 체크리스트

- [x] 릴리즈 키스토어 (`./.keystore/nightseed-release.jks`)
- [x] 키스토어 백업 (`./.keystore/BACKUP.md`)
- [x] 서명된 AAB (`app/build/outputs/bundle/release/app-release.aab` · 7.44 MB · jarsigner 검증 통과)
- [x] 앱 아이콘 PNG (`docs/store-assets/icon-512.png` · 512×512 · 244 KB)
- [x] 피처 그래픽 (`docs/store-assets/feature-1024x500.png` · 1024×500 · 417 KB)
- [x] 전화기 스크린샷 5장 (`docs/store-assets/screenshots/phone-01~05.png` · 1600×2560 · ADB 실기 캡처)
- [ ] (선택) 7인치/10인치 태블릿 스크린샷
- [ ] (선택) 유튜브 프로모 영상 URL
- [x] 개인정보처리방침 URL — `https://jeiel85.github.io/nightseed-bastion-privacy/` (GitHub Pages 게시 완료)

---

## 1. 앱 만들기 (Create app)

Play Console 첫 화면의 “앱 만들기” 다이얼로그 입력값.

| 항목 | 값 |
|---|---|
| 앱 이름 | `Nightseed Bastion` |
| 기본 언어 | `한국어 – ko-KR` |
| 앱 또는 게임 | **게임** |
| 무료 또는 유료 | **무료** |
| 선언 1 (개발자 프로그램 정책) | ✅ 동의 |
| 선언 2 (미국 수출법 준수) | ✅ 동의 |

---

## 2. 스토어 등록정보 (Store listing)

### 2-1. 앱 세부정보 — 한국어 (ko-KR)

| 필드 | 값 |
|---|---|
| 앱 이름 (30자 이내) | `나이트시드 배스천` |
| 간단한 설명 (80자 이내) | `부패한 밤의 씨앗 주변에 자라난 마지막 요새를 지켜내는 정통 로그라이트 디펜스.` |

**자세한 설명** (4000자 이내):

```text
마지막 등불을 밝히고, 밤의 씨앗이 내뿜는 어둠의 군단에 맞서 요새를 사수하세요!

나이트시드 배스천(Nightseed Bastion)은 낮 동안 진형을 확장하고, 황혼녘에 어둠의 세력과 위험한 계약을 맺으며, 밤에는 영웅을 직접 조작하여 몰려오는 마물을 격퇴하는 고품격 전략 로그라이트 디펜스 게임입니다.

■ 게임 핵심 시스템
• 낮의 건설 (Day Phase): 월웰, 감시탑, 가시 장벽 등 다양한 방어 건물을 전략적으로 배치하고 강화하세요.
• 황혼의 계약 (Dusk Phase): 황혼의 전조(Omen)를 분석하세요. 더 강력한 보상을 얻기 위해 영구적인 최대 체력을 깎거나 웨이브 속도를 가속시키는 위험한 황혼의 계약(Bargain)을 맺을 수 있습니다.
• 밤의 전투 (Night Phase): 모바일 환경에 최적화된 영웅 뱌그란트 워든(Vagrant Warden)을 직접 컨트롤하세요. 타워가 적을 타격하는 사이, 강력한 액티브 스킬 '달빛 베기(Mooncut)'와 '파수꾼의 표식(Warden's Mark)'을 적시에 꽂아 넣어야 합니다.
• 계정 영구 성장 (Meta Progression): 패배하더라도 획득한 '씨앗 재(Seed Ash)'를 활용해 대장간에서 영구적인 능력치를 개방하여 다음 도전을 한층 유리하게 이끌 수 있습니다.

■ 개성 넘치는 적들과 방어 기재
• 획기적인 타워 세트: 마력 자원을 수급하는 Moonwell, 단일 타겟 저격 Watchtower, 영웅 스킬 회복을 돕는 Ember Brazier 등.
• 전략적 대응이 필요한 마물군단: 장벽을 무자비하게 부수는 Grave Brute, 원거리 저격수 Hex Archer, 그리고 라인을 무작위로 이동하며 아군 타워를 봉쇄하는 대보스 Nightseed Herald.

지금 요새의 관문으로 나아가 최후의 등불을 수호해 내십시오!
```

### 2-2. 앱 세부정보 — 영어 (en-US) *(다국어 등록 시)*

| 필드 | 값 |
|---|---|
| 앱 이름 | `Nightseed Bastion` |
| 간단한 설명 | `Defend the last bastion grown around the corrupted Nightseed in this rogue-lite tactical defense.` |

**자세한 설명**:

```text
Ignite the last lantern and defend the final gate from the creeping horrors of the corrupted Nightseed!

Nightseed Bastion is an immersive, tactical rogue-lite mobile defense game where you expand your fortress by day, forge high-stakes shadow pacts at dusk, and directly control a mobile hero in high-action combat by night.

■ CORE LOOP
• Day Build: Place and upgrade unique structures like Moonwells, Watchtowers, and sturdy Thorn Walls on active lane defense slots.
• Dusk Omen & Bargain: Inspect lane threats. Make critical decisions by accepting Dusk Bargains—forging powerful temporary combat buffs in exchange for dangerous long-term curses.
• Night Combat: Take active control of the Vagrant Warden. Guide your champion across lanes, deploying devastating abilities like 'Mooncut' and target-painting 'Warden's Mark' to crush priority monsters.
• Meta Upgrades: Gather Seed Ash from your runs to spend at the cosmic Seed Ash Forge, unlocking permanent account-level power boosts for subsequent attempts.

■ ADVANCED THREATS & COUNTERS
• Dynamic Defensive Arsenal: Exploding Snare traps, Bell Shrines to decrypt precise hostile numbers, and Ember Braziers to boost your hero's energy.
• Intelligent Monster Behaviors: Heavy Grave Brutes focusing down walls, Hex Archers firing from outside range, and the colossal boss Nightseed Herald warping across paths while rooting your defensive structures.

Will you survive all 7 nights at the Moonwell Bastion, or will the darkness corrupt the last seed? Download now and hold the line!
```

### 2-3. 그래픽 자산

| 자산 | 사양 | 파일 | 상태 |
|---|---|---|---|
| 앱 아이콘 | 512×512 PNG, 244 KB | `docs/store-assets/icon-512.png` | ✅ 생성 완료 |
| 피처 그래픽 | 1024×500 PNG, 417 KB | `docs/store-assets/feature-1024x500.png` | ✅ 생성 완료 |
| 고해상도 아이콘 | 1024×1024 PNG | `docs/store-assets/icon-1024.png` | ✅ 보너스 |
| 전화기 스크린샷 1 (메인 메뉴) | 1600×2560 PNG | `docs/store-assets/screenshots/phone-01.png` | ✅ ADB 실기 캡처 |
| 전화기 스크린샷 2 (Day Build) | 1600×2560 PNG | `docs/store-assets/screenshots/phone-02.png` | ✅ ADB 실기 캡처 |
| 전화기 스크린샷 3 (Deploy 메뉴) | 1600×2560 PNG | `docs/store-assets/screenshots/phone-03.png` | ✅ ADB 실기 캡처 |
| 전화기 스크린샷 4 (Dusk Bargain) | 1600×2560 PNG | `docs/store-assets/screenshots/phone-04.png` | ✅ ADB 실기 캡처 |
| 전화기 스크린샷 5 (Night Combat) | 1600×2560 PNG | `docs/store-assets/screenshots/phone-05.png` | ✅ ADB 실기 캡처 |
| 프로모 영상 | YouTube URL | — | ⏳ 선택 (미준비) |
| 태블릿 스크린샷 | 7인치/10인치 | — | ⏳ 선택 (미준비) |

---

## 3. 카테고리 및 태그 (Categorization)

| 항목 | 값 |
|---|---|
| 앱 카테고리 | **게임 (Games)** |
| 카테고리 (2차) | **전략 (Strategy)** |
| 보조 태그 (최대 5개) | `타워 디펜스`, `로그라이크`, `싱글플레이어`, `오프라인`, `판타지` |

---

## 4. 연락처 정보 (Store listing contact details)

| 필드 | 값 |
|---|---|
| 이메일 주소 (공개) | `pedaiah85@gmail.com` |
| 전화번호 (선택) | *(공개 비추천 — 비워둠)* |
| 웹사이트 (선택) | `https://jeiel85.github.io/nightseed-bastion-preproduction/` |

---

## 5. 개인정보처리방침 (Privacy policy)

- **상태**: ✅ GitHub Pages에 게시 완료
- **URL**: `https://jeiel85.github.io/nightseed-bastion-privacy/`
- **저장소**: [jeiel85/nightseed-bastion-privacy](https://github.com/jeiel85/nightseed-bastion-privacy)
- **콘텐츠** (게임은 데이터를 수집하지 않으므로 단일 페이지 KO/EN 병기):

```markdown
# Nightseed Bastion 개인정보처리방침

최종 업데이트: 2026-05-24

Nightseed Bastion(이하 "본 앱")은 사용자의 개인정보를 수집하거나 외부 서버로
전송하지 않습니다. 모든 게임 진행 상황과 계정 진행도는 Android 기기 내부의
Room Database(SQLite)에 로컬로만 저장됩니다.

## 수집되는 정보
- 없음

## 외부로 전송되는 정보
- 없음

## 제3자 SDK
- 없음 (Firebase Analytics 등 추적 SDK 미사용)

## 권한 사용
- 인터넷 권한: 향후 업데이트 알림 및 Gemini AI 시나리오 생성 호출 시에만 사용
- 그 외 위치/카메라/연락처 등 민감 권한 사용 없음

## 어린이 정책
- 본 앱은 만 3세 이상 누구나 이용 가능합니다.
- 어린이로부터 어떠한 개인정보도 수집하지 않습니다.

## 데이터 삭제 요청
- 사용자가 앱을 삭제하면 모든 로컬 데이터가 함께 제거됩니다.
- 추가 문의: pedaiah85@gmail.com

## 변경 사항
이 정책이 변경될 경우, Play 스토어 등록 페이지를 통해 공지합니다.
```

> 위 콘텐츠는 이미 [GitHub Pages 저장소](https://github.com/jeiel85/nightseed-bastion-privacy)에 KO/EN 병기 HTML로 게시되어 있으며, Play Console 등록 시 그대로 사용됩니다.

---

## 6. 콘텐츠 등급 (Content rating — IARC)

설문 카테고리: **게임 (Games)**

| 질문 | 답변 |
|---|---|
| 폭력성 — 만화/판타지 캐릭터의 사실적이지 않은 폭력? | **예** (몬스터 처치, 만화풍 전투) |
| 폭력성 — 사실적이거나 잔혹한 폭력? | 아니요 |
| 폭력성 — 피, 신체 절단, 살상 묘사? | 아니요 |
| 성적 콘텐츠 | 아니요 |
| 노출 / 부분 노출 | 아니요 |
| 비속어 | 아니요 |
| 약물 / 알코올 / 담배 묘사 | 아니요 |
| 도박 (시뮬레이션 도박 포함) | 아니요 |
| 사용자 간 실시간 상호작용 (채팅 등) | 아니요 (싱글플레이어) |
| 사용자 위치 공유 | 아니요 |
| 디지털 구매 (인앱결제) | 아니요 (현재 버전) |

**예상 등급**: 모든 연령대 / 만 3세 이상 (E / Everyone)

---

## 7. 데이터 보안 (Data safety)

| 질문 | 답변 |
|---|---|
| 앱이 사용자 데이터를 수집/공유하나요? | **아니요** |
| 모든 사용자 데이터는 전송 중 암호화되나요? | 해당 없음 (수집/전송 없음) |
| 사용자가 데이터 삭제를 요청할 수 있나요? | 앱 삭제 시 로컬 데이터 자동 제거 |
| 데이터 수집을 거부할 수 있나요? | 해당 없음 |
| 독립적인 보안 검토를 받았나요? | 아니요 |

---

## 8. 광고 (Ads)

| 필드 | 값 |
|---|---|
| 앱에 광고가 포함되어 있나요? | **아니요** |

---

## 9. 앱 액세스 권한 (App access)

| 필드 | 값 |
|---|---|
| 모든 기능이 제한 없이 사용 가능한가요? | **예 — 로그인이나 추가 자격 증명 없이 모든 기능 이용 가능** |

---

## 10. 타겟층 및 콘텐츠 (Target audience and content)

| 필드 | 값 |
|---|---|
| 타겟 연령대 | **13세 이상** (전략 게임 난이도 고려) |
| 광고로 어린이 대상 콘텐츠 노출? | 아니요 |
| 어린이가 흥미를 가질 만한 요소? | 만화풍 비주얼은 일부 어린이 매력 가능 — 그러나 의도된 타겟 아님 |

---

## 11. 정부/뉴스/금융/건강 앱 분류

| 질문 | 답변 |
|---|---|
| 정부 앱인가요? | 아니요 |
| 뉴스 앱인가요? | 아니요 |
| 금융 서비스 앱인가요? | 아니요 |
| 건강 관련 앱인가요? | 아니요 |
| COVID-19 추적 앱인가요? | 아니요 |

---

## 12. 국가 및 지역 (Countries / regions)

| 필드 | 값 |
|---|---|
| 배포 국가 | **모든 국가/지역** (대한민국 + 글로벌) |
| 가격 책정 | **무료** |

---

## 13. 출시 트랙 (Release tracks)

본 등록 직후의 권장 순서:

1. **내부 테스트 (Internal testing)** — 본인 디바이스로 사전 검증 (현재 단계)
2. **비공개 테스트 (Closed testing)** — 지인 5~20명 베타
3. **오픈 테스트 (Open testing)** — 누구나 베타 참여 가능
4. **프로덕션 (Production)** — 일반 출시

### 13-1. 첫 내부 테스트 릴리즈 입력값

| 필드 | 값 |
|---|---|
| 출시명 (Release name) | `1.0.1 (4)` *(자동 채워짐)* |
| App Bundle 파일 | `NightseedBastion-v1.0.1-vc4.aab` *(make_release.ps1 결과물)* |
| 출시 노트 (ko-KR) | `나이트시드 배스천 v1.0.1 첫 내부 테스트 빌드입니다. 시스템 안정성 및 대미지 밸런스가 개선되었습니다.` |
| 출시 노트 (en-US) | `Nightseed Bastion v1.0.1 first internal-test build. System stability and gameplay balance updates.` |

---

## 14. 최초 등록 후 후속 작업

- [ ] Play App Signing 자동 활성화 → 인증서 페이지에서 SHA-1 = `30:F2:C7:9B:15:06:62:5B:37:9F:7B:DE:2E:1C:3D:33:B8:01:F7:11` 확인
- [ ] 그래픽 자산 4종(아이콘·피처·스크린샷 2장+) 업로드
- [ ] 개인정보처리방침 URL 게시 및 등록
- [ ] 내부 테스트 트랙에 본인 이메일 추가 → 디바이스에서 설치 확인
- [ ] **출시 → 프로덕션** 트랙으로 승격 시 “데이터 보안 / 앱 액세스 / 콘텐츠 등급 / 광고” 4개 섹션이 모두 ✅ 인지 마지막 점검

---

## 부록 A. 자동 입력 시 사용할 키-값 맵 (Chrome MCP / 스크립트용)

```json
{
  "app_name": "Nightseed Bastion",
  "default_language": "ko-KR",
  "app_or_game": "game",
  "free_or_paid": "free",
  "short_description_ko": "부패한 밤의 씨앗 주변에 자라난 마지막 요새를 지켜내는 정통 로그라이트 디펜스.",
  "short_description_en": "Defend the last bastion grown around the corrupted Nightseed in this rogue-lite tactical defense.",
  "category_primary": "GAME",
  "category_secondary": "STRATEGY",
  "tags": ["타워 디펜스", "로그라이크", "싱글플레이어", "오프라인", "판타지"],
  "contact_email": "pedaiah85@gmail.com",
  "contains_ads": false,
  "collects_data": false,
  "target_age": "13+",
  "countries": "ALL",
  "price": "FREE",
  "package_name": "com.aistudio.nightseedbastion.gamedef",
  "current_version_name": "1.0.1",
  "current_version_code": 4,
  "upload_key_sha1": "30:F2:C7:9B:15:06:62:5B:37:9F:7B:DE:2E:1C:3D:33:B8:01:F7:11",
  "upload_key_sha256": "95:0B:E1:66:4C:2F:63:2A:0B:5F:EA:7A:CF:A7:96:79:E5:1F:FF:D5:5B:1F:66:2E:90:74:6B:80:1C:35:80:28"
}
```

## 부록 B. 관련 문서

- `.keystore/BACKUP.md` — 키스토어 자격증명
- `docs/PLAY_STORE_SUBMISSION.md` — 스토어 등록 본문 원본 (한국어/영어 설명)
- `docs/PLAY_UPLOAD_CHECKLIST.md` — 매 릴리즈마다 사용하는 업로드 체크리스트
- `make_release.ps1` — 버전 자동 증가 + AAB 빌드 + 데스크탑 배포 스크립트
