package cc.creativecomputing.control.code.memorycompile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class CCInMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager>{

	final private Map<String, ByteArrayOutputStream> _myByteStreams = new HashMap<>();

	protected CCInMemoryFileManager(JavaCompiler theCompiler) {
		super(theCompiler.getStandardFileManager(null, null, null));
	}
	
	public void reset(){
		_myByteStreams.clear();
	}

    @Override
    public ClassLoader getClassLoader(final Location theLocation) {

        return new SecureClassLoader() {

            @Override
            protected Class<?> findClass(final String className) throws ClassNotFoundException {

                final ByteArrayOutputStream bos = _myByteStreams.get(className);
                if (bos == null) {
                    return null;
                }
                final byte[] b = bos.toByteArray();
                return super.defineClass(className, b, 0, b.length);
            }
        };
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
    	final Location location, 
    	final String className, 
    	final JavaFileObject.Kind kind, 
    	final FileObject sibling
    ) throws IOException {

        return new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {

            @Override
            public OutputStream openOutputStream() throws IOException {

                ByteArrayOutputStream bos = _myByteStreams.get(className);
                if (bos == null) {
                    bos = new ByteArrayOutputStream();
                    _myByteStreams.put(className, bos);
                }
                return bos;
            }
        };
    }
}
