# HeyDoc
The App which is treating your medical care.

This is my first android APP project.
It took about 5 weeks to the completion.

[주요기능] (Main functions)
1. 의사 상담 (Consulting Doctor/Chatting)
- 아이디 이용 상대방 추가 (Adding doctor on my list)
- 상대방과 실시간 1:1 대화 (Realtime Chatting with Doctor)
- RecyclerView, Firebase(Realtime database) API, Glide

2. 의료내역 기록 (Recording medical history)
- 내 의료기록 저장(앨범에서 이미지 추가 가능) (Saving my medical history and Attaching Images)
- 의료기록 제목/날짜/진료과/내용 등의 키워드로 검색(필터링) 가능 (Seaching by subject/date/major/contents etc and filtering them)
- SharedPreferences(JSON)

3. SOS 송신 (Sending SOS)
- 사전 등록해놓은 상대방에게 SOS 푸시(내 이름 및 과거 병력 포함 발송) (Sending SOS to people who selected before)
- Firebase(Cloud Messaging) API, JSON/GSON, OKhttp

4. 병원/약국 찾기 (Finding hospital, pharmacy)
- 병원/약국/각종 진료과 등의 키워드로 내 위치 반경 500m 의료시설 검색 (Finding medical facilities within 500m by keywords like Hospital, Pharmacy, Majors
- Google Maps API, Google Places API

5. 의료뉴스/팁 제공 (Giving medical news and tips)
- 최신 의료뉴스 전시 및 클릭 시 해당 페이지 이동 (Displaying news and moving on that page if you click)
- Web Crawling(Jsoup), Asynctask, Handler

[기타기능] (Sub functions)
1. 회원가입, 로그인, 아이디/비밀번호 찾기 (Sign up, Sign in, Finding ID/PW)
- 회원가입 시 앨범/카메라에서 이미지 가져오기 (Setting my profile image from album and camera)
- 정규식 사용, 텍스트 와쳐로 조건 만족 확인 (Checking regular form ID/PW)
- 이미지와 각종 데이터들을 firebase에 저장(로그인, 아이디/비밀번호 찾기 때 불러오기) (Saving data on Firebase and loading them)
- Firebase(Realtime Database, Storage) API, Glide, RecyclerView

2. 개인정보 수정 (Editting my personal infomation)
- 기존 데이터를 firebase에서 가져오고 수정 저장 (Loading my info from Firebase and Editting them)
- Firebase(Realtime Database, Storage), Glide, RecyclerView
