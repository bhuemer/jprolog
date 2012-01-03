package uk.ac.ucl.cs.programming.coursework.evaluation;

import uk.ac.ucl.cs.programming.coursework.terms.Term;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;

import java.util.Map;

/**
 *
 */
public interface EvaluationHandler {

    public boolean foundResult(Map<Variable, Term> result);

}
