# Smart Contact Manager

This is a Spring Boot application that allows users to manage their contacts. It provides features for user registration, login, and CRUD operations for contacts.

## Features

*   User registration and login
*   Add, view, update, and delete contacts
*   User profile page
*   Search for contacts
*   Pagination for contact list

## Technologies Used

*   Java 17
*   Spring Boot
*   Spring Data JPA
*   Spring Security
*   Thymeleaf
*   MySQL
*   Maven

## Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/aishaker129/Smart_Contact_Manager.git
    ```
2.  **Create a MySQL database:**
    *   Create a database named `smartcontact`.
3.  **Configure the application:**
    *   Open `src/main/resources/application.properties` and update the following properties with your MySQL database credentials:
        ```properties
        spring.datasource.username=your-username
        spring.datasource.password=your-password
        ```
4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
5.  **Access the application:**
    *   Open your web browser and go to `http://localhost:8282`.

## Endpoints

*   `/`: Home page
*   `/about`: About page
*   `/signup`: User registration page
*   `/signin`: User login page
*   `/user/index`: User dashboard
*   `/user/add-contact`: Add contact page
*   `/user/view_contact/{page}`: View contacts page
*   `/user/{cid}/contact`: View contact details page
*   `/user/delete/{cid}`: Delete contact
*   `/user/update_contact/{cid}`: Update contact page
*   `/user/profile`: User profile page
*   `/user_update/{id}`: Update user page
*   `/user-process-Update`: Process user update
*   `/deleteUser/{id}`: Delete user

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)
