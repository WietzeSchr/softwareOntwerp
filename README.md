# :notebook_with_decorative_cover: Table of Contents

- [About the Project](#star2-about-the-project)
- [Roadmap](#compass-roadmap)

## :star2: About the Project

## :compass: Roadmap

### Usecases

* [x] Open Textr with the given files
* [x] Edit the focused view by typing characters, press enter to insert a linebreak and press backspace to delete a character
* [x] Go to the next/previous view using Ctrl + N / Ctrl + P
* [x] Move the insertion point of the focused view using the arrow keys
* [x] Rotate the focused view with the next view clockwise or counter-clockwise using Ctrl + R / Ctrl + T
* [x] Close buffer using Shift + F4
    - [x] Close buffer when buffer is dirty: Y -> discard changes and close | N -> don't close
* [x] Save the focused view's buffer using Ctrl + S
* [x] Duplicate the focused view using Ctrl + D
    - [x] Keep views on the same buffer locked in place 
* [x] Open a new Snake game using Ctrl + G
    - [ ] Resize the game, fitting the snake as centered as possible (if possible at all) in new grid
* [x] Undo the last made edit (adding/deleting characters/linebreaks) using Ctrl + Z
* [x] Redo the last undo using Ctrl + U

### General

* [ ] Tests -> ~ 90% - 95%
* [ ] Documentation
* [ ] Sequence Diagrams
* [ ] Class Diagram

### Extra ideas

* [ ] Make a Listener class that checks wether something was updated to avoid unnecessary show() calls
* [x] Make show functions less 'heavy'
* [x] Make edit class abstract to deal with different types of edits
* [x] Check for use of ENUM classes
* [x] Make a general function for inserting a list of views

### Found bugs

* [x] NullPointerException bij Snake closeGame
* [x] OpenNewGame geeft direct GameOver
* [x] Grid word niet altijd goed aangepast
* [ ] NullpointerException bij sluiten FileBufferView naast GameView
