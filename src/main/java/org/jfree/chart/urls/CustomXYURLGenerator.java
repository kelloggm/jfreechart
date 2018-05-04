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
 * -------------------------
 * CustomXYURLGenerator.java
 * -------------------------
 * (C) Copyright 2002-2008, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Mar-2003 : Implemented Serializable (DG);
 * 20-Jan-2005 : Minor Javadoc update (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 02-Feb-2007 : Removed author tags from all over JFreeChart sources (DG);
 * 11-Apr-2008 : Implemented Cloneable, otherwise charts using this URL
 *               generator will fail to clone (DG);
 *
 */

package org.jfree.chart.urls;

import org.checkerframework.checker.index.qual.NonNegative;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.PublicCloneable;

import org.jfree.data.xy.XYDataset;

/**
 * A custom URL generator.
 */
public class CustomXYURLGenerator implements XYURLGenerator, Cloneable,
        PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -8565933356596551832L;

    /** Storage for the URLs. */
    private ArrayList urlSeries = new ArrayList();

    /**
     * Default constructor.
     */
    public CustomXYURLGenerator() {
        super();
    }

    /**
     * Returns the number of URL lists stored by the renderer.
     *
     * @return The list count.
     */
    public @NonNegative int getListCount() {
        return this.urlSeries.size();
    }

    /**
     * Returns the number of URLs in a given list.
     *
     * @param list  the list index (zero based).
     *
     * @return The URL count.
     */
    public int getURLCount(@NonNegative int list) {
        int result = 0;
        List urls = (List) this.urlSeries.get(list);
        if (urls != null) {
            result = urls.size();
        }
        return result;
    }

    /**
     * Returns the URL for an item.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The URL (possibly {@code null}).
     */
    public String getURL(@NonNegative int series, @NonNegative int item) {
        String result = null;
        if (series < getListCount()) {
            List urls = (List) this.urlSeries.get(series);
            if (urls != null) {
                if (item < urls.size()) {
                    result = (String) urls.get(item);
                }
            }
        }
        return result;
    }

    /**
     * Generates a URL.
     *
     * @param dataset  the dataset.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return A string containing the URL (possibly {@code null}).
     */
    @Override
    public String generateURL(XYDataset dataset, @NonNegative int series, @NonNegative int item) {
        return getURL(series, item);
    }

    /**
     * Adds a list of URLs.
     *
     * @param urls  the list of URLs ({@code null} permitted, the list
     *     is copied).
     */
    public void addURLSeries(List urls) {
        List listToAdd = null;
        if (urls != null) {
            listToAdd = new java.util.ArrayList(urls);
        }
        this.urlSeries.add(listToAdd);
    }

    /**
     * Tests this generator for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CustomXYURLGenerator)) {
            return false;
        }
        CustomXYURLGenerator that = (CustomXYURLGenerator) obj;
        int listCount = getListCount();
        if (listCount != that.getListCount()) {
            return false;
        }

        for (int series = 0; series < listCount; series++) {
            int urlCount = getURLCount(series);
            if (urlCount != that.getURLCount(series)) {
                return false;
            }

            for (int item = 0; item < urlCount; item++) {
                String u1 = getURL(series, item);
                String u2 = that.getURL(series, item);
                if (u1 != null) {
                    if (!u1.equals(u2)) {
                        return false;
                    }
                }
                else {
                    if (u2 != null) {
                        return false;
                    }
                }
            }
        }
        return true;

    }

    /**
     * Returns a new generator that is a copy of, and independent from, this
     * generator.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem with cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        CustomXYURLGenerator clone = (CustomXYURLGenerator) super.clone();
        clone.urlSeries = new java.util.ArrayList(this.urlSeries);
        return clone;
    }

}
