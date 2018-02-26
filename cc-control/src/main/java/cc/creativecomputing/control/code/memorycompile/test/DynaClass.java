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
