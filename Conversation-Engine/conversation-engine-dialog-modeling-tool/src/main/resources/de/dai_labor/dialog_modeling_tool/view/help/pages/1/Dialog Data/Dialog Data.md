# Dialgoue Data
## Intro
The Dialog Data View collects addition information required for the Conversation Engine to run the skill.
## Name of the Skill
The name of the skill is displayed to the User and alsoused for the logging messages
## Start and End State
- The start State defines the entry point of the skill's state machine and everytime the skill is called but is not running, will start from the given start state
- The end state tells the Conversation Engine when the given skill is finished and can be terminated
## Intents
The intents are used by the NLP-Component to understand which intents are used and can be filled. These intents are passed to the NLP-Component once the skill is added to the Conversation Engine.

**Each intent should be seperated by a new line**

## Entities
Like the intents, the entities are passed to the Conversation Engine upon adding the skill. Entites descripe the additional information that a skill uses to process the given intents.

**Each Entity should be seperated by a new line**
