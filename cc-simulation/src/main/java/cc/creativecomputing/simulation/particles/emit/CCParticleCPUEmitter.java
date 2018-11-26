/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.simulation.particles.emit;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;

public abstract class CCParticleCPUEmitter implements CCIParticleEmitter{

	protected CCParticles _myParticles;

	protected List<CCParticle> _myStateChanges = new ArrayList<CCParticle>();
	protected List<CCParticle> _myAllocatedParticles = new ArrayList<CCParticle>();
	
	private CCMesh _myEmitMesh;
	private FloatBuffer _myVertexBuffer;
	private FloatBuffer _myColorBuffer;
	private FloatBuffer _myPositionBuffer;
	private FloatBuffer _myInfoBuffer;
	private FloatBuffer _myVelocityBuffer;
	protected float[] _myFillArray;
	
	public CCParticleCPUEmitter(CCParticles theParticles) {
		_myParticles = theParticles;
		
		_myEmitMesh = new CCMesh(CCDrawMode.POINTS);
		_myVertexBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 3);
		_myColorBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 4);
		_myPositionBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 4);
		_myInfoBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 4);
		_myVelocityBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 3);
		_myFillArray = new float[1000 * 4];
	}
	
	public void fillColorData(FloatBuffer theBuffer, List<CCParticle> theParticles){
		int i = 0;
		for (CCParticle myParticle:theParticles){
			_myFillArray[i * 4 + 0] = (float)myParticle.color().r;
			_myFillArray[i * 4 + 1] = (float)myParticle.color().g;
			_myFillArray[i * 4 + 2] = (float)myParticle.color().b;
			_myFillArray[i * 4 + 3] = (float)myParticle.color().a;
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 4);
	}
	
	public void fillPositionData(FloatBuffer theBuffer, List<CCParticle> theParticles){
		int i = 0;
		for (CCParticle myParticle:theParticles){
			_myFillArray[i * 4 + 0] = (float)myParticle.position().x;
			_myFillArray[i * 4 + 1] = (float)myParticle.position().y;
			_myFillArray[i * 4 + 2] = (float)myParticle.position().z;
			_myFillArray[i * 4 + 3] = 0;
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 4);
	}
	
	public void fillInfoData(FloatBuffer theBuffer, List<CCParticle> theParticles){
		int i = 0;
		for (CCParticle myParticle:theParticles){
			_myFillArray[i * 4 + 0] = (float)myParticle.age();
			_myFillArray[i * 4 + 1] = (float)myParticle.lifeTime();
			_myFillArray[i * 4 + 2] = myParticle.groupX();//, myParticle.step();
			_myFillArray[i * 4 + 3] = myParticle.groupY();
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 4);
	}
	
	public void fillVelocityData(FloatBuffer theBuffer, List<CCParticle> theParticles){
		int i = 0;
		for (CCParticle myParticle:theParticles){
			_myFillArray[i * 3 + 0] = (float)myParticle.velocity().x;
			_myFillArray[i * 3 + 1] = (float)myParticle.velocity().y;
			_myFillArray[i * 3 + 2] = (float)myParticle.velocity().z;
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 3);
	}
	
	private void prepareBuffer(int theSize){
		if(theSize > _myEmitMesh.numberOfVertices()){
			_myVertexBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 3);
			_myColorBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 4);
			_myPositionBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 4);
			_myInfoBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 4);
			_myVelocityBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 3);
			_myFillArray = new float[theSize * 4];
		}else{
			_myVertexBuffer.limit(theSize * 3);
			_myColorBuffer.limit(theSize * 4);
			_myPositionBuffer.limit(theSize * 4);
			_myInfoBuffer.limit(theSize * 4);
			_myVelocityBuffer.limit(theSize * 3);
		}
		
		_myVertexBuffer.rewind();
		_myColorBuffer.rewind();
		_myPositionBuffer.rewind();
		_myInfoBuffer.rewind();
		_myVelocityBuffer.rewind();
	}
	
	public void transferEmitData(CCGraphics g){
		_myParticles.dataBuffer().beginDraw(g);
		_myParticles.initValueShader().start();
		
		_myEmitMesh.draw(g);
		
		_myParticles.initValueShader().end();
		_myParticles.dataBuffer().endDraw(g);
	}
	
	
	
	public void transferInfoData(CCGraphics g){
		_myParticles.dataBuffer().beginDraw(g,1);
		_myParticles.initValueShader().start();
		
		_myEmitMesh.draw(g);

		_myParticles.initValueShader().end();
		_myParticles.dataBuffer().endDraw(g);
	}
	
	private void transferColorData(CCGraphics g){
		_myParticles.dataBuffer().beginDraw(g,3);
		_myParticles.initValueShader().start();
		
		_myEmitMesh.draw(g);

		_myParticles.initValueShader().end();
		_myParticles.dataBuffer().endDraw(g);
	}
	
	private void transferChanges(CCGraphics theGraphics){
		if(_myStateChanges.size() == 0)return;
		
		prepareBuffer(_myStateChanges.size());
		
		for (CCParticle myParticle:_myStateChanges){
			_myVertexBuffer.put(myParticle.x() + 0.5f);
			_myVertexBuffer.put(myParticle.y() + 0.5f);
			_myVertexBuffer.put(0);
		}
		
		fillInfoData(_myInfoBuffer, _myStateChanges);
		fillColorData(_myColorBuffer, _myStateChanges);
		
		_myVertexBuffer.rewind();
		_myInfoBuffer.rewind();
		
		_myEmitMesh.clearAll();
		_myEmitMesh.vertices(_myVertexBuffer);
		_myEmitMesh.textureCoords(1, _myInfoBuffer, 4);
		_myEmitMesh.textureCoords(3, _myColorBuffer, 4);
		
		transferInfoData(theGraphics);
		transferColorData(theGraphics);
		
		_myStateChanges.clear();
	}
	
	private void transferEmits(CCGraphics theGraphics){
		if(_myAllocatedParticles.size() == 0)return;
		synchronized(_myAllocatedParticles){
			prepareBuffer(_myAllocatedParticles.size());
			
			for (CCParticle myParticle:_myAllocatedParticles){
				_myVertexBuffer.put(myParticle.x() + 0.5f);
				_myVertexBuffer.put(myParticle.y() + 0.5f);
				_myVertexBuffer.put(0);
			}
			
			fillColorData(_myColorBuffer, _myAllocatedParticles);
			fillPositionData(_myPositionBuffer, _myAllocatedParticles);
			fillInfoData(_myInfoBuffer, _myAllocatedParticles);
			fillVelocityData(_myVelocityBuffer, _myAllocatedParticles);
			
			_myVertexBuffer.rewind();
			_myColorBuffer.rewind();
			_myPositionBuffer.rewind();
			_myInfoBuffer.rewind();
			_myVelocityBuffer.rewind();
			
			_myEmitMesh.clearAll();
			_myEmitMesh.vertices(_myVertexBuffer);
			_myEmitMesh.textureCoords(0, _myPositionBuffer, 4);
			_myEmitMesh.textureCoords(1, _myInfoBuffer, 4);
			_myEmitMesh.textureCoords(2, _myVelocityBuffer, 3);
			_myEmitMesh.textureCoords(3, _myColorBuffer, 4);
			
			transferEmitData(theGraphics);
			
			_myAllocatedParticles.clear();
		}
	}
	
	public void setData(CCGraphics theGraphics) {
		transferEmits(theGraphics);
		transferChanges(theGraphics);
	}
}
