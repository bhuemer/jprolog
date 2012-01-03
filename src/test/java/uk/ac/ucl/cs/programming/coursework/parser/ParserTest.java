package uk.ac.ucl.cs.programming.coursework.parser;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ucl.cs.programming.coursework.rules.Rule;
import uk.ac.ucl.cs.programming.coursework.rules.RuleRegistry;
import uk.ac.ucl.cs.programming.coursework.terms.Constant;
import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class ParserTest {

    // ------------------------------------------ Test methods

    @Test
    public void testParseRules() throws Exception {
        final List<Rule> rules = new ArrayList<Rule>();
        Parser parser = new Parser();
        parser.parse(new ListRegistry(rules),
            "father_of(frank, tim). \n" +
            "father_of(frank, sam). \n" +
            "grand_father_of(GP, GC) :- father_of(GP, P), parent_of(P, GC).");

        assertEquals(3, rules.size());
    }

    @Test
    public void testParseRuleWithOneCondition() throws Exception {
        Parser parser = new Parser();
        Rule rule = parser.parseRule("male(M) :- father_of(M,_).");
        assertEquals(new Predicate("male", new Variable("M")), rule.getHead());
        assertEquals(1, rule.getConditions().size());
        assertEquals(new Predicate("father_of", new Variable("M"), new Variable()), rule.getConditions().get(0));
    }

    @Test
    public void testParseRuleWithConditions() throws Exception {
        Parser parser = new Parser();
        Rule rule = parser.parseRule("grand_father_of(GP, GC) :- father_of(GP, P), parent_of(P, GC).");
        Assert.assertEquals(new Predicate("grand_father_of", new Variable("GP"), new Variable("GC")), rule.getHead());
        Assert.assertEquals(2, rule.getConditions().size());
        Assert.assertEquals(new Predicate("father_of", new Variable("GP"), new Variable("P")), rule.getConditions().get(0));
        Assert.assertEquals(new Predicate("parent_of", new Variable("P"), new Variable("GC")), rule.getConditions().get(1));
    }

    @Test
    public void testParseFact() throws Exception {
        Parser parser = new Parser();
        Rule rule = parser.parseRule("father_of(tim, frank).");
        Assert.assertEquals(new Predicate("father_of", new Constant("tim"), new Constant("frank")), rule.getHead());
        Assert.assertEquals(0, rule.getConditions().size());
    }

    @Test
    public void testParsePredicateWithOnlyConstants() throws Exception {
        Parser parser = new Parser();
        Predicate predicate = parser.parsePredicate("father_of(tim, sam)");
        Assert.assertEquals("father_of", predicate.getName());
        Assert.assertEquals(2, predicate.getArgs().size());
        Assert.assertEquals(new Constant("tim"), predicate.getArgs().get(0));
        Assert.assertEquals(new Constant("sam"), predicate.getArgs().get(1));
    }

    @Test
    public void testParsePredicateWithOnlyVariables() throws Exception {
        Parser parser = new Parser();
        Predicate predicate = parser.parsePredicate("father_of(P, C)");
        Assert.assertEquals("father_of", predicate.getName());
        Assert.assertEquals(2, predicate.getArgs().size());
        Assert.assertEquals(new Variable("P"), predicate.getArgs().get(0));
        Assert.assertEquals(new Variable("C"), predicate.getArgs().get(1));
    }

    @Test
    public void testParseInvalidPredicateName() throws Exception {
        Parser parser = new Parser();
        try {
            parser.parsePredicate("father*of(P, C)");
            fail("This predicate contains invalid characters in the name.");
        } catch (ParseException ex) {
            // Expected exception
        }
    }

    @Test
    public void testParseInvalidPredicateBrackets() throws Exception {
        Parser parser = new Parser();
        try {
            parser.parsePredicate("father*of(P, C(");
            fail("The brackets of this predicate do not match.");
        } catch (ParseException ex) {
            // Expected exception
        }
    }

    // ------------------------------------------ Utility classes

    private static class ListRegistry implements RuleRegistry {

        private List<Rule> rules;

        // -------------------------------------- Constructors

        public ListRegistry(List<Rule> rules) {
            this.rules = rules;
        }

        // -------------------------------------- RuleRegistry methods

        public void registerRule(Rule rule) {
            rules.add(rule);
        }

    }

}
