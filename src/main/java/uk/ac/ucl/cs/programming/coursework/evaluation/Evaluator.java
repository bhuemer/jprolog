package uk.ac.ucl.cs.programming.coursework.evaluation;

import uk.ac.ucl.cs.programming.coursework.rules.Rule;
import uk.ac.ucl.cs.programming.coursework.rules.RuleResolver;
import uk.ac.ucl.cs.programming.coursework.rules.UnificationRuleMatcher;
import uk.ac.ucl.cs.programming.coursework.terms.Predicate;
import uk.ac.ucl.cs.programming.coursework.terms.Term;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;
import uk.ac.ucl.cs.programming.coursework.unification.UnificationPair;
import uk.ac.ucl.cs.programming.coursework.unification.UnificationStrategy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class Evaluator {

    private UnificationStrategy unifier;

    private RuleResolver ruleResolver;

    private AtomicInteger variableIdGenerator;

    // ------------------------------------------ Constructors

    public Evaluator(UnificationStrategy unifier, RuleResolver ruleResolver) {
        if (unifier == null) {
            throw new IllegalArgumentException(
                    "The given unifier must not be null.");
        }
        if (ruleResolver == null) {
            throw new IllegalArgumentException(
                    "The given rule resolver must not be null.");
        }

        this.unifier = unifier;
        this.ruleResolver = ruleResolver;
        this.variableIdGenerator = new AtomicInteger();
    }

    // ------------------------------------------ Public methods

    public boolean prove(EvaluationHandler handler, Predicate... query)
            throws EvaluationException {
        return prove(handler, Arrays.asList(query));
    }

    public boolean prove(EvaluationHandler handler, Collection<Predicate> query)
            throws EvaluationException {
        Map<Variable, Term> substitutions = new HashMap<Variable, Term>();
        for (Predicate predicate : query) {
            for (Term term : predicate.getArgs()) {
                if (term instanceof Variable) {
                    substitutions.put((Variable) term, term);
                }
            }
        }

        try {
            boolean shallContinue =
                    prove(handler, new LinkedList<Predicate>(query), substitutions, 0);

            // If the previous method returns true, this means that it didn't stop because
            // the user is satisfied with the results that he has been given, but rather
            // because this evaluator wasn't able to come up with another proof (or
            // it even could be that it isn't possible to prove this query at all).
            return !shallContinue;
        } catch (InfiniteRecursionException ex) {
            // Rethrow the same exception but with additional information about the parameters
            // that the user supplied. This wasn't possible at the place where this exception
            // was thrown originally as it's not possible to restore the original query.
            throw new InfiniteRecursionException(
                    "Couldn't evaluate the query '" + query + "' as an infinite recursion occurred.", ex);
        }
    }

    // ------------------------------------------ Utility methods

    private boolean prove(EvaluationHandler handler,
                        List<Predicate> query, Map<Variable, Term> substitutions, int depth)
            throws EvaluationException {
        // If it seems that the both the size of query and the depth of the call
        // stack just keep increasing, we're most probably stuck in an infinite
        // recursion.
        if (query.size() > 50 && depth > 50) {
            throw new InfiniteRecursionException();
        }

        if (query.isEmpty()) {
            return handler.foundResult(substitutions);
        } else {
            for (UnificationRuleMatcher.RuleMatch ruleMatch :
                    ruleResolver.findRules(new UnificationRuleMatcher(unifier, query.get(0)))) {
                // Wrap the current state in new collections so that we're able to roll back without
                // actually changing anything. That means, when it turns out that we can't prove a
                // certain rule, all we have to do is to walk up the call stack hierarchy, in order
                // to restore the previous state. This, however, also means, that every single frame
                // on this call stack must have a separate copy of those collections, a shallow copy
                // is more than enough though. There's no need to create a deep copy (term objects
                // are immutable anyway).
                List<Predicate> localQuery = new LinkedList<Predicate>(query);
                localQuery.remove(0); // Remove the first predicate, i.e. the one that we've resolved previously

                // .. same goes for substitutions later on when this method is called recursively again.

                // Note that another important thing to consider is that we have to rename certain
                // variables in the rule in order to avoid capturing them in an undesired way. 
                List<Predicate> conditions = new ArrayList<Predicate>(
                        renameVariables(ruleMatch.getRule()).getConditions());
                if (!conditions.isEmpty()) {
                    // Replace the current, previously fetched predicate with its conditions. That means
                    // I'll prepend these conditions to the query as that's the next thing this evaluator
                    // has to evaluate. Note that this replacement at the front of the query basically
                    // means that this evaluator tries to prove the given query using a depth-first
                    // search approach.
                    for (int i = 0; i < conditions.size(); ++i) {
                        localQuery.add(i, conditions.get(i));
                    }
                }

                // localQuery = substituteQuery(ruleMatch.getMgu(), localQuery);
                if (!prove(handler,
                        substituteQuery(ruleMatch.getMgu(), localQuery),
                        substituteResult(ruleMatch.getMgu(), new HashMap<Variable, Term>(substitutions)),
                            depth + 1)) {
                    return false;    
                }
            }

            return true;
        }
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>This method ensures that the identity of variables will be preserved if the
     * predicates are added to a query as a result of replacing another one. Otherwise
     * it could happen that variables are captured, like, for example if you have the
     * rule: "grand_parent_of(GP, GC) :- parent_of(GP, P), parent_of(P, GC)". and you
     * call it using the query "grand_parent_of(john, P)" you'd capture the variable
     * P even though the P within the rule is totally unrelated to the P of the query.
     * There are various other scenarios where something like that could happen so
     * the way to work around this issue is to rename variables and give them a unique
     * name internally. Note that it isn't sufficient if they've got unique names within
     * the knowledge base, as they even have to be unique amongst recursive calls
     * (which is why there is an additional parameter that indicates the depth).</p>
     *
     * @param rule the rule for which you want to rename the variables for
     *
     * @return the given rule after renaming the variables accordingly
     */
    private Rule renameVariables(Rule rule) {
        int uniqueVariableId = variableIdGenerator.incrementAndGet();

        Set<Variable> variables = new HashSet<Variable>();

        // At first I'll get a list of all variables that appear in the head of the rule.
        for (Term term : rule.getHead().getArgs()) {
            if (term instanceof Variable) {
                variables.add((Variable) term);
            }
        }

        List<Predicate> newConditions = new ArrayList<Predicate>(rule.getConditions().size());

        // The next step is to go through all conditions and see whether they use variables
        // that don't appear in the head of the rule. If that's the case, rename those
        // variables to ensure that they won't be captured later on.
        for (Predicate condition : rule.getConditions()) {
            for (Term term : condition.getArgs()) {
                if (term instanceof Variable && !variables.contains(term)) {
                    // If this is true, then we'll better rename this variable to some unique thing.
                    // In this case, I'll just prepend the prefix "__$$__" and a unique ID in front
                    // of the variable name.
                    Variable variable = (Variable) term;
                    condition = (Predicate) condition.substitute(
                            variable, new Variable("__$$__" +
                                    uniqueVariableId + "_" + variable.getName()));
                }
            }
            
            newConditions.add(condition);
        }

        return new Rule(rule.getHead(), newConditions);
    }

    private List<Predicate> substituteQuery(List<UnificationPair> mgu, List<Predicate> query) {
        ListIterator<Predicate> predicates = query.listIterator();
        while (predicates.hasNext()) {
            Predicate predicate = predicates.next();
            for (UnificationPair termPair : mgu) {
                predicate = (Predicate) predicate.substitute(
                        termPair.getLhs(), termPair.getRhs());
            }

            predicates.set(predicate);
        }

        return query;
    }

    private Map<Variable, Term> substituteResult(
            List<UnificationPair> mgu, Map<Variable, Term> substitutions) {
        for (UnificationPair termPair : mgu) {
            for (Map.Entry<Variable, Term> substitution : substitutions.entrySet()) {
                substitution.setValue(substitution.getValue().substitute(
                        termPair.getLhs(), termPair.getRhs()));
            }
        }

        return substitutions;
    }

}
