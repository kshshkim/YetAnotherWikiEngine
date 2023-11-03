# YetAnotherWikiEngine
### 소개
위키엔진 프로젝트입니다. 

테스트 커버리지 90% 이상을 목표로 하여, 적극적으로 테스트를 작성하였습니다. 커버리지는 `Jacoco`를 활용하여 측정하였습니다.

작성중...

### 기반 기술

- Java 17
- Spring Boot 2.7.13
- Spring Security
- Spring Data JPA
- QueryDSL
- MySQL 8

### 사용된 라이브러리

- FlexMark - 마크다운 파싱 (레퍼런스 추출)
- FasterXML Java UUID Generator - UUID v7 생성
- Jacoco - 테스트 커버리지 측정

### 패키지, 도메인 구분

- `auth`(회원, 인증)
    - `member`(회원)
    - `jwt`(AccessToken, RefreshToken)
- `core`(핵심 도메인)
    - `wikipage`(위키 페이지)
    - `wikireference`(내부 링크)
    - `contributor`(위키 기여자)
    - `permission`(편집, 관리 권한 등)
- `web`(웹 계층)

# 구조와 설명

## Core(핵심 도메인)

### WikiPage(위키 페이지)
<details>
<summary>위키 페이지 상세 - 펼치기</summary>

- 설명
    - 위키 문서에 대한 도메인
- `application` 계층
    - 커맨드와 쿼리 서비스 인터페이스를 애플리케이션 계층에서 나누어 정의함.
        - 커맨드에 비해 쿼리가 특정 DBMS나 DB 접근 기술 구현체에 더 의존적인 부분이 있기 때문임.
    - 외부로 도메인 객체를 반환하지 않음. 웹 계층에서 도메인 엔티티에 대한 정보를 모르도록 구성됨.
        - OSIV 비활성화시 Lazy 로드 등으로 인해 예상치 못한 문제가 발생할 수 있음.
    - `WikiPageCommandService`
        - 위키 문서 생성, 수정, 삭제 등, 위키 문서의 상태에 변화를 주는 기능에 대한 인터페이스
        - 트랜잭션 범위 설정
            - 위키 문서를 수정할 때, markdown을 파싱하여 내부 링크를 추출해야함. 이 파싱 과정이 트랜잭션의 바깥에서 수행되도록 유의함.
                - 나무위키의 "2022년" 문서는 파싱 이후 내부 링크를 추출하는데 170ms 가량 소요됨. (MacBook Pro 2021 M1Pro 16GB, Temurin 17.0.7)
            - 트랜잭션을 적절하게 설정하도록, `TransactionTemplate` 을 활용하였음.
    - `WikiPageQueryService`
        - 위키 문서 조회에 관한 인터페이스
        - 현재 QueryDSL을 이용해 쿼리하지만, NoSQL등으로 대체할 가능성을 열어둠.
- `domain` 계층
    - `WikiPageDomainService` (도메인 서비스 클래스)
        - DIP를 통해 다른 도메인에서 구현된 구현체를 호출할 수 있도록 도메인 서비스를 구성함. `WikiReferenceUpdater` , `WikiPagePermissionValidator` 등의 인터페이스는 다른 도메인 집합에서 구현됨.
            - 둘 모두 반환값을 갖지 않는 인터페이스이기 때문에, 추후 도메인 이벤트 기반으로 리팩터링 가능할 것으로 보임.
    - 도메인 모델
        - `WikiPage`
            - 책임
                - Aggregate Root. 변경 사항이 일어날 때 마다 cascade를 통해 `Revision` 엔티티를 영속화.
                - 버전 정보와 버전 토큰을 관리.(편집 충돌 방지)
                - 현재 버전의 문서 본문을 반환.
                - 제목, 최신 버전, 버전 충돌 방지를 위한 편집 토큰 등에 대한 정보를 반환.
        - `Revision`
            - 책임
                - `WikiPage`의 특정 버전에 대한 정보.
                    - `WikiPage`의 모든 수정사항은 버전 기록이 남아야함. 버전에 대한 정보는 수정될 일이 거의 없기 때문에 Immutable하게 설정.
                - 버전에 해당하는 `RawContent` 를 참조.
                - 버전에 해당하는 문서 본문을 반환.
                - 편집자, 버전 넘버, 이전 버전과의 size 차이, 편집 comment 등의 정보를 반환할 수 있음.
        - `RawContent`
            - markdown 파싱이 일어나지 않은 상태의 raw 원문이 저장됨.
            - 문서 본문, 본문의 길이에 대한 정보를 반환.
    - 인터페이스
        - `VersionCollisionValidator`
            - 버전 충돌이 일어나지 않음을 검증.
            - 현재는 `WikiPage` 의 버전 토큰을 대조하는 식으로 구현되었지만, 추후 보다 다양한 방식의 편집 충돌 방지 로직을 구현할 수 있을 것임.
        - `WikiPageCommandPermissionValidator`
            - 편집자와 WikiPage 정보를 받아, 편집자가 편집 권한을 가지고 있는지 검증.
        - `ReferenceTitleExtractor`
            - 마크다운 파서를 활용하여 문서 원문에서 내부 링크 목록을 추출.
        - `WikiReferenceUpdator`
            - 위키 본문에 수정이 일어날 때, 내부 참조 정보를 업데이트하기 위해 사용.
- `infra` 계층
    - `FlexMarkReferenceExtractor`
        - [FlexMark](https://github.com/vsch/flexmark-java) 라이브러리를 활용하여 구현한 위키 레퍼런스 추출기

</details>

### WikiReference(내부 참조)
<details>
<summary>내부 참조 상세 - 펼치기</summary>
  
- 설명
    - 위키 문서 간 내부 참조 정보에 대한 도메인
    - 참조하고 있는 문서, 참조되고 있는 문서 목록을 조회하기 위해 내부 참조를 저장
- 저장 형태
    - `WikiPage` 가 참조하는 `문서 제목`에 대하여 1개의 row로 저장
    - `Revision` 를 기준으로 참조를 저장하지 않는 이유는, RDB의 특성상 insert 쿼리에 과도한 비용이 발생하기 때문임. (참조가 많은 문서에 수정이 일어나면 모든 참조에 대해서 또 insert가 일어냠)
- 업데이트
    - 대부분의 위키 편집은 작은 단위로 이루어집니다. 이 때 마다 레퍼런스를 전부 수정하는 것은 불필요한 부하를 일으킬 수 있음.
    - 기존 참조 목록과 비교하여, 변경된 부분에 대해서만 `delete`, `insert`가 이루어지는 방식으로 구현함.
        - `insert ignore` 문을 사용하면 좀 더 간단하게 구현이 가능하지만, 다음과 같은 이유로 사용하지 않음.
            - ANSI 표준 SQL이 아님.
            - 성능 문제(불필요한 `insert` 가 시도됨)
- `WikiReference`
    - `WikiPage.id`, `참조하는 문서의 제목` 을 PK로 갖는 엔티티.
    - 읽기 접근이 훨씬 많을 것으로 예상됨. 클러스터 인덱스를 통해 접근할 수 있도록 하였음.
- `WikiReferenceUpdaterImpl`
    - 위키 문서간 내부 참조 업데이트를 위해 사용되는 구현체.
    - 업데이트 순서
        - 기존 레퍼런스 목록 조회 → 수정사항 반영하여 reference 제거 → 새로 생성된 reference insert 순서로 업데이트가 이루어짐.
- `WikiReferenceRepositoryImpl`
    - JDBC Template과 QueryDSL을 활용하여 구현한 Repository.
        - 대부분의 쿼리성 조회는 QueryDSL으로 구현함.
        - insert 쿼리의 경우 벌크 인서트시 성능 확보를 위해 JDBC Template과 ANSI 표준 SQL문을 활용함.
            - `rewriteBatchedStatements` 옵션을 활성화해야함.
            - SpringDataJPA의 `saveAll()`은 대상 엔티티가 많은 상황에 성능이 매우 떨어지는 문제가 있음.
                - hibernate 설정을 통해 insert 문을 한 번의 쿼리로 합쳐 넣는다고 해도, 각각의 insert 문이 하나로 합쳐지는 것은 아니기 때문에 여전히 만족스러운 성능이 나오지 않음.
            - JDBC Template과 `rewriteBatchedStatements` 를 통해 40배 이상 빠른 insert 처리 성능을 확보함. (벤치마크 테스트 코드 존재)
</details>

### Permission(권한)
<details>
<summary>권한 상세 - 펼치기</summary>

- 설명
    - 문서 편집시, 인가에 대한 로직을 처리함.
    - 위키 문서의 수정, 삭제, 이동 등의 행위에 대한 권한 레벨을 세부적으로 설정함.
    - 편집자의 권한과 ACL을 비교하여 수행 가능 여부를 판단함.
    - 자원(문서 등)에 대한 ACL 설정을 변경함.
- 역할 기반 접근 제어(RBAC)
    - `EVERYONE`, `NEW_MEMBER`, `MEMBER`, `ASSISTANT_MANAGER`, `MANAGER`, `ADMIN` 등, 여러 권한 수준 존재. `PermissionLevel` enum 클래스에 정의됨.
    - 사용자는 여러 권한을 가질 수 있으며, 가장 높은 권한을 대표 권한으로 사용함.
    - 아무런 권한이 없는 사용자는 `EVERYONE` 레벨의 행위만 수행 가능함.
    - 행위 목록은 `ActionType`  enum 클래스와, ACL 클래스(`Permission`)의 필드로 정의됨.
- 자원에 대한 접근 제어 리스트(ACL)
    - 네임스페이스에 대한 권한
        - 네임스페이스는 일반 문서, 템플릿 문서, 파일 문서 등 여러 종류가 존재함.
        - 네임스페이스의 권한 요구 설정값은 일종의 기본값으로, override 가능 여부를 설정 가능함.
            - override 허용 여부는 하위 권한으로 override, 상위 권한으로 override 따로 설정 가능함.
                - ex) `MEMBER` 이상만 가능한 행위를 `EVERYONE`으로 설정할수 없도록 제한하지만, `ADMIN`만 가능하도록 설정하고는 싶은 경우 등.
    - 위키 문서에 대한 권한
        - 위키 문서는 기본적으로 네임스페이스 권한을 따름.
        - 페이지별 세부 권한 요구사항을 설정할 수 있음.
            - 세부 권한에 대한 정보가 없으면 네임스페이스의 권한을 따름.
- 최적화 고려
    - ACL 엔티티(`Permission` 클래스)는 Immutable하게 구성함.
        - 캐싱 정합성을 확보하기 쉽도록 하기 위함.
    - 모든 ACL 조합 중, 자주 사용되는 경우의 수는 한정적임.
        - 자주 사용되는 조합을 프리셋으로 만들고, 이를 캐싱해둘수 있음.
    - 사용자 권한 수준을 JWT를 통해 stateless하게 받아올 수 있을 것임.
        - 사용자 권한 수준을 가져오는 로직을 추상화할수 있을 것임.

</details>

### Contributor(기여자, 편집자)

<details>
<summary>기여자 상세 - 펼치기</summary>
  
- 설명
    - 문서를 편집하는 주체에 대한 정보를 가짐.
    - 비로그인(익명) 기여자와 로그인(회원) 기여자 두 유형이 존재함.
- 기여자
    - 문서를 편집하는 actor.
    - 기여자는 공통적으로 외부에 보일 이름(name)을 가짐.
    - 기여자의 수정 내역을 조회할 수 있음.
    - 이외 추가적인 통계 정보를 제공할 수 있다고 가정하고 도메인을 분리함.
- 두 가지 유형의 기여자
    - `AnonymousContributor`
        - 기여자 이름으로 IP 주소를 가짐.(`InetAddress`)
        - 같은 IP 주소는 동일 기여자로 간주함.
    - `MemberContributor`
        - 회원 기여자는 외부에 보일 이름을 따로 지정할 수 있음.
- 이외
    - ID, 비밀번호 인증 등, 일반적인 회원 인증에 대한 정보를 포함하지 않. 별도로 구현하고, Event 기반으로 회원 가입이 일어날 때, 새로운 프로필을 생성함.
        - 인증에 대한 부분을 분리하여 독립적으로 작동하도록 구성.
  
</details>



## Auth(인증, 회원)
위키엔진 애플리케이션이 다른 인증 체계에도 쉽게 통합될수 있도록 관련 내용을 분리하였음.
<details>
<summary>인증, 회원 상세 - 펼치기</summary>
  
- 인증시 회원과 연결된 `AuthorityProfile`, `Contributor` 정보를 확인해야함.
- JWT에 `contributorID`, `contributorName` 을 넣어서 반환.

작성중…
  
</details>


## 문제와 해결
<details>
<summary>트랜잭션 시점 편집 충돌시, DB에서 예상치 못한 데드락이 발생하는 문제 (MySQL, MVCC) - 펼치기</summary>
  
- 발생
    - 트랜잭션 시점의 편집 충돌 상황을 테스트하던 중, 예상치 못한 데드락 문제가 발생함.
    - H2에서는 발생하지 않았지만, **MySQL로 테스트할 때에만 발생함**.
    - <details>
        <summary>로그 펼치기</summary>

        ```
        *** (1) TRANSACTION:
        TRANSACTION 1124784, ACTIVE 0 sec inserting
        mysql tables in use 1, locked 1
        LOCK WAIT 5 lock struct(s), heap size 1128, 2 row lock(s), undo log entries 2
        MySQL thread id 48, OS thread handle 281472362577856, query id 1107 172.17.0.1 root update
        insert into revision (comment, contributor_id, diff, raw_content, rev_version, size, page_id, rev_id) values ('8casio8F', x'e27e663c168b4a5785edd210c6d2c418', 8, x'018b847f254b7b5c9df64653c1c7136a', 2, 16, x'018b847f24677a02bbd2f2356703b54b', x'018b847f254a75e5a2590101006922fb')
        
        *** (1) HOLDS THE LOCK(S):
        RECORD LOCKS space id 31597 page no 4 n bits 72 index PRIMARY of table `wiki_dev`.`wiki_page` trx id 1124784 lock mode S locks rec but not gap
        Record lock, heap no 2 PHYSICAL RECORD: n_fields 9; compact format; info bits 0
         0: len 16; hex 018b847f24677a02bbd2f2356703b54b; asc     $gz    5g  K;;
        1: len 6; hex 0000001129a9; asc     ) ;;
        2: len 7; hex 01000000d01534; asc       4;;
        3: len 1; hex 01; asc  ;;
        4: len 16; hex 00000000000000000000000000000001; asc                 ;;
        5: len 8; hex 517737454546796b; asc Qw7EEFyk;;
        6: len 4; hex 80000001; asc     ;;
        7: len 30; hex 36646236306635622d363335392d343133382d626265622d343765396463; asc 6db60f5b-6359-4138-bbeb-47e9dc; (total 36 bytes);
        8: len 16; hex 018b847f250470ec9c4d527dd0bc39a6; asc     % p  MR}  9 ;;
        
        *** (1) WAITING FOR THIS LOCK TO BE GRANTED:
        RECORD LOCKS space id 31596 page no 5 n bits 72 index idx__revision__page_id__rev_version of table `wiki_dev`.`revision` trx id 1124784 lock mode S waiting
        Record lock, heap no 3 PHYSICAL RECORD: n_fields 3; compact format; info bits 0
         0: len 16; hex 018b847f24677a02bbd2f2356703b54b; asc     $gz    5g  K;;
        1: len 4; hex 80000002; asc     ;;
        2: len 16; hex 018b847f254a75e5a2590101006922fc; asc     %Ju  Y   i" ;;
        
        *** (2) TRANSACTION:
        TRANSACTION 1124785, ACTIVE 0 sec starting index read
        mysql tables in use 1, locked 1
        LOCK WAIT 8 lock struct(s), heap size 1128, 4 row lock(s), undo log entries 2
        MySQL thread id 49, OS thread handle 281472361521088, query id 1108 172.17.0.1 root updating
        update wiki_page set current_revision_id=x'018b847f254a75e5a2590101006922fc', is_active=1, owner_group_id=x'00000000000000000000000000000001', title='Qw7EEFyk', version=2, version_token='3c4c7473-5a41-4c13-a470-c45b6bef88f7' where page_id=x'018b847f24677a02bbd2f2356703b54b' and version=1
        
        *** (2) HOLDS THE LOCK(S):
        RECORD LOCKS space id 31596 page no 5 n bits 72 index idx__revision__page_id__rev_version of table `wiki_dev`.`revision` trx id 1124785 lock_mode X locks rec but not gap
        Record lock, heap no 3 PHYSICAL RECORD: n_fields 3; compact format; info bits 0
         0: len 16; hex 018b847f24677a02bbd2f2356703b54b; asc     $gz    5g  K;;
         1: len 4; hex 80000002; asc     ;;
         2: len 16; hex 018b847f254a75e5a2590101006922fc; asc     %Ju  Y   i" ;;
        
        *** (2) WAITING FOR THIS LOCK TO BE GRANTED:
        RECORD LOCKS space id 31597 page no 4 n bits 72 index PRIMARY of table `wiki_dev`.`wiki_page` trx id 1124785 lock_mode X locks rec but not gap waiting
        Record lock, heap no 2 PHYSICAL RECORD: n_fields 9; compact format; info bits 0
         0: len 16; hex 018b847f24677a02bbd2f2356703b54b; asc     $gz    5g  K;;
        1: len 6; hex 0000001129a9; asc     ) ;;
        2: len 7; hex 01000000d01534; asc       4;;
        3: len 1; hex 01; asc  ;;
        4: len 16; hex 00000000000000000000000000000001; asc                 ;;
        5: len 8; hex 517737454546796b; asc Qw7EEFyk;;
        6: len 4; hex 80000001; asc     ;;
        7: len 30; hex 36646236306635622d363335392d343133382d626265622d343765396463; asc 6db60f5b-6359-4138-bbeb-47e9dc; (total 36 bytes);
        8: len 16; hex 018b847f250470ec9c4d527dd0bc39a6; asc     % p  MR}  9 ;;
        
        *** WE ROLL BACK TRANSACTION (1)
        ```
        </details>
- 원인과 분석
    - MySQL의 특성에 의해 발생한 문제임.
        - `Revision` 엔티티가 삽입될 때, 참조 무결성 제약조건으로 인해서 `WikiPage`에 복수의 공유 락이 걸림.
            - MVCC 환경에선 일반적으로 락을 사용하지 않지만, MySQL의 경우, 참조 무결성 제약조건을 보장하기 위해 외래키(의 인덱스)에 대해 공유락을 걸게 됨.
        - 상황
            - 요약
                - **트랜잭션 A**(로그의 `(2) TRANSACTION`)는 `Revision`에 배타락을 가지고 `WikiPage`에 대한 배타락을 확보하기 위해 대기
                - **트랜잭션 B**(로그의 `(1) TRANSACTION`)는 `WikiPage`에 공유락을 가지고 `Revision`에 대한 공유락을 확보하기 위해 대기
            - 시간 순
                1. 트랜잭션 B가 시작됨. 
                2. 트랜잭션 A가 시작됨. 
                3. 트랜잭션 A는 `Revision` insert 쿼리 수행을 위해 `Revision`(의 unique 인덱스)에 배타락, `WikiPage`의 PK에 공유락 확보 시도, 모두 성공
                    1. `Revision` 배타락 - insert 쿼리 수행을 위해
                    2. `WikiPage` 공유락 - 외래키 제약조건 보장을 위해
                4. 트랜잭션 B는 마찬가지로 insert 쿼리 수행을 위해 락을 확보하기 위해 시도함. `WikiPage` PK에 대한 공유락 확보 성공, **`Revision`에는 A가 설정한 배타락이 걸려있기 때문에 대기** (로그에는 B가 공유락을 확보하기 위해 대기한다고 나오는데, 어째서 배타락이 아닌 공유락을 확보하려고 하는지에 대해선 명확한 답을 찾지 못하였음. 아래 의문점 단락 참조.)
                5. 트랜잭션 A는 `WikiPage`에 update 쿼리 수행을 위해 배타락 확보 시도, **`WikiPage`에는 B가 설정한 공유락이 걸려있기 때문에 대기**
                6. MySQL이 데드락 상황을 감지하고, B를 롤백시킴.
        - `WikiPage`에 update 쿼리가 필요한 이유
            - 최신 버전에 대한 정보를 `WikiPage` 에 저장하는 구조로 인한 update 쿼리
            - 편집 성공시 토큰 재생성 로직으로 인한 update 쿼리
            - JPA에서 관리하는 낙관적 락 버전 정보로 인한 update 쿼리
    - H2에서는 발생하지 않고, MySQL에서만 발생한 이유
        - H2에서는 `Revision` 삽입 시점에 unique **제약조건 위배로 트랜잭션이 실패**하게 됨.
            - (`wikipage_id`, `rev_version`) 인덱스에 unique 제약조건이 들어갔기 때문
        - MySQL의 특징
            - MySQL은 insert, update 쿼리가 잠재적으로 제약조건에 위배될 수 있는 경우, 공유락을 사용하여 제약조건을 만족시키도록 함. (MVCC, InnoDB 스토리지 엔진과는 별개로 MySQL의 작동 방식인 것으로 추정됨.)
            - 읽기 작업 시, MVCC에선 READ_COMMITTED 격리 수준에서도 다른 트랜잭션에서 commit된 내용에 영향을 받지 않음.
            - 쓰기 작업 시, MySQL, InnoDB의 MVCC는 언두로그를 이용한 구현이기 때문에 실제 테이블에 값이 들어가야함. 때문에 쓰기 작업에 대해선 제약조건을 만족해야함.
- 해결
    - `Revision` 엔티티의 외래키 참조무결성 제약 조건을 없애는 방향으로 해결함.
        - 바로 데드락 상황이 감지되었고 잘 처리했지만, 데드락이 발생하는 상황 자체가 이상적이지 않음.
        - `WikiPage`에 공유락이 설정되지 않기 때문에 먼저 `Revision`에 대한 락을 확보한 쪽의 편집이 성공하게 됨.
- 의문점
    - 트랜잭션 B는 어째서 `Revision`의 **공유락**을 확보하기 위해 대기하는가
      - 로그의 정보로 미루어보면, 배타락을 확보하기 전에 먼저 공유락부터 확보하려고 시도하는 것 같음.
      - 배타락에 대해서만 대기해도 될 것 같은데 어째서 공유락을 확보하려고 하는 것인가?
        - 배타락은 insert를 위해, 공유락은 해당 index의 unique 무결성 제약조건을 위해 확보하는 것으로, 용도가 다르다고 짐작되나, 명쾌하게 납득되진 않음.
          - insert 수행 이전에 수행 가능 여부를 확인하면서 공유락 설정 -> 이후 insert를 수행하면서 배타락 설정
          - insert 수행 이전에 배타락부터 걸게 되면 동시 처리 성능이 떨어질 수 있을 것 같음. (MVCC가 아닌 경우에는 확실히 떨어질 것 같지만, MVCC에선 공유락을 걸든 배타락을 걸든 큰 의미가 없어보임. 스토리지 엔진과는 별개로, MySQL의 작동 방식인 것 같음.)
      - 테스트에서 발생을 유도하지는 못했지만, 두 트랜잭션의 첫번째 insert 쿼리가 수행되는 시점에 모두 공유락 확보에 성공한다면 바로 데드락 상황이 나오지 않을지?
        - 이런저런 시도에도 이 조건으로 데드락 상황이 발생하지는 않았음.
          - 한 트랜잭션에서, 복수의 락 확보 시도가 하나의 단위로 수행되는 것인가?
    - MySQL과 InnoDB 스토리지 엔진의 내부 작동 방식에 대해 더 자세히 공부할 필요가 있을 것 같음.

</details>  

<details>
<summary>트랜잭션 범위를 벗어난 동시성 문제(편집 버전 충돌) - 펼치기</summary>

- 문제
    - 위키위키의 특성상, 사용자가 편집을 시작하는 시점과, 수정 내용을 commit하는 시점의 차이가 큼.
    - 이 사이에 다른 사용자가 commit을 한 경우, 출발한 버전이 다르기 때문에 실패시켜야함.
- 분석
    - 트랜잭션 시점에서 일어나는 충돌이 아님. 다른 방법을 통해 해결해야함.
- 해결
    - 무작위 생성된 버전 토큰을 일종의 낙관적 락으로 사용함.
        - 편집 토큰은 편집이 성공하면 재생성됨.
        - 편집 요청시 토큰을 첨부하는데, 이것이 현재 버전 토큰과 일치하지 않는 경우 요청 실패
            - 편집 충돌이 일어났다고 판단함.
    - 버전 번호가 아닌 무작위 문자열을 사용한 이유
        - 잦은 간격으로 편집이 일어날 때, 편집 충돌을 회피하기 위해서 버전 번호를 조작하여 요청하는 시도가 있을 것이라 생각됨. (실제로 다른 위키에 그러한 사례가 있었던 것으로 기억함)

</details>

<details>
<summary>위키 문서 간의 참조 정보 저장시 성능 문제 - 펼치기</summary>
  
- 배경
    - RDBMS를 활용하여 참조 정보를 저장함. (역링크 등에 활용)
    - `문서의ID-제목` 쌍을 저장해야함.
    - 대규모의 수정이 있는 경우, 수백, 수천건의 delete, insert 쿼리가 발생할 수 있음.
- 참조 정보가 많이 포함된 문서에 수정이 일어날 때의 문제
    - 원인
        - 본래 모든 reference를 지우고 새로 insert하는 식으로 간단하게 구현하였는데, 이 경우 많은 참조를 가지고 있는 문서를 수정할 때 비용이 너무 큰 문제가 있음.
    - 해결
        - 변경 사항에 대해서만 쿼리를 넣도록 수정함.
            - 기존 참조 목록과 새로운 참조 목록을 대조, 변경된 참조에 대해서만
    - 한계
        - 자잘한 수정이 불필요하게 큰 부하를 일으키는 것은 해결을 하였지만, 큰 규모의 수정이 일어나는 상황에 대해선 여전히 부하가 큼.
            - 그러나 대부분의 수정은 작은 규모로 일어나기 때문에 그렇게 큰 문제는 아닐듯함.
- 대량 insert시 성능 개선
    - 문제점
        - SpringDataJPA의 `saveAll()`은 대상 엔티티가 많은 상황에 성능이 매우 떨어짐.
            - auto increment PK를 사용하는 경우 특히 더 그렇지만, 복합키를 사용하기 때문에 해당사항이 없음.
            - SpringDataJPA의 경우 `@GeneratedValue`를 사용하지 않으면 `save`시 `Persistable` 인터페이스를 구현해줘야 정상적인 성능 측정이 가능하다는 점에 대해서 인지하고 있었으며, 이에 대해 적절히 처리를 했지만 성능이 불만족스러웠음.
            - 한 번에 모든 insert 쿼리를 넣는 옵션을 활성화해도 만족스러울 정도로 나아지지 않았음.
                - 한 번의 요청으로 모든 쿼리가 전달되기는 하지만, 여러개의 insert 쿼리를 하나로 합쳐주지는 않는 것으로 보임.
    - 해결방법
        - 성능 확보를 위해 JDBC Template과 ANSI 표준 SQL문을 활용함.
            - `rewriteBatchedStatements` 옵션을 활성화하여, 각각의 insert문을 bulk insert문으로 재작성함.
            - JDBC Template과 `rewriteBatchedStatements` 를 통해 40배 이상 빠른 insert 처리 성능. (벤치마크 테스트 코드 존재)
    - 결과
        - 40배 이상 더 빠르게 수행됨. (batch_size 100, 1000개 insert 기준)
        - DataJPA를 사용할 때, 1000개의 insert 쿼리에 평균적으로 350ms ~ 450ms 내외가 소요되었던 것에 비해, 평균적으로 20ms 안쪽으로 insert가 완료됨.
            - (Mac에서 Docker를 이용하여 띄운 인스턴스이기 때문에 차이가 있을 수 있음.)
            - 벤치마크 테스트 코드를 작성하여 수행한 결과임.
</details>         

<details>
<summary>@GeneratedValue PK를 사용하지 않을 시, Spring Data JPA에서 cascade 영속화가 제대로 되지 않는 버그 - 펼치기</summary>

작성중
</details>


<details>
<summary>마크다운 파싱으로 인한 트랜잭션 장기화 문제 - 펼치기</summary>
  
- 배경
    - 문서간 참조를 업데이트하기 위해서, raw 문서를 파싱하여 참조 링크를 분리해야함.
    - 스프링 `@Transactional` 어노테이션을 사용하는 경우, 메서드를 내부호출할 때 트랜잭션이 적용되지 않음.
- 예상되는 문제
    - 문서의 크기가 큰 경우, 마크다운 파싱에 시간이 오래 소요될 수 있음.
    - 트랜잭션의 범위 안에서 마크다운 파싱을 수행할 경우, 트랜잭션이 불필요하게 길어짐.
- 해결
    - `application` 계층에서 마크다운 파서를 호출하도록 설정, 이후 `TransactionTemplate`를 활용하여 트랜잭션을 시작하도록 함.
        - 메서드에서 `@Transactional` 어노테이션을 제거하고, `TransactionTemplate`을 사용하여 트랜잭션을 수행함.
        - Spring AOP의 내부호출 문제를 해결하기 위해 선택한 방법임.
    - 대안
        - `WikiPageDomainService`에 `@Transactional` 어노테이션을 달아서 해결할 수도 있음.
            - 도메인 계층 코드에 트랜잭션에 대한 책임이 설정되는 문제가 있음.
        - 클래스를 따로 만들어 해결
            - 이미 `WikiPageDomainService`까지 존재하는 상황에 클래스가 너무 많아지는 문제가 있음
  
</details>
            
