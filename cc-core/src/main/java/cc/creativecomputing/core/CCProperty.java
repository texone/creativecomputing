package cc.creativecomputing.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CCProperty {

	String name() default "";
	
	String desc() default "";
	
	double min()  default -1;
	
	double max()  default -1;
	
	String precision() default "";
	
	double defaultValue() default Double.NaN;
}
