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
package org.apache.commons.geometry.hull;

import java.util.Collection;

import org.apache.commons.geometry.core.ConvexHull;
import org.apache.commons.geometry.core.Point;

/**
 * Interface for convex hull generators.
 *
 * @param <P> Type of the {@link Point}
 *
 * @see <a href="http://en.wikipedia.org/wiki/Convex_hull">Convex Hull (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/ConvexHull.html">Convex Hull (MathWorld)</a>
 */
public interface ConvexHullGenerator<P extends Point<P>> {
    /**
     * Build a convex hull from the set of input points.
     *
     * @param points the set of input points
     * @return the convex hull
     * @throws IllegalStateException if generator fails to generate a convex hull for
     *      the given set of input points
     */
    ConvexHull<P> generate(Collection<P> points);
}
