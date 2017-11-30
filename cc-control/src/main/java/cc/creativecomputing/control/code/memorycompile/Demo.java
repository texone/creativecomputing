package cc.creativecomputing.control.code.memorycompile;

import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void main(final String[] args) {

        final InMemoryCompiler.IMCSourceCode cls1source;
        final InMemoryCompiler.IMCSourceCode cls2source;

        final StringBuilder sb = new StringBuilder();
        sb.append("package toast;\n");
        sb.append("public class DynaClass {\n");
        sb.append("    public static void main(final String[] args) {");
        sb.append("        System.out.println(\"Based massively on the work of Rekha Kumari, http://javapracs.blogspot.de/2011/06/dynamic-in-memory-compilation-using.html\");\n");
        sb.append("        System.out.println(\"This is the main method speaking.\");\n");
        sb.append("        System.out.println(\"Args: \" + java.util.Arrays.toString(args));\n");
        sb.append("        final Test test = new Test();\n");
        sb.append("    }\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"Hello, I am \" + ");
        sb.append("this.getClass().getSimpleName();\n");
        sb.append("    }\n");
        sb.append("}\n");
        cls1source = new InMemoryCompiler.IMCSourceCode("toast.DynaClass", sb.toString());

        sb.setLength(0);
        sb.append("package toast;\n");
        sb.append("public class Test {\n");
        sb.append("    public Test() {\n");
        sb.append("        System.out.println(\"class Test constructor reporting in.\");\n");
        sb.append("        System.out.println(new DynaClass());\n");
        sb.append("    }\n");
        sb.append("}\n");
        cls2source = new InMemoryCompiler.IMCSourceCode("toast.Test", sb.toString());

        final List<InMemoryCompiler.IMCSourceCode> classSources = new ArrayList<>();
        classSources.add(cls1source);
        classSources.add(cls2source);

        final InMemoryCompiler uCompiler = new InMemoryCompiler(classSources);
        final CompilerFeedback compilerFeedback = uCompiler.compile();
        System.out.println("\n\nCOMPILER FEEDBACK: " + compilerFeedback);

        if (compilerFeedback != null && compilerFeedback.success) {

            try {
                System.out.println("\nTOSTRING DEMO:");
                uCompiler.runToString(cls1source.fullClassName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("\nMAIN DEMO:");
                uCompiler.runMain(cls1source.fullClassName, new String[] { "test1", "test2" });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}