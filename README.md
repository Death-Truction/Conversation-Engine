<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/death-truction/Conversation-Engine">
    <img src="Images/Icon.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Conversation-Engine</h3>

  <p align="center">
    A dialog management system framework
    <br />
    <br />
    <a href="https://github.com/death-truction/Conversation-Engine/issues">Report Bug</a>
    ·
    <a href="https://github.com/death-truction/Conversation-Engine/issues">Request Feature</a>
  </p>
</p>



<details open="open"><summary><h2 style="display: inline-block">Table of Contents</h2></summary>
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
    <li><a href="#testing">Testing</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#logging">Logging</a></li>
    <li><a href="#documentation">Documentation</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#used-dependencies">Used Dependencies</a></li>
    <li><a href="#used-plugins">Used Plugins</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



## About The Project

Conversation-Engine is a framework that is supposed to be used as a dialog management system. It is designed to simplify the creation of chatbots by combining skills and a single NLP-Component} into an all in one system.<br>
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

1. Clone the Conversation-Engine Project
   ```sh
   git clone https://github.com/death-truction/Conversation-Engine.git
   ```
2. Change the directory to the project folder
    ```sh
    cd Conversation-Engine
    ```
3. Install the package to your local maven repository <b>(replace the version number with the latest release)</b>
    ```sh
    mvn clean install
    ```

## Testing

The project includes a <i>playground</i> to try the Conversation-Engine with some example skills. 
1. To start the playground simple run the following command in your cloned github repository:
    ```sh
    mvn exec:java
    ```
2. These languages are currently supported for the playground:
    * German
    * English
3. For a list of supported request checkout
    * [Current Skills German](https://github.com/Death-Truction/Conversation-Engine/blob/main/Current%20Skills%20German.md)
    * [Current Skills English](https://github.com/Death-Truction/Conversation-Engine/blob/main/Current%20Skills%20English.md)

## Usage

1. To use the Conversation-Engine Framework simply include the installed dependency in your pom <b>(replace the version number with the latest release)</b>
    ```xml
    ...
      <dependencies>
        ...
        <dependency>
          <groupId>de.dai-labor</groupId>
          <artifactId>conversation-engine</artifactId>
          <version>1.0.0</version>
        </dependency>
        ...
      </dependencies>
      ...
    ```
2. Implement the interfaces found in the interfaces package at one of the following locations:
    * [github](https://github.com/Death-Truction/Conversation-Engine/tree/main/Conversation-Engine/src/main/java/interfaces)
    * [javadocs](https://death-truction.github.io/Conversation-Engine/apidocs/interfaces/package-summary.html)
    * <i>For an implementation example checkout these test classes</i>
      * [NLPComponent](https://github.com/Death-Truction/Conversation-Engine/blob/main/Conversation-Engine/src/test/java/interfaces_implementation/NLPComponent.java)
      * [NLPAnswer](https://github.com/Death-Truction/Conversation-Engine/blob/main/Conversation-Engine/src/test/java/interfaces_implementation/NLPAnswer.java)
      * [Skill](https://github.com/Death-Truction/Conversation-Engine/blob/main/Conversation-Engine/src/test/java/skills/WeatherSkill.java)
      * [SkillAnswer](https://github.com/Death-Truction/Conversation-Engine/blob/main/Conversation-Engine/src/test/java/interfaces_implementation/SkillAnswer.java)
3. Create a valid state machine JSON-File 
    * by using the Dialog Modeling Tool inside the project
      * You can run the tool by navigating inside the folder and running the following command AFTER installing the conversation engine:
      ```sh
      mvn javafx:run
      ```
    * Create your own JSON-File by following [this JSON-Schema](https://github.com/Death-Truction/Conversation-Engine/blob/main/Conversation-Engine/src/main/resources/SkillStateMachine_Schema.json)
4. Create a new Conversation-Engine object and add your NLPComponent and your skills
    ```java
    NLPComponent nlp = new NLPComponent();
    ConversationEngine conversationEngine = new ConversationEngine(nlp);
    MySkill mySkill = new MySkill();
    String mySkillStateMachineJsonFile = loadJsonFileAsString("MySkill.json");
    stateMachine.addSkill(mySkill, mySkillStateMachineJsonFile);
    ```
5. Now you can simply send your inputs to the Conversation-Engine and get the answers in a List
    ```java
    List<String> answers = conversationEngine.userInput("Hello world!");
    ```
## Logging

The project includes two Logging-Facades named <i>"DeveloperLogger"</i> and <i>"ConversationLogger"</i><br>
To use the output of the loggers you have to add your own logging framework to the project and configure the loggers.<br>
Checkout the tests of the project, which are using the logging dependency [logback](https://death-truction.github.io/Conversation-Engine/dependencies.html) with a [simple configuration](https://github.com/Death-Truction/Conversation-Engine/blob/main/Conversation-Engine/src/test/resources/logback.xml).
## Documentation
* [Overview](https://death-truction.github.io/Conversation-Engine/index.html)
* [Javadoc](https://death-truction.github.io/Conversation-Engine/apidocs/index.html)

## Roadmap

See the [open issues](https://github.com/death-truction/Conversation-Engine/issues) for a list of proposed features (and known issues).


## License

Distributed under the MIT License. See [`License`][license-url] for more information.

## [Used Dependencies](https://death-truction.github.io/Conversation-Engine/dependencies.html)

## [Used Plugins](https://death-truction.github.io/Conversation-Engine/plugins.html)

## Acknowledgements

* [Best-README-Template](https://github.com/othneildrew/Best-README-Template)


<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/death-truction/Conversation-Engine.svg?style=for-the-badge
[contributors-url]: https://github.com/death-truction/Conversation-Engine/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/death-truction/Conversation-Engine.svg?style=for-the-badge
[forks-url]: https://github.com/death-truction/Conversation-Engine/network/members
[stars-shield]: https://img.shields.io/github/stars/death-truction/Conversation-Engine.svg?style=for-the-badge
[stars-url]: https://github.com/death-truction/Conversation-Engine/stargazers
[issues-shield]: https://img.shields.io/github/issues/death-truction/Conversation-Engine.svg?style=for-the-badge
[issues-url]: https://github.com/death-truction/Conversation-Engine/issues
[license-shield]: https://img.shields.io/github/license/death-truction/Conversation-Engine.svg?style=for-the-badge
[license-url]: https://github.com/death-truction/Conversation-Engine/blob/main/LICENSE