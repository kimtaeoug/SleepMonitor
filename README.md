<h1 align="center">SleepMonitor</h1>  

<div align="left">

![Stack](https://img.shields.io/badge/android-3DDC84?style=for-the-badge&logo=Android&logoColor=white)
![Stack](https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white)
![Stack](https://img.shields.io/badge/firebase-FFCA28?style=for-the-badge&logo=Firebase&logoColor=white)

</div>

## IntroDuction
Company : TuringBio
Desc : BLE 기반 수면 모니터링 및 분석 서비스
주요 기능 : 수면 모니터링, 수면 관련 분석, 수면 질 관련 챗봇 기능등
<div align="left">
<table>
   <tr>
      <td>
         <img width="250px" src="./screen_shots/0.jpg">
      </td>
      <td>
         <img width="250px" src="./screen_shots/7.jpg">
      </td>
      <td>
         <img width="250px" src="./screen_shots/5.jpg">
      </td>
      <td>
         <img width="250px" src="./screen_shots/4.jpg">
      </td>
   </tr>
</table> 
</div>

### 🧑‍💻 프로젝트내 담당 업무
<details>
    <summary>Bluetooth(BLE) 기능 개발</summary>
 <pre>
 1. BLE 관련 스캔 및 페이링, Controller 개발
  ㆍ Desc : 주변 Bluetooth 기기 스캔 및 페어링, 컨트롤 기능
  ㆍ Stack : BLE
</pre>
[Github Code](https://github.com/kimtaeoug/tunegem/tree/main/social_share)
</details>  
<details>
    <summary>Notification 기능 개발 및 Handler 개발</summary>
 <pre>
  ㆍ Desc : FCM을 활용해 Notification을 처리하고, 앱의 각 상태(Foreground, Background, Dead)에 따라 이를 처리하는 handler 기능 
  ㆍ Stack : FCM, FlutterLocalNotification</pre>
 <table>
    <tr>
       <td>
         <img width="150px" src="./images/noti/push_page.png">
      </td>
       <td>
         <img width="150px" src="./images/noti/push_filter.png">
      </td>
       <td>
         <img width="150px" src="./images/noti/push.png">
      </td>
   </tr>
 </table>
[Github Code](https://github.com/kimtaeoug/tunegem/tree/main/push_handler)
</details>  
<details>
    <summary>SDUI 기능 개발</summary>
 <pre>
  ㆍ Desc : ServerSideRendering 방식으로 홈화면을 구성하는 기능
  ㆍ Process : API 호출 -> Layout 구성(Shimmer 형태) 및 Queue에 각 Widget에 필요한 Function 추가 -> Layout 순서에 따라 각 Widget에 필요한 API 호출 -> Data를 각 Widget에 적용
  ㆍ Stack : Queue</pre>
<img width="200px" src="./images/etc/sdui.png"><br>
[Github Code](https://github.com/kimtaeoug/tunegem/tree/main/sdui)
</details>  
<details>
    <summary>Media Upload 및 편집 기능 개발</summary>
 <pre>
  ㆍ Desc : FFmpeg을 활용해 녹음 및 동영상을 편집하고 백그라운드로 업로드 하는 기능  
  ㆍ Stack : FFmpeg, MethodChannel, Isolate, Camera등</pre>
 <table>
    <tr>
       <td>
         <img width="150px" src="./images/video/record.png">
      </td>
       <td>
         <img width="150px" src="./images/video/video_editor.png">
      </td>
       <td>
         <img width="150px" src="./images/video/crop.png">
      </td>
       <td>
         <img width="150px" src="./images/video/record_audio.png">
      </td>
       <td>
         <img width="150px" src="./images/video/audio_editor.png">
      </td>
       <td>
         <img width="150px" src="./images/video/video.png">
      </td>
   </tr>
 </table>
[Github Code](https://github.com/kimtaeoug/tunegem/tree/main/media_upload_editor)
</details>  
<details>
    <summary>회원 관리 기능 리뉴얼</summary>
 <pre>
  ㆍ Desc : Firebase 기반 Membership 관리에서 자체 서버 관리로 Migration과 암호화, JWT, Social 로그인등의 기능
  ㆍ Stack : encrypt, jwt, google_login, apple_login등</pre>
 <table>
    <tr>
       <td>
         <img width="150px" src="./images/membership/login.png">
      </td>
      <td>
         <img width="150px" src="./images/membership/signup.png">
      </td>
      <td>
         <img width="150px" src="./images/membership/birth.png">
      </td>
      <td>
         <img width="150px" src="./images/membership/auth.png">
      </td>
   </tr>
 </table>
</details>  
<details>
    <summary>신규 서비스 개발 및 배포</summary>
 <pre>
  ㆍ Desc : 레슨, 오디션, 리포트, 릴스, 큐레이션등의 서비스 개발 및 배포</pre>
 <table>
    <tr>
       <td>
         <img width="120px" src="./images/etc/audition.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/audition2.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/chart.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/lesson.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/lesson_video.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/match1.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/quest.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/quiz.png">
      </td>
      <td>
         <img width="120px" src="./images/etc/report.png">
      </td>
   </tr>
 </table>
</details>  
<details>
    <summary>언어팩 개선</summary>
 <pre>
  ㆍ Desc : API로 받아오던 언어팩 데이터에서 APP bundle 데이터로 수정하고, hotfix기능을 추가</pre>
</details>  
<details>
    <summary>기존 개발 코드 성능 최적화 및 고도화</summary>
 <pre>
  ㆍ Desc : 스파게티 코드 형식의 데이터에 CleanArchitecture, MVVM, Routing, 
           Binding등을 적용시키고, UI들을 Component와 페이지로 구분해 새로 개발</pre>
</details>  

### ⚙️ 기술 Stack
* 상태 관리 및 바인딩, 라우팅 -> GetX
* 디자인 패턴 -> Clean Architecture
* 의존성 주입 -> DI
* Package 관리 -> Melos

### 협업 Tool
![Stack](https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white)
![Stack](https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white)
![Stack](https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=Notion&logoColor=white)
![Stack](https://img.shields.io/badge/github-181717?style=for-the-badge&logo=Github&logoColor=white)

### CI/CD
![Stack](https://img.shields.io/badge/githubactions-4A154B?style=for-the-badge&logo=GithubAction&logoColor=white)
![Stack](https://img.shields.io/badge/fastlane-00F200?style=for-the-badge&logo=FastLane&logoColor=white)
![Stack](https://img.shields.io/badge/firebase-FFCA28?style=for-the-badge&logo=Firebase&logoColor=white)
