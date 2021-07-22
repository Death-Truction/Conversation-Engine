# Dokumentation der derzeitigen Fähigkeiten

<details open="open">
  <summary><h2 style="display: inline-block">Inhaltsverzeichnis</h2></summary>
  <ol>
    <li><a href="#nlp-komponente">NLP-Komponente</a></li>
    <li><a href="#trigger-wörter">Trigger-Wörter</a></li>
    <li><a href="#anfragen-und-antworten-der-verfügbaren-skills">Anfragen und Antworten der verfügbaren Skills</a>
        <ul>
            <li><a href="#allgemeine-informationen">Allgemeine Informationen</a></li>
            <li><a href="#wetter-skill">Wetter Skill</a></li>
            <li><a href="#begrüßungs-skill">Begrüßungs Skill</a></li>
            <li><a href="#rezeptsuche-skill">Rezeptsuche Skill</a></li>
            <li><a href="#rezeptauswahl-skill">Rezeptauswahl Skill</a></li>
            <li><a href="#koch-skill">Koch Skill</a></li>
        </ul>
    </li>
    <li><a href="#liste-der-verfügbaren-rezepte">Liste der verfügbaren Rezepte</a></li>
  </ol>
</details>

## NLP-Komponente
- Ignoriert Groß- und Kleinschreibung
- Kann Ja/Nein Antworten auf passende Skill-Fragen verstehen
## Trigger-Wörter
- Abbruch (Stellt nur eine Frage, wenn mehr als ein Skill derzeitig ausgeführt werden)
    - Letzten (um den letzten Skill abzubrechen)
    - Alle (Um alle Skills abzubrechen)
## Anfragen und Antworten der verfügbaren Skills
### Allgemeine Informationen
#### Standort ohne Wetteranfrage
- Wird gespeichert aber (derzeit) nicht verwendet
##### Trigger-Wörter in der Anfrage:
    Eine der möglichen Entities ohne einen der Trigger von den anderen Skills
##### Mögliche Entities:
    Berlin
    Dortmund
    München
    Hamburg
##### Beispiel-Anfrage:
    Ich bin in Berlin
##### Antwort-Format:
    KEINE (Da kein Intent angegeben wurde -> kein Skill wurde angefragt)
#### Zutat ohne Rezept-Suche
#### Wird gespeichert und für den Koch-Skill verwendet
##### Trigger-Wörter in der Anfrage:
    Eine der möglichen Entities ohne einen der Trigger von den anderen Skills
##### Mögliche Entities:
    Paprika
    Kartoffeln
    Salami
    Brot
    Erbsen
##### Beispiel-Anfrage:
    Ich habe Kartoffeln zu Hause
##### Antwort-Format:
    KEINE (Da kein Intent angegeben wurde -> kein Skill wurde angefragt)

### Wetter-Skill
#### Trigger-Wörter in der Anfrage:
    Wetter
    Grad
    Temperatur
#### Mögliche Entities:
    Berlin
    Dortmund
    München
    Hamburg
#### Beispiel-Anfrage:
    Wie ist das Wetter in Berlin?
#### Antwort-Format:
    In Berlin sind es <Zufällige Zahl zwischen -20 und 40> Grad bei <Zufällige Wetterlage>
### Begrüßungs-Skill
#### Trigger-Wörter in der Anfrage:
    Hi
    Hallo
    Guten Tag
#### Mögliche Entities:
    KEINE
#### Beispiel-Anfrage:
    Guten Tag
#### Antwort-Format:
    <Begrüßung passend zur Uhrzeit>
### Rezeptsuche-Skill
#### Trigger-Wörter in der Anfrage:
    Rezepte mit
    Was für ein Rezept
    Welche Rezepte
    Essen
#### Mögliche Entities:
    Paprika
    Kartoffeln
    Salami
    Brot
    Erbsen
#### Beispiel-Anfrage:
    Welche Rezepte gibt es mit Paprika
#### Antwort-Format:
    Ich habe folgende Rezepte mit <Zutaten> gefunden: <Liste der Rezepte>
### Rezeptauswahl-Skill
-  Wird vom Koch-Skill genutzt
#### Trigger-Wörter in der Anfrage:
    Das Rezept <Rezept-Name>
#### Mögliche Entities:
    Der Name eines Rezeptes (siehe Liste der verfügbaren Rezepte)
#### Beispiel-Anfrage:
    Wähle das Rezept Paprika mit Kartoffeln und Erbsen
#### Antwort-Format:
    Das Rezept <Rezeptname> wurde erfolgreich ausgewählt.
### Koch-Skill
#### Trigger-Wörter in der Anfrage:
    Koche
    Zubereiten
    Nächster Schritt (Während des Kochvorgangs)
#### Mögliche Entities:
    Siehe Rezeptauswahl-Skill
#### Beispiel-Anfrage:
    Koche das Rezept Paprika mit Kartoffeln und Erbsen
    Ich möchte das Rezept Paprika-Kartoffelsuppe kochen
    Koche (Kocht das zuletzt ausgewählte Rezept)
#### Antwort-Format:
##### Wenn noch nicht alle Zutaten vorhanden sind:
    Haben Sie die Zutat <Zutatenname> zu Hause?
##### Wenn alle Zutaten vorhanden sind:
    Sie haben alle benötigten Zutaten
    Der <#.> Schritt: <Anweisung>
## Liste der verfügbaren Rezepte
-  Paprika mit Kartoffeln und Erbsen
-  Brot mit Salami
-  Paprika-Kartoffelsuppe

