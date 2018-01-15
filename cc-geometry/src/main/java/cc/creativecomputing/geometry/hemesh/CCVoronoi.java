package cc.creativecomputing.geometry.hemesh;

/*
 * The author of this software is Steven Fortune.  Copyright (c) 1994 by AT&T
 * Bell Laboratories.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

/* 
 * This code was originally written by Stephan Fortune in C code.  I, Shane O'Sullivan,
 * have since modified it, encapsulating it in a C++ class and, fixing memory leaks and
 * adding accessors to the Voronoi Edges.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

/* 
 * Java Version by Zhenyu Pan
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cc.creativecomputing.math.CCLine3;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCVoronoi {
	private class CCVoronoiEdge extends CCLine3{

		public int _myFaceIndex0;
		public int _myFaceIndex1;
		
		private CCHEEdge _myFace0Edge = null;
		private CCHEEdge _myFace1Edge = null;
		
		private CCVoronoiEdge(CCVoronoiFace theLeftFace, CCVoronoiFace theRightFace, double x1, double y1, double x2, double y2) {
			_myStart = new CCVector3(x1, y1, 0);
			_myEnd = new CCVector3(x2, y2, 0);

			_myFaceIndex0 = theLeftFace._myFaceIndex;
			_myFaceIndex1 = theRightFace._myFaceIndex;
		}
	}
	
	private class CCVoronoiFace implements Comparable<CCVoronoiFace>{
		private CCVector2 _myCoordinate;
		private int _myFaceIndex;
		private List<CCVoronoiEdge> _myEdges;
		private CCHEFace _myFace;

		public CCVoronoiFace() {
			_myCoordinate = new CCVector2();
		}

		@Override
		public int compareTo(CCVoronoiFace o) {
			CCVector2 s1 = _myCoordinate, s2 = o._myCoordinate;
			if (s1.y < s2.y) return -1;
			
			if (s1.y > s2.y) {
				return 1;
			}
			if (s1.x < s2.x) {
				return -1;
			}
			if (s1.x > s2.x) {
				return 1;
			}
			return (0);
		}
		
		private boolean isClockWise(LinkedList<CCHEEdge> mySortedEdges){
			if(mySortedEdges.size() < 3){
				return true;
			}
			CCVector3 myP0 = mySortedEdges.get(0).start().vector();
			CCVector3 myP1 = mySortedEdges.get(1).start().vector();
			CCVector3 myP2 = mySortedEdges.get(2).start().vector();
			double edge0X = myP1.x - myP0.x;
			double edge0Y = myP1.y - myP0.y;

			double edge1X = myP2.x - myP1.x;
			double edge1Y = myP2.y - myP1.y;
			double sign = (edge0X * edge1Y) - (edge0Y * edge1X);
			return sign < 0;
		}
		
		private void fixWinding(){
			if(_myEdges == null)return;
			CCVoronoiEdge myFirstEdge = _myEdges.remove(0);
			
			LinkedList<CCHEEdge> mySortedEdges = new LinkedList<>();
			mySortedEdges.addFirst(myFirstEdge._myFace0Edge);

			_myMesh.addHalfEdge(myFirstEdge._myFace0Edge);
			List<CCVoronoiEdge> myCopy = new ArrayList<>(_myEdges);
			
			while(myCopy.size() > 0){
				boolean found = false;
				for(int j = 0; j < myCopy.size();j++){
					CCVoronoiEdge myVoronoiEdge = myCopy.get(j);
					if(mySortedEdges.getFirst().isPrevious(myVoronoiEdge._myFace0Edge)){
						_myMesh.addHalfEdge(myVoronoiEdge._myFace0Edge);
						mySortedEdges.addFirst(myVoronoiEdge._myFace0Edge);
						found = true;
						myCopy.remove(j);
						break;
					}
					if(mySortedEdges.getFirst().isPrevious(myVoronoiEdge._myFace1Edge)){
						mySortedEdges.addFirst(myVoronoiEdge._myFace1Edge);
						found = true;
						myCopy.remove(j);
						break;
					}
					
					if(mySortedEdges.getLast().isNext(myVoronoiEdge._myFace0Edge)){
						mySortedEdges.addLast(myVoronoiEdge._myFace0Edge);
						found = true;
						myCopy.remove(j);
						break;
					}
					if(mySortedEdges.getLast().isNext(myVoronoiEdge._myFace1Edge)){
						mySortedEdges.addLast(myVoronoiEdge._myFace1Edge);
						found = true;
						myCopy.remove(j);
						break;
					}
				}
				if(!found){
					System.out.println("NOT FOUND:" + myCopy.size());
					break;
				}
			}
			
			if(!mySortedEdges.getLast().isNext(mySortedEdges.getFirst())){
				CCHEEdge myEdge0 = new CCHEEdge(mySortedEdges.getLast().end(), null);
				CCHEEdge myEdge1 = new CCHEEdge(mySortedEdges.getFirst().start(), null);
				myEdge0.pair(myEdge1);
				myEdge1.pair(myEdge0);
				mySortedEdges.addLast(myEdge0);
			}

			if(isClockWise(mySortedEdges)){
				for(int i = 0; i < mySortedEdges.size() - 1;i++){
					mySortedEdges.get(i).next(mySortedEdges.get(i + 1));
					mySortedEdges.get(i).face(_myFace);
					_myMesh.addHalfEdge(mySortedEdges.get(i));
				}
				_myMesh.addHalfEdge(mySortedEdges.getLast());
				mySortedEdges.getLast().face(_myFace);
				_myFace.edge(mySortedEdges.getFirst());
				
				mySortedEdges.getLast().next(mySortedEdges.getFirst());
			}else{
				for(int i = mySortedEdges.size() - 1; i > 0 ;i--){
					mySortedEdges.get(i).pair().next(mySortedEdges.get(i - 1).pair());
					mySortedEdges.get(i).pair().face(_myFace);
					_myMesh.addHalfEdge(mySortedEdges.get(i).pair());
				}
				mySortedEdges.getFirst().pair().face(_myFace);
				_myMesh.addHalfEdge(mySortedEdges.getFirst().pair());
				_myFace.edge(mySortedEdges.getLast().pair());
				
				mySortedEdges.getFirst().pair().next(mySortedEdges.getLast().pair());
				
//				myLeftEdge
			}
			
		}
	}
	
	private class Halfedge {
		Halfedge ELleft, ELright;
		Edge ELedge;
		boolean deleted;
		int ELpm;
		CCVoronoiFace vertex;
		double ystar;
		Halfedge PQnext;
		
		private Halfedge (Edge e, int pm) {
			ELedge = e;
			ELpm = pm;
			PQnext = null;
			vertex = null;
		}

		public Halfedge() {
			this(null,0);
		}
		
		/*
		 * This delete routine can't reclaim node, since pointers from hash table
		 * may be present.
		 */
		public void delete(){
			ELleft.ELright = ELright;
			ELright.ELleft = ELleft;
			deleted = true;
		}
		
		private void insert(Halfedge newHe) {
			newHe.ELleft = this;
			newHe.ELright = ELright;
			ELright.ELleft = newHe;
			ELright = newHe;
		}
	}
	
	private class Edge {
		public double a = 0, b = 0, c = 0;
		CCVoronoiFace[] ep; // JH: End points?
		CCVoronoiFace[] reg; // JH: Sites this edge bisects?

		Edge() {
			ep = new CCVoronoiFace[2];
			reg = new CCVoronoiFace[2];
		}
	}
	
	private class EdgeList{
		private int _mySize;
		private Halfedge hash[];
		private Halfedge leftEnd, rightEnd;
		
		private EdgeList() {
			_mySize = 2 * sqrt_nsites;
			hash = new Halfedge[_mySize];

			for (int i = 0; i < _mySize; i += 1) {
				hash[i] = null;
			}
			leftEnd = new Halfedge();
			rightEnd = new Halfedge();
			leftEnd.ELleft = null;
			leftEnd.ELright = rightEnd;
			
			rightEnd.ELleft = leftEnd;
			rightEnd.ELright = null;
			hash[0] = leftEnd;
			hash[_mySize - 1] = rightEnd;
		}
		
		/* Get entry from hash table, pruning any deleted nodes */
		private Halfedge gethash(int b) {
			if (b < 0 || b >= _mySize) return null;
			
			Halfedge he = hash[b];
			if (he == null || !he.deleted) {
				return he;
			}

			/* Hash table points to deleted half edge. Patch as necessary. */
			hash[b] = null;
			return null;
		}
		
		private Halfedge leftbnd(CCVector2 p) {

			/* Use hash table to get close to desired halfedge */
			// use the hash function to find the place in the hash map that this
			// HalfEdge should be
			int bucket = (int) ((p.x - xmin) / deltax * _mySize);

			// make sure that the bucket position in within the range of the hash
			// array
			if (bucket < 0) {
				bucket = 0;
			}
			if (bucket >= _mySize) {
				bucket = _mySize - 1;
			}

			Halfedge he = gethash(bucket);
			if (he == null)
			// if the HE isn't found, search backwards and forwards in the hash map
			// for the first non-null entry
			{
				for (int i = 1; i < _mySize; i += 1) {
					if ((he = gethash(bucket - i)) != null) {
						break;
					}
					if ((he = gethash(bucket + i)) != null) {
						break;
					}
				}
			}
			/* Now search linear list of halfedges for the correct one */
			if (he == leftEnd || (he != rightEnd && right_of(he, p))) {
				// keep going right on the list until either the end is reached, or
				// you find the 1st edge which the point isn't to the right of
				do {
					he = he.ELright;
				} while (he != rightEnd && right_of(he, p));
				he = he.ELleft;
			} else
			// if the point is to the left of the HalfEdge, then search left for
			// the HE just to the left of the point
			{
				do {
					he = he.ELleft;
				} while (he != leftEnd && !right_of(he, p));
			}

			/* Update hash table and reference counts */
			if (bucket > 0 && bucket < _mySize - 1) {
				hash[bucket] = he;
			}
			return he;
		}
	}
	
	private class PointQueue{
		private int PQcount;
		private int PQmin;
		private int PQhashsize;
		private Halfedge PQhash[];
		
		private PointQueue() {
			PQcount = 0;
			PQmin = 0;
			PQhashsize = 4 * sqrt_nsites;
			PQhash = new Halfedge[PQhashsize];

			for (int i = 0; i < PQhashsize; i += 1) {
				PQhash[i] = new Halfedge();
			}
		}
		
		private int bucket(Halfedge he) {
			int bucket = (int) ((he.ystar - ymin) / deltay * PQhashsize);
			if (bucket < 0) {
				bucket = 0;
			}
			if (bucket >= PQhashsize) {
				bucket = PQhashsize - 1;
			}
			if (bucket < PQmin) {
				PQmin = bucket;
			}
			return bucket;
		}

		// push the HalfEdge into the ordered linked list of vertices
		
		private void insert(Halfedge he, CCVoronoiFace v, double offset) {
			he.vertex = v;
			he.ystar = v._myCoordinate.y + offset;
			Halfedge last = PQhash[bucket(he)];
			Halfedge next;
			while ((next = last.PQnext) != null && (he.ystar > next.ystar || (he.ystar == next.ystar && v._myCoordinate.x > next.vertex._myCoordinate.x))) {
				last = next;
			}
			he.PQnext = last.PQnext;
			last.PQnext = he;
			PQcount += 1;
		}

		// remove the HalfEdge from the list of vertices
		private void delete(Halfedge he) {
			if(he.vertex == null)return;
			Halfedge last = PQhash[bucket(he)];
			while (last.PQnext != he) {
				last = last.PQnext;
			}
			last.PQnext = he.PQnext;
			PQcount -= 1;
			he.vertex = null;
		}

		private boolean empty() {
			return PQcount == 0;
		}

		private CCVector2 min() {
			CCVector2 answer = new CCVector2();

			while (PQhash[PQmin].PQnext == null) {
				PQmin += 1;
			}
			answer.x = PQhash[PQmin].PQnext.vertex._myCoordinate.x;
			answer.y = PQhash[PQmin].PQnext.ystar;
			return answer;
		}

		private Halfedge extractmin() {
			Halfedge curr = PQhash[PQmin].PQnext;
			PQhash[PQmin].PQnext = curr.PQnext;
			PQcount -= 1;
			return curr;
		}
	}
	
	// ************* Private members ******************
	private double borderMinX, borderMaxX, borderMinY, borderMaxY;
	private int siteidx;
	private double xmin, xmax, ymin, ymax, deltax, deltay;
	private int nvertices;
	private int _myNumberOfFaces;
	private CCVoronoiFace[] _myVoronoiFaces;
	private CCVoronoiFace bottomsite;
	private int sqrt_nsites;
	private double minDistanceBetweenSites = 0;
	
	private PointQueue _myPointQueue;
	private EdgeList _myEdgeList;

	private final static int LE = 0;
	private final static int RE = 1;

	
	private List<CCVoronoiEdge> _myGraphEdges;
	
	private List<CCLine3> _myEdges = null;
	private CCHEMesh _myMesh = null;

	/*********************************************************
	 * Public methods
	 ********************************************************/

	/**
	 * 
	 * @param xValuesIn
	 *            Array of X values for each site.
	 * @param yValuesIn
	 *            Array of Y values for each site. Must be identical length to
	 *            yValuesIn
	 * @param minX
	 *            The minimum X of the bounding box around the voronoi
	 * @param maxX
	 *            The maximum X of the bounding box around the voronoi
	 * @param minY
	 *            The minimum Y of the bounding box around the voronoi
	 * @param maxY
	 *            The maximum Y of the bounding box around the voronoi
	 * @return
	 */
	public CCVoronoi(List<CCVector2> thePoints, double minX, double maxX, double minY, double maxY) {
		if(thePoints.size() == 0)return;
		siteidx = 0;
		_myVoronoiFaces = null;

		_myGraphEdges = new LinkedList<CCVoronoiEdge>();
		
		sort(thePoints);

		// Check bounding box inputs - if mins are bigger than maxes, swap them
		double temp = 0;
		if (minX > maxX) {
			temp = minX;
			minX = maxX;
			maxX = temp;
		}
		if (minY > maxY) {
			temp = minY;
			minY = maxY;
			maxY = temp;
		}
		borderMinX = minX;
		borderMinY = minY;
		borderMaxX = maxX;
		borderMaxY = maxY;

		siteidx = 0;
		voronoi_bd();
	}
	
	public List<CCLine3> edges(){
		if(_myEdges != null)return _myEdges;
		_myEdges = new ArrayList<>();
		if(_myGraphEdges == null)return _myEdges;
		for(CCVoronoiEdge myEdge:_myGraphEdges){
			_myEdges.add(new CCLine3(myEdge.start().clone(), myEdge.end().clone()));
		}
		return _myEdges;
	}
	
	public CCHEMesh mesh(){
		if(_myMesh != null)return _myMesh;
		
		_myMesh = new CCHEMesh();
		for(int i = 0; i < _myNumberOfFaces;i++){
			CCHEFace myFace = new CCHEFace();
			_myVoronoiFaces[i]._myFace = myFace;
			_myVoronoiFaces[i]._myEdges = new ArrayList<>();
			_myMesh.addFace(myFace);
		}
		for(CCVoronoiEdge myEdge:_myGraphEdges){
			if(myEdge.start().equals(myEdge.end())){
				continue;
			}
			
			myEdge._myFace0Edge = new CCHEEdge(_myMesh.getVertex(myEdge.start().clone()), null);
			myEdge._myFace1Edge = new CCHEEdge(_myMesh.getVertex(myEdge.end().clone()), null);
			myEdge._myFace0Edge.pair(myEdge._myFace1Edge);
			myEdge._myFace1Edge.pair(myEdge._myFace0Edge);
			
			_myVoronoiFaces[myEdge._myFaceIndex0]._myEdges.add(myEdge);
			_myVoronoiFaces[myEdge._myFaceIndex1]._myEdges.add(myEdge);	
		}
		for(CCVoronoiFace myFace:_myVoronoiFaces){
			myFace.fixWinding();
		}
		
		
		return _myMesh;
	}

	/*********************************************************
	 * Private methods - implementation details
	 ********************************************************/

	private void sort(List<CCVector2> thePoints) {
		_myNumberOfFaces = thePoints.size();
		_myVoronoiFaces = new CCVoronoiFace[_myNumberOfFaces];
		_myGraphEdges = new LinkedList<CCVoronoiEdge>();

		nvertices = 0;

		double sn = (double) _myNumberOfFaces + 4;
		sqrt_nsites = (int) Math.sqrt(sn);
		
		xmin = Float.MAX_VALUE;
		ymin = Float.MAX_VALUE;
		xmax = Float.MIN_VALUE;
		ymax = Float.MIN_VALUE;
		
		for (int i = 0; i < _myNumberOfFaces; i++) {
			CCVector2 myPoint = thePoints.get(i);
			_myVoronoiFaces[i] = new CCVoronoiFace();
			_myVoronoiFaces[i]._myCoordinate.set(myPoint);
			_myVoronoiFaces[i]._myFaceIndex = i;

			if (myPoint.x < xmin) xmin = myPoint.x;
			if (myPoint.x > xmax) xmax = myPoint.x;

			if (myPoint.y < ymin) ymin = myPoint.y;
			if (myPoint.y > ymax) ymax = myPoint.y;
		}
		deltay = ymax - ymin;
		deltax = xmax - xmin;
		Arrays.sort(_myVoronoiFaces);
	}

	/* return a single in-storage site */
	private CCVoronoiFace nextone() {
		if (siteidx >= _myNumberOfFaces) return null;
		
		return _myVoronoiFaces[siteidx++];
	}

	private Edge bisect(CCVoronoiFace s1, CCVoronoiFace s2) {
		Edge newedge = new Edge();

		// store the sites that this edge is bisecting
		newedge.reg[0] = s1;
		newedge.reg[1] = s2;
		// to begin with, there are no endpoints on the bisector - it goes to
		// infinity
		newedge.ep[0] = null;
		newedge.ep[1] = null;

		// get the difference in x dist between the sites
		double dx = s2._myCoordinate.x - s1._myCoordinate.x;
		double dy = s2._myCoordinate.y - s1._myCoordinate.y;
		// make sure that the difference in positive
		double adx = dx > 0 ? dx : -dx;
		double ady = dy > 0 ? dy : -dy;
		// get the slope of the line
		newedge.c = s1._myCoordinate.x * dx + s1._myCoordinate.y * dy + (dx * dx + dy * dy) * 0.5;

		if (adx > ady) {
			newedge.a = 1.0f;
			newedge.b = dy / dx;
			newedge.c /= dx;// set formula of line, with x fixed to 1
		} else {
			newedge.b = 1.0f;
			newedge.a = dx / dy;
			newedge.c /= dy;// set formula of line, with y fixed to 1
		}

		return (newedge);
	}

	private void makevertex(CCVoronoiFace v) {
		v._myFaceIndex = nvertices;
		nvertices += 1;
	}
	
	private CCVoronoiFace leftreg(Halfedge he) {
		if (he.ELedge == null) {
			return (bottomsite);
		}
		return (he.ELpm == LE ? he.ELedge.reg[LE] : he.ELedge.reg[RE]);
	}

	private void clip_line(Edge e) {
		double x1 = e.reg[0]._myCoordinate.x;
		double x2 = e.reg[1]._myCoordinate.x;
		double y1 = e.reg[0]._myCoordinate.y;
		double y2 = e.reg[1]._myCoordinate.y;

		// if the distance between the two points this line was created from is
		// less than the square root of 2, then ignore it
		if (Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1))) < minDistanceBetweenSites) {
			return;
		}
		
		double pxmin = borderMinX;
		double pxmax = borderMaxX;
		double pymin = borderMinY;
		double pymax = borderMaxY;

		CCVoronoiFace s1, s2;
		if (e.a == 1.0 && e.b >= 0.0) {
			s1 = e.ep[1];
			s2 = e.ep[0];
		} else {
			s1 = e.ep[0];
			s2 = e.ep[1];
		}

		if (e.a == 1.0) {
			y1 = pymin;
			if (s1 != null && s1._myCoordinate.y > pymin) {
				y1 = s1._myCoordinate.y;
			}
			if (y1 > pymax) {
				y1 = pymax;
			}
			x1 = e.c - e.b * y1;
			y2 = pymax;
			if (s2 != null && s2._myCoordinate.y < pymax) {
				y2 = s2._myCoordinate.y;
			}

			if (y2 < pymin) {
				y2 = pymin;
			}
			x2 = (e.c) - (e.b) * y2;
			if (((x1 > pxmax) & (x2 > pxmax)) | ((x1 < pxmin) & (x2 < pxmin))) {
				return;
			}
			if (x1 > pxmax) {
				x1 = pxmax;
				y1 = (e.c - x1) / e.b;
			}
			if (x1 < pxmin) {
				x1 = pxmin;
				y1 = (e.c - x1) / e.b;
			}
			if (x2 > pxmax) {
				x2 = pxmax;
				y2 = (e.c - x2) / e.b;
			}
			if (x2 < pxmin) {
				x2 = pxmin;
				y2 = (e.c - x2) / e.b;
			}
		} else {
			x1 = pxmin;
			if (s1 != null && s1._myCoordinate.x > pxmin) {
				x1 = s1._myCoordinate.x;
			}
			if (x1 > pxmax) {
				x1 = pxmax;
			}
			y1 = e.c - e.a * x1;
			x2 = pxmax;
			if (s2 != null && s2._myCoordinate.x < pxmax) {
				x2 = s2._myCoordinate.x;
			}
			if (x2 < pxmin) {
				x2 = pxmin;
			}
			y2 = e.c - e.a * x2;
			if (((y1 > pymax) & (y2 > pymax)) | ((y1 < pymin) & (y2 < pymin))) {
				return;
			}
			if (y1 > pymax) {
				y1 = pymax;
				x1 = (e.c - y1) / e.a;
			}
			if (y1 < pymin) {
				y1 = pymin;
				x1 = (e.c - y1) / e.a;
			}
			if (y2 > pymax) {
				y2 = pymax;
				x2 = (e.c - y2) / e.a;
			}
			if (y2 < pymin) {
				y2 = pymin;
				x2 = (e.c - y2) / e.a;
			}
		}

		_myGraphEdges.add(new CCVoronoiEdge(e.reg[0], e.reg[1], x1, y1, x2, y2));
	}

	private void endpoint(Edge e, int lr, CCVoronoiFace s) {
		e.ep[lr] = s;
		if (e.ep[RE - lr] == null) {
			return;
		}
		clip_line(e);
	}

	/* returns 1 if p is to right of halfedge e */
	private boolean right_of(Halfedge el, CCVector2 p) {
		Edge e;
		CCVoronoiFace topsite;
		boolean right_of_site;
		boolean above, fast;
		double dxp, dyp, dxs, t1, t2, t3, yl;

		e = el.ELedge;
		topsite = e.reg[1];
        right_of_site = p.x > topsite._myCoordinate.x;
		if (right_of_site && el.ELpm == LE) {
			return (true);
		}
		if (!right_of_site && el.ELpm == RE) {
			return (false);
		}

		if (e.a == 1.0) {
			dyp = p.y - topsite._myCoordinate.y;
			dxp = p.x - topsite._myCoordinate.x;
			fast = false;
			if ((!right_of_site & (e.b < 0.0)) | (right_of_site & (e.b >= 0.0))) {
				above = dyp >= e.b * dxp;
				fast = above;
			} else {
				above = p.x + p.y * e.b > e.c;
				if (e.b < 0.0) {
					above = !above;
				}
				if (!above) {
					fast = true;
				}
			}
			if (!fast) {
				dxs = topsite._myCoordinate.x - (e.reg[0])._myCoordinate.x;
				above = e.b * (dxp * dxp - dyp * dyp) < dxs * dyp * (1.0 + 2.0 * dxp / dxs + e.b * e.b);
				if (e.b < 0.0) {
					above = !above;
				}
			}
		} else /* e.b==1.0 */

		{
			yl = e.c - e.a * p.x;
			t1 = p.y - yl;
			t2 = p.x - topsite._myCoordinate.x;
			t3 = yl - topsite._myCoordinate.y;
			above = t1 * t1 > t2 * t2 + t3 * t3;
		}
		return ((el.ELpm == LE) == above);
	}

	private CCVoronoiFace rightreg(Halfedge he) {
		if (he.ELedge == null)
		// if this halfedge has no edge, return the bottom site (whatever
		// that is)
		{
			return (bottomsite);
		}

		// if the ELpm field is zero, return the site 0 that this edge bisects,
		// otherwise return site number 1
		return (he.ELpm == LE ? he.ELedge.reg[RE] : he.ELedge.reg[LE]);
	}

	private double dist(CCVoronoiFace s, CCVoronoiFace t) {
		double dx, dy;
		dx = s._myCoordinate.x - t._myCoordinate.x;
		dy = s._myCoordinate.y - t._myCoordinate.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	// create a new site where the HalfEdges el1 and el2 intersect - note that
	// the CCVector2 in the argument list is not used, don't know why it's there
	private CCVoronoiFace intersect(Halfedge el1, Halfedge el2) {

		Edge e1 = el1.ELedge;
		Edge e2 = el2.ELedge;
		if (e1 == null || e2 == null) {
			return null;
		}

		// if the two edges bisect the same parent, return null
		if (e1.reg[1] == e2.reg[1]) {
			return null;
		}

		double d = e1.a * e2.b - e1.b * e2.a;
		if (-1.0e-10 < d && d < 1.0e-10) {
			return null;
		}

		double xint = (e1.c * e2.b - e2.c * e1.b) / d;
		double yint = (e2.c * e1.a - e1.c * e2.a) / d;

		Halfedge el;
		Edge e;
		if ((e1.reg[1]._myCoordinate.y < e2.reg[1]._myCoordinate.y) || (e1.reg[1]._myCoordinate.y == e2.reg[1]._myCoordinate.y && e1.reg[1]._myCoordinate.x < e2.reg[1]._myCoordinate.x)) {
			el = el1;
			e = e1;
		} else {
			el = el2;
			e = e2;
		}

		boolean right_of_site = xint >= e.reg[1]._myCoordinate.x;
		if ((right_of_site && el.ELpm == LE) || (!right_of_site && el.ELpm == RE)) {
			return null;
		}

		// create a new site at the point of intersection - this is a new vector
		// event waiting to happen
		CCVoronoiFace v = new CCVoronoiFace();
		v._myCoordinate.x = xint;
		v._myCoordinate.y = yint;
		return v;
	}

	/*
	 * implicit parameters: nsites, sqrt_nsites, xmin, xmax, ymin, ymax, deltax,
	 * deltay (can all be estimates). Performance suffers if they are wrong;
	 * better to make nsites, deltax, and deltay too big than too small. (?)
	 */
	private boolean voronoi_bd() {
		CCVoronoiFace newsite, bot, top, temp, p;
		CCVoronoiFace v;
		CCVector2 newintstar = null;
		int pm;
		Halfedge lbnd, rbnd, llbnd, rrbnd, bisector;
		Edge e;

		_myPointQueue = new PointQueue();
		_myEdgeList = new EdgeList();

		bottomsite = nextone();
		newsite = nextone();
		while (true) {
			if (!_myPointQueue.empty()) {
				newintstar = _myPointQueue.min();
			}
			// if the lowest site has a smaller y value than the lowest vector intersection,
			// process the site otherwise process the vector intersection

			if (newsite != null && (_myPointQueue.empty() || newsite._myCoordinate.y < newintstar.y || (newsite._myCoordinate.y == newintstar.y && newsite._myCoordinate.x < newintstar.x))) {
				/* new site is smallest -this is a site event */
				// get the first HalfEdge to the LEFT of the new site
				lbnd = _myEdgeList.leftbnd(newsite._myCoordinate);
				// get the first HalfEdge to the RIGHT of the new site
				rbnd = lbnd.ELright;
				// if this halfedge has no edge,bot =bottom site (whatever that
				// is)
				bot = rightreg(lbnd);
				// create a new edge that bisects
				e = bisect(bot, newsite);

				// create a new HalfEdge, setting its ELpm field to 0
				bisector = new Halfedge(e, LE);
				// insert this new bisector edge between the left and right
				// vectors in a linked list
				lbnd.insert(bisector);

				// if the new bisector intersects with the left edge,
				// remove the left edge's vertex, and put in the new one
				if ((p = intersect(lbnd, bisector)) != null) {
					_myPointQueue.delete(lbnd);
					_myPointQueue.insert(lbnd, p, dist(p, newsite));
				}
				lbnd = bisector;
				// create a new HalfEdge, setting its ELpm field to 1
				bisector = new Halfedge(e, RE);
				// insert the new HE to the right of the original bisector
				// earlier in the IF stmt
				lbnd.insert(bisector);

				// if this new bisector intersects with the new HalfEdge
				if ((p = intersect(bisector, rbnd)) != null) {
					// push the HE into the ordered linked list of vertices
					_myPointQueue.insert(bisector, p, dist(p, newsite));
				}
				newsite = nextone();
			} else if (!_myPointQueue.empty())
			/* intersection is smallest - this is a vector event */
			{
				// pop the HalfEdge with the lowest vector off the ordered list
				// of vectors
				lbnd = _myPointQueue.extractmin();
				// get the HalfEdge to the left of the above HE
				llbnd = lbnd.ELleft;
				// get the HalfEdge to the right of the above HE
				rbnd = lbnd.ELright;
				// get the HalfEdge to the right of the HE to the right of the
				// lowest HE
				rrbnd = rbnd.ELright;
				// get the Site to the left of the left HE which it bisects
				bot = leftreg(lbnd);
				// get the Site to the right of the right HE which it bisects
				top = rightreg(rbnd);

				v = lbnd.vertex; // get the vertex that caused this event
				makevertex(v); // set the vertex number - couldn't do this
				// earlier since we didn't know when it would be processed
				endpoint(lbnd.ELedge, lbnd.ELpm, v);
				// set the endpoint of
				// the left HalfEdge to be this vector
				endpoint(rbnd.ELedge, rbnd.ELpm, v);
				// set the endpoint of the right HalfEdge to
				// be this vector
				lbnd.delete(); // mark the lowest HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				_myPointQueue.delete(rbnd);
				// remove all vertex events to do with the right HE
				rbnd.delete(); // mark the right HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				pm = LE; // set the pm variable to zero

				if (bot._myCoordinate.y > top._myCoordinate.y)
				// if the site to the left of the event is higher than the
				// Site
				{ // to the right of it, then swap them and set the 'pm'
					// variable to 1
					temp = bot;
					bot = top;
					top = temp;
					pm = RE;
				}
				e = bisect(bot, top); // create an Edge (or line)
				// that is between the two Sites. This creates the formula of
				// the line, and assigns a line number to it
				bisector = new Halfedge(e, pm); // create a HE from the Edge 'e',
				// and make it point to that edge
				// with its ELedge field
				llbnd.insert(bisector); // insert the new bisector to the
				// right of the left HE
				endpoint(e, RE - pm, v); // set one end point to the new edge
				// to be the vector point 'v'.
				// If the site to the left of this bisector is higher than the
				// right Site, then this end point
				// is put in position 0; otherwise in pos 1

				// if left HE and the new bisector intersect, then delete
				// the left HE, and reinsert it
				if ((p = intersect(llbnd, bisector)) != null) {
					_myPointQueue.delete(llbnd);
					_myPointQueue.insert(llbnd, p, dist(p, bot));
				}

				// if right HE and the new bisector intersect, then
				// reinsert it
				if ((p = intersect(bisector, rrbnd)) != null) {
					_myPointQueue.insert(bisector, p, dist(p, bot));
				}
			} else {
				break;
			}
		}

		for (lbnd = _myEdgeList.leftEnd.ELright; lbnd != _myEdgeList.rightEnd; lbnd = lbnd.ELright) {
			e = lbnd.ELedge;
			clip_line(e);
		}

		return true;
	}

}
