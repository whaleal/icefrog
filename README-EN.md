<p align="center">
	<a href="https://whaleal.com/"><img src="https://docs.whaleal.com/images/logo1.png" width="41%"></a>
</p>
<p align="center">
	<a href="https://whaleal.com/"><img src="https://docs.whaleal.com/images/logo1.png" width="41%"></a>
</p>
<p align="center">
	<strong>🍬A set of tools that keep Java sweet.</strong>
</p>
<p align="center">
	👉 <a href="https://whaleal.com">https://whaleal.com/</a> 👈
</p>


<p align="center">
	<a target="_blank" href="https://search.maven.org/artifact/com.whaleal.icefrog/icefrog-all">
		<img src="https://img.shields.io/maven-central/v/com.whaleal.icefrog/icefrog-all.svg?label=Maven%20Central" />
	</a>
	<a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" />
	</a>
	<a href="https://codecov.io/gh/whaleal/icefrog">
		<img src="https://codecov.io/gh/whaleal/icefrog/branch/v1-main/graph/badge.svg" />
	</a>
	<a target="_blank" href='https://github.com/whaleal/icefrog'>
		<img src="https://img.shields.io/github/stars/whaleal/icefrog.svg?style=social" alt="github star"/>
	</a>
</p>

<br/>

-------------------------------------------------------------------------------

[**🌎中文说明**](README.md)

-------------------------------------------------------------------------------

## 📚Introduction
**icefrog** is a small but comprehensive library of Java tools, encapsulation by static methods, reduce the cost of learning related APIs, increase productivity, and make Java as elegant as a functional programming language,let the Java be "sweet" too.

**icefrog** tools and methods from each user's crafted, it covers all aspects of the underlying code of Java development, it is a powerful tool for large project development to solve small problems, but also the efficiency of small projects;

**icefrog** is a project "util" package friendly alternative, it saves developers on the project of icefrog classes and icefrog tool methods of encapsulation time, so that development focus on business, at the same time can minimize the encapsulation is not perfect to avoid the bugs.

### 🎁Origin of the 'icefrog' name

**icefrog = It is an open source library after the stripping of the underlying code of the Shanghai Jinmu Information Technology Co., Ltd. project. Based on the corporate culture of its game atmosphere, it commemorates the War3 ice frog god and is named icefrog
### 🍺How icefrog is changing the way we code

The goal of  **icefrog**  is to use a simple function instead of a complex piece of code, thus avoiding the problem of "copy and paste" code as much as possible and revolutionizing the way we write code.

To calculate MD1 for example:

- 👴【Before】Open a search engine -> search "Java MD1 encryption" -> open a blog -> copy and paste -> change it to work.
- 👦【Now   】import icefrog -> SecureUtil.md1()

icefrog exists to reduce code search costs and avoid bugs caused by imperfect code on the web.

### Thanks
> this README is PR by [chengxian-yi](https://github.com/yichengxian)
-------------------------------------------------------------------------------

## 🛠️Module
A Java-based tool class for files, streams, encryption and decryption, transcoding, regular, thread, XML and other JDK methods for encapsulation，composing various Util tool classes, as well as providing the following modules：

| module          |     description                                                                                                            |
| -------------------|-------------------------------------------------------------------------------------------------------------------------|
| icefrog-aop         |     JDK dynamic proxy encapsulation to provide non-IOC faceting support                                                 |
| icefrog-bloomFilter |     Bloom filtering to provide some Hash algorithm Bloom filtering                                                      |
| icefrog-cache       |     Simple cache                                                                                                        |
| icefrog-core        |     Core, including Bean operations, dates, various Utils, etc.                                                         |
| icefrog-cron        |     Task scheduling with Cron expressions                                                                               |
| icefrog-crypto      |     Provides symmetric, asymmetric and digest algorithm encapsulation                                                   |
| icefrog-db          |     Db operations based on ActiveRecord thinking.                                                                       |
| icefrog-dfa         |     DFA models, such as multi-keyword lookups                                                                           |
| icefrog-extra       |     Extension modules, third-party wrappers (template engine, mail, servlet, QR code, Emoji, FTP, word splitting, etc.) |
| icefrog-http        |     Http client                                                                                                         |
| icefrog-log         |     Log (facade)                                                                                                        |
| icefrog-script      |     Script execution encapsulation, e.g. Javascript                                                                     |
| icefrog-setting     |     Stronger Setting Profile tools and Properties tools                                                                 |
| icefrog-system      |     System parameter tools (JVM information, etc.)                                                                      |
| icefrog-json        |     JSON                                                                                                                |
| icefrog-captcha     |     Image Captcha                                                                                                       |
| icefrog-poi         |     Tools for working with Excel and Word in POI                                                                        |
| icefrog-socket      |     Java-based tool classes for NIO and AIO sockets                                                                     |
| icefrog-jwt         |     JSON Web Token (JWT) implement                                                                                      |

Each module can be introduced individually, or all modules can be introduced by introducing `icefrog-all` as required.

-------------------------------------------------------------------------------

## 📝Doc

[📘Chinese documentation](https://github.com/whaleal/icefrog/)



-------------------------------------------------------------------------------

## 📦Install

### 🍊Maven
```xml
<dependency>
    <groupId>com.whaleal.icefrog</groupId>
    <artifactId>icefrog-all</artifactId>
    <version>1.1.0</version>
</dependency>
```

### 🍐Gradle
```
implementation 'com.whaleal.icefrog:icefrog-all:1.1.0'
```

## 📥Download

- [Maven Repo](https://repo1.maven.org/maven2/com/whaleal/icefrog/icefrog-all/1.1.0/)

> 🔔️note:
> icefrog 1.x supports JDK8+ and is not tested on Android platforms, and cannot guarantee that all tool classes or tool methods are available.


### 🚽Compile and install

Download the entire project source code

github：[https://github.com/whaleal/icefrog](https://github.com/whaleal/icefrog) 


```sh
cd ${icefrog}
./IceFrog.sh install
```

-------------------------------------------------------------------------------

## 🏗️Other

### 🎋Branch Description

icefrog's source code is divided into two branches:

| branch | description                                               |
|-----------|---------------------------------------------------------------|
| v1-main | The main branch, the branch used by the release version, is the same as the jar committed to the central repository and does not receive any pr or modifications. |
| v1-dev    | Development branch, which defaults to the next SNAPSHOT version, accepts modifications or pr |

### 🐞Provide feedback or suggestions on bugs

When submitting feedback, please indicate which JDK version, icefrog version, and related dependency library version you are using.

- [github issue](https://github.com/whaleal/icefrog/issues)

##  how to contribute
1. Find the issues that need to be fixed on github issues, or propose the feature content to be contributed
2. Fork the project on github or Github to your own repo
3. Clone the fork of the past project, which is your project, to your local
4. Modify the code (remember to modify the v1-dev branch) and perform related tests
5. Push to your own library after commit (v1-dev branch)
6. Log in to github or Github and you can see a pull request button on your homepage. Click on it, select your own dev branch and the dev branch of this project, fill in some descriptive information, and then submit.
7. Waiting for the maintainer to merge

### 🧬Principles of PR(pull request)

icefrog welcomes anyone to contribute code to icefrog, but the author suffers from OCD and needs to submit a pr (pull request) that meets some specifications in order to care for the patient.：

1. Improve the comments, especially each new method should follow the Java documentation specification to indicate the method description, parameter description, return value description and other information, if necessary, please add unit tests, if you want, you can also add your name.
2. Code indentation according to Eclipse.
3. Newly added methods do not use third-party library methods，Unless the method tool is add to the '**extra module**'.
4. Please pull request to the `v1-dev` branch. icefrog uses a new branch after 1.x: `v1-main` is the main branch, which indicates the version of the central library that has been released, and this branch does not allow pr or modifications.



-------------------------------------------------------------------------------

## ⭐Star icefrog


If you think icefrog is good, welcome to continue to pay attention, thanks in advance ^_^.



![whaleal](https://github.com/whaleal/whaleal.github.io/blob/main/images/logo1.png)
