<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/death-truction/ConversationsEngine">
    <img src="Images/Icon.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">ConversationsEngine</h3>

  <p align="center">
    A dialog management system framework
    <br />
    <br />
    <a href="https://github.com/death-truction/ConversationsEngine/issues">Report Bug</a>
    ·
    <a href="https://github.com/death-truction/ConversationsEngine/issues">Request Feature</a>
  </p>
</p>



<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#documentation">Documentation</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#used-dependencies">Used Dependencies</a></li>
    <li><a href="#used-plugins">Used Plugins</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



## About The Project

ConversationsEngine is a framework that is supposed to be used as a dialog management system. It is designed to simplify the creation of chatbots by combining skills and a single NLP-Component} into an all in one system.<br>
The system is based on a finite state machine to create an accessible testing
environment.


## Getting Started

To get a local copy up and running follow these steps.

### Prerequisites

#### <b>Java</b>

<i>To install Java you can choose one of the following options</i>

  * OpenJDK
  
    Please visit https://openjdk.java.net/install/ for installation instructions

  * Oracle

    Please visit https://docs.oracle.com/en/java/javase/13/install/overview-jdk-installation.html for installation instructions
#### <b>Maven</b>

  * Please visit https://maven.apache.org/install.html for installation instructions

### Installation

1. Clone the ConversationsEngine Project
   ```sh
   git clone https://github.com/death-truction/ConversationsEngine.git
   ```
2. Create the Package
   ```sh
   mvn clean package
   ```
3. Install the package to your local maven repository <b>(replace the version number with the latest release)</b>
    ```sh
    mvn install:install-file -Dfile=target/conversations-engine-1.0.0.jar -DpomFile=pom.xml -Djavadoc=target/conversations-engine-1.0.0-javadoc.jar
    ```

## Testing
I created a <i>playground</i> to try the ConversationsEngine with some example skills. 
1. To start the playground simple run the following command in your cloned github repository:
    ```sh
    mvn exec:java
    ```
2. These languages are currently supported for the playground:
    * German
    * English (<b><i>Coming soon</i></b>)
3. For a list of supported request checkout [Dokumentation der NLPKomponente und der Skills.docx](https://github.com/Death-Truction/ConversationsEngine/blob/main/Dokumentation%20der%20NLPKomponente%20und%20der%20Skills.docx)

## Usage

1. To use the ConversationsEngine Framework simply include the installed dependency in your pom <b>(replace the version number with the latest release)</b>
    ```xml
    ...
      <dependencies>
        ...
        <dependency>
          <groupId>de.dai-labor</groupId>
          <artifactId>conversations-engine</artifactId>
          <version>1.0.0</version>
        </dependency>
        ...
      </dependencies>
      ...
    ```
2. Implement the interfaces found in the interfaces package at one of the following locations:
    * [github](https://github.com/Death-Truction/ConversationsEngine/tree/main/ConversationsEngine/src/main/java/interfaces)
    * [javadocs](https://death-truction.github.io/ConversationsEngine/apidocs/interfaces/package-summary.html)
    * <i>For an implementation example checkout these test classes</i>
      * [NLPComponent](https://github.com/Death-Truction/ConversationsEngine/blob/main/ConversationsEngine/src/test/java/interfaces_implementation/NLPComponent.java)
      * [NLPAnswer](https://github.com/Death-Truction/ConversationsEngine/blob/main/ConversationsEngine/src/test/java/interfaces_implementation/NLPAnswer.java)
      * [Skill](https://github.com/Death-Truction/ConversationsEngine/blob/main/ConversationsEngine/src/test/java/skills/WeatherSkill.java)
      * [SkillAnswer](https://github.com/Death-Truction/ConversationsEngine/blob/main/ConversationsEngine/src/test/java/interfaces_implementation/SkillAnswer.java)
3. Create a valid state machine JSON-File 
    * by using my GUI (<b><i>Coming soon</i></b>)
    * Create your own JSON-File by following [this JSON-Schema](https://github.com/Death-Truction/ConversationsEngine/blob/main/ConversationsEngine/src/main/resources/SkillStateMachine_Schema.json)
4. Create a new ConversationsEngine object and add your NLPComponent and your skills
    ```java
    NLPComponent nlp = new NLPComponent();
    ConversationsEngine conversationsEngine = new ConversationsEngine(nlp);
    MySkill mySkill = new MySkill();
    String mySkillStateMachineJsonFile = loadJsonFileAsString("MySkill.json");
    stateMachine.addSkill(mySkill, mySkillStateMachineJsonFile);
    ```
5. Now you can simply send your inputs to the ConversationsEngine and get the answers in a List
    ```java
    List<String> answers = conversationsEngine.userInput("Hello world!");
    ```
## Documentation
* [Overview](https://death-truction.github.io/ConversationsEngine/index.html)
* [Javadoc](https://death-truction.github.io/ConversationsEngine/apidocs/index.html)

## Roadmap

See the [open issues](https://github.com/death-truction/ConversationsEngine/issues) for a list of proposed features (and known issues).


## License

Distributed under the MIT License. See [`License`][license-url] for more information.

## [Used Dependencies](https://death-truction.github.io/ConversationsEngine/dependencies.html)

## [Used Plugins](https://death-truction.github.io/ConversationsEngine/plugins.html)

## Acknowledgements

* [Best-README-Template](https://github.com/othneildrew/Best-README-Template)


<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/death-truction/ConversationsEngine.svg?style=for-the-badge
[contributors-url]: https://github.com/death-truction/ConversationsEngine/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/death-truction/ConversationsEngine.svg?style=for-the-badge
[forks-url]: https://github.com/death-truction/ConversationsEngine/network/members
[stars-shield]: https://img.shields.io/github/stars/death-truction/ConversationsEngine.svg?style=for-the-badge
[stars-url]: https://github.com/death-truction/ConversationsEngine/stargazers
[issues-shield]: https://img.shields.io/github/issues/death-truction/ConversationsEngine.svg?style=for-the-badge
[issues-url]: https://github.com/death-truction/ConversationsEngine/issues
[license-shield]: https://img.shields.io/github/license/death-truction/ConversationsEngine.svg?style=for-the-badge
[license-url]: https://github.com/death-truction/ConversationsEngine/blob/main/LICENSE