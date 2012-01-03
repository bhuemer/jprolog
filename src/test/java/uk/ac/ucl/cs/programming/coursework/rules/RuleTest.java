package uk.ac.ucl.cs.programming.coursework.rules;

import org.junit.Test;
import uk.ac.ucl.cs.programming.coursework.terms.Constant;
import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RuleTest {

    @Test
    public void testSubstituteRule() throws Exception {
        // a(X, Y) :- b(X), c(Y).
        Rule rule = new Rule(new Predicate("a", new Variable("X"), new Variable("Y")),
                new Predicate("b", new Variable("X")),
                new Predicate("c", new Variable("Y")));

        rule = rule.substitute(new Variable("X"), new Constant("x"));
        rule = rule.substitute(new Variable("Y"), new Constant("y"));

        assertEquals(new Constant("x"), rule.getHead().getArgs().get(0));
        assertEquals(new Constant("y"), rule.getHead().getArgs().get(1));
        assertEquals(new Constant("x"), rule.getConditions().get(0).getArgs().get(0));
        assertEquals(new Constant("y"), rule.getConditions().get(1).getArgs().get(0));
    }

}
