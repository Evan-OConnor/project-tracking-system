<a name="readme-top"></a>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li>
      <a href="#project-overview">Project Overview</a>
      <ul>
        <li><a href="#features">Features</a></li>
        <li><a href="#system-architecture">System Architecture</a></li>
        <li><a href="#database-design">Database Design</a></li>
      </ul>
    </li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#screenshots">Screenshots</a></li>
    <li><a href="#documentation">Documentation</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

<p align="center">
  <img width="1654" height="1185" alt="dashboard" src="https://github.com/user-attachments/assets/096160de-31f6-4e4c-9925-ce86cb041cc1" />
</p>

The **Project Tracking System** is a multi-user, web-based application designed and implemented for a fictional organisation, _Aardvark Insurance Assessors_, as part of a capstone project at the University of Galway. It provides a centralised platform for office and administrative staff to manage insurance assessment projects and their associated operational and financial data. 

The system supports core business workflows, including project management, contact administration, employee timesheet and expense tracking, and the creation of invoices and receipts. It also provides reporting and search capabilities, enabling users to efficiently retrieve and analyse project and financial information. 

The application is built using a three-tier architecture, consisting of a Spring Boot backend developed in Java, a Thymeleaf-based frontend for server-side rendering, and a MySQL relational database. Data persistence is managed through Spring Data JPA, with a normalised schema maintained via Flyway migrations.

Secure authentication and role-based access control are implemented using Spring Security, with persistent user accounts, encrypted password storage using BCrypt, and controlled access to protected and administrative system functionality. 

Server-side PDF document generation of invoices, receipts, and cover letters is implemented through a structured pipeline involving service-layer data preparation, DTO mapping, template rendering using Thymeleaf, and HTML-to-PDF conversion.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- BUILT WITH -->
## Built With

* [![Java][Java-shield]][Java-url]
* [![Spring Boot][SpringBoot-shield]][SpringBoot-url]
* [![Spring Security][SpringSecurity-shield]][SpringSecurity-url]
* [![Spring Data JPA][SpringDataJPA-shield]][SpringDataJPA-url]
* [![Hibernate][Hibernate-shield]][Hibernate-url]
* [![MySQL][MySQL-shield]][MySQL-url]
* [![Flyway][Flyway-shield]][Flyway-url]
* [![Thymeleaf][Thymeleaf-shield]][Thymeleaf-url]
* [![Bootstrap][Bootstrap-shield]][Bootstrap-url]
* [![Maven][Maven-shield]][Maven-url]
* [![OpenHTMLtoPDF][PDF-shield]][PDF-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

Ensure the following are installed:

- Java 21
- Maven
- MySQL 8+

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Installation

Clone the repository:

```bash
git clone https://github.com/Evan-OConnor/project-tracking-system.git
cd project-tracking-system
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Database Setup

Create a MySQL database:

```sql
CREATE DATABASE project_tracking_system;
```

<br>

Update the database credentials in `src/main/resources/application.properties`:

```
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Run the Application

Start the application:

```bash
mvn spring-boot:run
```
<br>

Flyway will automatically apply the required database migrations on startup.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Access the Application

Open your browser and navigate to `http://localhost:8080`

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Default Login

Sign in using the default System Administrator account:

```
Username: U000001
Password: TempAdminPassword123
```

Note: To change the default password, update the BCrypt hash in `src/main/resources/db/migration/V5__seed_initial_admin.sql` before running the application for the first time.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- PROJECT OVERVIEW -->
## Project Overview

### Features

- Secure multi-user authentication using Spring Security
- Role-based access control (ADMIN / STAFF) for protected system functionality
- Secure password hashing using BCrypt
- Server-side rendered user interface using Thymeleaf with role-based access
- Project management with associated contacts, clients, and organisations
- Employee and system user account management
- Financial workflows including timesheets, expenses/outlays, invoices, and receipts
- VAT calculation, payment tracking, and discount handling for invoices and receipts
- Server-side PDF document generation for invoices, receipts, and cover letters
- Flexible search and reporting across core system entities
- Input validation using Jakarta Bean Validation and database constraints
- Database schema versioning and migrations using Flyway
- Unit and integration testing using JUnit, Mockito, and MockMvc

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### System Architecture

The application follows a three-tier layered architecture, separating presentation, business logic, and data access.

- **Presentation Layer**: Implemented using Spring MVC controllers and Thymeleaf templates, providing a server-rendered web interface for user interaction.  
- **Service Layer**: Contains core business logic and application workflows, enforcing business rules and coordinating operations between the web and data access layers.  
- **Data Access Layer**: Implemented using Spring Data JPA repositories, with Hibernate ORM mapping domain entities to a relational database schema.  
- **Security Layer**: Managed using Spring Security, providing authentication, role-based access control (RBAC), and secure password handling.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Database Design

The system uses a normalised relational database schema to store application data and maintain relationships between core business entities.

Key entities within the system include: 

- Projects
- Contacts
- Employees
- System Users
- System Roles
- Timesheets
- Expenses and Outlays
- Invoices
- Receipts

Relationships between entities are enforced through foreign key constraints and database-level validations, ensuring referential integrity.

The database schema is version-controlled using Flyway migrations, allowing schema changes to be tracked and applied consistently across development environments.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- TESTING -->
## Testing

Testing is performed at both unit and integration levels to verify core business logic, security behaviour, and request handling within the application.

- **Unit Testing:** Service-layer components are tested using JUnit and Mockito to validate business logic, financial calculations, and data integrity.

- **Integration Testing**: Controller endpoints are tested using MockMvc to verify request handling, validation, and role-based access control (RBAC) under different authentication scenarios.  

Tests cover key areas including:

- user authentication and security configuration  
- role-based access control (ADMIN / STAFF permissions)  
- user creation and management workflows  
- financial logic for invoices, receipts, and cost items  
- validation, error handling, and edge-case scenarios  
- controller request handling and response behaviour  

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- SCREENSHOTS -->
## Screenshots

### Login Page
<p align="center">
  <img width="1669" height="959" alt="localhost_8080_login" src="https://github.com/user-attachments/assets/6936ee85-97d5-4387-80a7-2d3ba593af3c" />
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Dashboard
<p align="center">
  <img width="1654" height="1185" alt="dashboard" src="https://github.com/user-attachments/assets/096160de-31f6-4e4c-9925-ce86cb041cc1" />
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Projects & Contacts
<p align="center">
  <img width="1654" height="1181" alt="projects-page" src="https://github.com/user-attachments/assets/02e610cb-f71d-4c7c-9b4d-099a22e8ec8d" />
</p>

<br>

<p align="center">
  <img width="1903" height="897" alt="contacts-page" src="https://github.com/user-attachments/assets/1a2d6464-5d57-4516-aabb-b467575fac51" />
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Financial Workflow (Invoices & Receipts)

<p align="center">
  <img width="1903" height="901" alt="invoice-page" src="https://github.com/user-attachments/assets/0f5570f8-47fe-4cc5-9f7e-41cdcab4d04e" />
</p>

<br>

<p align="center">
  <img width="1903" height="672" alt="receipts-page" src="https://github.com/user-attachments/assets/c1e08a49-2dcc-49e0-9083-f5048874cba9" />
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Generated Documents (PDFs)

<p align="center">
  <img width="60%" alt="invoice-pdf" src="https://github.com/user-attachments/assets/70e0b467-be13-4ac9-9d48-29dede9f6f97" />
</p>

<br>

<p align="center">
  <img width="60%" alt="cover-letter-pdf" src="https://github.com/user-attachments/assets/d9c834df-ca92-4b87-b278-f173d16b8113" />
</p>

<br>

<p align="center">
  <img width="60%" alt="receipt-pdf" src="https://github.com/user-attachments/assets/c45f6727-4e65-41ea-97b5-9a26310bdb3f" />
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Reporting
<p align="center">
  <img width="1903" height="705" alt="reports-page" src="https://github.com/user-attachments/assets/3b706b6d-36e2-4a50-9079-99cc545f14f2" />
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### User Management (Admin)
<p align="center">
  <img width="1903" height="954" alt="users-page" src="https://github.com/user-attachments/assets/7afb53d8-428f-4493-8dea-f7ad8a167d46" />
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Error Pages
<p align="center">
  <img width="1921" height="736" alt="error-403" src="https://github.com/user-attachments/assets/80081ddd-ed3b-41f1-a631-5091361f4a88" />
</p>

<br>

<p align="center">
  <img width="1921" height="736" alt="error-404" src="https://github.com/user-attachments/assets/475755cd-40ea-487e-9cc4-2c21d6115da7" />
</p>

<br>

<p align="center">
  <img width="1921" height="736" alt="error-500" src="https://github.com/user-attachments/assets/a3ae1882-0e07-401c-b7d3-3bdb62e13491" />
</p>



<!-- DOCUMENTATION -->
## Documentation
The full documentation for the system is available in the project report: 

- [CT5118-Report.pdf](https://github.com/user-attachments/files/26718661/CT5118-Report.pdf)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Evan O'Connor - [github.com/Evan-OConnor](https://github.com/Evan-OConnor) - [linkedin.com/in/evan-oc](https://linkedin.com/in/evan-oc/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[Java-shield]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/

[SpringBoot-shield]: https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot

[SpringSecurity-shield]: https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white
[SpringSecurity-url]: https://spring.io/projects/spring-security

[SpringDataJPA-shield]: https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[SpringDataJPA-url]: https://spring.io/projects/spring-data-jpa

[Hibernate-shield]: https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white
[Hibernate-url]: https://hibernate.org/

[MySQL-shield]: https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white
[MySQL-url]: https://www.mysql.com/

[Flyway-shield]: https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white
[Flyway-url]: https://flywaydb.org/

[Thymeleaf-shield]: https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white
[Thymeleaf-url]: https://www.thymeleaf.org/

[Bootstrap-shield]: https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com/

[Maven-shield]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/

[PDF-shield]: https://img.shields.io/badge/OpenHTMLtoPDF-000000?style=for-the-badge&logo=adobeacrobatreader&logoColor=white
[PDF-url]: https://github.com/danfickle/openhtmltopdf
