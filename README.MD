# ASA Lab SonarQube

In this Lab we are working with [SonarQube](https://www.sonarsource.com/products/sonarqube/) and use it to analyze Software Projects. SonarQube is very widely used and provides software metrics derived from [static code analysis](https://owasp.org/www-community/controls/Static_Code_Analysis). Please note that there are other solutions available performing similar activities as well.

<hr/>

## 1. Requirements

We are going to work with a locally installed version of SonarQube. There are a couple of options to startup a local instance. Either download a specific released version from the SonarQube homepage or use a docker-compose file to startup an instance.

As we build projects locally and run unit-tests we need some SDKs, and other requirements to perform this task. 

**NOTE**: Not all requirements are needed for all projects. There is a specific information regarding the required dependencies (also if you have a look at the github pages of the projects you will quickly see which requirements are necessary).

### Primary requirement - a decent shell/terminal
To execute the commands defined for the different projects a shell/terminal is needed. In a Unix-like environment like Mac/Linux typically a good shell is available out of the box (bash, zsh) in combination with a terminal (terminal, iTerm, Konsole, gnome-terminal, ...). For **Windows** a good combination of shell/terminal is [PowerShell](https://github.com/PowerShell/PowerShell)/[Windows Terminal](https://learn.microsoft.com/en-us/windows/terminal/). The commands below either work directly in PowerShell or there is a variant for PowerShell displayed. If you use [cmd.exe](https://en.wikipedia.org/wiki/Cmd.exe), you are without help. Nobody shoul use this old command-interpreter any more!

### 1.1 Java

We need an installed java version for SonarQube and also for the software examples we are going to analyze. It is very much recommended to use a **LTS OpenJDK** version. Go to https://adoptium.net/ to download the current java LTS version **Open-JDK-21**.

### 1.2 Maven build tool

We need Maven for one of the projects we are analyzing. Specifically we need a recent version >= **3.8.1**. Go to the [Maven download](https://maven.apache.org/download.cgi) page and install the correct version.

### 1.3 .NET

The .NET version of Microsoft is available for a number of different platforms (Windows, Mac, Linux) and can be freely downloaded and installed. .NET also uses a LTS approach. The current LTS version is .NET 8.0. Head over to https://dotnet.microsoft.com/en-us/download and install the LTS version **.NET 8.0**.

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
# linux/mac with bash/zsh/...
mkdir -p ./sonardata/data && mkdir -p ./sonardata/extensions && mkdir -p ./sonardata/logs
```
```powershell
# windows with powershell (use: https://github.com/PowerShell/PowerShell)
New-Item ".\sonardata\data" -Type Directory ; New-Item ".\sonardata\extensions" -Type Directory ; New-Item ".\sonardata\logs" -Type Directory
```

The following commands will start the SonarQube container (the 'Community Edition' / LTS Version).

```bash
# linux/mac
docker run \
    -v $(PWD)/sonardata/data:/opt/sonarqube/data \
    -v $(PWD)/sonardata/extensions:/opt/sonarqube/extensions \
    -v $(PWD)/sonardata/logs:/opt/sonarqube/logs \
    --name="sonarqube" -p 9000:9000 sonarqube:lts-community
```

```powershell
# windows with powershell
docker run `
    -v "$pwd/sonardata/data:/opt/sonarqube/data" `
    -v "$pwd/sonardata/extensions:/opt/sonarqube/extensions" `
    -v "$pwd/sonardata/logs:/opt/sonarqube/logs" `
    --name="sonarqube" -p 9000:9000 sonarqube:lts-community
```

The same can be achieved by using a docker compose file like the following.

```yaml
# compose.yaml
version: "3"
services:
  sonarqube:
    image: sonarqube:lts-community
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
docker compose -f ./compose.yaml up

# output
[+] Running 1/1
 ⠿ Container sonarqube  Created                                                       0.1s
Attaching to sonarqube
...
sonarqube  | 2023.03.26 13:31:31 INFO  app[][o.s.a.SchedulerImpl] SonarQube is operational
```

**NOTE**: On first start the default admin-credentials need to be used `admin:admin`

### 1.5 Sonar Scanner

SonarQube provides a couple of convenient scanners for common technologies or integrations (Gradle, Maven, ant, .NET, AzureDevops, Jenkins). If the project to analyze does not fall within this range of "predefined integrations" the standalone [Sonar Scanner](https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner/) can be used.

Again there are two possibilities to run the Scanner - download the **zip-file**. Download the OS-specific version or the JVM only version and extract the Scanner into a local directory.

### 1.6 Git Fun
For some projects it is recommended to work with a specific commit id. The only reason for this is, that the projects worked/were tested with this commit. It is quite possible that other commits or [HEAD](https://www.geeksforgeeks.org/git-head/) also work!

To change the a specific commit the following commands are needed:

```bash
git clone <repository>
git reset --hard <COMMIT-SHA-ID> # the commit id can be the long or short form
```

### 1.7 Windows / Virus-Scanner - Performance Penalty of realtime scanning
A realtime virus scanning engine like [Windows Defender](https://www.microsoft.com/en-us/windows/comprehensive-security?r=1) sometimes gets in the way during development. As a result compilation (java, ...) can take ages. To speed up the process it can make sense to disable realtime-scanning during compilation or excluce paths in the scan engine (be aware that this has a security impact!)

- [Deactivate Real-Time Scanning](https://support.microsoft.com/en-us/windows/turn-off-defender-antivirus-protection-in-windows-security-99e6004f-c54c-8509-773c-a4d776b77960)
- [Exclude Folder for Scanning](https://support.microsoft.com/en-us/windows/add-an-exclusion-to-windows-security-811816c0-4dfd-af4a-47e4-c301afe13b26)

<hr/>

## 2. Analyze Projects

We are going to analyze Open-Source projects and interpret the scan results / metrics.

- **plantUML** - A DSL to generate wide variety of UML diagrams (Java)
- **eShop** - Reference .NET application implementing a Shop example (.NET)
- **Jenkins** - Well known build server (Java)

With this projects we see the different integration possibilities of SonarQube and the coverage of different technology.

### 2.1 plantUML

[plantUML](https://github.com/plantuml/plantuml) is a versatile DSL written in Java which can be used to generate a number of diagram types. It provides the capability to create UML diagrams but is not limited to UML only; Gantt, MindMaps, ER-Diagrams, ...

> Generate diagrams from textual description

**DEPENDENCIES**: This project needs: `#Open-JDK-21` 

To analyze the application the following steps need to be performed:

1. Ensure the local SonarQube instance is running - http://localhost:9000
2. Create a new SonarQube project in **manual mode**
3. Enter data DisplaName / Key = `plantuml`
4. Click Analyze project **Locally**
5. Create a token
6. Analyze with **Gradle** option
7. Clone the git repository to local folder https://github.com/plantuml/plantuml.git 
  - use **specific commit#** `a0be1ed` (@see **1.6 Git Fun**)
8. Change into the local folder of the cloned repository (e.g. ./plantuml)
9.  Set the Gradle version via the Gradle-Wrapper

```bash
# sonarqube gradle plugin does not work with the latest gradle version 8.x/9.x
# https://community.sonarsource.com/t/sonarqube-gradle-task-with-gradle-8-produces-a-crash-on-report-getoutputlocation/81252
./gradlew wrapper --gradle-version=7.6.4 --distribution-type=bin
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
+	toolVersion = "0.8.11"
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
# (Mac/Linux)
./gradlew test sonar -PjavacRelease=21 \                 
  -Dsonar.projectKey=plantuml \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<generated-sonar-key>
```

12.    Review the imported analyzes results within the SonarQube instance.

### 2.2 dotnet-architecture/eShopOnWeb

The [eShop](https://github.com/dotnet-architecture/eShopOnWeb) application demonstrates a best-practice approach for ASP.NET applications by implementing a sample shop/e-commerce solution.

> A reference .NET application implementing an eCommerce web site using a services-based architecture. 

**DEPENDENCIES**: This project needs: `#.NET 8.0`

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
10. clone the git repository to local folder https://github.com/dotnet-architecture/eShopOnWeb (commit# `f34c5a7` - @see **1.6 Git Fun**)
11. change into the local folder of the cloned repository (e.g. ./eShopOnWeb)
12. Start sonar-scanner and build

```bash
dotnet sonarscanner begin \
  /k:"eShopOnWeb" \
  /d:sonar.host.url="http://localhost:9000" \
  /d:sonar.login="<generated-sonar-key>" \
  /d:sonar.cs.vscoveragexml.reportsPaths=coverage.xml

dotnet build Everything.sln --no-incremental
dotnet test --collect "Code Coverage;Format=cobertura" Everything.sln
dotnet-coverage merge ./**/*.cobertura.xml -f xml -o "coverage.xml"

dotnet sonarscanner end /d:sonar.login="<generated-sonar-key>"
```

13. Review the imported analyzes results within the SonarQube instance.


### 2.3 Jenkins CI Server

[Jenkins](https://github.com/jenkinsci/jenkins) is a well known build-server which is very widely used. It is Java based, Open-Source and can be freely used to setup your own build-environment.

> In a nutshell, Jenkins is the leading open-source automation server. Built with Java, it provides over 1,800 plugins to support automating virtually anything, so that humans can spend their time doing things machines cannot.

**DEPENDENCIES**: This project needs: `#Open-JDK-21`, `#Maven`

To analyze the source of Jenkins additional parameters are needed, apart from the description given during the SonarQube setup. No additional tools are needed, except for a recent (>= 3.8.1) Maven version.

1. Ensure the local SonarQube instance is running - http://localhost:9000
2. Create a new SonarQube project in **manual mode**
3. Enter data DisplaName / Key = `jenkins`
4. Click Analyze project **Locally**
5. Create a token
6. Analyze with **Maven** option
7. clone the git repository to local folder https://github.com/jenkinsci/jenkins.git (commit# `323ad6a4a1` - @see **1.6 Git Fun**)
8. change into the local folder of the cloned repository (e.g. ./jenkins)
9. Start maven with goal sonar

```bash 
# (Mac/Linux)
mvn clean verify sonar:sonar \
  -Dmaven.test.failure.ignore=true \
  -Plight-test \
  -Penable-jacoco \
  -Dsonar.coverage.jacoco.xmlReportPaths=${PWD}/coverage/target/site/jacoco-aggregate/jacoco.xml \
  -Dsonar.projectKey=jenkins \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<generated-sonar-key>
```

For windows the current path in powershell is used in a different way:

```powershell 
# (Windows)
mvn clean verify sonar:sonar \
  -Dmaven.test.failure.ignore=true \
  -Plight-test \
  -Penable-jacoco \
  -Dsonar.coverage.jacoco.xmlReportPaths="$pwd/coverage/target/site/jacoco-aggregate/jacoco.xml" \
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



11.  Review the imported analyzes results within the SonarQube instance.

<hr/>

After all projects are imported the SonarQube results will look similar to this:

![SonarQube Projects](./.attachments/sonarqube-projects.png)
