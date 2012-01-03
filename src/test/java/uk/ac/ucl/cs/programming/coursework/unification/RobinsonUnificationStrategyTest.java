package uk.ac.ucl.cs.programming.coursework.unification;

/**
 * <p>Test class for
 * <code>uk.ac.ucl.cs.programming.coursework.unification.RobinsonUnificationStrategy</code>.</p>
 */
public class RobinsonUnificationStrategyTest extends AbstractUnificationStrategyTest {

    // ------------------------------------------ Template methods

    @Override
    protected UnificationStrategy createUnificationStrategy() {
        return new RobinsonUnificationStrategy();
    }

}
