package uk.ac.ucl.cs.programming.coursework.rules;

import org.junit.Test;
import uk.ac.ucl.cs.programming.coursework.evaluation.EvaluationHandler;
import uk.ac.ucl.cs.programming.coursework.evaluation.Evaluator;
import uk.ac.ucl.cs.programming.coursework.evaluation.InfiniteRecursionException;
import uk.ac.ucl.cs.programming.coursework.terms.Constant;
import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Term;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;
import uk.ac.ucl.cs.programming.coursework.unification.RobinsonUnificationStrategy;
import uk.ac.ucl.cs.programming.coursework.unification.UnificationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class EvaluatorTest {

    // ------------------------------------------ Test methods

    @Test
    public void testSimpleRules() throws Exception {
        // parent_of(tom, sam).
        // parent_of(sam, chris).
        // parent_of(sam, frank).
        // grand_parent_of(GP, C) :- father_of(GP, P), father_of(P, C).
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.registerRule(new Rule(new Predicate("parent_of", new Constant("tom"), new Constant("sam"))));
        knowledgeBase.registerRule(new Rule(new Predicate("parent_of", new Constant("sam"), new Constant("chris"))));
        knowledgeBase.registerRule(new Rule(new Predicate("parent_of", new Constant("sam"), new Constant("frank"))));
        knowledgeBase.registerRule(new Rule(new Predicate("grand_parent_of", new Variable("GP"), new Variable("C")),
                new Predicate("parent_of", new Variable("GP"), new Variable("P")),
                new Predicate("parent_of", new Variable("P"), new Variable("C"))));

        final List<String> result = new ArrayList<String>();

        Evaluator evaluator = new Evaluator(new RobinsonUnificationStrategy(), knowledgeBase);
        evaluator.prove(new EvaluationHandler() {
            public boolean foundResult(Map<Variable, Term> terms) {
                Constant constant = (Constant) terms.get(new Variable("GC"));
                result.add(constant.getName());

                return true;
            }
        }, new Predicate("grand_parent_of", new Constant("tom"), new Variable("GC")));

        assertEquals(2, result.size());
        assertTrue(result.contains("chris"));
        assertTrue(result.contains("frank"));
    }

    @Test
    public void testInfiniteRecursion() throws Exception {
        // father_of(GP, C) :- father_of(GP, P), father_of(P, C).
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.registerRule(new Rule(new Predicate("father_of", new Variable("GP"), new Variable("C")),
                new Predicate("father_of", new Variable("GP"), new Variable("P")), new Predicate("father_of", new Variable("P"), new Variable("C"))));

        try {
            Evaluator evaluator = new Evaluator(new RobinsonUnificationStrategy(), knowledgeBase);
            evaluator.prove(new EvaluationHandler() {
                public boolean foundResult(Map<Variable, Term> result) {
                    fail("This evaluation shouldn't find a result, but it has found '" + result + "'");
                    return false;
                }
            }, new Predicate("father_of", new Constant("john"), new Constant("richard")));

            fail("This evaluation should have provoked an exception as it's an infinite recursion.");
        } catch (InfiniteRecursionException ex) {
            // Expected exception
        }
    }

}
