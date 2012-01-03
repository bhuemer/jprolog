package uk.ac.ucl.cs.programming.coursework.evaluation;

/**
 *
 */
public class InfiniteRecursionException extends EvaluationException {

    public InfiniteRecursionException() {
    }

    public InfiniteRecursionException(String message) {
        super(message);
    }

    public InfiniteRecursionException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
