# spring-boot-session-api-demo

API usage


**** SignnUp api *****

curl -v -X POST 'http://localhost:8000/signup' --header 'Content-Type: application/json' --data '{"email" : "demo@mail.com","password" : "pass","name": "demo"}'

response  <<<< {"name":"demo","email":"demo@mail.com","userId":"3c688b3f-dc3a-49f3-acec-0af4d53f1ae4"} >>>>





**** login api *****

curl -v -X POST 'http://localhost:8000/login' --header 'Content-Type: application/json' --data '{"email" : "laxman@mail.com", "password" : "pass"}'

** token is provided in the response headers with key 'token'

response headers  <<<token: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzYzY4OGIzZi1kYzNhLTQ5ZjMtYWNlYy0wYWY0ZDUzZjFhZTQiLCJleHAiOjE1OTY0Nzc4MTJ9.w7cyd8DhhQwFQ7k7IU--XgHB1IH2r3sZB_17F5XafJvhSQRfYWkAYbkG3faaFWHJf_vXWrV7WV3P2oUZdrkdEA >>>>





**** dummy protected api ****

curl -v -X GET 'http://localhost:8000/dummy' --header 'Authorization: Bearer {TOKEN_FROM_LOGIN_API}'

response  <<<< Just showing the restricted dummy page >>>>
