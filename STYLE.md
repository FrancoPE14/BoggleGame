## Naming Conventions

### Packages and Modules

Use only lowercase letters, with consecutive words concatenated 
(e.g.`com.example.deepspace`). 

### Classes and Interfaces

Use **UpperCamelCase** 

Additionally, the name itself depends on the class/interface
- Classes are typically nouns or noun phrases (e.g. `Car`). 
- Interface names are typically adjectives (e.g. `Driveable`).
- Test classes should end with `Test` (e.g. `CarTest`).

### Methods

Use **lowerCamelCase** and are typically verbs or verb phrases (e.g., `sendMessage`, `stop`).

### Constants

Use **UPPER_SNAKE_CASE** (all uppercase with words separated by underscores). Constants are `static final` fields that are deeply immutable.
    - _Example_: `static final int MAX_VALUE = 100;`

### Variables

Use **lowerCamelCase**. Names should be short yet meaningful. Avoid using `_` or `$` characters at the beginning of names. Loop variables like `i`, `j`, `k` are acceptable for simple loops.


## Formatting and Layout

### Indentation

Use 4 spaces. This is the default indentation for a tab in IntelliJ

```java
public class Car {
    public static String indent = "";
}
```

### Line Length

Limit lines to 80-100 characters.

### Braces

Place the opening brace `{` on the same line as the statement it belongs to 
```java
if (condition) { 
}
```

 Always use braces for `if`, `for`, `while` statements, even for single-line bodies,

### Whitespace

Use blank spaces around operators (e.g., `a = b + c`), after commas, and between control structures and parentheses (e.g., `if (condition)`).


## Source File 

### File name

For a source file containing classes, the file name consists of the case-sensitive name of the top-level class.
- The top-level class is a class that is not nested

###  Source file structure

A source file consists of these sections, **in order**:

1. Package declaration
2. Imports
3. Exactly one top-level class declaration


**Two blank lines** should separate each section.

### Imports

No wildcard imports
```java
import java.util.ArrayList;
```

### Class Contents

A class consists of these sections, **in order**:

1. Fields
2. Constructors
3. Other methods

**Two blank lines** should separate each section.

**One blank line** should separates methods.


## Javadocs

### Classes

```
/**
 * TODO: Describe the purpose of this class.
 */
public class ExampleClass {
}
```

### Methods

```
/**
 * TODO: Brief description of what this method does.
 *
 * @param paramName TODO: describe this parameter
 * @return TODO: describe the return value
 * @throws ExceptionType TODO: describe when this exception is thrown
 */
```