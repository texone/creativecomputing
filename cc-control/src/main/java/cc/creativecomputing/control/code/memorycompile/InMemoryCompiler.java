package cc.creativecomputing.control.code.memorycompile;
import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MASSIVELY based on http://javapracs.blogspot.de/2011/06/dynamic-in-memory-compilation-using.html by Rekha Kumari
 * (June 2011)
 */
final public class InMemoryCompiler {

    final public static class IMCSourceCode {

        final public String fullClassName;
        final public String sourceCode;

        /**
         * @param fullClassName Full name of the class that will be compiled. If the class should be in some package,
         *                      fullName should contain it too, for example: "testpackage.DynaClass"
         * @param sourceCode    the source code
         */
        public IMCSourceCode(final String fullClassName, final String sourceCode) {

            this.fullClassName = fullClassName;
            this.sourceCode = sourceCode;
        }
    }

    final public boolean valid;

    final private List<IMCSourceCode> classSourceCodes;
    final private JavaFileManager fileManager;

    public InMemoryCompiler(final List<IMCSourceCode> classSourceCodes) {

        this.classSourceCodes = classSourceCodes;

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            fileManager = null;
            valid = false;
            System.err.println("ToolProvider.getSystemJavaCompiler() returned null! This program needs to be run on a system with an installed JDK.");
            return;
        }
        valid = true;

        fileManager = new ForwardingJavaFileManager<JavaFileManager>(compiler.getStandardFileManager(null, null, null)) {

            final private Map<String, ByteArrayOutputStream> byteStreams = new HashMap<>();

            @Override
            public ClassLoader getClassLoader(final Location location) {

                return new SecureClassLoader() {

                    @Override
                    protected Class<?> findClass(final String className) throws ClassNotFoundException {

                        final ByteArrayOutputStream bos = byteStreams.get(className);
                        if (bos == null) {
                            return null;
                        }
                        final byte[] b = bos.toByteArray();
                        return super.defineClass(className, b, 0, b.length);
                    }
                };
            }

            @Override
            public JavaFileObject getJavaFileForOutput(final Location location, final String className, final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {

                return new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {

                    @Override
                    public OutputStream openOutputStream() throws IOException {

                        ByteArrayOutputStream bos = byteStreams.get(className);
                        if (bos == null) {
                            bos = new ByteArrayOutputStream();
                            byteStreams.put(className, bos);
                        }
                        return bos;
                    }
                };
            }
        };
    }

    public CompilerFeedback compile() {

        if (!valid) {
            return null;
        }
        final List<JavaFileObject> files = new ArrayList<>();
        for (IMCSourceCode classSourceCode : classSourceCodes) {
            URI uri = null;
            try {
                uri = URI.create("string:///" + classSourceCode.fullClassName.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension);
            } catch (Exception e) {
                //                e.printStackTrace();
            }
            if (uri != null) {
                final SimpleJavaFileObject sjfo = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {

                    @Override
                    public CharSequence getCharContent(final boolean ignoreEncodingErrors) {

                        return classSourceCode.sourceCode;
                    }
                };
                files.add(sjfo);
            }
        }

        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (files.size() > 0) {
            final JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, files);
            return new CompilerFeedback(task.call(), diagnostics);
        } else {
            return null;
        }
    }

    public void runToString(final String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        if (!valid) {
            return;
        }
        final Class<?> theClass = getCompiledClass(className);
        final Object instance = theClass.newInstance();
        System.out.println(instance);
    }

    public void runMain(final String className, final String[] args) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {

        if (!valid) {
            return;
        }
        final Class<?> theClass = getCompiledClass(className);
        final Method mainMethod = theClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, new Object[] { args });
    }

    public Class<?> getCompiledClass(final String className) throws ClassNotFoundException {

        if (!valid) {
            throw new IllegalStateException("InMemoryCompiler instance not usable because ToolProvider.getSystemJavaCompiler() returned null: No JDK installed.");
        }
        final ClassLoader classLoader = fileManager.getClassLoader(null);
        final Class<?> ret = classLoader.loadClass(className);
        if (ret == null) {
            throw new ClassNotFoundException("Class returned by ClassLoader was null!");
        }
        return ret;
    }
}