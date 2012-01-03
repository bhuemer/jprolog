package uk.ac.ucl.cs.programming.coursework.main;

import uk.ac.ucl.cs.programming.coursework.evaluation.EvaluationHandler;
import uk.ac.ucl.cs.programming.coursework.terms.Term;
import uk.ac.ucl.cs.programming.coursework.terms.Variable;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class ConsoleEvaluationHandler implements EvaluationHandler {

    private PrintStream output;

    private Reader console;

    // ------------------------------------------ Constructors

    public ConsoleEvaluationHandler(PrintStream output, Reader console) {
        if (output == null) {
            throw new IllegalArgumentException("The given output stream must not be null.");
        }
        if (console == null) {
            throw new IllegalArgumentException("The given console must not be null.");
        }

        this.output = output;
        this.console = console;
    }

    // ------------------------------------------ EvaluationHandler methods

    public boolean foundResult(Map<Variable, Term> result) {
        // Print the whole combination of variables that satisfies the evaluated query.
        Iterator<Map.Entry<Variable, Term>> bindings = result.entrySet().iterator();
        while (bindings.hasNext()) {
            Map.Entry<Variable, Term> binding = bindings.next();

            output.printf("%s = %s", binding.getKey().getName(), binding.getValue());
            if (bindings.hasNext()) {
                output.println();
            }
        }

        if (result.isEmpty()) {
            System.out.println("true");

            // In this case, it doesn't make sense to continue.
            return false;
        } else {
            // .. and afterwards ask the user whether he wants to see another proof.
            char c = 0;
            while (c != ';' && c != ',') {
                try {
                    c = (char) console.read();
                } catch (IOException ex) {
                    System.out.println("An I/O error occurred, please repeat.");
                }
            }

            // If the user enters ',', the evaluator looks for another way to prove the query.
            return c == ',';
        }
    }

}
