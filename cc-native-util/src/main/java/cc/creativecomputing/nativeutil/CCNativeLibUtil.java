package cc.creativecomputing.nativeutil;

import java.io.File;
import java.lang.reflect.Field;

import com.jogamp.common.jvm.JNILibLoaderBase;
import com.jogamp.common.util.cache.TempJarCache;

public class CCNativeLibUtil {

	public static void prepareLibraryForLoading(Class<?> theClass, String theLibName) {
		TempJarCache.initSingleton();
		JNILibLoaderBase.addNativeJarLibs(new Class<?>[] { theClass }, theLibName);
		String myPath = TempJarCache.findLibrary(theLibName);
		System.out.println(myPath);
		File myFile = new File(myPath).getParentFile();
		System.setProperty("java.library.path", myFile.getAbsolutePath());

		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
