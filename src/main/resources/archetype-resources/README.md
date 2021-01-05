# ${artifactId}-${version}

*This document is under construction.*

## Contents
* [Technology stack](#technology-stack)
* [Pre-requisites check](#pre-requisites-check)
* [Installation](#installation)
* [Set AWS service credentials](#set-aws-service-credentials)
* [Deploy and remove cloud stack, run locally](#deploy-and-remove-cloud-stack-run-locally)
* [Running tests](#running-tests)

## Technology stack
* [Java JDK 1.8](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) or [11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) - Required Java version. **1.8** will work, but **11** is preferred. 
* [Maven 3](https://maven.apache.org/) - Dependency and build management.
* [Lombok Plugin](https://projectlombok.org/) - Make sure your IDE supports **Lombok**.
* [IntelliJ IDEA](https://www.jetbrains.com/) - Or any IDE of your choice.
* [Git](https://git-scm.com/) - Versioning system.
* [NodeJS 12+](https://nodejs.org/en/download/) - Pre-requisite for the *Serverless Framework* and deployment scripts.
* [Serverless Framework](https://www.serverless.com/framework/docs/getting-started/) - Infra-structure as code.
* [Docker](https://www.docker.com/get-started) - Used to build native API version.

## Pre-requisites check
The following procedures will ensure all pre-requisite technologies are installed and functional
on your system. Run these commands from your system's terminal. Make sure they return results similar
to the sample outputs and that they don't denote the absence of the dependency. If any isn't present 
or properly configured, please refer to the vendor's instructions or use the provided links. Also 
make sure you own an AWS account with development permissions.

#### [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
```
git --version
```
> git version 2.17.1

#### [Java](https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html)
```
java -version
```
> java version "1.8.0_251"\
> Java(TM) SE Runtime Environment (build 1.8.0_251-b08)\
> Java HotSpot(TM) 64-Bit Server VM (build 25.251-b08, mixed mode)

#### [Maven](https://mkyong.com/maven/how-to-install-maven-in-windows/)
```
mvn -v
```
> Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)\
> Maven home: /Applications/apache-maven-3.6.3

#### [NodeJS](https://nodejs.org/en/download/)
```
node -v
```
> v12.13.0

#### [Node Package Manager (NPM)](https://nodejs.org/en/download/)
```
npm -v
```
> 6.12.0

#### [Serverless Framework](https://www.serverless.com/framework/docs/providers/aws/guide/installation/)
```
serverless -v
```
> Framework Core: 1.77.1\
> Plugin: 3.6.18\
> SDK: 2.3.1\
> Components: 2.33.0

#### [Docker](https://docs.docker.com/get-docker/)
```
docker -v
```
> Docker version 19.03.5, build 633a0ea

## Installation
Make sure you install and confirm the installation of the pre-required technologies if not already
done *(Java JDK, Maven, IDE's Lombok Plugin, NodeJS 12+, Git)*. Details on how this should be done
will not be covered on this doc. Execute from the command terminal:

1. Make a local copy of this repository.
    ```
    git clone <REPOSITORY_PATH>.git
    ```
2. From the project root, install the Node dependencies.
    ```
    npm install
    ```
3. Open the project in your preferred IDE and use your **pom.xml** to update the project's dependencies.


## Set AWS service credentials
To register or update your AWS credentials on the service's profile, as follows:

1. Obtain or generate the AWS IAM credentials to be used on this service and retrieve 
   it's **ACCESS KEY ID** and **SECRET ACCESS KEY**. If you're unsure on how to create an IAM user,
   [follow the AWS documentation here](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html).
   You can also use your local credentials. You can check them by running from the terminal:
   ```
   # ON MAC & LINUX
   nano ~/.aws/credentials
   
   # ON WINDOWS
   notepad C:\Users\<YOUR_WINDOWS_USER_NAME>\.aws\credentials
    ```

2. Generate a local profile with the acquired credentials by running the 
   command below from the project's root folder. *Pay close attention to the syntax with double 
   dashes and spacing*. Replace **<ACCESS_KEY_ID>** and **<SECRET_ACCESS_KEY>** by your aqcuired 
   credentials.

    ```
    npm run set-service-credentials -- --key <ACCESS_KEY_ID> --secret <SECRET_ACCESS_KEY>
    ```

## Deploy and remove cloud stack, run locally
For simplicity and cross-platform compatibility, the command sequences are encapsulated on 
*package.json*'s scripts section. You can refer to it for further details. As a suggested 
workflow, proceed as follows:
* Deploy the project once to update the infrastructure.
* Work/Debug locally.
* When ready, deploy the project again, test remotely.

The following commands should be run from the projects root folder:

### Deploy stack to AWS
```
npm run deploy
```

### Run the API locally
```
npm run start-local
```
> **Note:** The stack must be deployed <ins>at least once whenever new environment variables 
> or AWS resources are introduced before running locally</ins>. The script will parse the environment 
> variables located in the *serverless.yml* definition and provide them at runtime.

### Remove stack
```
npm run remove
```

## Running tests
The command sequences are encapsulated on *package.json*'s scripts section. You can refer to it for 
further details. The following commands should be run from the projects root folder:

### Run all tests except integration
```
npm run test
```

### Run integration tests, pointing to local API
```
npm run test-integration-local
```
> **Note:** Make sure the local API is online by executing **npm run start-local** before running
> the integration tests.

### Run integration tests, pointing to remote AWS API
```
npm run test-integration-cloud
```
> **Note:** Make sure the AWS API has been deployed before running the integration tests.