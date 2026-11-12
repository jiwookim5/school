# Team_G · JAVA QUEST

객체지향프로그래밍 G조 프로젝트입니다. 기룡이와 함께 Java 핵심 개념을 미니게임으로 학습하는 데스크톱 프로그램을 개발합니다.

## 공통 인터페이스

- Java 17 이상 / Java Swing / 텍스트 파일 저장
- 백엔드 공통 소스: [src/teamapi/v1](src/teamapi/v1)
- 메서드·반환값·호출 순서: [인터페이스 규격](docs/interface-v1.md)
- 프론트 연결 예시: [ErrorGameScreenExample](src/teamapi/v1/example/ErrorGameScreenExample.java)

각 담당자는 공통 인터페이스를 기준으로 게임 로직과 Swing 화면을 구현합니다.

## Swing UI 시연

기룡이와 떠나는 가로형 행성 월드맵을 제공합니다. 월드는 지구, 달, 토성, 태양 순서이며, 각 월드에서 같은 네 스테이지 경로를 보여줍니다. 월드를 선택하면 지도 배경과 해당 월드의 진행 상태가 바뀝니다.

- 학습 챕터: 변수와 자료형, 조건문과 반복문, 메소드, 배열, 클래스, 패키지
- 게임 화면: 개념 카드 짝 맞추기, 코드 입력, 내려오는 정답 모으기, 코드 오류 찾기
- 진행 상태는 DB 없이 PC의 로컬 파일에 저장합니다.
- 로컬 저장 파일: 사용자 홈 폴더의 `.javaquest/progress.properties`

### 실행

JDK 17 이상이 필요합니다. 프로젝트 루트 PowerShell에서 실행합니다.

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object FullName)
Copy-Item -Recurse -Force src\main\resources\* out\
java -cp out kr.ac.kyonggi.javaquest.JavaQuestApp
```

### 조작

- 월드맵에서 ←/→ 방향키로 스테이지를 고르고 Enter로 입장하거나, 노드를 클릭합니다.
- 월드맵은 가로로 스크롤할 수 있습니다. 현재 시연에서 개념 카드와 정답 수집 스테이지를 플레이할 수 있습니다.
- 내려오는 정답 게임은 ←/→ 방향키로 기룡이를 움직여 목표 코드 줄을 순서대로 받습니다.

웹 시연물은 개인 로컬 사본으로만 보관하며 이 저장소에는 포함하지 않습니다.
