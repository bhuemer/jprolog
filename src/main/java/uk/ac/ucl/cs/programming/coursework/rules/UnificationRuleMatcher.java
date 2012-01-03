package uk.ac.ucl.cs.programming.coursework.rules;

import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.unification.UnificationPair;
import uk.ac.ucl.cs.programming.coursework.unification.UnificationStrategy;

import java.util.List;

/**
 *
 */
public class UnificationRuleMatcher implements RuleMatcher<UnificationRuleMatcher.RuleMatch> {

    private UnificationStrategy unifier;

    private Predicate target;

    // ------------------------------------------ Constructors

    public UnificationRuleMatcher(UnificationStrategy unifier, Predicate target) {
        if (unifier == null) {
            throw new IllegalArgumentException(
                    "The given unification strategy must not be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException(
                    "The given target predicate must not be null.");
        }

        this.unifier = unifier;
        this.target = target;
    }

    // ------------------------------------------ RuleMatcher methods

    public RuleMatch matchRule(Rule rule) {
        List<UnificationPair> mgu =
                unifier.unify(new UnificationPair(rule.getHead(), target));
        
        // If there exists a most general unifier, then we have found a suitable rule
        if (mgu != null) {
            return new RuleMatch(rule, mgu);
        } else {
            return null;
        }
    }

    // ------------------------------------------ Utility classes

    /**
     *
     *
     */
    public static class RuleMatch {

        private Rule rule;

        private List<UnificationPair> mgu;

        public RuleMatch(Rule rule, List<UnificationPair> mgu) {
            this.rule = rule;
            this.mgu = mgu;
        }

        public Rule getRule() {
            return rule;
        }

        public List<UnificationPair> getMgu() {
            return mgu;
        }

    }
    
}
