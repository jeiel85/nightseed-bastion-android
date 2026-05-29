# 15_VIBE_CODING_PLAYBOOK

## How to Prompt an AI Coding Agent

Always provide:

1. Backlog item ID.
2. Relevant docs.
3. Files allowed to change.
4. Acceptance criteria.
5. Verification commands.

## Prompt Template

```text
작업: Nightseed Bastion P0-003 RunStateMachine 구현

참고 문서:
- AGENTS.md
- docs/03_CORE_LOOP_AND_STATE_MACHINE.md
- docs/10_SCENE_SCRIPT_CONTRACTS.md
- docs/13_BACKLOG.md

허용 변경:
- scripts/core/RunStateMachine.gd
- scripts/autoload/RunManager.gd
- scenes/boot/Boot.tscn 또는 테스트용 최소 씬
- HISTORY.md
- CHANGELOG.md는 사용자 영향이 있을 때만

완료 조건:
- 모든 core state 정의
- 유효하지 않은 transition 거부
- state_changed signal 발생
- 간단한 수동 검증 방법 문서화

검증:
- Godot headless 로드가 가능하면 실행
- 실행하지 못하면 이유를 HISTORY.md에 명확히 기록
```

## Bad Prompt Example

```text
게임 만들어줘
```

This is too broad and will cause scope creep.

## Good Prompt Example

```text
P0-008만 구현해줘. Watchtower와 Thorn Wall을 Day phase에서 슬롯에 배치할 수 있게 하고, 비용 차감과 실패 사유 표시까지 해줘. UI는 임시 버튼으로 충분하고 아트 교체는 하지 마.
```

## Agent Stop Checklist

Agent must stop if implementation requires:

- New SDK
- Store credential
- Signing key
- Network permission
- Destructive Git command
- Save migration with data-loss risk
- Design that imitates a specific existing game screen
