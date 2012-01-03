package uk.ac.ucl.cs.programming.coursework.terms;

import org.junit.Test;
import uk.ac.ucl.cs.programming.coursework.terms.Constant;
import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Term;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PredicateTest {

    // ------------------------------------------ Test methods

    @Test
    public void testSubstituteNoVariable() throws Exception {
        Predicate predicate = new Predicate("sells",
                Arrays.<Term>asList(new Variable("S"), new Variable("P"), new Variable("B")));

        Predicate substitutedPredicate =
                (Predicate) predicate.substitute(new Variable("Q"), new Constant("usa"));
        assertEquals("sells", substitutedPredicate.getName());
        assertEquals(3, substitutedPredicate.getArgs().size());
        assertEquals(new Variable("S"), substitutedPredicate.getArgs().get(0));
        assertEquals(new Variable("P"), substitutedPredicate.getArgs().get(1));
        assertEquals(new Variable("B"), substitutedPredicate.getArgs().get(2));
    }

    @Test
    public void testSubstituteOneVariable() throws Exception {
        Predicate predicate = new Predicate("sells",
                Arrays.<Term>asList(new Variable("S"), new Variable("P"), new Variable("B")));

        Predicate substitutedPredicate =
                (Predicate) predicate.substitute(new Variable("S"), new Constant("usa"));
        assertEquals("sells", substitutedPredicate.getName());
        assertEquals(3, substitutedPredicate.getArgs().size());
        assertEquals(new Constant("usa"), substitutedPredicate.getArgs().get(0));
        assertEquals(new Variable("P"), substitutedPredicate.getArgs().get(1));
        assertEquals(new Variable("B"), substitutedPredicate.getArgs().get(2));
    }

    @Test
    public void testSubstituteThreeVariables() throws Exception {
        Predicate predicate = new Predicate("sells",
                Arrays.<Term>asList(new Variable("S"), new Variable("S"), new Variable("S")));

        Predicate substitutedPredicate =
                (Predicate) predicate.substitute(new Variable("S"), new Constant("usa"));
        assertEquals("sells", substitutedPredicate.getName());
        assertEquals(3, substitutedPredicate.getArgs().size());
        assertEquals(new Constant("usa"), substitutedPredicate.getArgs().get(0));
        assertEquals(new Constant("usa"), substitutedPredicate.getArgs().get(1));
        assertEquals(new Constant("usa"), substitutedPredicate.getArgs().get(2));
    }

}
