# SecureGuard Insurance - Insurance Management System

A comprehensive web-based insurance management application built with Spring Boot that enables users to manage insurance policies, file claims, process payments, and track transactions.

## 📋 Table of Contents

- [Tech Stack](#tech-stack)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Integrations](#integrations)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Project Structure](#project-structure)

## 🛠 Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.4.0** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **Spring Cloud OpenFeign** - HTTP client for microservices communication
- **Spring Cache** - Caching support
- **Hibernate** - ORM framework
- **Lombok** - Code generation to reduce boilerplate
- **Bean Validation** - Input validation

### Frontend
- **Thymeleaf** - Server-side template engine
- **HTML5/CSS3** - Frontend markup and styling
- **JavaScript** - Client-side interactivity

### Database
- **MySQL 8.0** - Relational database management system
- **Hibernate DDL Auto** - Automatic schema generation

### Build Tool
- **Maven** - Dependency management and build automation

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for unit tests
- **AssertJ** - Fluent assertions library

## ✨ Features

### User Management
- **User Registration & Authentication**
  - Secure user registration with validation
  - Login/logout functionality
  - Password encryption using BCrypt
  - Role-based access control (USER, ADMIN, EMPLOYEE)

- **User Profile Management**
  - Update personal information (name, address, email, phone)
  - Change password functionality
  - View account details

### Policy Management
- **Policy Types**
  - Life Insurance (Person)
  - Vehicle Insurance
  - Property Insurance

- **Policy Operations**
  - Create new insurance policies
  - View all policies with filtering
  - Cancel policies
  - Automatic premium calculation based on policy type:
    - Life Insurance: 0.5% of coverage amount
    - Vehicle Insurance: 6% of coverage amount
    - Property Insurance: 1% of coverage amount
  - Policy status tracking (Active, Pending, Inactive, Canceled)

### Claim Management
- **Claim Submission**
  - File claims for different insurance types
  - Automatic claim number generation
  - Claim type assignment based on policy type

- **Claim Processing**
  - Claim status tracking (Registered, Reviewing, Approved, Declined, Paid)
  - Employee/Admin claim approval and rejection
  - Liquidation amount assignment
  - Automatic daily payment processing via scheduled job

- **Claim Viewing**
  - View all user claims
  - Filter claims by status
  - View claim details and history

### Payment & Transactions
- **Wallet System**
  - Digital wallet for each user
  - Default wallet creation upon registration (€20.00)
  - Wallet balance management
  - Top-up functionality (add €200)

- **Transaction Tracking**
  - Transaction history (Deposits, Withdrawals)
  - Transaction status tracking
  - Automatic transaction creation for payments

- **Payment Processing**
  - Premium payments for policies
  - Claim payment processing
  - Scheduled daily payments for approved claims (runs at 13:47 daily)

### Dashboard & Analytics
- **User Dashboard**
  - Overview of policies, claims, and wallet balance
  - Recent transactions display
  - Upcoming payments
  - Statistics (Total Coverage, Total Premium, Claims This Year)
  - Notification center

- **Admin Dashboard**
  - User management (view, activate/deactivate users)
  - Role management
  - System-wide statistics

- **Employee Dashboard**
  - Claim review and processing
  - User management capabilities

### Notifications
- **Notification System**
  - SMS notifications for claim status updates
  - Notification history viewing
  - Delete all notifications functionality
  - Cached notification retrieval for performance

### Security Features
- **Authentication & Authorization**
  - Spring Security integration
  - Role-based access control (RBAC)
  - Secure password storage
  - Session management

- **Input Validation**
  - Form validation using Bean Validation
  - Custom exception handling
  - Error messages display

## 🏗 System Architecture

### Core Modules

1. **User Module** (`com.project.ins.user`)
   - User entity and repository
   - User service with authentication logic
   - User role management

2. **Policy Module** (`com.project.ins.policy`)
   - Policy entity and repository
   - Policy service with business logic
   - Premium calculation logic

3. **Claim Module** (`com.project.ins.claim`)
   - Claim entity and repository
   - Claim service with processing logic
   - Claim status management

4. **Wallet Module** (`com.project.ins.wallet`)
   - Wallet entity and repository
   - Wallet service with balance management

5. **Transaction Module** (`com.project.ins.transaction`)
   - Transaction entity and repository
   - Transaction service with history tracking

6. **Notification Module** (`com.project.ins.notification`)
   - Notification service
   - Feign client for external notification service

### Key Components

- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic implementation
- **Repositories**: Data access layer
- **DTOs**: Data transfer objects for request/response
- **Security**: Authentication and authorization
- **Scheduled Jobs**: Automated tasks (daily payments)

## 🔌 Integrations

### External Notification Service
- **Integration Type**: REST API via Spring Cloud OpenFeign
- **Service URL**: `http://localhost:8082/api/v1/`
- **Endpoints**:
  - `POST /sms` - Send SMS notifications
  - `GET /sms?userId={userId}` - Retrieve user notifications
  - `DELETE /sms?userId={userId}` - Delete all user notifications

- **Features**:
  - SMS notifications for claim payments
  - Notification caching for improved performance
  - Automatic retry on failure

### Database Integration
- **MySQL Database**: Centralized data storage
- **Connection**: JDBC connection to MySQL server
- **ORM**: Hibernate for object-relational mapping
- **Schema Management**: Automatic DDL updates

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Running notification service on port 8082 (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ins
   ```

2. **Configure Database**
   - Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/insurance_company?createDatabaseIfNotExist=true
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Open browser and navigate to: `http://localhost:8080`
   - The first registered user will automatically receive ADMIN role

### Default Configuration
- **Server Port**: 8080
- **Database**: MySQL (configured in application.properties)
- **Notification Service**: http://localhost:8082/api/v1/

## ⚙️ Configuration

### Application Properties
- Database connection settings
- Hibernate DDL auto-update mode
- Logging configuration
- Cache configuration (enabled)

### Security Configuration
- Form-based authentication
- Role-based access control
- Password encoding (BCrypt)

### Scheduled Tasks
- **Daily Payments**: Runs daily at 13:47 (1:47 PM)
  - Processes all approved claims
  - Transfers claim amounts to user wallets
  - Sends SMS notifications
  - Creates transaction records

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/project/ins/
│   │   ├── claim/          # Claim management module
│   │   ├── config/         # Configuration classes
│   │   ├── exception/      # Custom exceptions
│   │   ├── job/            # Scheduled tasks
│   │   ├── notification/   # Notification service integration
│   │   ├── numbergenerator/# Number generation utilities
│   │   ├── policy/         # Policy management module
│   │   ├── security/       # Security configuration
│   │   ├── transaction/    # Transaction management
│   │   ├── user/           # User management module
│   │   ├── wallet/         # Wallet management module
│   │   └── web/            # Controllers and DTOs
│   └── resources/
│       ├── static/         # CSS, images
│       ├── templates/      # Thymeleaf templates
│       └── application.properties
└── test/
    └── java/com/project/ins/
        ├── Integrationtest/   # Integration tests
        ├── claim/             # Claim service tests
        ├── notification/      # Notification service tests
        ├── policy/            # Policy service tests
        ├── transaction/       # Transaction service tests
        ├── user/              # User service tests
        ├── wallet/            # Wallet service tests
        └── web/               # Controller API tests
```

## 🔐 User Roles

1. **USER**
   - Create and manage policies
   - Submit and view claims
   - Manage wallet and transactions
   - View notifications

2. **EMPLOYEE**
   - All USER permissions
   - Review and process claims
   - Access employee dashboard

3. **ADMIN**
   - All EMPLOYEE permissions
   - User management (activate/deactivate)
   - Role management
   - System administration

## 📝 API Endpoints

### Public Endpoints
- `GET /` - Landing page
- `GET /login` - Login page
- `POST /login` - Login authentication
- `GET /register` - Registration page
- `POST /register` - User registration
- `GET /about` - About page
- `GET /terms` - Terms and conditions

### Authenticated Endpoints
- `GET /home` - User dashboard
- `GET /profile` - User profile
- `PATCH /profile/update` - Update user information
- `PATCH /profile/change-password` - Change password

### Policy Endpoints
- `GET /policy` - Create policy page
- `POST /policy/create` - Create new policy
- `GET /policy-view` - View all policies
- `PATCH /policy/{id}/cancel` - Cancel policy

### Claim Endpoints
- `GET /claims` - Submit claim page
- `POST /claims` - Submit new claim
- `GET /claims/all` - View all claims
- `PATCH /claims/{id}/cancel` - Cancel claim

### Admin/Employee Endpoints
- `GET /admin` - Admin dashboard
- `PATCH /admin/user/{id}/role` - Update user role
- `PATCH /admin/user/{id}/{status}` - Update user status (activate/deactivate)
- `GET /employee` - Employee dashboard
- `PATCH /employee/claims/{id}/approve` - Approve claim
- `PATCH /employee/claims/{id}/decline` - Decline claim

### Wallet & Transaction Endpoints
- `GET /transactions` - View transactions
- `PATCH /wallets/{id}/balance` - Top up wallet
- `GET /payment-view` - View payments

### Notification Endpoints
- `GET /notifications` - View all notifications
- `DELETE /notifications/delete-all` - Delete all notifications

## 🧪 Testing

The project includes comprehensive unit and integration tests:

### Unit Tests
- **User Service Tests** (`UserServiceUTest`) - Authentication, registration, profile management
- **Claim Service Tests** (`ClaimServiceUTest`) - Claim creation, processing, filtering
- **Policy Service Tests** (`PolicyServiceUTest`) - Policy creation, cancellation, coverage calculations
- **Transaction Service Tests** (`TransactionServiceUTest`) - Transaction creation and history
- **Wallet Service Tests** (`WalletServiceUTest`) - Wallet operations, top-up, balance management
- **Notification Service Tests** (`NotificationServiceUTest`) - Notification sending and retrieval

### Integration Tests
- **Register Integration Test** (`RegisterITest`) - End-to-end user registration flow

### Controller API Tests (WebMvcTest)
- **Admin Controller Tests** (`AdminControllerApiTest`)
- **Claim Controller Tests** (`ClaimControllerApiTest`)
- **Employee Controller Tests** (`EmployeeControllerApiTest`)
- **Home Controller Tests** (`HomeControllerApiTest`)
- **Index Controller Tests** (`IndexControllerApiTest`)
- **Payment Controller Tests** (`PaymentControllerApiTest`)
- **Policy Controller Tests** (`PolicyControllerApiTest`)
- **Transaction Controller Tests** (`TransactionControllerApiTest`)
- **Wallet Controller Tests** (`WalletControllerApiTest`)

**Total Test Coverage**: 89 tests covering all major components

Run tests:
```bash
mvn test
```

Or with Java 17 explicitly:
```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn test
```

## 📊 Key Features Summary

✅ User authentication and authorization  
✅ Policy creation and management  
✅ Claim submission and processing  
✅ Digital wallet system  
✅ Transaction tracking  
✅ Automated daily payments  
✅ SMS notifications  
✅ Role-based access control  
✅ Responsive web interface  
✅ Caching for performance optimization  

## 📞 Support

For issues, questions, or contributions, please contact the development team.

---

**Version**: 0.0.1-SNAPSHOT  
**License**: Proprietary  
**Copyright**: © 2025 SecureGuard Insurance. All rights reserved.
