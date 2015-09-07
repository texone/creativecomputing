package cc.creativecomputing.gl4;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil;
import cc.creativecomputing.core.util.CCReflectionUtil.CCField;
import cc.creativecomputing.gl4.GLShaderProgram.GLUniformInfo;
import cc.creativecomputing.gl4.GLShaderProgram.GLUniformType;
import cc.creativecomputing.gl4.texture.GLTexture2D;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix3x3;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector4;

import com.jogamp.opengl.GL4;

public class GLUniformParameters {
	
	private static abstract class GLUniformParameter{
		protected GLUniformInfo _myInfo;
		protected CCField<GLUniform> _myField;
		
		protected GLUniformParameter(GLUniformInfo theInfo, CCField<GLUniform> theField){
			_myInfo = theInfo;
			_myField = theField;
		}
		
		protected void checkParameters(GLUniformType...theTypes){
			for(GLUniformType myType:theTypes){
				if(_myInfo.type() == myType)return;
			}
			CCLog.error("parameter class: " + _myField.value().getClass().getName() + " does not fit uniform type " + _myInfo.type());
		}
		
		public abstract void apply(GLShaderProgram theProgram);
	}
	
	private static class GLUniformColorParameter extends GLUniformParameter{

		protected GLUniformColorParameter(GLUniformInfo theInfo, CCField<GLUniform> theField) {
			super(theInfo, theField);
			checkParameters(GLUniformType.FLOAT_VEC3, GLUniformType.FLOAT_VEC4);
		}

		@Override
		public void apply(GLShaderProgram theProgram) {
			CCColor myColor = (CCColor)_myField.value();
			switch(_myInfo.type()){
			case FLOAT_VEC3:
				theProgram.uniform3f(_myInfo.location(), (float)myColor.r, (float)myColor.g, (float)myColor.b);
				break;
			case FLOAT_VEC4:
				theProgram.uniform4f(_myInfo.location(),myColor);
				break;
			default:
			}
		}
		
	}
	
	private static class GLUniformVector4Parameter extends GLUniformParameter{

		protected GLUniformVector4Parameter(GLUniformInfo theInfo, CCField<GLUniform> theField) {
			super(theInfo, theField);
			checkParameters(GLUniformType.FLOAT_VEC3, GLUniformType.FLOAT_VEC4);
		}

		@Override
		public void apply(GLShaderProgram theProgram) {
			CCVector4 myVector = (CCVector4)_myField.value();
			switch(_myInfo.type()){
			case FLOAT_VEC3:
				theProgram.uniform3f(_myInfo.location(), (float)myVector.x, (float)myVector.y, (float)myVector.z);
				break;
			case FLOAT_VEC4:
				theProgram.uniform4f(_myInfo.location(), myVector);
				break;
			default:
			}
		}
		
	}
	
	private static class GLUniformFloatParameter extends GLUniformParameter{

		protected GLUniformFloatParameter(GLUniformInfo theInfo, CCField<GLUniform> theField) {
			super(theInfo, theField);
			checkParameters(GLUniformType.FLOAT);
		}

		@Override
		public void apply(GLShaderProgram theProgram) {
			theProgram.uniform1f(_myInfo.location(), (Float)_myField.value());
		}
		
	}
	
	private static class GLUniformIntParameter extends GLUniformParameter{

		protected GLUniformIntParameter(GLUniformInfo theInfo, CCField<GLUniform> theField) {
			super(theInfo, theField);
			checkParameters(GLUniformType.INT);
		}

		@Override
		public void apply(GLShaderProgram theProgram) {
			theProgram.uniform1i(_myInfo.location(), (Integer)_myField.value());
		}
		
	}
	
	private static class GLUniformMatrix3Parameter extends GLUniformParameter{

		protected GLUniformMatrix3Parameter(GLUniformInfo theInfo, CCField<GLUniform> theField) {
			super(theInfo, theField);
			checkParameters(GLUniformType.FLOAT_MAT3);
		}

		@Override
		public void apply(GLShaderProgram theProgram) {
			theProgram.uniformMatrix3f(_myInfo.location(), (CCMatrix3x3)_myField.value());
		}
		
	}
	
	private static class GLUniformMatrix4Parameter extends GLUniformParameter{

		protected GLUniformMatrix4Parameter(GLUniformInfo theInfo, CCField<GLUniform> theField) {
			super(theInfo, theField);
			checkParameters(GLUniformType.FLOAT_MAT4);
		}

		@Override
		public void apply(GLShaderProgram theProgram) {
			theProgram.uniformMatrix4f(_myInfo.location(), (CCMatrix4x4)_myField.value());
		}
		
	}
	
	private static class GLUniformSampler2DParameter extends GLUniformParameter{
		protected GLUniformSampler2DParameter(GLUniformInfo theInfo, CCField<GLUniform> theField) {
			super(theInfo, theField);
			checkParameters(GLUniformType.SAMPLER_2D);
		}

		@Override
		public void apply(GLShaderProgram theProgram) {
			GL4 gl = GLGraphics.currentGL();
			GLTexture2D myTexture = (GLTexture2D)_myField.value();
			gl.glActiveTexture(_myField.annotation().binding());
			myTexture.bind();
			theProgram.uniform1i(_myInfo.location(), _myField.annotation().binding());
		}
	}
	
	private List<GLUniformParameter> _myParameters = new ArrayList<>();
	
	private Object _myParentObject;

	
	protected GLUniformParameters(GLShaderProgram theProgram, Object theParent){
		_myParentObject = theParent;
		link(theProgram);
	}
	
	protected GLUniformParameters(GLShaderProgram theProgram){
		_myParentObject = this;
		link(theProgram);
	}
	
	private void link(GLShaderProgram theProgram){
		List<CCField<GLUniform>> myFields = CCReflectionUtil.getFields(_myParentObject, GLUniform.class);
		for(CCField<GLUniform> myField:myFields){
			GLUniformInfo myUniformInfo = theProgram.uniform(myField.annotation().name());
			if(myUniformInfo == null){
				CCLog.error(myField.annotation().name() + "not available as uniform");
				continue;
			}
			Class<?> myFieldClass = myField.value().getClass();
			if(myFieldClass == CCColor.class){
				_myParameters.add(new GLUniformColorParameter(myUniformInfo, myField));
			}else if(myFieldClass == CCVector4.class){
				_myParameters.add(new GLUniformVector4Parameter(myUniformInfo, myField));
			}else if(myFieldClass == Float.class){
				_myParameters.add(new GLUniformFloatParameter(myUniformInfo, myField));
			}else  if(myFieldClass == Integer.class){
				_myParameters.add(new GLUniformIntParameter(myUniformInfo, myField));
			}else if(myFieldClass == CCMatrix3x3.class){
				_myParameters.add(new GLUniformMatrix3Parameter(myUniformInfo, myField));
			}else if(myFieldClass == CCMatrix4x4.class){
				_myParameters.add(new GLUniformMatrix4Parameter(myUniformInfo, myField));
			}else if(myFieldClass == GLTexture2D.class){
				_myParameters.add(new GLUniformSampler2DParameter(myUniformInfo, myField));
			}
		}
	}
	
	public void apply(GLShaderProgram theProgram){
		for(GLUniformParameter myParameter:_myParameters){
			myParameter.apply(theProgram);
		}
	}
}
