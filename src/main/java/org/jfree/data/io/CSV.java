/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2016, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * --------
 * CSV.java
 * --------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 24-Nov-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.io;

import org.checkerframework.common.value.qual.*;
import org.checkerframework.checker.index.qual.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * A utility class for reading {@link CategoryDataset} data from a CSV file.
 * This initial version is very basic, and won't handle errors in the data
 * file very gracefully.
 */
public class CSV {

    /** The field delimiter. */
    private char fieldDelimiter;

    /** The text delimiter. */
    private char textDelimiter;

    /**
     * Creates a new CSV reader where the field delimiter is a comma, and the
     * text delimiter is a double-quote.
     */
    public CSV() {
        this(',', '"');
    }

    /**
     * Creates a new reader with the specified field and text delimiters.
     *
     * @param fieldDelimiter  the field delimiter (usually a comma, semi-colon,
     *                        colon, tab or space).
     * @param textDelimiter  the text delimiter (usually a single or double
     *                       quote).
     */
    public CSV(char fieldDelimiter, char textDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
        this.textDelimiter = textDelimiter;
    }

    /**
     * Reads a {@link CategoryDataset} from a CSV file or input source.
     *
     * @param in  the input source.
     *
     * @return A category dataset.
     *
     * @throws IOException if there is an I/O problem.
     */
    public CategoryDataset readCategoryDataset(Reader in) throws IOException {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        BufferedReader reader = new BufferedReader(in);
        List columnKeys = null;
        int lineIndex = 0;
        String line = reader.readLine();
        while (line != null) {
            if (lineIndex == 0) {  // first line contains column keys
                columnKeys = extractColumnKeys(line);
            }
            else {  // remaining lines contain a row key and data values
                extractRowKeyAndData(line, dataset, columnKeys);
            }
            line = reader.readLine();
            lineIndex++;
        }
        return dataset;

    }

    /**
     * Extracts the column keys from a string.
     *
     * @param line  a line from the input file.
     *
     * @return A list of column keys.
     */
    private List extractColumnKeys(String line) {
        List keys = new java.util.ArrayList();
        int fieldIndex = 0;
        int start = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == this.fieldDelimiter) {
                if (fieldIndex > 0) {  // first field is ignored, since
                                       // column 0 is for row keys
                    @SuppressWarnings("index") // https://github.com/kelloggm/checker-framework/issues/138
                    String key = line.substring(start, i);
                    keys.add(removeStringDelimiters(key));
                }
                start = i + 1;
                fieldIndex++;
            }
        }
        @SuppressWarnings("index") // https://github.com/kelloggm/checker-framework/issues/219: the for loop above cannot increment start more than line.length times, so start is IOH of line
        String key = line.substring(start, line.length());
        keys.add(removeStringDelimiters(key));
        return keys;
    }

    /**
     * Extracts the row key and data for a single line from the input source.
     *
     * @param line  the line from the input source.
     * @param dataset  the dataset to be populated.
     * @param columnKeys  the column keys.
     */
    private void extractRowKeyAndData(String line,
                                      DefaultCategoryDataset dataset,
                                      List columnKeys) {
        Comparable rowKey = null;
        int fieldIndex = 0;
        @IndexOrHigh("line") int start = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == this.fieldDelimiter) {
                if (fieldIndex == 0) {  // first field contains the row key
                            String key = line.substring(start, i);
                    rowKey = removeStringDelimiters(key);
                }
                else {  // remaining fields contain values
                    Double value = Double.valueOf(
                        removeStringDelimiters(line.substring(start, i))
                    );
                    dataset.addValue(
                        value, rowKey,
                        (Comparable) columnKeys.get(fieldIndex - 1)
                    );
                }
                @SuppressWarnings("index") // line is CSV formatted, so skipping is okay here
                @IndexOrHigh("line") int skipIndex = i + 1;
                start = skipIndex;
                fieldIndex++;
            }
        }

        @SuppressWarnings("index") // CSV format guarantees at least one field
        @Positive int fieldIndexTmp = fieldIndex;
        fieldIndex = fieldIndexTmp;

        Double value = Double.valueOf(
            removeStringDelimiters(line.substring(start, line.length()))
        );
        dataset.addValue(
            value, rowKey, (Comparable) columnKeys.get(fieldIndex - 1)
        );
    }

    /**
     * Removes the string delimiters from a key (as well as any white space
     * outside the delimiters).
     *
     * @param key  the key (including delimiters).
     *
     * @return The key without delimiters.
     */
    @SuppressWarnings({"index", "value"}) // I manually audited this. It relies on several properties of the passed String that I can't express: that removing whitespace leaves a MinLen(1) string, that if the first character of the trimmed string is a delimiter, there is another character, that if the last character is a delimiter, it isn't the only character
    private String removeStringDelimiters(String key) {
        String k = key.trim();
        if (k.charAt(0) == this.textDelimiter) {
            k = k.substring(1);
        }
        if (k.charAt(k.length() - 1) == this.textDelimiter) {
            k = k.substring(0, k.length() - 1);
        }
        return k;
    }

}
