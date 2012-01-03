package uk.ac.ucl.cs.programming.coursework.rules;

import uk.ac.ucl.cs.programming.coursework.support.AdaptionIterator;
import uk.ac.ucl.cs.programming.coursework.support.FilterIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class KnowledgeBase implements RuleRegistry, RuleResolver {

    /** The collection of known rules in this knowledge base. */
    private List<Rule> rules = new ArrayList<Rule>();

    // ------------------------------------------ RuleRegistry methods

    public synchronized void registerRule(Rule rule) {
        // This method creates a shallow copy of the previously registered
        // rules so that registration of new rules doesn't interfere with
        // any ongoing proves. Iteration over this collection would throw
        // a ConcurrentModificationException otherwise, if you register a
        // rule while someone else tries to find all applicable rules.
        // Note that no deep copy is required as rules are immutable anyway
        // and it's more the structural change of the collection that I'm
        // worried about rather than actually changing those rules. 
        rules = new ArrayList<Rule>(rules);
        rules.add(rule); // Add the new rule to the shallow copy
    }

    // ------------------------------------------ RuleResolver methods

    public <T> Iterable<T> findRules(final RuleMatcher<T> filter) {
        // This huge composition of various other objects basically just returns an iterator (actually an iterable
        // object that creates these iterators, but yeah) and this iterator iterates over all known rules. However,
        // only the ones are returned that are filtered by the given RuleMatcher object. If this filter returns
        // null instead of an object of type T, the NonNullFilter will simply ignore that rule then.
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new FilterIterator<T>(
                    new AdaptionIterator<Rule, T>(
                        rules.iterator(), new RuleAdaptor<T>(filter)), new FilterIterator.NonNullFilter<T>());
            }
        };
    }

    // ------------------------------------------ Private classes

    private class RuleAdaptor<T> implements AdaptionIterator.Adaptor<Rule, T> {

        private RuleMatcher<T> matcher;

        // ------------------------------------------------ Constructors

        public RuleAdaptor(RuleMatcher<T> matcher) {
            if (matcher == null) {
                throw new IllegalArgumentException(
                        "The given filter must not be null.");
            }

            this.matcher = matcher;
        }

        // ------------------------------------------------ Adaptor methods

        public T adapt(Rule rule) {
            return matcher.matchRule(rule);
        }

    }

}
