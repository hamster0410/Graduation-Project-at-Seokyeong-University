# R언어 학습 커뮤니티

# 📃개요

- 프로젝트 목적
- 프로젝트 아이디어 및 배경 설명
- Flow Chart
- 시스템 스펙 및 설정
- 사용된 기술 스택
- ERD 설계
- 동작 화면
- issue 사항

# 📌 프로젝트 목적

- 서경대 컴퓨터공학과 복수전공 졸업작품을 위해 개발하였습니다.
- 국비교육(클라우드 엔지니어링)과 복수전공 과정 동안 배운 지식들을 최대한 활용한 웹 서비스 개발 하는것입니다.
- 웹에서 코딩하고 소통할 수 있는 코딩 커뮤니티를 개발하는 것이 목적입니다.

# 💡 아이디어 및 배경

- 빅데이터 시장의 증가와 데이터 분석가에 대한 수요 증가하는 추세입니다.
- 그에 다른 데이터 분석 언어인 R에 대한 사람들의 수요가 커질 것으로 예상합니다.
- 코딩을 처음 접하는 사람들이 R에 대해 공부하고 소통할 수 있는 커뮤니티 구축하면 좋겠다는 생각이 들었습니다.

#  <img src="https://github.com/user-attachments/assets/7214e980-8399-4a69-9fe2-4e9ac047ac8a" alt="Flow Chart" style="width:30px; height:auto;"> Flow Chart

- 세션 기반 로그인 인증 구현 - redis 서버에 세션 저장
- 웹 게시판 CRUD, 댓글, 좋아요, 동영상 링크 업로드
- 네이버 검색 API를 활용한 도서 정보 검색
- 웹 서버에서 JSch라이브러리를 사용해 도커스웜 RStudio 서비스 실행, 및 삭제
- 서버간 NFS 마운트를 사용해 사용자가 작성한 프로그램을 업로드
- 샤이니 서버 접속 - 다른 사람 프로그램 사용

![제목을-입력해주세요_-002 (1)](https://github.com/user-attachments/assets/96099b41-e373-4f74-9abc-0d841395c690)


# 💻 시스템 스펙 및 설정

![image](https://github.com/user-attachments/assets/f518f2d0-0ad0-40b3-a5ef-7a14bc5f8322)

### Redis 서버

```bash
$ apt-get update
$ apt-get upgrade
$ sudo apt-get install redis-server
$ vi /etc/redis/redis.conf
$ maxmemory 1g
$ maxmemory-policy allkeys-lru
$ bind 0.0.0.0 
$ requirepass 아무거나
$ apt-get install redis-tools
```

### ManagerNode & WorkerNode

```bash
매니저노드, 워커노드 도커설치
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

### ManagerNode

```bash
JSCH를 위한 허용 설정
vi /etc/ssh/sshd_config
AllowTcpForwarding yes

도커 스웜 시작
docker swarm init --advertise-addr 192.168.99.121

샤이니서버 설치 
https://posit.co/download/shiny-server/

NFS 서버 설치
sudo apt install nfs-kernel-server -y

NFS 공유 디렉토리 설정
/etc/exports 파일에 디렉토리를 공유하도록 설정 

/usr/local/lib/R/site-library 공유iP대역(rw,sync,no_subtree_check)
/usr/home 공유iP대역(rw,sync,no_subtree_check)
sudo exportfs -ra
sudo systemctl restart nfs-kernel-server

도커 네트워크 
docker network create -d overlay rstudio
```

### WorkerNode

```bash

docker swarm join --token SWMTKN-1-5dbiad4b2at4ha56tzhljfn9kk2rm4hy3c2mh2b9usbzswzwl1-38ixsz9pcrm3ajw99wkupojfp 192.168.0.19:2377

/mnt경로에 shiny_library와 data 디렉토리 생성
root@workernode2:/mnt# mkdir data
root@workernode2:/mnt# mkdir shiny-library

NFS 클라이언트 설치
sudo apt install nfs-common -y

Manager노드에 파일 마운트 
sudo mount nfs_server_ip:/usr/local/lib/R/site-library /mnt/site-library

자동 마운트 설정 (선택 사항)
클라이언트에서 시스템이 부팅될 때마다 자동으로 NFS 공유를 마운트하려면 /etc/fstab 파일에 다음 라인을 추가

nfs_server_ip:/usr/local/lib/R/site-library /mnt/site-library nfs defaults 0 0
nfs_server_ip:/home /home nfs defaults 0 0
```

### Dockerfile

```yaml
FROM rocker/rstudio

RUN apt-get update && \
    apt-get install -y \
    libssl-dev \
    libcurl4-openssl-dev \
    libxml2-dev \
```

# <img src="https://github.com/user-attachments/assets/d9c862bb-d681-4788-9233-36db34292c38" alt="사용된 기술 스택" width="40"> 사용된 기술 스택

![%EC%A0%9C%EB%AA%A9%EC%9D%84-%EC%9E%85%EB%A0%A5%ED%95%B4%EC%A3%BC%EC%84%B8%EC%9A%94_-001](https://github.com/user-attachments/assets/ccc954de-18ad-47f7-a890-e495396d0d97)

# 🧱  ERD 설계

![Untitled_(1)](https://github.com/user-attachments/assets/bbbdbee6-fd84-4b42-b3e4-d14e4b68acf7)
