# ASA Lab SonarQube

In this Lab we are working with [SonarQube](https://www.sonarsource.com/products/sonarqube/) and use it to analyze Software Projects. SonarQube is very widely used and provides software metrics derived from [static code analysis](https://owasp.org/www-community/controls/Static_Code_Analysis). Please note that there are other solutions available performing similar activities as well.

<hr/>

## 1. Requirements

We are going to work with a locally installed version of SonarQube. There are a couple of options to startup a local instance. Either download a specific released version from the SonarQube homepage or use a docker-compose file to startup an instance.

### 1.1 Java

We need an installed java version for SonarQube and also for the software examples we are going to analyze. It is very much recommended to use a **LTS OpenJDK** version. Go to https://adoptium.net/ to download the current java LTS version **Open-JDK-17**.

### 1.2 Maven build tool

We need Maven for one of the projects we are analyzing. Specifically we need a recent version >= **3.8.1**. Go to the [Maven download](https://maven.apache.org/download.cgi) page and install the correct version.

### 1.3 .NET

The .NET version of Microsoft is available for a number of different platforms (Windows, Mac, Linux) and can be freely downloaded and installed. .NET also uses a LTS approach. The current LTS version is .NET 6.0. Our .NET example uses the current version which is .NET 7.0. Head over to https://dotnet.microsoft.com/en-us/download and install the current version **.NET 7.0**.

### 1.4 SonarQube Installation

#### 1.4.1 SonarQube standalone instance

We will use the 'Community Edition' because it can be used freely without the need for any payment. Download this version from the page https://www.sonarsource.com/products/sonarqube/downloads/.

Unpack to downloaded archive to a working directory. Change into directory `./sonarqube-9.9.xx/bin` and use the folder for your specific OS (linux, mac, win). 
Within the OS-specific folder start SonarQube in `console mode`.

```bash
# linux/mac
./sonar.sh console

# output
/usr/bin/java
Running SonarQube...
Removed stale pid file: ./SonarQube.pid
...
2023.03.26 14:58:43 INFO  app[][o.s.a.SchedulerImpl] Process[ce] is up
2023.03.26 14:58:43 INFO  app[][o.s.a.SchedulerImpl] SonarQube is operational
```

#### 1.4.2 SonarQube as a container

If your OS is capable of running containers, SonarQube does not need to be downloaded, starting the SonarQube container image is sufficient to run a local instance.

SonarQube has documentation of running a container version: https://github.com/SonarSource/docker-sonarqube/blob/master/examples.md

First create a local folder where SonarQube will store data, logs and its configuration. 

```bash
# linux/mac
mkdir -p ./sonardata/data && mkdir -p ./sonardata/extensions && mkdir -p ./sonardata/logs

# windows
# use Explorer.exe ;)
```

The following commands will start the SonarQube container (the 'Community Edition' version 9.9).

```bash
# linux/mac
docker run \
    -v $(PWD)/sonardata/data:/opt/sonarqube/data \
    -v $(PWD)/sonardata/extensions:/opt/sonarqube/extensions \
    -v $(PWD)/sonardata/logs:/opt/sonarqube/logs \
    --name="sonarqube" -p 9000:9000 sonarqube:9.9-community
```

The same can be achieved by using a docker compose file like the following.

```yaml
# compose.yaml
version: "3"
services:
  sonarqube:
    image: sonarqube:9.9-community
    container_name: sonarqube
    volumes:
      - ./sonardata/data:/opt/sonarqube/data
      - ./sonardata/extensions:/opt/sonarqube/extensions
      - ./sonardata/logs:/opt/sonarqube/logs
    ports:
      - "9000:9000"
```

To run the compose file execute the following command:

```bash
docker-compose -f compose.yaml up

# output
[+] Running 1/1
 ⠿ Container sonarqube  Created                                                       0.1s
Attaching to sonarqube
...
sonarqube  | 2023.03.26 13:31:31 INFO  app[][o.s.a.SchedulerImpl] SonarQube is operational
```

### 1.5 Sonar Scanner

SonarQube provides a couple of convenient scanners for common technologies or integrations (Gradle, Maven, ant, .NET, AzureDevops, Jenkins). If the project to analyze does not fall within this range of "predefined integrations" the standalone [Sonar Scanner](https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner/) can be used.

Again there are two possibilities to run the Scanner - download the **zip-file**. Download the OS-specific version or the JVM only version and extract the Scanner into a local directory.

<hr/>

## 2. Analyze Projects

We are going to analyze Open-Source projects and interpret the scan results / metrics.

- **plantUML** - A DSL to generate wide variety of UML diagrams (Java)
- **eShopOnWeb** - Reference .NET application implementing a Shop example (.NET)
- **Jenkins** - Well known build server (Java and Javascript)
- **VSCode** - The extendable Open-Source Editor (Javascript/Typescript)

With this projects we see the different integration possibilities of SonarQube and the coverage of different technology.

### 2.1 plantUML

[plantUML](https://github.com/plantuml/plantuml) is a versatile DSL written in Java which can be used to generate a number of diagram types. It provides the capability to create UML diagrams but is not limited to UML only; Gantt, MindMaps, ER-Diagrams, ...

> Generate diagrams from textual description

To analyze the application the following steps need to be performed:

1. Ensure the local SonarQube instance is running - http://localhost:9000
2. Create a new SonarQube project in **manual mode**
3. Enter data DisplaName / Key = `plantuml`
4. Click Analyze project **Locally**
5. Create a token
6. Analyze with **Gradle** option
7. Clone the git repository to local folder https://github.com/plantuml/plantuml.git (commit# `b9a27f044`)
8. Change into the local folder of the cloned repository (e.g. ./plantuml)
9. Set the Gradle version via the Gradle-Wrapper

```bash
# sonarqube gradle plugin does not work with the latest gradle version 8.x
# https://community.sonarsource.com/t/sonarqube-gradle-task-with-gradle-8-produces-a-crash-on-report-getoutputlocation/81252
./gradlew wrapper --gradle-version=7.6.1 --distribution-type=bin
``` 

10. Change the file `build.gradle.kts` file in the cloned git repo in `./plantuml` and enable SonarQube integration

```diff
--- build.gradle.kts	2023-04-04 18:39:01.512208110 +0200
+++ sonar/build.gradle.kts	2023-04-04 18:38:56.052125037 +0200
@@ -10,8 +10,28 @@
 	java
 	`maven-publish`
 	signing
+	id("org.sonarqube") version "3.5.0.2730"
+	id("jacoco")
 }
 
+jacoco {
+	toolVersion = "0.8.8"
+}
+
+tasks.jacocoTestReport {
+	reports {
+    		xml.required.set(true)
+    		csv.required.set(false)
+    		html.required.set(false)
+	}
+}
+
+// when subproject has Jacoco pLugin applied we want to generate XML report for coverage
+plugins.withType<JacocoPlugin> {
+	tasks["test"].finalizedBy("jacocoTestReport")
+}
``` 
11. Start the gradle build

```bash
./gradlew test sonar -PjavacRelease=17  \                 
  -Dsonar.projectKey=plantuml \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<generated-sonar-key>
```

12.   Review the imported analyzes results within the SonarQube instance.

### 2.2 dotnet-architecture/eShopOnWeb

The [eShopOnWeb](https://github.com/dotnet-architecture/eShopOnWeb) application demonstrates a best-practice approach for ASP.NET applications by implementing a sample shop/e-commerce solution.

> Sample ASP.NET Core reference application, powered by Microsoft, demonstrating a single-process (monolithic) application architecture and deployment model. 

The steps to analyze the application are very similar to the example of plantUML. Just the last steps differ, because there is a dotnet integration option.

1. Ensure the local SonarQube instance is running - http://localhost:9000
2. Create a new SonarQube project in **manual mode**
3. Enter data DisplaName / Key = `eShopOnWeb`
4. Click Analyze project **Locally**
5. Create a token
6. Analyze with **.NET** option
7. Choose build tool **.NET core**
8. Install the `sonar-scanner` as a dotnet-tool `dotnet tool install --global dotnet-sonarscanner`
9. Install the `dotnet-coverage` tool - `dotnet tool install --global dotnet-coverage`
10. clone the git repository to local folder https://github.com/dotnet-architecture/eShopOnWeb.git (commit# `8d9a917`)
11. change into the local folder of the cloned repository (e.g. ./eShopOnWeb)
12. Start sonar-scanner and build

```bash
dotnet sonarscanner begin \
  /k:"eShopOnWeb" \
  /d:sonar.host.url="http://localhost:9000" \
  /d:sonar.login="<generated-sonar-key>" \
  /d:sonar.cs.vscoveragexml.reportsPaths=coverage.xml

dotnet build Everything.sln --no-incremental
dotnet-coverage collect "dotnet test Everything.sln" -f xml -o "coverage.xml"

dotnet sonarscanner end /d:sonar.login="<generated-sonar-key>"
```

13. Review the imported analyzes results within the SonarQube instance.


### 2.3 Jenkins CI Server

[Jenkins](https://github.com/jenkinsci/jenkins) is a well known build-server which is very widely used. It is Java based, Open-Source and can be freely used to setup your own build-environment.

> In a nutshell, Jenkins is the leading open-source automation server. Built with Java, it provides over 1,800 plugins to support automating virtually anything, so that humans can spend their time doing things machines cannot.

To analyze the source of Jenkins additional parameters are needed, apart from the description given during the SonarQube setup. No additional tools are needed, except for a recent (>= 3.8.1) Maven version.

1. Ensure the local SonarQube instance is running - http://localhost:9000
2. Create a new SonarQube project in **manual mode**
3. Enter data DisplaName / Key = `jenkins`
4. Click Analyze project **Locally**
5. Create a token
6. Analyze with **Maven** option
7. clone the git repository to local folder https://github.com/jenkinsci/jenkins.git (commit# `319dfc6134`)
8. change into the local folder of the cloned repository (e.g. ./jenkins)
9. Start maven with goal sonar

```bash
mvn clean verify sonar:sonar \
  -Dmaven.test.failure.ignore=true \
  -Plight-test \
  -Penable-jacoco \
  -Dsonar.coverage.jacoco.xmlReportPaths=${PWD}/coverage/target/site/jacoco-aggregate/jacoco.xml \
  -Dsonar.projectKey=jenkins \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<generated-sonar-key>
```

10. Review the imported analyzes results within the SonarQube instance.

**NOTE**: A short explanation of the additional parametersl

- **-Dmaven.test.failure.ignore** -> If the full tests are executed, continue even if one test fails
- **-Plight-test** -> Do not execute ALL tests to be faster. A full test-run takes more than a [hour!]( https://github.com/jenkinsci/jenkins/blob/master/CONTRIBUTING.md#testing-changes)
- **-Penable-jacoco** -> Collect coverage information with [Jacoco](https://www.jacoco.org/jacoco/)
- **-Dsonar.coverage.jacoco.xmlReportPaths** -> The path of the combined Jacoco coverage data.
- -Dsonar.projectKey, -Dsonar.host.url, -Dsonar.login --> Std parameters for SonarQube.

### 2.4 VSCode

[VSCode](https://github.com/microsoft/vscode) is a very successful Open-Source Editor published by Microsoft. It is Javascript/Typescript based using the [Electron](https://github.com/electron/electron) runtime and can be used on Windows/Mac/Linux. 

> Code editing. Redefined. Free. Built on open source. Runs everywhere.

VSCode can also be analyzed by SonarQube. To enable this, it is necessary to check the build-requirements defined on the VSCode GitHub page: https://github.com/microsoft/vscode/wiki/How-to-Contribute

- Git
- Node.JS
- Yarn 1
- Python
- C/C++ compiler

If you follow the instructions, a local build is possible. VSCode provides tests which generate coverage information. Those can be imported into SonarQube as well. There is a separate description for testing of VSCode: https://github.com/microsoft/vscode/blob/main/test/unit/README.md

The steps to analyze the project and import into SonarQube are as follows:

1. Ensure the local SonarQube instance is running - http://localhost:9000
2. Create a new SonarQube project in **manual mode**
3. Enter data DisplaName / Key = `vscode`
4. Click Analyze project **Locally**
5. Create a token
6. Analyze with **Other** option
7. Selecte your **OS** and work with `sonar-scanner`
8. Clone the git repository to local folder https://github.com/microsoft/vscode.git (commit# `59ec5734f1e`)
9. Change into the local folder of the cloned repository (e.g. ./vscode)
10. Execute yarn to generate a build --> `yarn`
11. Start the local build of VSCode once `./scripts/code.sh` --> the folder `out` will be created
12. Start tests with coverage `./scripts/test.sh --coverage` --> coverage-info will be created in `.build/coverage`
13. Change the .gitignore file and remove the `out` folder, needed because coverage uses the path `out` and sonar-scanner would skip the folder for analysis based on .gitignore.

```diff
--- .gitignore	2023-04-05 12:51:40.913287644 +0200
+++ .gitignore.sonar	2023-04-05 18:44:57.878453679 +0200
@@ -5,7 +5,6 @@
 node_modules/
 .build/
 extensions/**/dist/
-/out*/
 /extensions/**/out/
 build/node_modules
 coverage/
```

14. Use sonar-scanner to analyze the vscode source

```bash
sonar-scanner \
  -Dsonar.projectKey=vscode \
  -Dsonar.sources=${PWD}/out \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.javascript.lcov.reportPaths=${PWD}/.build/coverage/lcov.info \
  -Dsonar.login=<generated-sonar-key> 
```

15. Review the imported analyzes results within the SonarQube instance.

<hr/>

After all projects are imported the SonarQube results will look similar to this:

![SonarQube Projects](./.attachments/sonarqube-projects.png)