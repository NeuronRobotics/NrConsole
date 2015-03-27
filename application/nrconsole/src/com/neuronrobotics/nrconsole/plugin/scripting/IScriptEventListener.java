package com.neuronrobotics.nrconsole.plugin.scripting;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

public interface IScriptEventListener {
	
	void onGroovyScriptFinished(GroovyShell shell, Script script, Object result);
	
	void onGroovyScriptError(GroovyShell shell, Script script, Exception except);

}
