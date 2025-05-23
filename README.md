# Examify - Online Examination System

A full-stack web application designed to support teachers in conducting online assessments and exams, while enabling students to take exams and complete assessments, inspired by platforms like Azota, Quizizz, and Kahoot.

> **Capstone Project** | Team Size: 9 (1 Product Owner, 1 Scrum Master, 7 Developers)  
> **Timeline**: 3/2025

![Examify Landing Page](https://github.com/user-attachments/assets/b9684a0d-8606-4709-9455-09b9f6a55c9c)

---

## 📌 Description

Examify is an online examination system developed as part of a Scrum course by **Axon Active** and **HCMUT**, focusing on Agile Scrum methodologies within the Advanced Software Engineering curriculum. The project was completed over 2 days, consisting of 3 sprints (each approximately 4 hours). The platform enables teachers to create and manage tests, while students can access them using a passcode, take exams, complete assessments, and submit answers online. Results are tracked per student and per test, allowing teachers to analyze class performance.

---

## 📶 Website Access

Access the deployed website here:  
[https://examify-online.azurewebsites.net/](https://examify-online.azurewebsites.net/)

---

## 📂 GitHub Repositories

- **Backend**: [github.com/ABsievil/BE-Examify-System](https://github.com/ABsievil/BE_Examify-System)  
- **Frontend**: [github.com/Gloxiniaaa/Examify-FE](https://github.com/Gloxiniaaa/Examify-FE)

---
## 🧪 Access Accounts

| Role    | Username | Password   |
|---------|----------|------------|
| Teacher | admin    | 1245       |
| Student | ndduc    | 1245       |

Some passcodes to take exams:  
- Passcode 1: 12345678 
- Passcode 2: 888888
- Passcode 3: 999999

---

## 🚀 Technologies Used

### Backend:
- Java, Spring Boot
- RESTful API
- Spring Security, JWT Authentication
- PostgreSQL
- Deployment: Azure

### Frontend:
- ReactJS, JavaScript
- HTML5, CSS3
- Axios for HTTP requests

### Tools:
- Trello (project management): [Tech Hunter Trello Board](https://trello.com/b/iAkqhwDt/tech-hunter)
- Notion (API documentation): [Examify APIs Document](https://www.notion.so/API-Doc-Examify-1be5fd4cfa478035acfde2bfed6518fd?pvs=4)

---

## 👥 Team Roles and Responsibilities

| Name              | Role               |
|-------------------|--------------------|
| Nguyễn Ngọc Duy   | Product Owner      |
| Nguyễn Đình Đức   | Scrum Master & Backend Developer |
| Nguyễn Cao Cường  | Backend Developer  |
| Chế Minh Đức      | Tester             |
| Mã Trường Vũ      | Database Designer  |
| Nguyễn Mạnh Hùng  | Frontend Developer |
| Mai Văn Hoàng Duy | Frontend Developer |
| Lâm Kim Huy       | Frontend Developer |
| Nguyễn Văn Sơn    | Frontend Developer |

---

## 🧹 Core Functionalities

- **User Roles**: Teacher, Student — each with distinct permissions and UI.

- **Authentication & Authorization**:
  - Secure login and logout for Teachers and Students.
  - Passwords are hashed using the SHA-256 encryption algorithm.
  - Role-based access control (Teacher, Student).
  - OAuth2 integration for secure third-party authentication.
  - Send mail OTP for secure user verification during registration or password reset.
  
- **User Profile Management**:
  - Change user information (e.g., name, email, password).

- **Test Management (Teacher)**:
  - Create, edit, and manage simple assessments (multiple-choice questions).
  - View class results and analyze performance per student and per test.

- **Test Access and Exam Participation (Student)**:
  - Access tests using a unique passcode.
  - Take exams and complete assessments online.
  - Submit answers and view scores and past test results.

- **Result Tracking**:
  - Results are stored per student and per test for easy tracking and analysis.

- **User-Friendly Design**:
  - Simple and usable testing solution for both teachers and students.

---

## 📽️ Demo

Watch the demo videos here:  
- [Presentation 1](https://youtu.be/JVZiYtB-YWg)  
- [Presentation 2](https://youtu.be/d8ybREd4UaA)

---

## 🛠️ Setup Instructions

### Backend:
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend:
```bash
cd frontend
npm install
npm start
```

---

## 🛠️ Challenges Faced

- **Too Many Business Ideas**: The team struggled to agree on a single concept due to numerous ideas, which caused initial delays in defining the project scope.
- **Communication Issues**: Lack of effective communication between Backend (BE) and Frontend (FE) members led to integration challenges.
- **Prioritization Difficulties**: The team focused on multiple aspects simultaneously, and new ideas with better solutions caused confusion in prioritization.

## 📚 Lessons Learned

- **Improved Communication**: The team learned to communicate effectively in English with each other, enhancing collaboration.
- **Technical Skill Growth**: Members improved their technical skills and teamwork practices through the project.
- **Team Bonding**: Organized outside team-building activities (e.g., coffee outings, Esports) to strengthen team cohesion.

---
