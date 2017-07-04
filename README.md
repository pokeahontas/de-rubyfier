# de-rubyfier
## Description:

This application is ment for generating textual call graphs for Ruby on Rails projects.
For parsing the rails source code the parser generator framework ANTLR4(http://www.antlr.org/) was used. 
For parsing .erb files I used an existing HTML grammar from ANTLR, which I extended with some erb specific syntax 
and I implemented a grammar for ruby code(which is by far not complete, in the sense of being a static interpreter)

## Technical Details:

Here is a rough summary of what happens in the code:

- Phase 1: All .erb files in the project are parsed and all embedded ruby code in between scriptlet tags is written into a list of strings, where every entry represents the embedded code for one file.
- Phase 2: Embedded code is parsed with the Ruby parser and all used functions are saved in a HashSet
- Phase 3: All .rb files are parsed and all defined functions are saved in a HashMap where the keys are function names and the values are filepaths for the files where the functions where defined.
- Phase 4: Again go through all ruby code and for methods that were used in erb files create function call graphs.


## How to use:
After downloading/cloning the repository:
1. Download and setup maven (https://maven.apache.org/)
2. Navigate to project folder
3. `mvn clean install`
4. `mvn exec:java -Dexec.mainClass=com.pokeahontas.bac.DeRubyfier.DeRubyfier`
5. Filechooser is then opened and then the app folder from a rails project should be chosen.
Only directories named 'app' are accepted as input, as this is the standard name of the main source code folder of every rails app.
