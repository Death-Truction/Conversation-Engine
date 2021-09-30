# Simulation Settings
## Intro
The Simulation Settings are only used for the Simulation within the Dialogue Modeling Tool.
## Language
The language defines in which language the Conversation Engine should run. This changes default outputs by the Conversation Engine but NOT any outputs by the NLP-Component or Skill
## Logging Level
The logging level determines which messages are logged during the Simulation
## NLP-Component
The NLP-Component is required by the Conversation Engine to process the user inputs. To Sleect a NLP-Component you have to add select the jar file containing the NLP-Component first. Then you will get a list of classes, that implement the INLPComponent interface, from which you have to select one class, that will be used as the NLP-Component.
## Skill
The Skill will be selected in the same way as the NLP-Component.
## Conversation Inputs
The Conversation Inputs will be processed one at a time. These will be used by the simulation to display the progress of the Conversation Engine step by step using the given skill.

**Each Conversation Input has to be seperated by a new line**