# Serverless Quarkus/Spring AWS API Archetype

Maven archetype to generate a serverless AWS API. It will provide a Quarkus/Spring application
running from a single Lambda function, with its endpoints exposed through the API Gateway.
Native and JVM compilation, Cognito and Parameter Store (SSM) integration, fully automated deploy 
process and integration tests are provided out of the box. No local GraalVM installation required.

## Contents
* [Introduction](#introduction)
* [Quickstart](#quickstart)
* [Technology stack](#technology-stack)
* [Pre-requisites check](#pre-requisites-check)
* [Installation](#installation)
* [Usage](#usage)

## Introduction

The single most troublesome characteristic of cloud functions is the **cold start**, which adds uncomfortable
amounts of delay to the completion of a function's execution. After being dormant for longer periods
of time (usually around 5 minutes, depending on different factors) the function container shuts down
and needs to be restarted. This feature manifests dramatically when writing cloud functions in
*compiled languages*, such as Java or C#, making a single cold start worth over 10 or even 15 seconds
of delay, rendering such solutions **impractical for latency sensitive applications**, such as APIs
serving end users via mobile or web clients.

Another cumbersome aspect of cloud function programming, is the fact that in the standard approach of
writing APIs with them, each endpoint's verb ends up getting assigned to a single function, making
such architecture **highly coupled to the infrastucture** and usually **less than standardized**.

The architectural attempt presented aims to approach these two topics through:
* **A single cloud function must serve the entire API**. This approach allows the usage of tried
  and tested frameworks, in this case *Spring*, in the form of Quarkus Spring Extensions, permiting
  as such a standardized development pattern. This general concept would allow the API to be easily
  migrated to any other Spring compatible infra-structure, such as ECS or Elastic Beanstalk.


* **Usage of lightning fast application initialization solution.** Java/Spring applications were not
  designed to have a fast startup procedure. Their original idea is to be instantiated once and
  respond to requests while staying up. The [Quarkus](https://quarkus.io/) framework
  supplies an incredibly fast boot time (usually under 0.5s) with a Spring "Façade" for a fraction
  of the usual computational resources. It provides the startup agility one would need to mitigate the
  cold start issue and run a *smoothly auto-scaling* Java application on a cloud function.


The following instructions should allow the developer to build, execute and deploy projects generated
from this archetype locally and on a provided AWS Account.

## Quickstart
Before using the archetype, make sure you double check all pre-requisite technologies are installed
and functional. The archetype should also have been downloaded and installed locally as explained
in the **Installation** section of this document. You will also need your AWS credentials.
```
mvn archetype:generate \
  -DarchetypeGroupId=com.soundlab \
  -DarchetypeArtifactId=serverless-quarkus-spring-aws-archetype \
  -DgroupId=<YOUR_ORGANIZATION_GROUP_ID> \
  -DartifactId=<PROJECT_NAME> \
  -DawsStackName=<AWS_STACK_NAME_FOR_THIS_PROJECT> \
  -DawsAccessKeyId=<AWS_ACCESS_KEY_ID_FOR_THIS_PROJECT> \
  -DawsSecretAccessKey=<AWS_SECRET_ACCESS_KEY_FOR_THIS_PROJECT>
```
The archetype will perform the following actions:
1. Generate a Maven project will all required dependencies and build procedures.
2. Generate a Quarkus/Spring application boilerplate code with a default *ping* endpoint. By default,
   it will provide dependencies and code to integrate the API to AWS Parameter Store (SSM) via AWS SDK 2.0.
3. Generate an AWS credentials profile to be used on the service.
4. Provide pre-configured *Serverless Framework* definition for JVM and native versions of the API.
5. Provide NPM scripts to build, deploy, test and run the JVM and native versions remotely and locally.
6. Provide integration tests based on RestAssured to test the endpoints remotely and locally.
7. Install necessary NPM dependencies.
8. Initialize a local GIT repository and generate an initial commit.
9. Optionally secure the API with Cognito (by adding *-Dsecure=true* to the above command).
10. Optionally deploy the API to AWS upon project creation (by adding *-Ddeploy=true* to the above command).
11. Generate README.md with usage instructions.

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
to the sample outputs and that they don't denote the absence of the dependency If any isn't present 
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
Make sure you install and/or confirm the installation of the pre-required technologies if not already
done *(Java JDK, Maven, IDE's Lombok Plugin, NodeJS 12+, Git, Docker, Serverless Framework)*. 
Execute from the command terminal:

1. Create a general maven archetypes folder on your system to store this archetype.
   ```
   mkdir maven-archetypes
   ```

2. Navigate to your archetypes folder and make a local copy of this repository.
   ```
   cd maven-archetypes
   git clone https://github.com/RogerVFbr/serverless-quarkus-spring-aws-archetype.git
   ```

2. Navigate to the project root and run the following command to build the archetype and provide it 
   locally.
   ```
   cd serverless-quarkus-spring-aws-archetype
   mvn clean install
   ```

## Usage
1. Obtain or generate the AWS IAM credentials with proper policies to be used on this service and retrieve
   it's **ACCESS KEY ID** and **SECRET ACCESS KEY**. If you're unsure on how to create/configure an IAM user,
   [follow the AWS documentation here](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html).
   You can also use your local credentials. You can check them by running from the terminal:
   ```
   # ON MAC & LINUX
   nano ~/.aws/credentials
   
   # ON WINDOWS
   notepad C:\Users\<YOUR_WINDOWS_USER_NAME>\.aws\credentials
   ```
   Make sure youre IAM user has policies attached to be able to manipulate **CloudFormation, Systems
   Manager, Lambda and CloudWatch Logs.** If unsure about the policies needed, use the **AdministratorAccess**
   managed policy, and narrow it down before releasing the project in production.
   

2. Navigate to your default projects folder and choose one of the following generation commands to generate 
   a new project. Replace the content within the angled brackets (including the brackets themselves) 
   by the proper information and the acquired AWS credentials. Run the command from your default projects 
   folder.
   #### Basic Project
    ```
    mvn archetype:generate \
      -DarchetypeGroupId=com.soundlab \
      -DarchetypeArtifactId=serverless-quarkus-spring-aws-archetype \
      -DgroupId=<YOUR_ORGANIZATION_GROUP_ID> \
      -DartifactId=<PROJECT_NAME> \
      -DawsStackName=<AWS_STACK_NAME_FOR_THIS_PROJECT> \
      -DawsAccessKeyId=<AWS_ACCESS_KEY_ID_FOR_THIS_PROJECT> \
      -DawsSecretAccessKey=<AWS_SECRET_ACCESS_KEY_FOR_THIS_PROJECT>
    ```
   #### Cognito secured project
    ```
    mvn archetype:generate \
      -DarchetypeGroupId=com.soundlab \
      -DarchetypeArtifactId=serverless-quarkus-spring-aws-archetype \
      -DgroupId=<YOUR_ORGANIZATION_GROUP_ID> \
      -DartifactId=<PROJECT_NAME> \
      -DawsStackName=<AWS_STACK_NAME_FOR_THIS_PROJECT> \
      -DawsAccessKeyId=<AWS_ACCESS_KEY_ID_FOR_THIS_PROJECT> \
      -DawsSecretAccessKey=<AWS_SECRET_ACCESS_KEY_FOR_THIS_PROJECT> \
      -Dsecure=true
    ```
   #### Cognito secured project, instantly deploy after generation
    ```
    mvn archetype:generate \
      -DarchetypeGroupId=com.soundlab \
      -DarchetypeArtifactId=serverless-quarkus-spring-aws-archetype \
      -DgroupId=<YOUR_ORGANIZATION_GROUP_ID> \
      -DartifactId=<PROJECT_NAME> \
      -DawsStackName=<AWS_STACK_NAME_FOR_THIS_PROJECT> \
      -DawsAccessKeyId=<AWS_ACCESS_KEY_ID_FOR_THIS_PROJECT> \
      -DawsSecretAccessKey=<AWS_SECRET_ACCESS_KEY_FOR_THIS_PROJECT> \
      -Dsecure=true \
      -Ddeploy=true
    ```

3. Check your newly created project's **README.md** for further usage instructions.