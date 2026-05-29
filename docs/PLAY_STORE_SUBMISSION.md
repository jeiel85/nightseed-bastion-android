# Google Play Store 제출 체크리스트 — Nightseed Bastion

이 문서는 Nightseed Bastion의 Google Play 스토어 신규 등록 및 프로덕션 출시 등록 양식입니다.

---

## 1. 앱 스토어 등록 기본 정보

| 항목 | 입력값 (한국어 ko-KR) | Input Value (영어 en-US) |
|---|---|---|
| **앱 이름 (App Name)** | `나이트시드 배스천` | `Nightseed Bastion` |
| **짧은 설명 (Short Description)** | `부패한 밤의 씨앗 주변에 자라난 마지막 요새를 지켜내는 정통 로그라이트 디펜스.` | `Defend the last bastion grown around the corrupted Nightseed in this rogue-lite tactical defense.` |
| **전체 설명 (Full Description)** | *(아래 텍스트 란 참조)* | *(See English description below)* |

### 전체 설명 (한국어 ko-KR)
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

### Full Description (영어 en-US)
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

---

## 2. 콘텐츠 등급 및 데이터 보안 정보 (IARC & Data Safety)

- **앱 유형**: 게임 (Rogue-lite Defense)
- **폭력성**: 낮음 (만화적 전투 묘사)
- **예상 등급**: 3세 이상 이용가 (Everyone)
- **수집 데이터**: 
  - 없음 (개인식별정보를 전혀 서버에 전송하지 않으며, 모든 성장 정보 및 활성 런 기록은 안드로이드 기기 내부 SQLite Room Database에 로컬 저장됩니다.)
- **데이터 보안 관행**: 전송 중 데이터 암호화(해당 없음), 로컬 DB 샌드박싱 적용.
