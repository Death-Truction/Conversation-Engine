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
    project_description
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
    <li><a href="#license">License</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



## About The Project

TODO:


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


## Usage

1. To use the Conversations-Engine Framework simply include the installed dependency in your pom <b>(replace the version number with the latest release)</b>
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