import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// 부하 테스트 설정
export const options = {
  vus: 100, // 동시에 접속할 가상 유저 수
  duration: '5m', // 테스트 지속 시간
};

const body = {
   email: "hello@hello.com",
   password: "password!123"
};

const url = "http://localhost:8080/api/v1/auth/login"

export default function () {
  let res = http.post(url, JSON.stringify(body), {
    headers: { 'Content-Type': 'application/json' },
  });
  sleep(randomIntBetween(1, 5)); // sec
}
