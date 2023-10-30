# Подключение к БД
Для подключения к базе данных PostgreSQL требуется изменить в файле application.yaml следующие строчки:
* URL: [https://github.com/TuPi4Ok/app/blob/adb8e9daad3de6076baac2d4c125b6124b398c0b/app/src/main/resources/application.yaml#L10C3-L10C3](https://github.com/TuPi4Ok/app/blob/adb8e9daad3de6076baac2d4c125b6124b398c0b/app/src/main/resources/application.yaml#L10C1-L10C1)
* Username: [https://github.com/TuPi4Ok/app/blob/adb8e9daad3de6076baac2d4c125b6124b398c0b/app/src/main/resources/application.yaml#L10C3-L10C3](https://github.com/TuPi4Ok/app/blob/adb8e9daad3de6076baac2d4c125b6124b398c0b/app/src/main/resources/application.yaml#L11)
* Password: [https://github.com/TuPi4Ok/app/blob/adb8e9daad3de6076baac2d4c125b6124b398c0b/app/src/main/resources/application.yaml#L10C3-L10C3](https://github.com/TuPi4Ok/app/blob/adb8e9daad3de6076baac2d4c125b6124b398c0b/app/src/main/resources/application.yaml#L12)
# Запуск
```sh
cd app
bash ./gradlew bootRun
```
# Документация Swagger
http://localhost:5000/api/swagger-ui/#/
# Коментарий к решению
* Контроллер аренды реализован таким образом, что при завершении аренды со счета пользователя снимается сумма за аренду, при этом сумма может уйти в минус(т.е. пользователь может остаться должен). Но при этом пользователь с отрецательным балансом не может начать аренду.
* При авторизации с помощью JWT в swagger, требуется указать ключевое слово"Bearer "(Пример ввода: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sInN1YiI6ImFkbWluIiwiaWF0IjoxNjk4NTA2MTUyLCJleHAiOjE2OTg1MDY3NTJ9.qSqJM4H27y15hIxBl3ZvnsMacetq4MslDjnNQDaMs0o")