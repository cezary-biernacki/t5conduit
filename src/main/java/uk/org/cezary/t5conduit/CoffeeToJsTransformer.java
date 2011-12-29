// Copyright 2011 Cezary Biernacki. Licensed under the Apache License, Version 2.0 (the "License").
//
// You may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package uk.org.cezary.t5conduit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import uk.org.cezary.t5conduit.internal.Pool;


/**
 * Transforms <code>.coffee</code> to JavaScript. 
 * <p>
 * Created: 15 Sep 2011
 * </p>
 * 
 * @author Cezary Biernacki
 * @see <a href="http://coffeescript.org/">http://coffeescript.org/</a>
 * 
 * Based on <a href="http://yeungda.github.com/jcoffeescript">JCoffeeScript</a>
 */

public class CoffeeToJsTransformer implements ResourceTransformer {

    private static final String COFFEE_JS = "coffee-script-1.2.0.js";

    private final Pool<Scriptable> pool = new Pool<Scriptable>(3, 
        	new Invokable<Scriptable>() {
    	    	@Override
    	    	public Scriptable invoke() {
    	    		return buildGlobalScope();
    			}
        	}
        );


    public CoffeeToJsTransformer() {}

	private Scriptable buildGlobalScope() {
        try {
			return buildGlobalScopeInternal();
		} catch (IOException e) {
			throw new RuntimeException("failed to compile: " + COFFEE_JS, e);
		}
	}

	private Scriptable buildGlobalScopeInternal()
			throws UnsupportedEncodingException, IOException {
		final Reader reader = new InputStreamReader(getClass().getResourceAsStream(COFFEE_JS), "UTF-8");

        try {
            Context context = Context.enter();
            try {
                context.setOptimizationLevel(-1); // Without this, Rhino hits a 64K bytecode limit and fails
        		Scriptable scope = context.initStandardObjects();
                context.evaluateReader(scope, reader, COFFEE_JS, 1, null);
                return scope;
            } finally {
                Context.exit();
            }
        } finally {
            reader.close();
        }
	}
	
	

    @Override
    public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
    	final StringBuilder b = new StringBuilder();
        
        final InputStreamReader reader = new InputStreamReader(source.openStream(), "UTF-8");
        try {
            int c;
            while ((c = reader.read()) != -1) {
                b.append((char) c);
            }
        } finally {
            reader.close();
        }
        
        final String result = this.compile(b.toString(), source);
        return new ByteArrayInputStream(result.getBytes("UTF-8"));
    }

    private synchronized String compile(final String sourceText, final Resource source) {
    	return pool.withObject(new Pool.Processor<String, Scriptable>() {
    		@Override
    		public String process(Scriptable globalScope) {
    	        return compileInternal(globalScope, sourceText, source);
    		}
		});
    }

	private String compileInternal(Scriptable globalScope, String sourceText, Resource source) {
		final Context context = Context.enter();
        try {
            Scriptable scope = context.newObject(globalScope);
            scope.setParentScope(globalScope);
            scope.put("source", scope, sourceText);
            return (String) context.evaluateString(scope, "CoffeeScript.compile(source);",
                    "'compiling: " + source.getPath() +"'", 0, null);
        } finally {
            Context.exit();
        }
	}
}
