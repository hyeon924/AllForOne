# AllForOne

## 1. 프로젝트 소개

- 일상적으로 접하는 모든 것에 대해 정보를 공유하고, 보다 나은 선택을 할 수 있도록 돕는 리뷰 서비스

<br>

## 2. 팀원 구성

<div align="center">

| **임재혁** | **장준규** | **황혜현** |

</div>

<br>

## 3. 기획
* 기획 [LINK](https://docs.google.com/document/d/1HoOn87LdUlP368RPX4knaAJzC6p45pzMxuQmHBfkRHo/edit?tab=t.0#heading=h.5x0d5h95i329)
  
* 요구사항정의서 [LINK](https://docs.google.com/spreadsheets/d/15VNfscAwX6ZR8g0V_4zoGpNoQeUxaIQVDSti1GYBhGA/edit?gid=0#gid=0)
  
* ERD [LINK](https://www.erdcloud.com/d/nmdae4hzh86FBmDn5)
  
* 피그마 [LINK](https://www.figma.com/design/vvc8BtuWPkb2a0itWcssW8/%EC%98%AC%ED%8F%AC%EC%9B%90-%EC%99%80%EC%9D%B4%EC%96%B4-%ED%94%84%EB%A0%88%EC%9E%84?node-id=57-94&node-type=canvas&t=WRhkKSO74mxLGOYw-0)
  
<br>

## 4. 개발 환경
- Front : HTML, css, Bootstrap
- Back-end : JAVA, Spring Boot,Thymeleaf, mySQL
- 버전 및 이슈관리 : Github, Git
- 협업 툴 : Notion, Github Wiki

<br>

## 5. 사용한 개발 기술과 전략

### Front-end
- HTML과 CSS는 웹 개발의 기초이며, Bootstrap을 추가하여 빠르고 일관된 UI 개발과 반응형 디자인을 구현합니다.
- Bootstrap은 다양한 컴포넌트를 미리 제공하므로 개발 시간과 리소스를 절약할 수 있습니다.

### Back-end
- Java는 안정성과 확장성이 뛰어난 언어로, Spring Boot는 빠른 개발과 마이크로서비스 구조에 적합한 프레임워크입니다.
- Spring Boot의 자동 설정 기능을 통해 서버 환경을 빠르게 구축하고 필요한 기능을 쉽게 추가할 수 있습니다.
- Thymeleaf는 Spring Boot와의 높은 호환성 덕분에 서버 측 렌더링에 적합하며, 직관적인 문법으로 HTML과의 연동이 쉽습니다.
- MySQL은 안정성과 성능이 뛰어난 관계형 데이터베이스로, Spring Boot와의 연동이 용이하여 데이터 관리에 적합합니다.

### 버전 및 이슈 관리
- Git은 분산 버전 관리 시스템으로 코드의 버전 관리 및 추적을 효율적으로 수행할 수 있습니다.
- GitHub는 코드 리뷰와 이슈 관리를 통해 협업을 강화하며, 원활한 협업 환경을 제공합니다.

### 협업 툴
- Notion은 일정 관리 및 문서 작업에 유용하며, 팀 간의 소통을 원활하게 돕습니다.
- GitHub Wiki는 개발 문서 및 자료를 체계적으로 관리해 정보 공유를 용이하게 합니다.

<br>

## 6. 개발 기간
- 2024-09-30 ~ 2024-11-02

<br>

## 7. 트러블 슈팅
- 문제 : 게시글 수정 (등록 된 게시글을 수정할때 기존의 데이터 값이 수정 폼에 반영되지 않음)
- 원인 : html에 타임리프로 데이터를 받아오지 못하였음
- 해결 : 타임리프 문법을 추가하여 기존의 데이터 값을 표시

<br>

## 8. 프로젝트 후기
- 처음 진행하는 팀 프로젝트에서 큰 문제 없이 진행할 수 있었음에 감사합니다. 지금까지 Git은 혼자서만 관리해 본 경험으로 팀원들과 관리를 어떻게 해야 할지에 대한 막연함이 컸지만, 팀원들의 도움으로 무리 없이 진행할 수 있었습니다. 기능별로 브랜치를 생성하여 완료될 때마다 팀원들과 코드 확인을 하고, 충돌이 일어나는 경우가 적어서 다행이었고 값진 경험이었습니다. 그리고 이번 프로젝트에서 처음 부트스트랩을 사용해 봤는데, 각자의 장단점을 뚜렷하게 경험할 수 있었습니다. 개발이 빠르게 진행되는 장점은 너무 좋았지만, 세부적인 디자인 컨트롤이 약간 어려워 디자인의 컨셉이 뚜렷하다면 CSS를 직접 작성하는 것이 좋겠다는 생각을 했습니다. 그 이외에도 느낀 점들이 많고, 더 고쳐 나가고 싶은 부분도 있습니다. 다음 프로젝트 때는 이번 경험을 바탕으로 더욱더 좋은 서비스를 만들고자 합니다.
