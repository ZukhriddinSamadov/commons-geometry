/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.geometry.euclidean.twod.hull;

import java.io.Serializable;

import org.apache.commons.geometry.core.partitioning.Region;
import org.apache.commons.geometry.core.partitioning.RegionFactory;
import org.apache.commons.geometry.euclidean.twod.Cartesian2D;
import org.apache.commons.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.geometry.euclidean.twod.Line;
import org.apache.commons.geometry.euclidean.twod.Segment;
import org.apache.commons.geometry.hull.ConvexHull;
import org.apache.commons.numbers.arrays.LinearCombination;
import org.apache.commons.numbers.core.Precision;

/**
 * This class represents a convex hull in an two-dimensional euclidean space.
 */
public class ConvexHull2D implements ConvexHull<Euclidean2D, Cartesian2D>, Serializable {

    /** Serializable UID. */
    private static final long serialVersionUID = 20140129L;

    /** Vertices of the hull. */
    private final Cartesian2D[] vertices;

    /** Tolerance threshold used during creation of the hull vertices. */
    private final double tolerance;

    /**
     * Line segments of the hull.
     * The array is not serialized and will be created from the vertices on first access.
     */
    private transient Segment[] lineSegments;

    /**
     * Simple constructor.
     * @param vertices the vertices of the convex hull, must be ordered
     * @param tolerance tolerance below which points are considered identical
     * @throws IllegalArgumentException if the vertices do not form a convex hull
     */
    public ConvexHull2D(final Cartesian2D[] vertices, final double tolerance)
        throws IllegalArgumentException {

        // assign tolerance as it will be used by the isConvex method
        this.tolerance = tolerance;

        if (!isConvex(vertices)) {
            throw new IllegalArgumentException("Vertices do not form a convex hull in CCW winding");
        }

        this.vertices = vertices.clone();
    }

    /**
     * Checks whether the given hull vertices form a convex hull.
     * @param hullVertices the hull vertices
     * @return {@code true} if the vertices form a convex hull, {@code false} otherwise
     */
    private boolean isConvex(final Cartesian2D[] hullVertices) {
        if (hullVertices.length < 3) {
            return true;
        }

        int sign = 0;
        for (int i = 0; i < hullVertices.length; i++) {
            final Cartesian2D p1 = hullVertices[i == 0 ? hullVertices.length - 1 : i - 1];
            final Cartesian2D p2 = hullVertices[i];
            final Cartesian2D p3 = hullVertices[i == hullVertices.length - 1 ? 0 : i + 1];

            final Cartesian2D d1 = p2.subtract(p1);
            final Cartesian2D d2 = p3.subtract(p2);

            final double crossProduct = LinearCombination.value(d1.getX(), d2.getY(), -d1.getY(), d2.getX());
            final int cmp = Precision.compareTo(crossProduct, 0.0, tolerance);
            // in case of collinear points the cross product will be zero
            if (cmp != 0.0) {
                if (sign != 0.0 && cmp != sign) {
                    return false;
                }
                sign = cmp;
            }
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian2D[] getVertices() {
        return vertices.clone();
    }

    /**
     * Get the line segments of the convex hull, ordered.
     * @return the line segments of the convex hull
     */
    public Segment[] getLineSegments() {
        return retrieveLineSegments().clone();
    }

    /**
     * Retrieve the line segments from the cached array or create them if needed.
     *
     * @return the array of line segments
     */
    private Segment[] retrieveLineSegments() {
        if (lineSegments == null) {
            // construct the line segments - handle special cases of 1 or 2 points
            final int size = vertices.length;
            if (size <= 1) {
                this.lineSegments = new Segment[0];
            } else if (size == 2) {
                this.lineSegments = new Segment[1];
                final Cartesian2D p1 = vertices[0];
                final Cartesian2D p2 = vertices[1];
                this.lineSegments[0] = new Segment(p1, p2, new Line(p1, p2, tolerance));
            } else {
                this.lineSegments = new Segment[size];
                Cartesian2D firstPoint = null;
                Cartesian2D lastPoint = null;
                int index = 0;
                for (Cartesian2D point : vertices) {
                    if (lastPoint == null) {
                        firstPoint = point;
                        lastPoint = point;
                    } else {
                        this.lineSegments[index++] =
                                new Segment(lastPoint, point, new Line(lastPoint, point, tolerance));
                        lastPoint = point;
                    }
                }
                this.lineSegments[index] =
                        new Segment(lastPoint, firstPoint, new Line(lastPoint, firstPoint, tolerance));
            }
        }
        return lineSegments;
    }

    /** {@inheritDoc} */
    @Override
    public Region<Euclidean2D> createRegion() throws IllegalStateException {
        if (vertices.length < 3) {
            throw new IllegalStateException("Region generation requires at least 3 vertices but found only " + vertices.length);
        }
        final RegionFactory<Euclidean2D> factory = new RegionFactory<>();
        final Segment[] segments = retrieveLineSegments();
        final Line[] lineArray = new Line[segments.length];
        for (int i = 0; i < segments.length; i++) {
            lineArray[i] = segments[i].getLine();
        }
        return factory.buildConvex(lineArray);
    }
}
