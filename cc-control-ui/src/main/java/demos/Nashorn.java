package demos;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Nashorn {
	public static void main(String[] args) throws ScriptException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval("print('Hello World!');");
	}
}
