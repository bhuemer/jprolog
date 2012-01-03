package uk.ac.ucl.cs.programming.coursework.rules;

import org.junit.Test;
import uk.ac.ucl.cs.programming.coursework.terms.Constant;
import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Term;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;
import uk.ac.ucl.cs.programming.coursework.unification.RobinsonUnificationStrategy;
import uk.ac.ucl.cs.programming.coursework.unification.UnificationPair;
import uk.ac.ucl.cs.programming.coursework.unification.UnificationStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class KnowledgeBaseTest {

    @Test
    public void testResolveSimpleFacts() throws Exception {
        final UnificationStrategy unifier = new RobinsonUnificationStrategy();

        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.registerRule(new Rule(new Predicate("father_of", new Constant("john"), new Constant("mark"))));
        knowledgeBase.registerRule(new Rule(new Predicate("father_of", new Constant("john"), new Constant("tim"))));
        knowledgeBase.registerRule(new Rule(new Predicate("father_of", new Constant("john"), new Constant("laura"))));
        knowledgeBase.registerRule(new Rule(new Predicate("father_of", new Constant("frank"), new Constant("richard"))));

        // The following code basically resolves the query "father_of(john, Child)" ..
        Iterator<Term> children = knowledgeBase.findRules(new RuleMatcher<Term>() {
                public Term matchRule(Rule rule) {
                    List<UnificationPair> result = unifier.unify(
                            new UnificationPair(
                                    new Predicate("father_of", new Constant("john"), new Variable("Child")),
                                        rule.getHead()));
                    if (result == null) {
                        return null;
                    } else {
                        return result.get(0).getRhs();
                    }
                }
            }
        ).iterator();

        List<Term> foundChildren = new ArrayList<Term>();
        while (children.hasNext()) {
            foundChildren.add(children.next());
        }

        // .. which should return three values for the variable Child.
        assertEquals(3, foundChildren.size());
        assertTrue(foundChildren.contains(new Constant("mark")));
        assertTrue(foundChildren.contains(new Constant("tim")));
        assertTrue(foundChildren.contains(new Constant("laura")));
    }

}
