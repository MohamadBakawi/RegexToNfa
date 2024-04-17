# RegexToNfa
 The program provides a simple graphical user interface (GUI) for users to test regular expressions against input strings or convert regular expressions to finite automata.
 
The main method displays a dialog box that prompts the user to choose an option. The options include testing a regular expression against an input string, converting a regular expression to a nondeterministic finite automaton (NFA), and quitting the program.

The testRegularExpression method takes a regular expression and an input string as input and uses the matches method of the String class to determine whether the input string matches the regular expression.

The convertRegexToAutomaton method takes a regular expression as input and uses the RegexToNfa class to convert it to an NFA representation. The RegexToNfa class uses a recursive descent parser to parse the regular expression and construct an NFA representation using the NfaFragment class.

The NfaFragment class represents a fragment of an NFA, which can be combined with other fragments using operations such as concatenation, union, and closure. Each NfaFragment object has a start state, an end state, and a set of epsilon transitions to other states.
