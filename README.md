# Galaxy Z Fold 8 Launcher (Fold8Launcher)

갤럭시 Z 폴드8(Galaxy Z Fold8)의 폴더블 폼팩터 특성을 극대화한 맞춤형 안드로이드 런처(Home Launcher) 프로젝트입니다.

---

## 📱 폴더블 폼팩터 맞춤 주요 기능

### 1. 듀얼 디스플레이 지능형 레이아웃 (Dual Display Adaptation)
- **외측 커버 화면 (Cover Screen)**:
  - 좁고 긴 화면비에 최적화된 4열 그리드.
  - 엄지손가락이 닿기 편한 하단 원핸드 독(Dock) 및 신속 검색바.
  - 빠른 앱 실행 및 앱 페어 가로 스크롤 스트립.
- **내측 메인 대화면 (Main Screen)**:
  - 태블릿급 대화면을 위한 북-폴드(Book-fold) 듀얼 페이지 레이아웃.
  - 좌측: 대형 디지털 시계, 멀티윈도우 앱 페어 대시보드, 퀵 검색.
  - 우측: 5열 고해상도 앱 런치패드.
  - 화면 중앙: 은은한 힌지 가이드 인디케이터.

### 2. 폴더블 플렉스 모드 (Flex Mode / Posture Awareness)
- 기기를 75°~115° 각도로 반쯤 접었을 때(`FoldingFeature.State.HALF_OPENED`) 자동 전환.
- **상단 스탠드 뷰**: 탁상에 거치했을 때 한눈에 들어오는 대형 시계 및 미디어 퀵 컨트롤.
- **하단 런치패드**: 바닥면에 놓이는 한 손 터치 제어 6열 컴팩트 앱 팔레트.

### 3. 스마트 태스크바 & 멀티태스킹 (Taskbar & Multi-Window)
- 대화면 하단에 플로팅되는 생산성 태스크바.
- 전체 앱 서랍 토글 버튼, 고정된 즐겨찾기 앱, 최근 실행한 앱(최대 4개) 및 앱 페어 바로가기 제공.
- **분할 화면(Split Screen) 즉시 실행**: 앱 아이콘 롱프레스 메뉴를 통해 `FLAG_ACTIVITY_LAUNCH_ADJACENT` 플래그로 분할 화면 실행 지원.

### 4. 멀티윈도우 앱 페어 (App Pair)
- 자주 함께 사용하는 2개의 앱(예: 삼성 인터넷 + 삼성 노트, 유튜브 + 메시지 등)을 하나의 아이콘으로 묶어 등록.
- 탭 한 번으로 양쪽 화면에 분할 실행.

### 5. 매끄러운 화면 연속성 (App Continuity)
- 커버 화면과 메인 화면을 열고 닫을 때 Activity 재생성 없이 즉각적인 애니메이션과 상태 유지 (`configChanges` 및 Compose `rememberFoldState()` 반응형 구조).

---

## 🛠 기술 스택

- **Language**: Kotlin 2.0.20
- **Build**: Gradle 8.9 + Kotlin DSL (`build.gradle.kts`, `libs.versions.toml`)
- **UI Framework**: Jetpack Compose BoM 2024.09.00 + Material 3 Adaptive
- **Foldable & Window Tracker**: `androidx.window:window:1.3.0`
- **Architecture**: Clean Architecture (Domain, Data, UI) + ViewModel (MVI / StateFlow)
- **Local Storage**: Jetpack DataStore Preferences
- **Launcher APIs**: `LauncherApps`, `PackageManager`, `RoleManager.ROLE_HOME`, `Intent.CATEGORY_HOME`

---

## 📂 프로젝트 구조

```
fold8_launcher/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/fold8/launcher/
│       │   │   ├── MainActivity.kt                # 홈 인텐트 수신 및 메인 엔트리포인트
│       │   │   ├── domain/model/
│       │   │   │   ├── FoldState.kt               # 커버/메인/플렉스 모드 모델
│       │   │   │   └── AppItem.kt                 # 앱 및 앱 페어 모델
│       │   │   ├── data/
│       │   │   │   ├── AppRepository.kt           # 설치 앱 로드 및 분할 실행 인텐트
│       │   │   │   ├── AppPairRepository.kt       # 멀티윈도우 앱 페어 관리
│       │   │   │   └── LayoutPreferences.kt       # 독/화면 레이아웃 DataStore
│       │   │   └── ui/
│       │   │       ├── LauncherApp.kt             # 루트 화면 전환 및 연속성 코디네이터
│       │   │       ├── LauncherViewModel.kt       # UI 상태 및 이벤트 뷰모델
│       │   │       ├── fold/FoldDetector.kt       # WindowInfoTracker 힌지 센서 감지
│       │   │       ├── cover/CoverHomeScreen.kt   # 외측 커버 화면 UI
│       │   │       ├── main/MainHomeScreen.kt     # 내측 대화면 듀얼 패널 UI
│       │   │       ├── flex/FlexHomeScreen.kt     # 힌지 각도별 플렉스 모드 UI
│       │   │       ├── taskbar/TaskbarView.kt     # 하단 생산성 태스크바
│       │   │       ├── appdrawer/AppDrawerSheet.kt# 전체 앱 서랍 및 실시간 검색
│       │   │       ├── widgets/FoldWidgets.kt     # 시계, 앱 아이콘, 앱 페어 다이얼로그
│       │   │       └── theme/                     # 테마 및 컬러 팔레트
│       │   └── res/
│       └── test/
│           └── java/com/fold8/launcher/FoldLauncherTest.kt # 폴더블 상태 단위 테스트
├── gradle/
│   ├── libs.versions.toml                         # 의존성 버전 카탈로그
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew.bat
```

---

## 🚀 빌드 및 실행 방법

### Android Studio에서 실행
1. Android Studio를 열고 `Open`을 선택합니다.
2. `c:\Users\codecrain\projects\fold8_launcher` 폴더를 지정합니다.
3. Gradle Sync가 완료되면, 폴더블 기기(Galaxy Z Fold 에뮬레이터 또는 실기기)를 선택하고 `Run (Shift + F10)`을 누릅니다.

### 기본 런처로 설정
1. 앱이 실행되면 홈 버튼을 누릅니다.
2. 기본 홈 앱 선택 팝업에서 **Fold8 Launcher**를 선택하고 **항상(Always)**을 탭합니다.
3. (또는 **설정 > 애플리케이션 > 기본 앱 선택 > 홈 앱**에서 `Fold8 Launcher` 지정)
