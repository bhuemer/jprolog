package uk.ac.ucl.cs.programming.coursework.unification;

import org.junit.Test;
import uk.ac.ucl.cs.programming.coursework.terms.Constant;
import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p>Base test class for implementations of the interface 
 * <code>uk.ac.ucl.cs.programming.coursework.unification.UnificationStrategy</code>.</p>
 */
public abstract class AbstractUnificationStrategyTest {

    // ------------------------------------------ Test methods

    /**
     * <p>Tests the most basic case I can think of, you just try to unify a variable with a constant.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnifyVariableAndConstant() throws Exception {
        // X = y.
        // X = y
        // true

        UnificationStrategy unifier = createUnificationStrategy();
        List<UnificationPair> result =
                unifier.unify(new UnificationPair(new Variable("X"), new Constant("y")));

        assertEquals(1, result.size());
        assertEquals(new Constant("y"), result.get(0).getRhs());
    }

    /**
     * <p>Tests whether it is possible to unify two variables to the same constant, if you specify a
     * transitive dependency.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnifyTwoVariablesAndConstant() throws Exception {
        // X = Y, Y = y.
        // X = y,
        // Y = y

        UnificationStrategy unifier = createUnificationStrategy();
        List<UnificationPair> result = unifier.unify(
                new UnificationPair(new Variable("X"), new Variable("Y")),
                new UnificationPair(new Variable("Y"), new Constant("y")));

        assertEquals(2, result.size());
        assertEquals(new Constant("y"), result.get(0).getRhs());
        assertEquals(new Constant("y"), result.get(1).getRhs());
    }

    /**
     * <p>Tests whether the unification algorithm fails if we're trying to unify a variable with
     * two different constants.</p>
     * 
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnifyVariableWithDifferentConstants() throws Exception {
        // X = x, X = y.
        // .. false

        UnificationStrategy unifier = createUnificationStrategy();
        List<UnificationPair> result = unifier.unify(
                new UnificationPair(new Variable("X"), new Constant("x")),
                new UnificationPair(new Variable("X"), new Constant("y")));

        assertNull(result);
    }

    /**
     * <p>Tests whether unification succeeds if we're dealing with three different variables, even
     * though we've only got two pairs of terms, which means, one variable remains unbound.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnifyThreeDifferentVariables() throws Exception {
        // X = Y, Y = Z.
        // X = Z,
        // Y = Z

        UnificationStrategy unifier = createUnificationStrategy();
        List<UnificationPair> result = unifier.unify(
                new UnificationPair(new Variable("X"), new Variable("Y")),
                new UnificationPair(new Variable("Y"), new Variable("Z")));

        assertEquals(2, result.size());
        assertEquals(new Variable("Z"), result.get(0).getRhs());
        assertEquals(new Variable("Z"), result.get(1).getRhs());
    }

    /**
     * <p>Tests whether it is possible to unify two predicates that involve some variables
     * and nested predicates.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnifyPredicates() throws Exception {
        // p(X,Y,h(Y)) = p(Z,a,Z).
        // X = h(a),
        // Y = a,
        // Z = h(a)
        // true

        UnificationStrategy unifier = createUnificationStrategy();
        List<UnificationPair> result =
            unifier.unify(new UnificationPair(
                new Predicate("p",
                    new Variable("X"), new Variable("Y"), new Predicate("h", new Variable("Y"))),
                new Predicate("p",
                    new Variable("Z"), new Constant("a"), new Variable("Z"))));

        Predicate ha = new Predicate("h", new Constant("a"));
        assertEquals(3, result.size());
        assertEquals(ha, result.get(0).getRhs());
        assertEquals(new Constant("a"), result.get(1).getRhs());
        assertEquals(ha, result.get(2).getRhs());
    }

    /**
     * <p>Tests whether it is possible to unify two predicates that shares some variables
     * with another pair of terms.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnifyPredicatesAndVariables() throws Exception {
        // a(b,X) = a(b,c(Y,d)), Y = e.
        // X = c(e, d),
        // Y = e

        UnificationStrategy unifier = createUnificationStrategy();
        List<UnificationPair> result =
            unifier.unify(new UnificationPair(
                new Predicate("a", new Constant("b"), new Variable("X")),
                new Predicate("a", new Constant("b"), new Predicate("c",
                        new Variable("Y"), new Constant("d")))),
                          new UnificationPair(
                new Variable("Y"), new Constant("e")));

        assertEquals(2, result.size());
        assertEquals(new Constant("e"), result.get(0).getRhs());
        assertEquals(new Predicate("c", new Constant("e"), new Constant("d")), result.get(1).getRhs());
    }

    /**
     * <p>Tests whether it is possible to unify a variable with something where that variable occurs
     * again, i.e. if it's possible to unify cyclic terms, which shouldn't be the case.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnifyCyclicTerms() throws Exception {
        // X = a(b, X)
        // ... false

        UnificationStrategy unifier = createUnificationStrategy();
        List<UnificationPair> result = unifier.unify(new UnificationPair(
                new Variable("X"), new Predicate("a", new Constant("b"), new Variable("X"))));

        assertNull("It shouldn't be possible to unify a cyclic term.", result);
    }

    // ------------------------------------------ Template methods

    protected abstract UnificationStrategy createUnificationStrategy();
}
