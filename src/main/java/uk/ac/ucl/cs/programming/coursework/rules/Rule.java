package uk.ac.ucl.cs.programming.coursework.rules;

import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Rule {

    /** The head of the rule, i.e. more or less the predicate this rule defines. */
    private Predicate head;

    /** Conditions for this rule, i.e. the list of predicates in the body of the rule */
    private List<Predicate> conditions;

    // ------------------------------------------ Constructor

    public Rule(Predicate head, Predicate... conditions) {
        this(head, Arrays.asList(conditions));
    }

    public Rule(Predicate head, List<Predicate> conditions) {
        if (head == null) {
            throw new IllegalArgumentException("The given head of this rule must not be null.");
        }

        this.head = head;
        this.conditions = conditions != null ? conditions : new ArrayList<Predicate>();
    }

    // ------------------------------------------ Public methods

    public Predicate getHead() {
        return head;
    }

    public List<Predicate> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public Rule substitute(Term term, Term replacement) {
        List<Predicate> substitutedConditions = new ArrayList<Predicate>(conditions.size());
        for (Predicate condition : conditions) {
            substitutedConditions.add((Predicate) condition.substitute(term, replacement));
        }

        return new Rule((Predicate) head.substitute(term, replacement), substitutedConditions);
    }

}
