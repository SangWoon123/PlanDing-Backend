#!/bin/bash

# 이미지 삭제
docker rmi planding
docker rmi rlatkddns1234/planding:ec2

# Docker 이미지 빌드
docker build -t planding --platform linux/amd64 .

# Docker 이미지 태그 지정
docker tag planding rlatkddns1234/planding:ec2

# Docker Hub로 이미지 푸시
docker push rlatkddns1234/planding:ec2

echo "모든 작업이 완료되었습니다."
