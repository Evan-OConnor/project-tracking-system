<a name="readme-top"></a>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#features">Features</a></li>
        <li><a href="#system-architecture">System Architecture</a></li>
        <li><a href="#database-design">Database Design</a></li>
      </ul>
    </li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

<p align="center">
  <img width="1920" height="930" alt="image" src="https://github.com/user-attachments/assets/1d81d379-9a85-4f7f-96a5-68b57f0fa87e" />
</p>

A multi-user web-based project tracking system developed as part of a two-person capstone project for the Higher Diploma in Software Development & Design at the University of Galway.

The application enables a fictional organisation, Aardvark Insurance Assessors, to manage projects, financial data, contacts, employees, and documentation through a secure web interface.

The system implements a layered architecture with secure authentication, role-based access control, and a normalised relational database schema.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Java][Java-shield]][Java-url]
* [![Spring Boot][SpringBoot-shield]][SpringBoot-url]
* [![Spring Security][SpringSecurity-shield]][SpringSecurity-url]
* [![Spring Data JPA][SpringDataJPA-shield]][SpringDataJPA-url]
* [![MySQL][MySQL-shield]][MySQL-url]
* [![Flyway][Flyway-shield]][Flyway-url]
* [![Thymeleaf][Thymeleaf-shield]][Thymeleaf-url]
* [![Bootstrap][Bootstrap-shield]][Bootstrap-url]
* [![Maven][Maven-shield]][Maven-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Features

- Multi-user authentication using Spring Security
- Role-based access control (RBAC) for system administration and staff users
- Project management, including associated contacts and client information
- Financial tracking through timesheets, expenses/outlays, invoices, and receipts
- Employee and system user account management
- Search functionality for system users
- Input validation using Jakarta Bean Validation and database constraints
- Database schema versioning and migrations using Flyway
- Unit testing for service and security components using JUnit and Mockito

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### System Architecture

The application follows a three-tier layered architecture that separates presentation, business logic, and data access.

- The presentation layer is implemented using Spring MVC controllers and Thymeleaf templates to provide a server-rendered web interface for interacting with the system.
- The service layer contains core business logic and workflows, enforcing business rules, coordinating operations, and acting as the boundary between the web layer and the persistence layer.
- The data access layer is implemented using Spring Data JPA repositories. Domain entities are mapped to relational tables using Hibernate ORM, enabling structured persistence and retrieval of application data.
- Authentication and authorisation are implemented using Spring Security, providing secure password hashing and role-based access control (RBAC) to restrict access to system functionality.

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

Relationships between entities are enforced through foreign key constraints, ensuring referential integrity and consistent data management across the system.

The database schema is version-controlled using Flyway migrations, allowing schema changes to be tracked and applied consistently across development environments.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started
This project is currently in active development and is not yet ready for external setup or execution. The repository is being developed incrementally as part of a university capstone project, and the main development branches have not yet been merged into a stable release.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- TESTING -->
## Testing

Unit tests verify core service and security functionality within the application. Testing is performed using JUnit and Mockito, enabling isolated testing of business logic and authentication components.

Current tests focus on validating:
- user authentication and security configuration
- system user creation and management workflows
- role-based access control behaviour within the application

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- Contact -->
## Contact

Evan O'Connor - [github.com/Evan-OConnor](https://github.com/Evan-OConnor) - [linkedin.com/in/evan-oc](https://linkedin.com/in/evan-oc/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[Java-shield]: https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/

[SpringBoot-shield]: https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot

[SpringSecurity-shield]: https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white
[SpringSecurity-url]: https://spring.io/projects/spring-security

[SpringDataJPA-shield]: https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[SpringDataJPA-url]: https://spring.io/projects/spring-data-jpa

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
