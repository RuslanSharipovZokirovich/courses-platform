# 📚 Online Course Platform (Java + Spring Boot)

## 📌 Project Overview

This is a web application for publishing and accessing online courses, built with **Java**, **Spring Boot**, and **PostgreSQL**. The project serves as both a learning exercise and a foundation for a real-world platform. It includes authentication, course/module/video structure, and an admin panel (in progress).

---

## 🛠️ Features

### 🔑 Authentication & Authorization

- Register by email: system generates and sends a temporary password
- Login using email and password
- JWT-based token authentication for secure endpoints
- Access to courses is restricted to pre-approved emails in the admin panel

### 🧑‍💻 Admin Panel (in progress)

- Add new courses: title, description, icon
- Add modules to courses
- Add videos to modules
- Rich text editor for module descriptions (planned)
- Ability to attach useful links to modules

### 🎓 Courses & Modules

- Each course consists of multiple modules
- Each module includes a video and description
- Only authorized users can view their allowed courses
- Videos can be embedded via YouTube or linked/stored locally

---

## ⚙️ Technologies Used

| Technology        | Purpose                          |
|-------------------|----------------------------------|
| Java 17           | Main programming language        |
| Spring Boot       | Backend framework                |
| Spring Security   | Authentication and authorization |
| Spring Data JPA   | ORM and database interaction     |
| PostgreSQL        | Relational database              |
| JWT (jjwt)        | Token-based authentication       |
| Lombok            | Cleaner code with annotations    |
| Maven             | Dependency management            |

---

## 📁 Project Structure

---

## ✅ Implemented

✅ Email-based registration with auto-generated password  
✅ Login with JWT token generation  
✅ Password hashing and secure storage  
✅ PostgreSQL integration  
✅ Course → Module → Video hierarchy  
✅ Restricted access to courses based on email  
🚧 Admin panel under development  
🚧 No frontend UI yet (REST API only)

---

## 🚀 Roadmap

- [ ] Full admin dashboard (Spring + Thymeleaf)
- [ ] File & video uploads
- [ ] Email notifications (SMTP support)
- [ ] Student progress tracking
- [ ] Student dashboard interface

---

## 👤 Author

**Ruslan Sharipov Zokirovich**  
Java Developer | Uzbekistan → Thailand | Planning relocation to the US  
Email: ruslan.sharipov.zokirovich@gmail.com  
Telegram: [@ruslsharipov](https://t.me/ruslsharipov)