package uk.org.cezary.t5conduit.internal;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A dummy Environment object necessary to force Less compiler to detect Rhino
 * @author cezary
 
 */
public class EnvironmentDummy extends ScriptableObject {
	private static final long serialVersionUID = 1L;

	@Override
	public String getClassName() {
		return "Environment";
	}
	
    public static void init(Scriptable scope) {
    	final EnvironmentDummy dummy = new EnvironmentDummy();
    	dummy.setParentScope(scope);
    	scope.put("environment", scope, dummy);
    }


}
