package cc.creativecomputing.demo.topic.simulation.sph;

import java.util.Iterator;
import java.util.LinkedList;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.events.CCKeyEvent.CCKeyCode;
import cc.creativecomputing.gl.app.events.CCMouseEvent.CCMouseButton;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class SPH3 extends CCGL2Adapter {
	
	class CCSPHCell implements Iterable<CCSPHParticle>{
		LinkedList<CCSPHParticle> particles = new LinkedList<>();

		@Override
		public Iterator<CCSPHParticle> iterator() {
			return particles.iterator();
		}
	}
	
	// copywright (c) Eli Davies, Stylus Technology, april 2013
	class CCSPHSystem {
		int np = 0;
		CCSPHParticle[] particles = new CCSPHParticle[20000];
		
		CCSPHCell[][] cells;
		
		double up = 0, right = 0, left = 0, down = 0, buffer = 0;
		int cellWidth = 10;
		int cellHeight = 10;
		double d0 = 12;
		double w0 = 3;
		double d1 = 10;
		double d2 = 15;
		
		@CCProperty(name = "gravity", min = -0.1, max = 0)
		double gravity = -0.02;
		@CCProperty(name = "damping", min = 0.9, max = 1)
		double damping = 0.999;
		@CCProperty(name = "maxSpeed", min = 0, max = 10)
		double maxSpeed = 4;
		@CCProperty(name = "tension", min = 0, max = 1)
		double tension = 0.008;
		@CCProperty(name = "repulsion", min = 0, max = 1)
		double repulsion = 0.0008;
		@CCProperty(name = "stickyness", min = 0, max = 1)
		double stickyness = 1;
		CCVector2 lm = new CCVector2(0, 0);
		
		private int _myCellsX;
		private int _myCellsY;

		CCSPHSystem(int theWidth, int theHeight) {
			_myCellsX = theWidth / cellWidth + 2;
			_myCellsY = theHeight / cellHeight + 2;
			cells = new CCSPHCell[_myCellsX][_myCellsY];
			for(int x = 0; x < _myCellsX; x++){
				for(int y = 0; y < _myCellsY; y++){
					cells[x][y] = new CCSPHCell();
				}
			}
			for (int i = 0; i < np; i++) {
				particles[i] = new CCSPHParticle();
			}
		}
		
		public void addParticle(CCSPHParticle theParticle){
			particles[np] = theParticle;
			np++;
		}

		void setBnd(double nup, double ndown, double nright, double nleft, double nbuffer) {
			up = nup;
			right = nright;
			down = ndown;
			left = nleft;
			buffer = nbuffer;
		}
		
		private void buildGrid(){
			for(int x = 0; x < _myCellsX; x++){
				for(int y = 0; y < _myCellsY; y++){
					cells[x][y].particles.clear();
				}
			}
			for(CCSPHParticle myParticle:particles){
				if(myParticle == null)break;
				
				myParticle.cellX = (int) (myParticle.position.x / cellWidth);
				myParticle.cellY = (int) (myParticle.position.y / cellHeight);
				
				CCSPHCell myCell = cells[myParticle.cellX][myParticle.cellY];
				
				myCell.particles.add(myParticle);
			}
		}
		
		private void advection(){
			for(CCSPHParticle p:particles){
				if(p == null)break;
				
				p.velocity.addLocal(p.acceleration);
				p.velocity.y += gravity;
				p.acceleration.multiplyLocal(0.9);
				p.next.set(p.position).addLocal(p.velocity);
				
				p.velocity.truncateLocal(maxSpeed);
				
				if (p.position.y > down - buffer) {
					p.position.y = down - buffer;
					p.velocity.y *= -1;
					p.velocity.x *= stickyness;
				}
				if (p.position.x > right - buffer) {
					p.position.x = right - buffer;
					p.velocity.x *= -1;
					p.velocity.y *= stickyness;
				}
				if (p.position.y < up + buffer) {
					p.position.y = up + buffer;
					p.velocity.y *= -1;
					p.velocity.x *= stickyness;
				}
				if (p.position.x < left + buffer) {
					p.position.x = left + buffer;
					p.velocity.x *= -1;
					p.velocity.y *= stickyness;
				}

				p.position.addLocal(p.velocity).addLocal(p.next).divideLocal(2);
			}
//			for (int i = 0; i < numParticle; i++) {
//				Particle p = particles[i];
//				p.vel.addLocal(p.acc.multiply(timeStep));
//				p.pos.addLocal(p.vel.multiply(timeStep));
//
//				if (p.pos.x < 0.0f) {
//					p.vel.x = p.vel.x * wallDamping;
//					p.pos.x = 0.0f;
//				}
//				if (p.pos.x >= worldSize.x) {
//					p.vel.x = p.vel.x * wallDamping;
//					p.pos.x = worldSize.x - 0.0001f;
//				}
//				if (p.pos.y < 0.0f) {
//					p.vel.y = p.vel.y * wallDamping;
//					p.pos.y = 0.0f;
//				}
//				if (p.pos.y >= worldSize.y) {
//					p.vel.y = p.vel.y * wallDamping;
//					p.pos.y = worldSize.y - 0.0001f;
//				}
//
//				p.ev = (p.ev.add(p.vel)).divide(2);
//			}
		}

		public void step() {
			interactions();
			buildGrid();
			
			flock();
			advection();
			lm.set(mouse().position);
		}

		public void advect(CCSPHParticle p) {
			
			
			
//			double dx = p.position.x - mouse().position.x;
//			double dy = p.position.y - mouse().position.y;
//			double dist = CCMath.sqrt(dx * dx + dy * dy);
//			if (dist < 40) {
//				if (mouse().isPressed && !key().isPressed) {
//					if (mouse().button == CCMouseButton.LEFT) {
//						p.velocity.x = (p.velocity.x + (mouse().position.x - lm.x) * 0.5) / 2;
//						p.velocity.y = (p.velocity.x + (mouse().position.y - lm.y) * 0.5) / 2;
//					}
//				}
//			}
//			if (dist < 20)
//				if (mouse().isPressed)
//					if (mouse().button == CCMouseButton.RIGHT)
//						p.velocity.y = -2;
		}
		
		
		private void iterateNeighbors(CCSPHParticle p1, int x1, int y1){
			CCSPHCell myCell = cells[x1][y1];
			for(CCSPHParticle p2:myCell.particles){
				double dist = p1.position.distance(p2.position);
				
				if (dist < d0) {
					double coeff = dist * w0;
					p1.velocity.x = WM(p1.velocity.x, p2.velocity.x, coeff);
					p1.velocity.y = WM(p1.velocity.y, p2.velocity.y, coeff);
				}
				
				if(dist > d1 && dist > d2)continue;
				
				density(p1, p2, dist);
			}
		}
		
		private void iterateCell(int theCellX, int theCellY, boolean mod){
			CCSPHCell myCell0 = cells[theCellX][theCellY];
			
			for(CCSPHParticle p1 : myCell0.particles){
//				advect(p1);
//				constrain(p1);
				
				if(mod){
					for(int x1 = theCellX; x1 <= theCellX + 1; x1++){
						if(x1 >= _myCellsX)continue;
						for(int y1 = theCellY; y1 <= theCellY + 1; y1++){
							if(y1 >= _myCellsY)continue;
							
							iterateNeighbors(p1, x1, y1);
						}
					}
				}else{
					for(int x1 = theCellX - 1; x1 <= theCellX; x1++){
						if(x1 < 0)continue;
						for(int y1 = theCellY - 1; y1 <= theCellY; y1++){
							if(y1 < 0)continue;
							
							iterateNeighbors(p1, x1, y1);
						}
					}	
				}
			}
		}

		private void flock() {
			
			boolean mod = true;//animator().frames() % 2 == 1;
			if(mod){
				for(int x0 = 0; x0 < _myCellsX; x0++){
					for(int y0 = 0; y0 < _myCellsY;y0++){
						iterateCell(x0, y0, mod);
					}
				}
			}else{
				for(int x0 = _myCellsX - 1; x0 >= 0; x0--){
					for(int y0 = _myCellsY - 1; y0 >= 0; y0--){
						iterateCell(x0, y0, mod);
					}
				}
			}
		
//			for(int i = 0; i < np;i++){
//				CCSPHParticle p1 = particles[i];
//				advect(p1);
//				constrain(p1);
//				for (int j = i + 1; j < np; j++) {
//					CCSPHParticle p2 = particles[j];
//
//					if (CCMath.abs(p1.cellX - p2.cellX) > 1 || CCMath.abs(p1.cellY - p2.cellY) > 1) continue;
//					
//					double dist = p1.position.distance(p2.position);
//					
//					if (dist < d0) {
//						double coeff = dist * w0;
//						p1.velocity.x = WM(p1.velocity.x, p2.velocity.x, coeff);
//						p1.velocity.y = WM(p1.velocity.y, p2.velocity.y, coeff);
//					}
//					
//					if(dist > d1 && dist > d2)continue;
//					
//					density(p1, p2, dist);
//				}
//			}
			
		}

		private void density(CCSPHParticle p1, CCSPHParticle p2, double dist) {
			
			double dx = p1.position.x - p2.position.x;
			double dy = p1.position.y - p2.position.y;
			double temp = 2;
			double dr;
				
			if (dist > d1 && dist < d1 * 4)
				dr = (d1 + dist) / 2;
			else
				dr = d1;
				

			double angle = CCMath.atan2(dy, dx);
			double myAxScale = 0;
			
			if (dist < d1) {
				myAxScale += CCMath.pow(dr - dist, 3) * repulsion;
			}
			if (dist < d2) {
				myAxScale += (dr - dist) * tension;
			}
			double ax = CCMath.cos(angle) * myAxScale;
			double ay = CCMath.sin(angle) * myAxScale;

			if (ax > temp)
				ax = temp;
			if (ax < -temp)
				ax = -temp;
			if (ay > temp)
				ay = temp;
			if (ay < -temp)
				ay = -temp;
			
			p1.velocity.x += ax;
			p1.velocity.y += ay;
			p2.velocity.x -= ax;
			p2.velocity.y -= ay;
		}

		void interactions() {
			if (key().isPressed && key().keyCode == CCKeyCode.VK_UP) {
				if (mouse().isPressed && mouse().button == CCMouseButton.LEFT) {
					for (int i = 0; i < 1; i++) {
						np++;
						particles[np - 1] = new CCSPHParticle();
						particles[np - 1].position.y = mouse().position.y;
						particles[np - 1].position.x = mouse().position.x + CCMath.random(40) - 20;
						particles[np - 1].next.x = particles[np - 1].position.x;
						particles[np - 1].next.y = particles[np - 1].position.y;
						particles[np - 1].velocity.y = -2;
					}
				}
				if (mouse().isPressed && mouse().button == CCMouseButton.RIGHT) {
					if (np > 3)
						np -= 3;
				}
			}
		}


		private double cub(double x) {
			return x * x * x;
		}

		double WM(double x1, double x2, double w) {
			return (x1 * w + x2) / (w + 1);
		}
	}

	CCSPHSystem _mySPH;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		 _mySPH = new CCSPHSystem( g.width(), g.height());
		// frameRate(24);
		_mySPH.setBnd(0, g.height(), g.width(), 0, 10);

		for (int i = 0; i < 150; i++) {
			for (int j = 0; j < 70; j++) {
				CCSPHParticle myParticle = new CCSPHParticle();
				myParticle.position.x = i * 12 + 50;
				myParticle.position.y = j * 12 + 50;
				_mySPH.addParticle(myParticle);
			}
		}
		g.strokeWeight(0);
		g.color(255);
		
		for(String myExtension:g.extensions()){
			CCLog.info(myExtension);
		}
		
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.pushMatrix();
		g.translate(-g.width() / 2, -g.height() / 2);
		g.clearColor(255);
		g.clear();
		for (int i = 0; i < 1; i++)
			_mySPH.step();

		g.color(0, 50);
//		g.beginShape(CCDrawMode.LINES);
//		for (int i = 0; i < _mySPH.np; i++) {
//			CCSPHParticle p = _mySPH.particles[i];
//			g.vertex(p.position.x, p.position.y);
//			g.vertex(p.position.x + p.velocity.x * 10, p.position.y + p.velocity.y * 10);
//
//		}
//		g.endShape();
		g.pointSize(6);
		g.color(0, 50);
		g.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < _mySPH.np; i++) {
			CCSPHParticle p = _mySPH.particles[i];
			g.vertex(p.position.x, p.position.y);
		}
		g.endShape();
		g.popMatrix();
	}

	private class Box {
		double x, y, w, h;

		Box(double nx, double ny, double nw, double nh) {
			nw /= 2;
			nh /= 2;
			x = nx + nw;
			y = ny + nh;
			w = nw;
			h = nh;
		}
	}

	private class Circle {
		double x, y, r, px, py;

		Circle(double nx, double ny, double nr) {
			x = nx;
			y = ny;
			r = nr;
		}

		void update() {
			px = x;
			py = y;
		}
	}

	

	public static void main(String[] args) {

		SPH3 demo = new SPH3();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 900);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
