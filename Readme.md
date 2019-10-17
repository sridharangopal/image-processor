## Spring Boot File Upload / Download Rest API Example


## Steps to Setup


**1. Configure MySQL database**

Create a MySQL database named `file_demo`, and change the username and password in `src/main/resources/application.properties` as per your MySQL
installation -

```properties
spring.datasource.username= <YOUR MYSQL USERNAME>
spring.datasource.password= <YOUR MYSQL PASSWORD>
```

**2. Run the app using gradle locally**

```bash
cd image-processor
./gradlew bootRun
```

The application can be accessed at `http://localhost:8080`.

You may also package the application in the form of a jar and then run the jar file like so -

```bash
./gradlew clean build
java -jar build/libs/app.jar
```

**3. Build and Run on Docker**

The app can also be packaged to run on Docker. All you have to do is to build the docker image and run it.
```bash
docker build . -t com.yourname.home.apps.image-processor
docker run -p 8080:8080 com.yourname.home.apps.image-processor
```