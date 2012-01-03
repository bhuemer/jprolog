package uk.ac.ucl.cs.programming.coursework.main;

import uk.ac.ucl.cs.programming.coursework.evaluation.EvaluationException;
import uk.ac.ucl.cs.programming.coursework.evaluation.Evaluator;
import uk.ac.ucl.cs.programming.coursework.evaluation.InfiniteRecursionException;
import uk.ac.ucl.cs.programming.coursework.parser.ParseException;
import uk.ac.ucl.cs.programming.coursework.parser.Parser;
import uk.ac.ucl.cs.programming.coursework.parser.ParserReader;
import uk.ac.ucl.cs.programming.coursework.rules.KnowledgeBase;
import uk.ac.ucl.cs.programming.coursework.unification.RobinsonUnificationStrategy;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *
 */
public class Application {

    private Parser parser = new Parser();

    private KnowledgeBase knowledgeBase = new KnowledgeBase();

    private InputStreamReader console = new InputStreamReader(System.in);

    public void run() {
        try {
            System.out.println("Prolog Interpreter 0.9: ");
            System.out.println(" Type 'exit.' to quit this application.");
            System.out.println(" Type '[...].' to load a file containing facts and rules.");
            System.out.println(" Otherwise just enter a Prolog query.");
            System.out.println(" (Note that you can load some default rules if you enter '[classpath:/family].')");
            System.out.print("> ");

            ParserReader reader = new ParserReader('.');
            reader.read(console, new ParserReader.ParserHandler() {
                public boolean handleContent(String content) throws IOException, ParseException {
                    content = content.trim();

                    if (content.equals("exit.")) {
                        return false;
                    } else if (content.startsWith("[") && content.endsWith("].")) {
                        String file = content.substring(1, content.length() - 2) + ".pl";
                        loadFile(file);
                    } else {
                        content = content.substring(0, content.indexOf('.'));
                        proveQuery(content);
                    }

                    System.out.print("> ");
                    return true;
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    // ---------------------------------------------------- Utility methods

    private void loadFile(String fileName) {
        try {
            KnowledgeBase newKnowledgeBase = new KnowledgeBase();

            Reader content = null;
            if (fileName.startsWith("classpath:")) {
                // Skip the "classpath:" prefix ..
                fileName = fileName.substring("classpath:".length());

                InputStream resource = getClass().getResourceAsStream(fileName);
                if (resource == null) {
                    System.out.println("Couldn't find the file '" + fileName + "' in the classpath.");
                    return;
                }

                // .. and load that file from the classpath.
                content = new InputStreamReader(resource);            
            } else {
                // Load the file from the file system.
                content = new FileReader(fileName);
            }
            
            parser.parse(newKnowledgeBase, content);

            System.out.println("Successfully loaded the file '" + fileName + "'.");
            knowledgeBase = newKnowledgeBase;
        } catch (IOException ex) {
            System.out.println("Couldn't load the file '" + fileName + "' ['"
                    + ex.getMessage() + "', absolute path = '" + new File(fileName).getAbsolutePath() + "')].");
        } catch (ParseException ex) {
            System.out.println("Couldn't parse the file '" + fileName + "' ['" + ex.getMessage() + "'].");
        }
    }

    private void proveQuery(String content) {
        try {
            Evaluator evaluator = new Evaluator(new RobinsonUnificationStrategy(), knowledgeBase);
            boolean result = evaluator.prove(
                    new ConsoleEvaluationHandler(System.out, console), parser.parsePredicates(content));
            if (!result) {
                System.out.println("false");
            }
        } catch (InfiniteRecursionException ex) {
            System.out.println("ERROR: Infinite recursion.");
        } catch (EvaluationException ex) {
            System.out.println("ERROR: General evaluation error. ['" + ex.getMessage() + "'].");
        } catch (ParseException ex) {
            System.out.println("ERROR: The given query is invalid ['" + ex.getMessage() + "'].");
        }
    }

    public static void main(String[] args) {
        new Application().run();
    }

}
