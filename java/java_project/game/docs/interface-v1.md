# 팀 공통 Java 규격 v1 — 2026-11-02
공통 메서드와 데이터 구조 v1입니다. 팀 저장소 반영/팀 합의는 별도입니다.
점수 환산·30점 클리어·순차 해금·ID 길이 등은 임시로 맞추었습니다.
팀에서 다른 게임 규칙을 정하면 각 담당분야 팀원들 자유로 맞추셔도 무관합니다.
Java 17 이상, Swing 전용 프로젝트에 사용할 순수 Java 인터페이스와 DTO입니다.
실제 회원가입/채점/파일 저장 구현은 포함하지 않습니다.
기존 team-interface 초안 대신 이 폴더의 src/teamapi/v1 전체를 사용합니다.

## main에 올릴 파일
src/teamapi/v1 전체, README.md, .gitignore.
build와 class 파일, 실제 사용자 데이터는 올리지 않습니다.
각 담당자는 main을 자신의 브랜치에 병합한 뒤 각 Manager에서 인터페이스를 구현합니다.
공통 DTO 필드/메서드 변경은 팀 합의 후 main에서 함께 반영합니다.
각 게임 Manager는 GameService를 구현합니다. 회원 담당은 UserService,
메인 화면 데이터 담당은 ChapterService를 구현합니다.
엔티티는 Manageable / Manager 구조를 유지하며 DTO에 정답·비밀번호를 노출하지 않습니다.

## 기존 정리에서 확정한 변경
- 회원가입은 boolean 유지. 로그인은 User, 실패 null 유지.
- 현재 게임 문제는 Game 대신 Question 반환.
- 답 제출은 boolean 유지하되 questionId를 추가해 이전 문제 중복 제출 방지.
- Game과 GameResult에 소요 시간, 상태, 답변 문항 수 등 명시.
- 게임 번호는 게임 종류가 아니라 매 플레이 생성하는 고유 실행 ID(String).
- 게임 종류는 GameType, 챕터 번호는 int. 챕터마다 사용할 게임 종류는 Chapter에 지정.
- 별 1개 이상이면 클리어. 총점은 100점으로 환산한다.
- 카드 공개/낙하 좌표/체력 등 게임별 상태는 별도 확장 인터페이스로 분리한다.

## 필수 호출 순서
signUp → login → getChapters/getProgress → startGame
→ getCurrentQuestion → submitAnswer → getExplanation
→ nextQuestion → (다음 문제 존재하면 반복)
→ nextQuestion이 null이면 endGame → 결과 표시.
submitAnswer는 인덱스를 옮기지 않는다. nextQuestion만 옮긴다.
중간 이탈은 abortGame. 종료 실패 시 같은 gameId로 endGame/abortGame 재시도.
정상 종료 후 abort 또는 중단 후 정상 종료는 거부한다.

## 공통 오류 규칙
- 잘못된 ID/알 수 없는 문제/지원하지 않는 답안 형식: IllegalArgumentException.
- 중복 제출/잘못된 호출 순서/로그인 사용자 불일치/잠긴 챕터: IllegalStateException.
- 올바른 형식의 오답: false. 오류 예외와 구분한다.
- 파일 I/O 실패: IOException. 사용자에게 저장 실패와 재시도를 표시한다.
- nextQuestion 중복 클릭은 새 문제가 PLAYING이므로 거부한다.
- 마지막 문항도 제출 후 nextQuestion 호출로 FINISHED로 바꾼다.
- 시간초과·체력소진으로 끝나는 게임은 백엔드에서 FINISHED 처리할 수 있다.
  이 경우 미응답 문항도 전체 문항 수에 포함해 결과를 계산한다.
  프론트는 Swing Timer에서 getGame을 호출해 종료 상태를 확인하고 endGame으로 이동한다.
  getGame은 제한시간 게임에서 실제 경과 시간을 갱신하므로 화면 갱신 횟수로 시간을 재지 않는다.
- 한 문항은 최종 제출 1회가 기본이다. 카드 게임의 한 쌍 선택도 1회 시도 단위로 본다.
  기억력 게임의 다회 재시도/실시간 이벤트는 담당자가 별도 규격으로 합의한다.
- 프론트는 받은 객체를 표시하고 백엔드에 요청한다. 점수나 상태를 직접 수정하지 않는다.

## 점수·별·진행 저장

- `currentScore`: 원점수.
- `totalPossibleScore`: 전체 원점수 만점.
- 출제 수와 원점수 만점은 시작 시 확정하며 둘 다 1 이상.
- 최종 점수는 아래 식으로 계산하며 0–100점 범위로 제한한다.

```java
finalScore = floor(currentScore * 100.0 / totalPossibleScore)
cleared = !aborted && stars >= 1
```

| 최종 점수 | 획득 별 |
|---|---|
| 80–100점 | 3개 |
| 50–79점 | 2개 |
| 30–49점 | 1개 |
| 0–29점 | 0개 |

- 중도 종료는 `stars=0`, `cleared=false`. 점수 등 풀이 지표는 참고 기록으로만 보존한다.
- 누적 점수는 정상 완료 게임의 `finalScore`를 합산한다. 별은 챕터별 최고 별을 유지한다.
- 정답률은 정상 완료 게임들의 누적 정답 수 ÷ 누적 전체 문항 수 × 100으로 계산한다.
- 정답률 계산에 미응답을 포함하며, 게임별 정답률의 단순 평균은 사용하지 않는다.
- 첫 챕터는 해금한다. 앞 챕터 최초 클리어 시 다음 챕터를 해금하며 재도전을 허용한다.
- 개별 게임의 원점수 배점·제한시간·체력은 각 담당자가 정하고 데이터에 기록한다.
- 파일 저장 및 누적 반영은 공통 결과 저장 담당 한 곳에서 처리한다.
- `gameId` 중복 처리 방지 기록을 남겨 재호출·재실행 후에도 보상을 중복 지급하지 않는다.
- DTO는 전달 데이터이므로 최종 구현체에서 범위·ID·상태를 검증한다.

## 로그인
이름 입력 매개변수가 없으므로 첫 이름은 ID로 설정.
ID는 영문·숫자·밑줄 4–20자, 비밀번호는 8–64자 제안.
잘못된 입력 형식은 IllegalArgumentException, 중복 ID는 false.
비밀번호는 솔트+PBKDF2 등 비밀번호 해시로 저장하고 평문/단순 SHA 해시는 사용하지 않는다.
User는 표시용 정보만 가진다. 내부 계정 엔티티는 별도로 둔다.
동시 로그인은 프로그램당 1명, 활성 게임도 1개. 로그아웃 전 게임 종료/중단.
문제 정답/해설은 백엔드 내부에 보관한다. Question에 정답을 추가하지 않는다.

## txt 예시
모든 파일 UTF-8. 구분자는 실제 탭이며 필드 내 탭/개행은 이스케이프해야 한다.
여기 예시는 <TAB>으로 탭을 표시. 비밀번호 해시 등은 예시 값이며 실제 계정이 아니다.

users.txt
id<TAB>name<TAB>saltBase64<TAB>passwordHashBase64<TAB>iterations
test01<TAB>test01<TAB>예시솔트<TAB>예시해시<TAB>설정반복횟수

chapters.txt
chapterId<TAB>order<TAB>title<TAB>content<TAB>gameType
2<TAB>2<TAB>오류 찾기<TAB>자료형과 오류<TAB>ERROR_FIX

game-results.txt
gameId<TAB>userId<TAB>chapterId<TAB>gameType<TAB>cleared<TAB>aborted<TAB>finalScore<TAB>correctCount<TAB>answeredCount<TAB>totalQuestionCount<TAB>stars<TAB>elapsedMillis
실행UUID<TAB>test01<TAB>2<TAB>ERROR_FIX<TAB>true<TAB>false<TAB>80<TAB>4<TAB>5<TAB>5<TAB>3<TAB>45000

파일 변환/이스케이프 구현은 저장 담당이 정하고 공통 적용한다.
코드에는 ||, 탭, 줄바꿈이 포함되므로 단순 파이프 split은 사용하지 않는다.

## 적용 범위와 팀원이 확인할 점
이 규격은 컴파일 가능한 계약이며 실제 Manager 구현과 통합 검증을 대신하지 않습니다.
카드 게임의 전체 카드 목록/뒤집기, 낙하 좌표/선택 이벤트, 체력 수치는 현재 규격에 없습니다.
각 게임 담당자는 별도 확장 인터페이스를 추가하고 결과는 GameResult로 통일하세요.
이 자료만으로 게임 4종의 모든 조작까지 규격이 완료된 것은 아닙니다.
기존 수업 Manageable에 read/print/matches 등 메서드가 있다면 함께 제공한 최소 Manageable과
무조건 교체하지 말고 기존 인터페이스를 유지하며 어댑터/DTO를 연결하세요.
record를 사용하므로 모든 팀원이 Java 17 이상이어야 합니다. 필드 접근은 user.id() 형식입니다.
기존 프로젝트의 README/.gitignore는 덮어쓰지 말고 설명은 docs에, ignore 규칙은 기존 파일에 병합하세요.

## 컴파일
PowerShell:
$teamSources = @(Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 -d build $teamSources
