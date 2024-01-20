<a href="https://codeclimate.com/github/TuPi4Ok/app/maintainability"><img src="https://api.codeclimate.com/v1/badges/14d567e84af4e43fa10f/maintainability" /></a>
<a href="https://codeclimate.com/github/TuPi4Ok/app/test_coverage"><img src="https://api.codeclimate.com/v1/badges/14d567e84af4e43fa10f/test_coverage" /></a>
# rental_service
rental_service - is a web application that is a transportation service management system that provides user registration and authentication. Users can view, rent, and manage various modes of transportation such as cars, bicycles, and scooters. The app supports new account registration, authentication, retrieving current account data, and updating and deleting accounts. It also allows administrators to view, create, modify, and delete both user accounts and vehicle information. Vehicle rentals, rental history, and the ability to add funds to an account balance are also included in the app's functionality.
# Strat
```sh
cd app
./gradlew bootRun
```
# Start with docker
```sh
doker compose up
```
# Swagger documentation
By launching the application you can view the documentation at: http://localhost:5000/api/swagger-ui/
# Tech stack
* Spring framework (data, security, web, validation)
* PostgreSQL, H2
* liquibase
* JUnit 5
* Docker
* CI/CD with GitHub Actions
