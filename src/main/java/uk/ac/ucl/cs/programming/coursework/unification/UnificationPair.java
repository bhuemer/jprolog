package uk.ac.ucl.cs.programming.coursework.unification;

import uk.ac.ucl.cs.programming.coursework.terms.Term;

/**
 *
 */
public class UnificationPair {

    private Term lhs;
    private Term rhs;

    public UnificationPair(Term lhs, Term rhs) {
        if (lhs == null || rhs == null) {
            throw new IllegalArgumentException(
                    "Both terms must not be null.");
        }

        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Term getLhs() {
        return lhs;
    }

    public Term getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        return String.format("UnificationPair[lhs='%s', rhs='%s']", lhs, rhs);
    }


}
