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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import net.segoia.netcell.gui.components.JBrokenArrow;

public class MathUtil {

    public static Line2D minDist(List<Point2D> sourcePoints, List<Point2D> destPoints) {
	if (sourcePoints == null || destPoints == null || sourcePoints.size() == 0 || destPoints.size() == 0) {
	    throw new IllegalArgumentException("Some source and destination points must be specified.");
	}
	Line2D.Float minSegment = new Line2D.Float(sourcePoints.get(0), destPoints.get(0));
	double minDist = sourcePoints.get(0).distance(destPoints.get(0));
	for (Point2D sourcePoint : sourcePoints) {
	    for (Point2D destPoint : destPoints) {
		double dist = sourcePoint.distance(destPoint);
		if (dist < minDist) {
		    minDist = dist;
		    minSegment = new Line2D.Float(sourcePoint, destPoint);
		}
	    }
	}
	return minSegment;
    }

    public static List<Point2D> getMiddleEdges(Rectangle2D rectangle) {
	List<Point2D> middles = new ArrayList<Point2D>(4);

	middles.add(new Point2D.Double(rectangle.getMinX(), rectangle.getCenterY()));
	middles.add(new Point2D.Double(rectangle.getCenterX(), rectangle.getMaxY()));
	middles.add(new Point2D.Double(rectangle.getMaxX(), rectangle.getCenterY()));
	middles.add(new Point2D.Double(rectangle.getCenterX(), rectangle.getMinY()));
	return middles;
    }

    public static boolean checkOnEdge(Rectangle2D r, Point2D p) {
	return (p.getY() == r.getMaxY() || p.getY() == r.getMinY() || p.getX() == r.getMaxX() || p.getX() == r
		.getMinX());
    }

    public static double getShortestPath(Point2D source, Point2D dest, List<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {
	Line2D directLine = new Line2D.Double(source, dest);

	path.add(source);
	double dx = dest.getX();
	double dy = dest.getY();
	double sx = source.getX();
	double sy = source.getY();
	/* check if we cross the obstacles */
	for (Rectangle2D obstacle : obstacles) {
	    /* check if the source point is contained in an obstacle */
	    // if(obstacle.contains(source) || obstacle.contains(dest)){
	    // return null;
	    // }
	    if (obstacle.intersectsLine(directLine) && !checkOnEdge(obstacle, source) && !checkOnEdge(obstacle, dest)) {
		List<Point2D> l1 = null;
		List<Point2D> l2 = null;
		Point2D newPoint = null;
		double dist1 = 0;
		double dist2 = 0;

		if (dx > sx) {
		    newPoint = new Point2D.Double(obstacle.getMaxX() + minDistToObstacle, sy);
		    dist1 = source.distance(newPoint);
		    if (!path.contains(newPoint)) {
			l1 = new ArrayList<Point2D>(path);
			dist1 += getShortestPath(newPoint, dest, obstacles, minDistToObstacle, l1);
		    }
		}

		// newPoint = new Point2D.Double(obstacle.getMinX() - minDistToObstacle, sy);
		// double dist2 = source.distance(newPoint);
		// if (!path.contains(newPoint)) {
		// l2 = new ArrayList<Point2D>(path);
		// dist2 += getShortestPath(newPoint, dest, obstacles, minDistToObstacle, l2);
		// }

		if (dy > sy) {
		    newPoint = new Point2D.Double(sx, obstacle.getMaxY() + minDistToObstacle);
		    dist2 = source.distance(newPoint);
		    if (!path.contains(newPoint)) {
			l2 = new ArrayList<Point2D>(path);
			dist2 += getShortestPath(newPoint, dest, obstacles, minDistToObstacle, l2);
		    }
		}
		// else {
		// l2 = getShortestPath(new Point2D.Double(sx, obstacle.getMinY() - minDistToObstacle), dest,
		// obstacles, minDistToObstacle);
		// }

		if (l1 == null) {
		    if (l2 == null) {
			return -1;
		    }
		    path.clear();
		    path.addAll(l2);
		    return dist2;
		}

		if (l2 == null) {
		    path.clear();
		    path.addAll(l1);
		    return dist1;
		}

		if (dist1 < dist2) {
		    path.clear();
		    path.addAll(l1);
		    return dist1;
		} else {
		    path.clear();
		    path.addAll(l2);
		    return dist2;
		}
	    }
	}
	path.add(dest);
	return source.distance(dest);
    }

    public static double getShortestPath2(Point2D source, Point2D dest, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {

	Line2D directLine = new Line2D.Double(source, dest);
	path.add(source);
	double dx = dest.getX();
	double dy = dest.getY();
	double sx = source.getX();
	double sy = source.getY();

//	if (dx != sx && dy != sy) {
//	    List<Point2D> oldSource = new ArrayList<Point2D>();
//	    oldSource.add(source);
//	    List<Point2D> newSources = new ArrayList<Point2D>();
//	    Point2D yAligned = new Point2D.Double(sx, dy);
//	    Point2D xAligned = new Point2D.Double(dx, sy);
//
//	    List<Point2D> destList = new ArrayList<Point2D>();
//	    destList.add(dest);
//	    
//	    newSources.add(xAligned);
//	    newSources.add(yAligned);
//	    
//	    ArrayList<Point2D> p1 = new ArrayList<Point2D>(path);
//	    ArrayList<Point2D> p2 = new ArrayList<Point2D>(path);
//	    
//	    getShortestPath2(oldSource, newSources, obstacles, minDistToObstacle, path);
//	    getShortestPath2(newSources, destList, obstacles, minDistToObstacle, path);
//	    
//
//	    int nextIndex = path.size();
//	    double dist = getShortestPath2(newSources, destList, obstacles, minDistToObstacle, path);
//	    if (dist >= 0) {
//		return dist + source.distance(path.get(nextIndex));
//	    }
//
//	}

	/* check if we cross the obstacles */
	for (Rectangle2D obstacle : obstacles) {
	    /* check if the source point is contained in an obstacle */
	    // if (obstacle.contains(source)) {
	    // return -1;
	    // }

	    if (obstacle.intersectsLine(directLine)/* && !checkOnEdge(obstacle, source) && !checkOnEdge(obstacle, dest) */) {

		List<Point2D> newSources = new ArrayList<Point2D>();
		Point2D newPoint = null;
		/* right */
		newPoint = new Point2D.Double(obstacle.getMaxX() + minDistToObstacle, sy);
		if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		    newSources.add(newPoint);
		}

		/* bottom */
		newPoint = new Point2D.Double(sx, obstacle.getMaxY() + minDistToObstacle);
		if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		    newSources.add(newPoint);
		}
		/* left */
		newPoint = new Point2D.Double(obstacle.getMinX() - minDistToObstacle, sy);
		if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		    newSources.add(newPoint);
		}
		/* up */
		newPoint = new Point2D.Double(sx, obstacle.getMinY() - minDistToObstacle);
		if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		    newSources.add(newPoint);
		}

		List<Point2D> destList = new ArrayList<Point2D>();
		destList.add(dest);
		int nextIndex = path.size();
		double dist = getShortestPath2(newSources, destList, obstacles, minDistToObstacle, path);
		if (dist >= 0) {
		    return dist + source.distance(path.get(nextIndex));
		}
		return dist;
	    }
	}
	path.add(dest);
	return source.distance(dest);
    }

    public static double getShortestPath3(Point2D source, Point2D dest, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {

	Line2D directLine = new Line2D.Double(source, dest);
	path.add(source);
	double dx = dest.getX();
	double dy = dest.getY();
	double sx = source.getX();
	double sy = source.getY();

	if (dx != sx && dy != sy) {
	    List<Point2D> newSources = new ArrayList<Point2D>();
	    newSources.add(new Point2D.Double(sx, dy));
	    newSources.add(new Point2D.Double(dx, sy));
	    List<Point2D> destList = new ArrayList<Point2D>();
	    return getShortestPath2(newSources, destList, obstacles, minDistToObstacle, path);
	}

	/* check if we cross the obstacles */
	for (Rectangle2D obstacle : obstacles) {
	    /* check if the source point is contained in an obstacle */
	    if (obstacle.contains(source)) {
		if (obstacle.contains(dest)) {
		    /* source and destination are contained in the same obstacle. connect them */
		    break;
		}

	    }

	    if (obstacle.intersectsLine(directLine)/* && !checkOnEdge(obstacle, source) && !checkOnEdge(obstacle, dest) */) {

		// List<Point2D> newSources = new ArrayList<Point2D>();
		// Point2D newPoint = null;
		// /* right */
		// newPoint = new Point2D.Double(obstacle.getMaxX() + minDistToObstacle, sy);
		// if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		// newSources.add(newPoint);
		// }
		//
		// /* bottom */
		// newPoint = new Point2D.Double(sx, obstacle.getMaxY() + minDistToObstacle);
		// if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		// newSources.add(newPoint);
		// }
		// /* left */
		// newPoint = new Point2D.Double(obstacle.getMinX() - minDistToObstacle, sy);
		// if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		// newSources.add(newPoint);
		// }
		// /* up */
		// newPoint = new Point2D.Double(sx, obstacle.getMinY() - minDistToObstacle);
		// if (!path.contains(newPoint) && !checkOverlapping(obstacles, new Line2D.Double(source, newPoint))) {
		// newSources.add(newPoint);
		// }

		List<Point2D> newSources = getPathOptions(source, obstacles, obstacle, minDistToObstacle, path);
		List<Point2D> destList = new ArrayList<Point2D>();
		destList.add(dest);
		int nextIndex = path.size();
		double dist = getShortestPath2(newSources, destList, obstacles, minDistToObstacle, path);
		if (dist >= 0) {
		    return dist + source.distance(path.get(nextIndex));
		}
		return dist;
	    }
	}
	path.add(dest);
	return source.distance(dest);
    }

    private static List<Point2D> getPathOptions(Point2D source, Collection<Rectangle2D> obstacles,
	    Rectangle2D obstacle, double minDistToObstacle, List<Point2D> path) {
	double sx = source.getX();
	double sy = source.getY();
	List<Point2D> newSources = new ArrayList<Point2D>();
	Point2D newPoint = null;
	/* right */
	newPoint = new Point2D.Double(obstacle.getMaxX() + minDistToObstacle, sy);
	if (!path.contains(newPoint)
		&& (obstacle.contains(source) || !checkOverlapping(obstacles, new Line2D.Double(source, newPoint)))) {
	    newSources.add(newPoint);
	}

	/* bottom */
	newPoint = new Point2D.Double(sx, obstacle.getMaxY() + minDistToObstacle);
	if (!path.contains(newPoint)
		&& (obstacle.contains(source) || !checkOverlapping(obstacles, new Line2D.Double(source, newPoint)))) {
	    newSources.add(newPoint);
	}
	/* left */
	newPoint = new Point2D.Double(obstacle.getMinX() - minDistToObstacle, sy);
	if (!path.contains(newPoint)
		&& (obstacle.contains(source) || !checkOverlapping(obstacles, new Line2D.Double(source, newPoint)))) {
	    newSources.add(newPoint);
	}
	/* up */
	newPoint = new Point2D.Double(sx, obstacle.getMinY() - minDistToObstacle);
	if (!path.contains(newPoint)
		&& (obstacle.contains(source) || !checkOverlapping(obstacles, new Line2D.Double(source, newPoint)))) {
	    newSources.add(newPoint);
	}
	return newSources;
    }

    public static boolean checkOverlapping(Collection<Rectangle2D> obstacles, Line2D line) {
	for (Rectangle2D obstacle : obstacles) {
	    if (obstacle.intersectsLine(line)) {
		return true;
	    }
	}
	return false;
    }

    public static List<Point2D> getShortestPath(List<Point2D> sources, List<Point2D> dest,
	    Collection<Rectangle2D> obstacles, double minDistToObstacle) {
	List<Point2D> minPath = null;
	double minDist = Double.MAX_VALUE;
	for (Point2D s : sources) {
	    for (Point2D d : dest) {
		List<Point2D> path = new ArrayList<Point2D>();
		double dist = getShortestPath2(s, d, obstacles, minDistToObstacle, path);
		if (path != null && (minPath == null || dist < minDist)) {
		    minPath = path;
		    minDist = dist;
		}
	    }
	}
	return minPath;
    }

    public static double getShortestPath2(List<Point2D> sources, List<Point2D> dest, Collection<Rectangle2D> obstacles,
	    double minDistToObstacle, List<Point2D> path) {
	List<Point2D> minPath = null;
	double minDist = Double.MAX_VALUE;
	for (Point2D s : sources) {
	    for (Point2D d : dest) {
		List<Point2D> newPath = new ArrayList<Point2D>(path);
		double dist = getShortestPath2(s, d, obstacles, minDistToObstacle, newPath);
		if (dist >= 0 && (minPath == null || dist < minDist)) {
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

    public static void main(String[] args) {
	Rectangle rect1 = new Rectangle(50, 50, 100, 100);
	List<Rectangle2D> obstacles = new ArrayList<Rectangle2D>();
	obstacles.add(rect1);
	List<Point2D> path = new ArrayList<Point2D>();

	MathUtil.getShortestPath2(new Point(10, 10), new Point(300, 300), obstacles, (double) 10, path);
	System.out.println(path);
	JFrame frame = new JFrame();
	frame.setSize(800, 600);

	frame.add(new JBrokenArrow(path));
	// frame.add(new JArrow(new Point(10,10), new Point(300,300)));
	frame.setVisible(true);
	// System.out.println(rect1.intersectsLine(new Line2D.Double(new Point(10, 10), new Point(300, 300))));
    }
}
