# H2 콘솔 초기설정
브라우저에서 http://localhost:8080/h2-console에 접속합니다.

* JDBC URL: jdbc:h2:mem:testdb
* User Name: sa
* Password: (비워둡니다)

=============================================================

## 실습 내용
프로세스 생명주기 관리
SpringBoot app 1개, Nginx 1개를 가지고 멀티노드를 클러스터로 관리



## Docker 기본 명령어
```
docker images
docker ps -a
docker run -d -p 80:8080 --name sample-app thwowjd:docker-swarm-sample:app1.0
docker exec -it sample-app
docker rm -f [container ID]
docker rmi [image ID]

## Docker-compose 명령어
docker compose up -d
```

## 설정파일들은 애플리케이션 프로젝트에 보관
Dockerfile
nginx config

각 Dockerfile로 이미지를 만들어서 Docker hub에 푸쉬
docker-compose.yml 파일 작성할때 리모트 리포지토리에서 이미지를 가져온다

## 매니저노드에서 이미지로 클러스터 실행하기

### 도커 매니저 노드의 기본 ip 까지 설정
docker swarm init --advertise-addr 172.31.43.185

### 위를 실행하면 아래 명령어가 나오고 이를 워커 노드에 실행하면 클러스터가 갖추어진다
docker swarm join --token SWMTKN-1-5ao1pfd7voaahy9vmonno73j0138pna3w9v3nm0fru4tq2pftz-2inudhk6fk179fkcfmqkfu7ao 172.31.43.185:2377

### 노드 확인 (매니저 노드에서만 사용 가능)
docker node ls

### service로써 실행시켜줘야 됨
docker service create --name app \
--replicas 2 \
-p 8080:8080 \
thwowjd/docker-swarm-sample:app

### service 리스트 확인 (매니저 노드에서만)
docker service ls

### 해당 서비스의 Task를 확인
docker service ps [servece ID]

### 워커 노드에서 컨테이너 할당현황 확인
docker ps

### 서비스 종료
docker service rm [service name]

docker service ls
docker service ps [service ID]

### swarm에는 ingress 네트워크로 자체 로드밸런싱(라운드로빈 방식)을 해주고 있어서
### 서버3개중 2개에 컨테이너를 할당되어도 어느서버로 접근하든 접속 가능

# docker stack을 이용하여 배포하기

### compose 파일을 이용하여 실행
docker stack deploy -c docker-compose.yml swarm-sample

### stack 에서 스케일 아웃
docker service scale swarm-sample=3

### 무중단 배포?? app 새 버전을 바로 배포
docker service update \
--image thwowjd/docker-swarm-sample:app0.1 \
swarm-sample


## 비주얼라이저
docker service create \
--name=viz
--publish=8000:8080/tcp \
--contraint=node.role==manager \
--mount=type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock \
dockersamples/visualizer


=====================================

## 용어정리
- Service: 같은 이미지로 생성된 컨테이너의 집합
- Task: Service내에 컨테이너를 Task라 부른다
- Replica: 함께 생성된 Task



## 혼동되는 개념 정리
- commit 과 build 의 차이
둘다 이미지를 생성한다는 점에서는 같지만, 
commit은 백업의 개념 
build는 Dockerfile에 이미지 생성부터 컨테이너 실행까지의 일련의 과정을 파일로 정리


## 보안그룹 규칙 for Docker swarm
보안 그룹 설정에서 매니저 노드에 대해 TCP 포트 번호 2377, 4789, 7946을 인바운드 규칙으로 추가해주는 것이 필요합니다. 
각 포트의 역할은 다음과 같습니다:
- TCP 2377: 클러스터 관리 통신을 위한 포트. Swarm 매니저 노드 간의 통신에 사용됩니다.
- TCP/UDP 4789: VXLAN 오버레이 네트워크 통신에 사용되는 포트. 서비스 간 네트워킹을 위해 필요합니다.
- TCP/UDP 7946: 노드 간의 통신 및 서비스 발견을 위한 포트. Swarm 노드 간의 클러스터 내부 통신에 사용됩니다.










