// Copyright 2011 Cezary Biernacki., Licensed under the Apache License, Version 2.0 (the "License").
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

import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


/**
 * Transforms <code>.coffee</code> to JavaScript. 
 * <p>
 * Created: 15 Sep 2011
 * </p>
 * 
 * @author Cezary Biernacki
 * @see <a href="http://coffeescript.org/">http://coffeescript.org/</a>
 */

public class CoffeeToJsTransformer implements ResourceTransformer {

    private static final String COFFEE_JS = "coffee-script-1.1.2.js";
    
    private final Scriptable globalScope;

    public CoffeeToJsTransformer() throws IOException {
        final Reader reader = new InputStreamReader(getClass().getResourceAsStream(COFFEE_JS), "UTF-8");

        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1); // Without this, Rhino hits a 64K bytecode limit and
                                              // fails
            try {
                globalScope = context.initStandardObjects();
                context.evaluateReader(globalScope, reader, COFFEE_JS, 0, null);
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
        
        final String result = this.compile(b.toString());
        return new ByteArrayInputStream(result.getBytes("UTF-8"));
    }

    private String compile(String source) {

        final Context context = Context.enter();
        try {
            Scriptable scope = context.newObject(globalScope);
            scope.setParentScope(globalScope);
            scope.put("source", scope, source);
            return (String) context.evaluateString(scope, "CoffeeScript.compile(source);",
                    "CoffeeToJsTransformer", 0, null);
        } finally {
            Context.exit();
        }
    }
}
