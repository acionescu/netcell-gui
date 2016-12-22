/**
 * netcell-gui - A Swing GUI for netcell ESB
 * Copyright (C) 2009  Adrian Cristian Ionescu - https://github.com/acionescu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.segoia.math;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.segoia.netcell.gui.components.Anchor;
import net.segoia.netcell.gui.components.GraphFigure;

public class GraphUtil {

    public static double getShortestPath(Point2D source, GraphFigure destGraph, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {
	return getShortestPath(source, destGraph.getAvailableAnchors(), obstacles, minDistToObstacle, path);

    }

    public static double getShortestPath(List<Point2D> sources, GraphFigure destGraph,
	    Collection<Rectangle2D> obstacles, double minDistToObstacle, List<Point2D> path) {
	List<Point2D> minPath = null;
	List<Anchor> anchors = destGraph.getAvailableAnchors();
	double minDist = Double.MAX_VALUE;
	for (Point2D source : sources) {
	    List<Point2D> newPath = new ArrayList<Point2D>(path);
	    double dist = getShortestPath(source, anchors, obstacles, minDistToObstacle, newPath);

	    if (dist >= 0) {
		if (minPath == null || newPath.size() < minPath.size()
			|| (newPath.size() == minPath.size() && dist < minDist)) {
		    minPath = newPath;
		    minDist = dist;
		}
	    }
	}

	if (minPath != null) {
	    path.clear();
	    path.addAll(minPath);
	    return minDist;
	}
	return -1;

    }

    public static double getShortestPath(Point2D source, List<Anchor> anchors, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {

	List<Point2D> minPath = null;
	double minDist = Double.MAX_VALUE;
	Anchor winningAnchor = null;
	for (Anchor anchor : anchors) {
	    List<Point2D> newPath = new ArrayList<Point2D>(path);
	    double dist = getShortestPath(source, anchor, obstacles, minDistToObstacle, newPath);

	    if (dist >= 0) {
		if (minPath == null || newPath.size() < minPath.size()
			|| (newPath.size() == minPath.size() && dist < minDist)) {
		    // System.out.println(minPath+" lost to "+newPath+" with "+minDist+" to "+dist);
		    minPath = newPath;
		    minDist = dist;
		    winningAnchor = anchor;
		}
	    }
	}

	if (minPath != null) {
	    path.clear();
	    path.addAll(minPath);
	    path.add(winningAnchor.getRoot());
	    return minDist;
	}
	return -1;
    }

    public static double getShortestPath(List<Point2D> sources, List<Anchor> anchors, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {

	List<Point2D> minPath = null;
	double minDist = Double.MAX_VALUE;
	for (Point2D source : sources) {
	    for (Anchor anchor : anchors) {
		List<Point2D> newPath = new ArrayList<Point2D>(path);
		double dist = getShortestPath(source, anchor, obstacles, minDistToObstacle, newPath);

		if (dist >= 0) {
		    if (minPath == null || newPath.size() < minPath.size()
			    || (newPath.size() == minPath.size() && dist < minDist)) {
			minPath = newPath;
			minDist = dist;
		    }
		}
	    }
	}
	if (minPath != null) {
	    path.clear();
	    path.addAll(minPath);
	    return minDist;
	}
	return -1;
    }

    public static double getShortestPath(List<Point2D> sources, Anchor anchor, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {

	List<Point2D> minPath = null;
	double minDist = Double.MAX_VALUE;
	for (Point2D source : sources) {
	    List<Point2D> newPath = new ArrayList<Point2D>(path);
	    double dist = getShortestPath(source, anchor, obstacles, minDistToObstacle, newPath);

	    if (dist >= 0) {
		if (minPath == null || newPath.size() < minPath.size()
			|| (newPath.size() == minPath.size() && dist < minDist)) {
		    minPath = newPath;
		    minDist = dist;
		}
	    }
	}

	if (minPath != null) {
	    path.clear();
	    path.addAll(minPath);
	    return minDist;
	}
	return -1;
    }

    public static double getShortestPath(Point2D source, Anchor anchor, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {

	Point2D dest = anchor.getLocation();

	if (path.contains(source) || path.contains(dest)) {
	    // throw new RuntimeException("Point already exists!!! -> "+source + " :
	    // "+path.indexOf(source)+"/"+path.size());
	    return -1;
	}

	double dx = dest.getX();
	double dy = dest.getY();
	double sx = source.getX();
	double sy = source.getY();

	// System.out.println(source +" -> "+dest);
	if (dx != sx && dy != sy) {
	    // List<Point2D> newSources = new ArrayList<Point2D>();

	    Point2D destNormalPoint = anchor.getNormalPoint(source);
	    // Point2D destNormalPoint = anchor.getNormalPoint(minDistToObstacle);

	    Point2D sourceNormalPoint;

	    boolean destNormalHoriz = false;

	    if (destNormalPoint.getY() == source.getY()) {
		sourceNormalPoint = new Point2D.Double((source.getX()+dest.getX())/2, dest.getY());
		destNormalHoriz = true;
	    } else {
		sourceNormalPoint = new Point2D.Double(dest.getX(), source.getY());
	    }

	    List<Point2D> dPath = new ArrayList<Point2D>(path);
	    double ddist = MathUtil.getShortestPath2(source, destNormalPoint, obstacles, minDistToObstacle, dPath);

	    List<Point2D> sPath = new ArrayList<Point2D>(path);
	    double sdist = MathUtil.getShortestPath2(source, sourceNormalPoint, obstacles, minDistToObstacle, sPath);

	    if (ddist >= 0) {
		dPath.remove(destNormalPoint);
		
		
		Point2D dPrev = dPath.get(dPath.size() - 1);
//
		if (!destNormalHoriz) {
		    if (dPrev.getY() == destNormalPoint.getY()) {
			destNormalPoint = dPrev;
			dPath.remove(destNormalPoint);
//			System.out.println(destNormalPoint+" -> "+ dPath+" -> "+dest);
			
		    }
		} else {
		    if (dPrev.getX() == destNormalPoint.getX()) {
			destNormalPoint = dPrev;
		    }
		}

		double dl = MathUtil.getShortestPath2(destNormalPoint, dest, obstacles, minDistToObstacle, dPath);
		if (dl >= 0) {
//		    System.out.println(destNormalPoint+" -> "+dPath);
		    ddist += dl;
		} else {
		    ddist = -1;
		}
	    }

	    if (sdist >= 0) {
		sPath.remove(sourceNormalPoint);
		
		Point2D sPrev = sPath.get(sPath.size() - 1);

//		if (destNormalHoriz) {
//		    if (sPrev.getX() == sourceNormalPoint.getX()) {
//			sourceNormalPoint = sPrev;
//		    }
//		} else {
//		    if (sPrev.getY() == sourceNormalPoint.getY()) {
//			sourceNormalPoint = sPrev;
//		    }
//		}
		double sl = MathUtil.getShortestPath2(sourceNormalPoint, dest, obstacles, minDistToObstacle, sPath);
		if (sl >= 0) {
		    sdist += sl;
		} else {
		    sdist = -1;
		}
	    }

	    if (ddist >= 0 && sdist >= 0) {
		int dPathSize = dPath.size();
		int sPathSize = sPath.size();
		if (dPathSize < sPathSize || (dPathSize == sPathSize && ddist < sdist)) {
		    path.clear();
		    path.addAll(dPath);
		    return ddist;
		} else {
		    path.clear();
		    path.addAll(sPath);
		    return sdist;
		}
	    } else if (ddist >= 0) {
		path.clear();
		path.addAll(dPath);
		return ddist;
	    } else if (sdist >= 0) {
		path.clear();
		path.addAll(sPath);
		return sdist;
	    }

	    return -1;

	}
	path.add(source);
	Line2D directLine = new Line2D.Double(source, dest);

	for (Rectangle2D obstacle : obstacles) {

	    if (obstacle.intersectsLine(
		    directLine)/* && !checkOnEdge(obstacle, source) && !checkOnEdge(obstacle, dest) */) {

		List<Point2D> newSources = new ArrayList<Point2D>();
		Point2D newPoint = null;

		/* vertical */
		if (sx == dx) {
		    /* right */
		    newPoint = new Point2D.Double(obstacle.getMaxX() + minDistToObstacle, sy);
		    if (!path.contains(newPoint)
			    && !MathUtil.checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
			newSources.add(newPoint);
		    }

		    /* left */
		    newPoint = new Point2D.Double(obstacle.getMinX() - minDistToObstacle, sy);
		    if (!path.contains(newPoint)
			    && !MathUtil.checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
			newSources.add(newPoint);
		    }
		}
		/* horizontal */
		else {
		    /* bottom */
		    newPoint = new Point2D.Double(sx, obstacle.getMaxY() + minDistToObstacle);
		    if (!path.contains(newPoint)
			    && !MathUtil.checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
			newSources.add(newPoint);
		    }

		    /* up */
		    newPoint = new Point2D.Double(sx, obstacle.getMinY() - minDistToObstacle);
		    if (!path.contains(newPoint)
			    && !MathUtil.checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
			newSources.add(newPoint);
		    }
		}

		List<Point2D> tPath = new ArrayList<Point2D>(path);

		int nextIndex = tPath.size();
		double dist = getShortestPath(newSources, anchor, obstacles, minDistToObstacle, tPath);
		if (dist >= 0) {
		    if (nextIndex > 2) {
			System.out.println("removing " + source);
			tPath.remove(source);
		    }

		    path.clear();
		    path.addAll(tPath);
		    return (dist + source.distance(path.get(nextIndex)));
		}
		return dist;
	    }
	}

	path.add(dest);
	return source.distance(dest);
    }

}
