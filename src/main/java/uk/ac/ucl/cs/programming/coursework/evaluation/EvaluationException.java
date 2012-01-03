package uk.ac.ucl.cs.programming.coursework.evaluation;

/**
 *
 */
public abstract class EvaluationException extends Exception {

    public EvaluationException() {
        
    }

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

}
