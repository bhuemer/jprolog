package uk.ac.ucl.cs.programming.coursework.terms;

import java.util.*;

/**
 * 
 */
public class Predicate implements Term {

    /** The name of this predicate, e.g. "sells" or "produces" */
    private String name;

    private List<Term> args;

    // ------------------------------------------ Constructor

    public Predicate(String name, Term... args) {
        this(name, Arrays.asList(args));
    }

    public Predicate(String name, List<Term> args) {
        if (name == null) {
            throw new IllegalArgumentException("The given predicate name must not be null.");
        }
        
        this.name = name;
        this.args = args;
    }

    // ------------------------------------------ Public methods

    public String getName() {
        return name;
    }

    public List<Term> getArgs() {
        return Collections.unmodifiableList(args);
    }

    // ------------------------------------------ Term methods

    public boolean occurs(Term term) {
        for (Term arg : args) {
            if (arg.equals(term)) {
                return true;
            }
        }

        return false;
    }

    public Term substitute(Term term, Term replacement) {
        // The only thing that you can substitute in a predicate is an argument,
        // so we just have to iterate over all arguments and propagate the
        // substitution request.
        List<Term> substitutedArgs = new ArrayList<Term>(args.size());
        for (Term arg : args) {
            substitutedArgs.add(arg.substitute(term, replacement));
        }

        return new Predicate(name, substitutedArgs);
    }

    // ------------------------------------------ Object methods

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Predicate predicate = (Predicate) obj;

        if (!getArgs().equals(predicate.getArgs())) return false;
        if (!getName().equals(predicate.getName())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getArgs().hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuffer predicate = new StringBuffer(getName()).append("(");

        Iterator args = getArgs().iterator();
        while (args.hasNext()) {
            predicate.append(args.next());

            if (args.hasNext()) {
                predicate.append(", ");
            }
        }

        predicate.append(")");

        return predicate.toString();
    }

}
