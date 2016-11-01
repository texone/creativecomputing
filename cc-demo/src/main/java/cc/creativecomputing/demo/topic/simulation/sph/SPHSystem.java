package cc.creativecomputing.demo.topic.simulation.sph;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

class SPHSystem {

	private double kernel;
	private double mass;

	private int maxParticle;
	private int numParticle;

	private CCVector2i gridSize = new CCVector2i();
	private CCVector2 worldSize = new CCVector2();
	private double cellSize;
	private int totCell;

	// params
	private CCVector2 gravity = new CCVector2();
	private double stiffness;
	private double restDensity;
	private double timeStep;
	private double wallDamping;
	private double viscosity;

	private Particle[] particles;
	private Cell[] cells;

	public SPHSystem() {
		kernel = 0.01f;
		mass = 0.02f;

		maxParticle = 10000;
		numParticle = 0;

		worldSize.x = 1f;
		worldSize.y = 1f;
		cellSize = kernel;
		gridSize.x = (int) (worldSize.x / cellSize);
		gridSize.y = (int) (worldSize.y / cellSize);
		totCell = (int) (gridSize.x) * (int) (gridSize.y);

		// params
		gravity.x = 0.0f;
		gravity.y = 1.8f;
		stiffness = 1000.0f;
		restDensity = 1000.0f;
		timeStep = 0.2f;
		wallDamping = 0.0f;
		viscosity = 1.0f;

		particles = new Particle[maxParticle];
		for(int i = 0; i < maxParticle;i++){
			particles[i] = new Particle();
		}
		cells = new Cell[totCell];
		for(int i = 0; i < totCell;i++){
			cells[i] = new Cell();
		}

		System.out.printf("SPHSystem:\n");
		System.out.printf("GridSizeX: %d\n", gridSize.x);
		System.out.printf("GridSizeY: %d\n", gridSize.y);
		System.out.printf("TotalCell: %d\n", totCell);
	}

	public void initFluid() {
		for (double i = worldSize.x * 0.3f; i <= worldSize.x * 0.7f; i += kernel * 0.6f) {
			for (double j = worldSize.y * 0.3f; j <= worldSize.y * 0.9f; j += kernel * 0.6f) {
				CCVector2 pos = new CCVector2();
				CCVector2 vel = new CCVector2(0.0f, 0.0f);
				pos.x = i;
				pos.y = j;
				System.out.println(i + ":" + j);
				addSingleParticle(pos, vel);
			}
		}

		System.out.printf("NUM Particle: %d\n", numParticle);
	}

	public void addSingleParticle(CCVector2 pos, CCVector2 vel) {
		Particle p = particles[numParticle];
		p.pos = pos;
		p.vel = vel;
		p.acc = new CCVector2(0.0f, 0.0f);
		p.ev = vel;
		p.dens = restDensity;
		p.next = null;
		numParticle++;
	}

	private CCVector2i calcCellPos(CCVector2 pos) {
		CCVector2i res = new CCVector2i();
		res.x = (int) (pos.x / cellSize);
		res.y = (int) (pos.y / cellSize);
		return res;
	}

	private int calcCellHash(CCVector2i pos) {
		if (pos.x < 0 || pos.x >= gridSize.x || pos.y < 0 || pos.y >= gridSize.y) {
			return 0xffffffff;
		}

		int hash = (int) (pos.y) * (int) (gridSize.x) + (int) (pos.x);
		if (hash >= totCell) {
			System.out.printf("ERROR!\n");
			// getchar();
		}
		return hash;
	}

	// kernel function
	private double poly6(double r2) {
		return 315.0f / (64.0f * CCMath.PI * CCMath.pow(kernel, 9)) * CCMath.pow(kernel * kernel - r2, 3);
	}

	private double spiky(double r) {
		return -45.0f / (CCMath.PI * CCMath.pow(kernel, 6)) * (kernel - r) * (kernel - r);
	}

	private double visco(double r) {
		return 45.0f / (CCMath.PI * CCMath.pow(kernel, 6)) * (kernel - r);
	}

	// animation
	private void compTimeStep() {

		double maxAcc = 0.0f;
		double curAcc;
		for (int i = 0; i < numParticle; i++) {
			Particle p = particles[i];
			curAcc = p.acc.length();
			if (curAcc > maxAcc)
				maxAcc = curAcc;
		}
		if (maxAcc > 0.0f) {
			timeStep = kernel / maxAcc * 0.4f;
		} else {
			timeStep = 0.2f;
		}
	}

	private void buildGrid() {
		for (int i = 0; i < totCell; i++)
			cells[i].head = null;
		
		for (int i = 0; i < numParticle; i++) {
			Particle p = particles[i];
			int hash = calcCellHash(calcCellPos(p.pos));

			if (cells[hash].head == null) {
				p.next = null;
				cells[hash].head = p;
			} else {
				p.next = cells[hash].head;
				cells[hash].head = p;
			}
		}
	}

	private double INF = 1E-12f;

	private void compDensPressure() {
		
		CCVector2i nearPos = new CCVector2i();
		
		for (int k = 0; k < numParticle; k++) {
			Particle p = particles[k];
			p.dens = 0.0f;
			p.pres = 0.0f;
			CCVector2i cellPos = calcCellPos(p.pos);
			
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					nearPos.x = cellPos.x + i;
					nearPos.y = cellPos.y + j;
					int hash = calcCellHash(nearPos);
					if (hash == 0xffffffff)
						continue;
					
					Particle np = cells[hash].head;
					while (np != null) {
						CCVector2 distVec = np.pos.subtract(p.pos);
						double dist2 = distVec.lengthSquared();

						if (dist2 < INF || dist2 >= kernel * kernel) {
							np = np.next;
							continue;
						}

						p.dens = p.dens + mass * poly6(dist2);
						np = np.next;
					}
				}
			}
			p.dens = p.dens + mass * poly6(0.0f);
			p.pres = (CCMath.pow(p.dens / restDensity, 7) - 1) * stiffness;
		}
	}

	private void compForce() {
//		CCVector2i nearPos = new CCVector2i();
		
		for (int k = 0; k < numParticle; k++) {
			Particle p = particles[k];
			p.acc = new CCVector2(0.0f, 0.0f);
			CCVector2i cellPos = calcCellPos(p.pos);
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					CCVector2i nearPos = new CCVector2i();
					nearPos.x = cellPos.x + i;
					nearPos.y = cellPos.y + j;
					int hash = calcCellHash(nearPos);
					if (hash == 0xffffffff)
						continue;
					Particle np = cells[hash].head;
					while (np != null) {
						CCVector2 distVec = p.pos.subtract(np.pos);
						double dist2 = distVec.lengthSquared();

						if (dist2 < kernel * kernel && dist2 > INF) {
							double dist = CCMath.sqrt(dist2);
							double V = mass / p.dens;

							double tempForce = V * (p.pres + np.pres) * spiky(dist);
							p.acc.subtractLocal(distVec.multiply(tempForce / dist));

							CCVector2 relVel;
							relVel = np.ev.subtract(p.ev);
							tempForce = V * viscosity * visco(dist);
							p.acc.addLocal(relVel.multiply(tempForce));
						}

						np = np.next;
					}
				}
			}
			p.acc.divideLocal(p.dens);
			p.acc.addLocal(gravity);
		}
	}

	private void advection() {
		CCLog.info(timeStep);
		for (int i = 0; i < numParticle; i++) {
			Particle p = particles[i];
			p.vel.addLocal(p.acc.multiply(timeStep));
			p.pos.addLocal(p.vel.multiply(timeStep));

			if (p.pos.x < 0.0f) {
				p.vel.x = p.vel.x * wallDamping;
				p.pos.x = 0.0f;
			}
			if (p.pos.x >= worldSize.x) {
				p.vel.x = p.vel.x * wallDamping;
				p.pos.x = worldSize.x - 0.0001f;
			}
			if (p.pos.y < 0.0f) {
				p.vel.y = p.vel.y * wallDamping;
				p.pos.y = 0.0f;
			}
			if (p.pos.y >= worldSize.y) {
				p.vel.y = p.vel.y * wallDamping;
				p.pos.y = worldSize.y - 0.0001f;
			}

			p.ev = (p.ev.add(p.vel)).divide(2);
		}
	}

	public void animation() {
		buildGrid();
		compDensPressure();
		compForce();
		compTimeStep();
		advection();
	}

	// getter
	public int getNumParticle() {
		return numParticle;
	}

	public CCVector2 getWorldSize() {
		return worldSize;
	}

	public Particle[] getParticles() {
		return particles;
	}

	public Cell[] getCells() {
		return cells;
	}

};