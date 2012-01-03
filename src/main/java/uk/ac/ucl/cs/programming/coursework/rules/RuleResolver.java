package uk.ac.ucl.cs.programming.coursework.rules;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public interface RuleResolver {

    /**
     * <p>Finds all applicable rules, note that the given filter determines
     * which rules actually are applicable. This method is guaranteed to
     * return a non-null object, however, the returned object might have
     * no next element at all, if there aren't any applicable rules.</p>
     *
     * @param filter the matcher that determines which rules are applicable
     * 
     * @return an iterator object that iterates over all applicable rules
     */
    public <T> Iterable<T> findRules(RuleMatcher<T> filter);

}
