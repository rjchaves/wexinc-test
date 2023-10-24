# wexinc-test
This is a spring boot project, you can run with _mvn spring-boot:run_, the project has 2 enpoints that you can see in the openAPI description http://localhost:8080/swagger-ui/index.html.
It also have a postman collection with both endpoints.

You can use search for transactions in the GET /transaction endpoint passing or not currency and country name. If you pass these data the application will get the exchange data and try to convert the amount, if you don´t pass,
the application will return only the transaction data stored in the database without conversion.
