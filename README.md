## Digital Library Management System

A console-based Java application to automate library operations — book management, student handling, and automatic fine calculation.

---

##  About

This project was developed as part of the **Programming in Java** course. It addresses common issues in manual library management like lost records, delayed fines, and inefficient book tracking.

---

##  Features

### Admin (Librarian)
- Secure login authentication
- Add new books to inventory
- Update stock quantity
- View all books
- Track issued books

### Student
- Search books by title
- Check real-time availability
- Borrow books (if stock available)
- Return books with automatic fine calculation
- View borrowed books

### Automation
- Due date: 14 days from issue
- Late fine: ₹10 per day
- Background thread for overdue monitoring

---

##  Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Database | MySQL 8.0 |
| JDBC Driver | MySQL Connector/J 9.5.0 |
| IDE | VS Code |

---

## 🚀 Installation & Setup

### Prerequisites
- Java JDK 21 or higher
- MySQL 8.0 installed and running
- Git (optional)

### Step 1: Clone the Repository
```bash
git clone https://github.com/yourusername/Library-Management-System.git
cd Library-Management-System
```

### Step 2: Setup Database
Open MySQL and run:
```sql
source library_setup.sql;
```

### Step 3: Update Database Credentials
Open `src/DBConnection.java` and modify:
```java
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### Step 4: Compile
```bash
javac -cp "lib/mysql-connector-j-9.5.0.jar;." src/*.java
```

### Step 5: Run
```bash
java -cp "lib/mysql-connector-j-9.5.0.jar;." src.Main
```

> **For Mac/Linux:** Replace `;` with `:` in both commands

---

##  Project Structure

```
Library-Management-System/
│
├── src/
│   ├── Main.java              # Entry point & menu
│   ├── DBConnection.java      # Database connection (Singleton)
│   ├── BookDAO.java           # Database operations
│   ├── Book.java              # Book model
│   ├── Member.java            # Student model
│   └── OverdueCheckTask.java  # Background monitoring thread
│
├── lib/
│   └── mysql-connector-j-9.5.0.jar
│
├── library_setup.sql          # Database schema
└── README.md                  # This file
```

---

## 🎮 Usage Guide

### Default Login
| Role | Email/ID | Password |
|------|----------|----------|
| Admin | admin@library.com | admin123 |

*Students need to register first*

### Menu Options

**Admin Menu:**
1. Add Book
2. Update Stock
3. View All Books
4. View Issued Books
5. Logout

**Student Menu:**
1. Search Books
2. Issue Book
3. Return Book
4. View My Books
5. Logout

---

## 🐛 Troubleshooting

| Error | Solution |
|-------|----------|
| `Connection refused` | Ensure MySQL service is running |
| `Access denied for user` | Check password in DBConnection.java |
| `Class not found` | Compile with JAR file in classpath |
| `Unknown database` | Run library_setup.sql first |


## 👨‍💻 Author

**Pallavi Joshi**  # Java-Project-Vityarthi
