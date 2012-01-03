package uk.ac.ucl.cs.programming.coursework.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 */
public class ParserReader {

    private char separator;

    // ------------------------------------------ Constructors

    public ParserReader(char separator) {
        this.separator = separator;
    }

    // ------------------------------------------ Public methods

    public void read(Reader reader, ParserHandler handler) throws IOException, ParseException {
        StringBuffer buffer = new StringBuffer();
        int c = reader.read();
        while (c != -1) {
            buffer.append((char) c);

            if (c == separator) {
                boolean shallContinue =
                        handler.handleContent(buffer.toString());
                if (!shallContinue) {
                    return;
                }

                buffer = new StringBuffer();
            }

            // Read the next character
            c = reader.read();
        }
    }

    // ------------------------------------------ Public callback interfaces

    public interface ParserHandler {

        public boolean handleContent(String content) throws IOException, ParseException;

    }

}
