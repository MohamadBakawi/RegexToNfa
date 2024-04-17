import javax.swing.JOptionPane;
import java.util.HashSet;
import java.util.Set;

public class RegularExpressionTester {

    public static void main(String[] args) {
        while (true) {
            String option = (String) JOptionPane.showInputDialog(null, "Choose an option:", "Regular Expression Tester", JOptionPane.PLAIN_MESSAGE, null, new String[]{"1. Test Regular Expression", "2. Convert Regular Expression to Finite Automaton", "4. Quit"}, "1. Test Regular Expression");

            if (option == null || option.equals("4. Quit")) {
                break; // User clicked cancel, closed the dialog, or chose to quit
            }

            switch (option) {
                case "1. Test Regular Expression":
                    testRegularExpression();
                    break;
                case "2. Convert Regular Expression to Finite Automaton":
                    convertRegexToAutomaton();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, or 4.");
            }
        }
    }

    private static void testRegularExpression() {
        String regex = JOptionPane.showInputDialog("Enter a regular expression:");

        if (regex != null) {
            String input = JOptionPane.showInputDialog("Enter a string to test:");

            if (input != null) {
                boolean isMatch = regexMatchesString(regex, input);
                String resultMessage = isMatch ? "Matches" : "Does not match";
                JOptionPane.showMessageDialog(null, "\"" + input + "\" " + resultMessage + " the pattern \"" + regex + "\"");
            }
        }
    }

    private static void convertRegexToAutomaton() {
        String regex = JOptionPane.showInputDialog("Enter a regular expression:");

        if (regex != null) {
            String nfaRepresentation = RegexToNfa.convert(regex);
            JOptionPane.showMessageDialog(null, "NFA Representation:\n" + nfaRepresentation);
        }
    }

    private static void convertAutomatonToRegex() {
        JOptionPane.showMessageDialog(null, "Automaton to Regex conversion result will be displayed here.");
    }

    private static boolean regexMatchesString(String regex, String input) {
        return input.matches(regex);
    }
}

class RegexToNfa {

    private static int stateCounter = 0;

    public static String convert(String regex) {
        stateCounter = 0; // Reset state counter for each conversion
        NfaFragment nfa = regexToNfa(regex);
        return nfa.toString();
    }

    private static NfaFragment regexToNfa(String regex) {
        if (regex.length() == 1) {
            return new NfaFragment(regex.charAt(0));
        }

        char[] symbols = regex.toCharArray();
        NfaFragment resultNfa = null;

        for (char symbol : symbols) {
            NfaFragment currentNfa;

            switch (symbol) {
                case '|':
                    NfaFragment left = popNfa();
                    NfaFragment right = regexToNfa(regex.substring(1));
                    currentNfa = union(left, right);
                    break;
                case '.':
                    left = popNfa();
                    right = regexToNfa(regex.substring(1));
                    currentNfa = concatenate(left, right);
                    break;
                case '*':
                    left = popNfa();
                    currentNfa = closure(left);
                    break;
                default:
                    currentNfa = new NfaFragment(symbol);
            }

            if (resultNfa == null) {
                resultNfa = currentNfa;
            } else {
                resultNfa = concatenate(resultNfa, currentNfa);
            }

            regex = regex.substring(1);
        }

        return resultNfa;
    }

    private static NfaFragment popNfa() {
        stateCounter++;
        return new NfaFragment(stateCounter - 1, stateCounter);
    }

    private static NfaFragment union(NfaFragment left, NfaFragment right) {
        stateCounter++;
        NfaFragment nfa = new NfaFragment(stateCounter - 1, stateCounter);
        nfa.addEpsilonTransition(left.getStartState());
        nfa.addEpsilonTransition(right.getStartState());
        left.addEpsilonTransition(nfa.getEndState());
        right.addEpsilonTransition(nfa.getEndState());
        return nfa;
    }

    private static NfaFragment concatenate(NfaFragment left, NfaFragment right) {
        left.addEpsilonTransition(right.getStartState());
        return new NfaFragment(left.getStartState(), right.getEndState());
    }

    private static NfaFragment closure(NfaFragment nfa) {
        stateCounter++;
        NfaFragment closureNfa = new NfaFragment(stateCounter - 1, stateCounter);
        closureNfa.addEpsilonTransition(nfa.getStartState());
        closureNfa.addEpsilonTransition(closureNfa.getEndState());
        nfa.addEpsilonTransition(nfa.getEndState());
        nfa.addEpsilonTransition(closureNfa.getEndState());
        return closureNfa;
    }

    private static class NfaFragment {
        private int startState;
        private int endState;
        private Set<Integer> epsilonTransitions;
        private char symbol;

        public NfaFragment(char symbol) {
            stateCounter++;
            this.startState = stateCounter - 1;
            stateCounter++;
            this.endState = stateCounter - 1;
            this.epsilonTransitions = new HashSet<>();
            this.symbol = symbol;
        }

        public NfaFragment(int startState, int endState) {
            this.startState = startState;
            this.endState = endState;
            this.epsilonTransitions = new HashSet<>();
            this.symbol = 0; // 0 represents epsilon transition
        }

        public int getStartState() {
            return startState;
        }

        public int getEndState() {
            return endState;
        }

        public Set<Integer> getEpsilonTransitions() {
            return epsilonTransitions;
        }

        public char getSymbol() {
            return symbol;
        }

        public void addEpsilonTransition(int state) {
            epsilonTransitions.add(state);
        }

        @Override
        public String toString() {
            StringBuilder transitions = new StringBuilder();

            if (symbol != 0) {
                transitions.append(String.format("(%d, %c, %d)\n", startState, symbol, endState));
            }

            for (int epsilonTransition : epsilonTransitions) {
                transitions.append(String.format("(%d, ε, %d)\n", startState, epsilonTransition));
            }

            if (symbol == 0 && epsilonTransitions.isEmpty()) {
                transitions.append(String.format("(%d, ε, %d)\n", startState, endState));
            }

            return transitions.toString();
        }
        }
    }

