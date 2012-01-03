package uk.ac.ucl.cs.programming.coursework.parser;

import uk.ac.ucl.cs.programming.coursework.rules.Rule;
import uk.ac.ucl.cs.programming.coursework.rules.RuleRegistry;
import uk.ac.ucl.cs.programming.coursework.terms.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Parser {

    /**
     * The regular expression pattern for predicates within rules. This pattern is
     * different to the other predicate pattern because when this parser digests a
     * rule, it doesn't care about the details of predicates. It's just something
     * that has a name, two brackets and something in between those brackets.
     */
    private static final String PREDICATE_RULE_PATTERN = "[a-zA-Z0-9_]+\\([^\\)]*\\)";

    /** The compiled regular expression pattern that matches a single rule. */
    private Pattern rulePattern;

    /** The compiled regular expression pattern that matches a single predicate. */
    private Pattern predicatePattern;

    // ------------------------------------------ Constructors

    public Parser() {
        // Compile the pattern that matches just a single rule. A rule can be either a fact or an actual rule, which
        // means it doesn't have to have conditions, but if the token ":-" appears, it better have at least one
        // condition. Additionally note that this pattern assumes that the caller trims the input string before.
        this.rulePattern = Pattern.compile(
                "(" + PREDICATE_RULE_PATTERN + ")\\s*(?::-\\s*(" + PREDICATE_RULE_PATTERN + "(?:\\s*,\\s*" + PREDICATE_RULE_PATTERN + ")*))?\\s*\\.");

        // Compile the pattern that matches just a single predicate. 
        this.predicatePattern = Pattern.compile(
                "([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_]+)\\s*(?:,\\s*([a-zA-Z0-9_]+))*\\s*\\)");
    }

    // ------------------------------------------ Public methods

    public void parse(RuleRegistry registry, CharSequence input) throws IOException, ParseException {
        parse(registry, new StringReader(input.toString()));
    }

    public void parse(final RuleRegistry registry, Reader input) throws IOException, ParseException {
        ParserReader reader = new ParserReader('.');
        reader.read(input, new ParserReader.ParserHandler() {
            public boolean handleContent(String content) throws ParseException {
                Rule rule = parseRule(content.trim());
                registry.registerRule(rule);

                return true; // Parse the whole file
            }
        });
    }

    public Rule parseRule(String input) throws ParseException {
        Matcher matcher = rulePattern.matcher(input);
        if (!matcher.matches()) {
            throw new ParseException(
                "The given input '" + input + "' is not a valid rule.");
        }

        List<Predicate> conditions = new ArrayList<Predicate>();
        if (matcher.groupCount() == 2 && matcher.group(2) != null) {
            conditions = parsePredicates(matcher.group(2));
        }

        return new Rule(parsePredicate(matcher.group(1)), conditions);
    }

    public List<Predicate> parsePredicates(String input) throws ParseException {
        List<Predicate> predicates = new ArrayList<Predicate>();

        int numberOfBrackets = 0;

        int start = 0;
        for (int end = 0; end < input.length(); ++end) {
            char currentChar = input.charAt(end);
            switch (currentChar) {
                case '(':
                    numberOfBrackets++;
                    break;

                case ')':
                    numberOfBrackets--;
                    break;

                case ',':
                    if (numberOfBrackets == 0) {
                        String predicate = input.substring(start, end);
                        predicates.add(parsePredicate(predicate.trim()));

                        // Skip the current character for the next search (hence the +1)
                        // and start looking for more predicates from this position
                        // onwards.
                        start = end + 1;
                    }
                    break;
            }
        }

        String lastPredicate = input.substring(start);
        predicates.add(parsePredicate(lastPredicate.trim()));

        return predicates;
    }

    /**
     * <p>This method returns a single predicate by parsing the given input. If this input
     * doesn't represent a valid predicate, a ParseException will be thrown.</p>
     *
     * @param input
     *
     * @return
     *
     * @throws ParseException if the given input doesn't represent a valid predicate
     */
    public Predicate parsePredicate(String input) throws ParseException {
        Matcher matcher = predicatePattern.matcher(input);
        if (!matcher.matches()) {
            throw new ParseException(
                "The given input '" + input + "' is not a valid predicate.");
        }

        List<Term> arguments = new ArrayList<Term>();
        for (int i = 2; i <= matcher.groupCount(); i++) {
            String argument = matcher.group(i);
            if (argument != null) {
                if (argument.equals("_")) {
                    arguments.add(new Variable());    
                } else if (Character.isUpperCase(argument.charAt(0))) {
                    arguments.add(new Variable(argument));
                } else {
                    try {
                        arguments.add(new uk.ac.ucl.cs.programming.coursework.terms.Number(
                                Integer.parseInt(argument)));
                    } catch (NumberFormatException ex) {
                        arguments.add(new Constant(argument));
                    }
                }
            }
        }

        return new Predicate(matcher.group(1), arguments);
    }

}
