# ASA Lab SonarQube

In this Lab we are working with [SonarQube](https://www.sonarsource.com/products/sonarqube/) and use it to analyze Software Projects. SonarQube is very widely used and provides software metrics derived from [static code analysis](https://owasp.org/www-community/controls/Static_Code_Analysis). Please note that there are other solutions available performing similar activities as well.

## 1. Requirements

We are going to work with a locally installed version of SonarQube. There are a couple of options to startup a local instance. Either download a specific released version from the SonarQube homepage or use a docker-compose file to startup an instance.

### 1.1 Java

We need an installed java version for SonarQube and also for the software examples we are going to analyze. It is very much recommended to use a LTS OpenJDK version. Go to https://adoptium.net/en-GB/ to download the current java LTS version.

### 1.2 SonarQube standalone instance

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

### 1.3 SonarQube as a container

If your OS is capable of running containers, SonarQube does not need to be downloaded, starting the SonarQube container image is sufficient to run a local instance.

SonarQube has documentation of running a container version: https://github.com/SonarSource/docker-sonarqube/blob/master/examples.md

First create a local folder where SonarQube will store data, logs and its configuration. 

```bash
# linux/mac
mkdir -p ./sonardata/data && mkdir -p ./sonardata/extensions && mkdir -p ./sonardata/logs

# windows

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

### 1.4 Sonar Scanner

SonarQube provides a couple of convenient scanners for common technologies or integrations (Gradle, Maven, ant, .NET, AzureDevops, Jenkins). If the project to analyze does not fall within this range of "predefined integrations" the standalone [Sonar Scanner](https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner/) can be used.

Again there are two possibilities to run the Scanner - download the **zip-file**. Download the OS-specific version or the JVM only version and extract the Scanner into a local directory.

### 1.5 Ant build tool

One of the projects we are going to analyze uses the [ant build system](https://ant.apache.org/). Follow the instructions to install ant - https://ant.apache.org/bindownload.cgi or use the convenience tools of your OS:

```bash
# mac
brew install ant
```

## 2. Analyze Projects

We are going to analyze Open-Source projects and interpret the scan results / metrics.

### 2.1 draw.io

Draw.io is a very useful diagram application which can be described as "Microsoft Visio in the Web-Browser". The github page explains draw.io

> draw.io is a JavaScript, client-side editor for general diagramming and whiteboarding

To analyze the application the following steps need to be performed:

1. Ensure the local SonarQube instance is running - http://localhost:9000
2. Create a new SonarQube project in **manual mode**
3. Enter data DisplaName / Key
4. Analyze project **Locally**
5. Create a token
6. Analyze with **other build** option
7. Select your OS
8. clone the git repository to local folder https://github.com/jgraph/drawio
9. change into the local folder
10. change to build folder `./etc/build`
11. build the project with `ant` and `ant javac`
12. run the `sonar-scanner`

```bash
sonar-scanner -Dsonar.projectKey=draw.io \
    -Dsonar.sources=./src \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.login=<generated-sonar-key> \
    -Dsonar.java.binaries=./src/main/webapp/WEB-INF/classes

```

13.  Review the imported analyzes results within the SonarQube instance.

