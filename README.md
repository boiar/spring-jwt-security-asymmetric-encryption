# JWT Security with Asymmetric Encryption
This project explains the importance of asymmetric encryption in securing JWTs (JSON Web Tokens), how to generate your private/public keys, and compares symmetric and asymmetric encryption approaches.




## Project Structure 
```
src/main/
├── java/com/example/auth_security/
│   ├── auth/
│   │   ├── controller
│   │   ├── service
│   │   │   ├── impl
│   │   │   └── interfaces
│   │   ├── request
│   │   └── response 
│   │
│   ├── common/
│   │   ├── config
│   │   ├── entity
│   │   ├── messages
│   │   ├── request
│   │   └── validation
│   │
│   ├── security
│   │
│   ├── exception
│   │
│   ├── role/
│   │   └── entity
│   │
│   ├── user/
│   │   ├── controller
│   │   ├── entity
│   │   ├── service
│   │   │   ├── impl
│   │   │   └── interfaces
│   │   ├── repository
│   │   ├── request
│   │   ├── mapper
│   │   └── messages
│   │
│
└── resources/
    └── messages/   (shared messages)
        ├── messages_en.properties
        └── messages_ar.properties
```
