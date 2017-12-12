package cc.creativecomputing.control.code.memorycompile.test;

public class DynaClass {
    public static void main(final String[] args) {
        System.out.println("TEXONE Based massively on the work of Rekha Kumari, http://javapracs.blogspot.de/2011/06/dynamic-in-memory-compilation-using.html");
        System.out.println("This is the main method speaking.");
        System.out.println("Args: " + java.util.Arrays.toString(args));
        final Test test = new Test();
    }
    
    
    public String toString() {
        return "Hello1, I am " + 
		this.getClass().getSimpleName() +  " " + new Test().toString() + " ; " + new TestNewClass().check();
    }
}