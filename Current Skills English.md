# Documentation of current skills

<details open = "open">
    <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
    <ol>
        <li><a href="#nlp-component">NLP component</a></li>
        <li><a href="#trigger-words">trigger words</a></li>
        <li><a href="#request-and-responsive-of-available-skills">Queries and answers of the available skills</a>
            <ul>
                <li><a href="#general-information">General information</a></li>
                <li><a href="#wetter-skill">Weather Skill</a></li>
                <li><a href="#welcome skill">welcome skill</a></li>
                <li><a href="#rezeptsuche-skill">recipe search skill</a></li>
                <li><a href="#rezeptwahl-skill">recipe selection skill</a></li>
                <li><a href="#koch-skill">Koch Skill</a></li>
            </ul>
        </li>
        <li><a href="#list-of-available-recipes">List of available recipes</a></li>
    </ol>
</details>

## NLP component
- Ignores upper and lower case
- Can understand yes/no answers to matching skill questions
## trigger words
- Abort (only asks one question if more than one skill is currently being executed)
    - Last (to cancel the last skill)
    - All (to cancel all skills)
## Inquiries and answers about the available skills
### General information
#### Location without weather request
- Is saved but (currently) not used
##### trigger words in the request:
    One of the possible entities without one of the triggers from the other skills
##### Possible entities:
    Berlin
    Dortmund
    Munich
    Hamburg
##### Example request:
    I'm in berlin
##### Reply format:
    NONE (since no intent was specified ->no skill was requested)
#### Ingredient without recipe search
#### Is saved and used for the cooking skill
##### trigger words in the request:
    One of the possible entities without one of the triggers from the other skills
##### Possible entities:
    pepper
    potatoes
    salami
    bread
    peas
##### Example request:
    I have potatoes at home
##### Reply format:
    NONE (since no intent was specified ->no skill was requested)
### Weather Skill
#### trigger words in the request:
    weather
    degree
    temperature
#### Possible entities:
    Berlin
    Dortmund
    Munich
    Hamburg
#### Example request:
    How is the weather in Berlin?
#### Response format:
    In Berlin it is <random number between -20 and 40>degrees with <random weather conditions>
### greeting skill
#### trigger words in the request:
    Hi
    Hello
    Good day
#### Possible entities:
    NO
#### Example request:
    Good day
#### Response format:
    <Greeting at the right time>
### Recipe search skill
#### trigger words in the request:
    recipes with
    recipe with
    which recipe
    what recipe
    food
#### Possible entities:
    pepper
    potatoes
    salami
    bread
    peas
#### Example request:
    What recipes are there with peppers
#### Response format:
    I found the following recipes with <ingredients> : <List of recipes>
### Recipe selection skill
- Used by the cooking skill
#### trigger words in the request:
    The recipe <recipe-name>
#### Possible entities:
    The name of a recipe (see the list of available recipes)
#### Example request:
    Choose the recipe Pepper with Potatoes and Peas
#### Response format:
    The recipe <recipe-name> was selected successfully.
### Cooking skill
#### trigger words in the request:
    Cook
    Prepare
    Next step (during the cooking process)
#### Possible entities:
    See recipe selection skill
#### Example request:
    Cook the recipe for Pepper with Potatoes and Peas
    I want to cook the Pepper-Potatoe-Soup recipe
    Cook (cooks the last selected recipe)
#### Response format:
##### If all the ingredients are not yet available:
    Do you have the ingredient <ingredient name> at home?
##### When all the ingredients are present:
    You have all the ingredients you need
    The <#> Step: <instruction>
## List of available recipes
- Pepper with Potatoes and Peas
- Bread with salami
- Pepper-Potatoe-Soup