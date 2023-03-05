# Software-Design Lab
Welcome to the **Software-Design Lab** where we try out and practice the way how software projects are started. The basic idea is to work through **requirements**, get and understanding of **use-cases**, design a **component-structure**, examine the temporal behavior of the application with **sequence diagrams** and use [Spring Boot](https://spring.io/projects/spring-boot) to implement the project in [Java](https://adoptium.net/).

## Spring Boot
The typical way to start a new Spring project is to go to [https://start.spring.io/](https://start.spring.io/) and select which components to include. This is completely valid and will lead to the overall same projects structure. This project here adds some components and sample-implementations to work as an "enhanced" starting-point.

<hr/>

## Development Requirements
To work with the source the following software components need to be installed on your local machine.

**NOTE**: A nice thing about Java is its platform-independence. So you can work with Windows/Linux/Mac ... all supported.

* Java: Open-JDK ([Adoptium](https://adoptium.net/), ...) - Info about the [Java version history](https://en.wikipedia.org/wiki/Java_version_history)
  * jdk-8 (LTS[-2]) | jdk-11 (LTS[-1]) | **jdk-17** (LTS[0]) --> we are using the latest LTS ```jdk-17```
* IDE: [IntelliJ](https://www.jetbrains.com/idea/), [VSCode+Java](https://code.visualstudio.com/docs/languages/java), [Eclipse](https://www.eclipse.org/ide/)
* Git: https://git-scm.com/ (command-line) or you use some Git-GUI (https://git-scm.com/downloads/guis)


**NOTE Jetbrains**: You can work with the free Community Edition with is perfectly OK for Java development. Some advanced features are only available if you use the commercial variant (Jetbrains Ultimate). You can apply for a educational license if you register with your FH Email address (ac.at).

## Template project structure
The example project has a number of components included and configured to be ready.

   * Spring Boot Starter Data --> Persistence to databases via [JPA](https://www.oracle.com/technical-resources/articles/java/jpa.html)
   * Spring Boot Starter Web, Tomcat --> HTTP Server
   * Spring Boot Starter Thymeleaf --> Templates used for HTML views
   * Bootstrap --> CSS toolkit to simplify HTML UI (https://getbootstrap.com/)
   * H2 database --> [embedded database](https://en.wikipedia.org/wiki/Embedded_database) in pure Java

## IDE
Typically you can just import/open the project in IntelliJ, VSCode, ... The IDEs provide code-completion and other benefits so it is strongly recommended to use a IDE (you can develop in VIM, EMACS, Notepad as well - but is this really necessary 🧐)

### Debugging with IntelliJ
When using the Community Version it can happen, that a breakpoint will not hit. Someone found a solution for this:

```spring-boot:run -Dspring-boot.run.fork=false```

source: https://dev.to/davey/debugging-springboot-application-in-intellij-idea-ce-22j9

<hr/>

## Database Persistence
The application uses an embedded database [H2](https://h2database.com/html/main.html). It is very similar to [SQLite](https://www.sqlite.org/index.html) (=> the standard for embedded databases used in millions of installations Firefox, Chrome, Android, iOS, ...).

Spring Boot offers excellent support to interact with databases with JPA. The template project has basic persistence enabled. For Spring Boot applications the configuration is done in [```application.properties```](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html).

```properties
# create/upadte schema with the given datasource
# https://stackoverflow.com/questions/42135114/how-does-spring-jpa-hibernate-ddl-auto-property-exactly-work-in-spring
# - create: drops tables before create - data is lost
# - update: adds tables, fields - does not destroy data
spring.jpa.hibernate.ddl-auto=create
# define the dialect ot use for the specific datasource implementation
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# H2 Database
spring.h2.console.enabled=true
## a named/custom datasource was defined
## this need to be introduced to spring by implementing a configuration with @EnableJpaRepositories
## @see myappDataSourceConfiguration
myapp.datasouce.driverClassName=org.h2.Driver
myapp.datasource.username=sa
myapp.datasource.password=
# specify a file for the embedded database - so that data is retained
#myapp.datasource.jdbcurl=jdbc:h2:file:/path/where/you/want/your/data/myappdb.db;
# in-memory database bound to the lifetime of your application. on restart everything is gone/new
myapp.datasource.url=jdbc:h2:mem:myappdb;
```

If you want your data and database to survive a server restart you need to change the ```datasource``` to use a file instead of in-memory use (also change the ddl-auto setting, otherwise you will find the following log-output).

```
Hibernate: drop table if exists customer CASCADE
Hibernate: drop table if exists orders CASCADE
Hibernate: drop table if exists orders_products CASCADE
Hibernate: drop table if exists product CASCADE
```

The changed setting would look like this:

```properties
# - create: drops tables before create - data is lost
# - update: adds tables, fields - does not destroy data
spring.jpa.hibernate.ddl-auto=update
...
# specify a file for the embedded database - so that data is retained
myapp.datasource.jdbcurl=jdbc:h2:file:/path/where/you/want/your/data/myappdb.db;
# in-memory database bound to the lifetime of your application. on restart everything is gone/new
#myapp.datasource.url=jdbc:h2:mem:myappdb;
```

### Initialization Code
There is a code-part of the Spring Boot application which is always run, when the server starts. This is ued as a sample to have a logic which initializes the system on start. Useful for testing, but not a really good idea in production!
The logic is located in

```java
at.ac.fhsalzburg.swd.spring.startup.CommandLineAppStartupRunner:run
```

If you do not want the standard-initialization remove/comment the code in the run method.

### H2 Console
The H2 database provides a web-based console to interact with the database. This is activated in the template project (again in the ```application.properties```)

**NOTE**: A very, very, very bad idea to have this enabled in production, because you do not want to give users access to your db. But honestly H2 in production might not be a good idea in the first place!

```properties
# H2 Database
spring.h2.console.enabled=true
```

Once the server is started you can access the H2 console via a web browser http://localhost:8080/h2-console

The login uses the same settings as you have provided in the configuration. Just use the same JDBC setting you have in the ```application.properties```; e.g. use

* ```jdbc:h2:mem:myappdb``` for the in-memory DB
* ```jdbc:h2:file:/path/where/you/want/your/data/myappdb.db```

**HINT**: For Windows Users - do not use backslash in the path e.g. ```C:\Documents\Path\database.db```. Either escape the backslash with another backslash (looks stupid) or use slash ```/``` as the path-deliminator as well
```C:/Documents/Path/database.db```

<hr/>

## Build, Run and Deploy
The template uses [maven](https://maven.apache.org/) as the project tool/layout. The [maven-wrapper](https://maven.apache.org/wrapper/) is included so no need to install maven standalone.
Maven provides a number of commands needed to build, test, run the application.

```bash
# build the application --> dependencies are automatically downloaded by maven
./mvnw compile

# execute unit-tests
./mvnw test

# clean the output folder './target'
./mvnw clean

# run the spring-boot application
./mvnw spring-boot:run

# deploy the application - create a fat-jar in the ./target folder
# the file 'spring-0.0.1-SNAPSHOT.jar' is created in ./target
# it contains ALL dependencies and can be copied somewhere else to be run
./mvnw package
```

Once the application is run it can be accessed via a browser by visiting the URL http://localhost:8080/

### REST API
If someone wants to play the with REST API this is possible by using command line tools like [curl](https://curl.se/), [httpie](https://httpie.io) or API tools like [postman](https://www.postman.com/).

```bash
curl -v -X GET http://localhost:8080/api/customers
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /api/customers HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sun, 02 Oct 2022 10:48:26 GMT
<
* Connection #0 to host localhost left intact
[{"id":1,"firstName":"Max","lastName":"Mustermann","tel":"123","credit":100,"birthDate":"2022-10-02T10:47:56.026+00:00","email":"max@muster.man"}]
```
