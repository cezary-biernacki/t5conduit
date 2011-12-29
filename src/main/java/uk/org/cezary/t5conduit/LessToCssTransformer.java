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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.annotations.UsesConfiguration;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.cezary.t5conduit.internal.Pool;
import uk.org.cezary.t5conduit.internal.WrappedLoader;



/**
 * Transforms <code>.less</code> to CSS. <p>
 * Created: 5 Oct 2011
 * </p>
 * 
 * @author Cezary Biernacki
 * 
 * @see <a href="http://lesscss.org/">http://lesscss.org/</a>
 */
@UsesConfiguration(DependencySourceLoader.class)
public class LessToCssTransformer implements ResourceTransformer {
    private static final Logger log = LoggerFactory.getLogger(LessToCssTransformer.class);
    
    private static final String LESS_JS = "less-1.1.5.js";
    private final boolean productionMode; 
    
    private final List<DependencySourceLoader> loaders;
    
    private final String ctxPathVarName; 
    private final AssetSource assetSource;
    
    
    private final Pool<Scriptable> pool = new Pool<Scriptable>(3, 
    	new Invokable<Scriptable>() {
	    	@Override
	    	public Scriptable invoke() {
	    		return buildGlobalScope();
			}
    	}
    );

    public LessToCssTransformer(
                final List<DependencySourceLoader> loaders, 
                @Inject @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode,
                @Inject @Symbol(T5ConduitConstants.LESS_CTX_PATH_VAR_NAME) String ctxPathVarName,
                AssetSource assetSource) 
    
    throws IOException {
        this.productionMode = productionMode;
        
        this.ctxPathVarName = ctxPathVarName;
        this.assetSource = assetSource;
        
        final ArrayList<DependencySourceLoader> ownLoaders = new ArrayList<DependencySourceLoader>(loaders);
        ownLoaders.add(new RelativeDependencySourceLoader());
        this.loaders = ownLoaders;
        
    }

	private Scriptable buildGlobalScope() {
        try {
			return buildGlobalScopeInternal();
		} catch (IOException e) {
			throw new RuntimeException("Failed to prepare less compiler from " + LESS_JS, e);
		}
	}

	private Scriptable buildGlobalScopeInternal() throws IOException {
		Context context = Context.enter();
        context.setOptimizationLevel(-1); // Without this, Rhino hits a 64K bytecode limit and
                                          // fails
        
        try {
            ScriptableObject scope = context.initStandardObjects();
            scope.put("support", scope, this);
            context.evaluateString(scope, "function print(txt) { return support.printFromParser(txt); };", "init", 2, null);
            
            evaluateFile(scope, context, "less-before.js");
            evaluateFile(scope, context, LESS_JS);
            evaluateFile(scope, context, "less-after.js");
            return scope;
        } finally {
            Context.exit();
        }
	}
	
	private void evaluateFile(ScriptableObject scope, Context context, String name) throws IOException {
		final Reader reader = new InputStreamReader(getClass().getResourceAsStream(name), "UTF-8");
		try {
			context.evaluateReader(scope, reader, name, 1, null);
		} finally {
			reader.close();
		}
	}

    @Override
    public InputStream transform(Resource resource, ResourceDependencies dependencies) throws IOException {
        
        if (resource.toURL().toString().contains("/hidden/")) {

            return new ByteArrayInputStream("/* hidden *.less file can not be fetched directly */\n".getBytes("UTF-8"));
        }
        
        final StringBuilder b = new StringBuilder();
        if (hasCtxPath()) {
	        final Asset ctx = assetSource.getUnlocalizedAsset("context:/");
	        final String ctxUrl = ctx.toClientURL();
	        b.append(String.format("@%s: \"%s\";\n", ctxPathVarName, ctxUrl));
        }
        
        final Reader reader = new BufferedReader(new InputStreamReader(resource.openStream(), "UTF-8"));
        try {
            int c;
            while ((c = reader.read()) != -1) {
                b.append((char) c);
            }
        } finally {
            reader.close();
        }
        
        final String result = this.compile(b.toString(), resource, dependencies);
        return new ByteArrayInputStream(result.getBytes("UTF-8"));
    }

	private boolean hasCtxPath() {
		return !ctxPathVarName.isEmpty();
	}

    private synchronized String compile(final String source, final Resource resource, final ResourceDependencies dependencies) {
        return pool.withObject(new Pool.Processor<String, Scriptable>() {
        	@Override
        	public String process(Scriptable gs) {
        		return compileInternal(source, resource, dependencies, gs);
        	}
		});
    }

	private String compileInternal(final String source,
			final Resource resource, final ResourceDependencies dependencies, final Scriptable gs) {
        final Context context = Context.enter();
        try {
			Scriptable scope = context.newObject(gs);
			scope.setParentScope(gs);
			scope.put("source", scope, source);
			
			final WrappedLoader wrappedLoader = prepareLoader(resource, dependencies, gs);
			final Scriptable jsLoader = Context.toObject(wrappedLoader, gs);
			gs.put("loader", gs, jsLoader);
			
			final String shouldCompress = this.productionMode ? "true" : "false";
			final String cmd = String.format("doParse(source, %s, '%s');", shouldCompress, resource.getPath());
			final String result = (String) context.evaluateString(scope, cmd, 
					String.format("%s compiling '%s'", LESS_JS, resource.getPath()), 
					hasCtxPath() ? 0 : 1, null);
			return result;
        } finally {
        	try {
			gs.delete("loader");
        	} finally { 
        		Context.exit();
        	}
        }
	}

    private WrappedLoader prepareLoader(final Resource resource, final ResourceDependencies dependencies, final Scriptable scope) {
        final DependencySourceLoader aggregatedLoader = new DependencySourceLoader() {
            @Override
            public String readFile(String name, Resource baseResource, ResourceDependencies dependencies) {
                
                for (DependencySourceLoader loader : loaders) {
                    String result;
                    try {
                        result = loader.readFile(name, baseResource, dependencies);
                    } catch (IOException e) {
                        log.error("failed to read: " + name, e);
                        return String.format("\n/*\n ---\n ---\n --- failed to read: '%s' because of %s\n ---\n ---\n*/\n", name, e.getMessage()); 
                    }
                    
                    if (result != null) {
                        if (!productionMode) {
                            result = String.format("\n/* --- %s ---- */\n%s\n/* --- */\n", name, result); 
                        }
                        return result;
                    }
                }
                
                log.warn("can not find file : '{}' referenced from resource '{}'", name, resource);
                return String.format("\n/*\n ---\n ---\n --- missing file '%s'\n ---\n ---\n*/\n", name);
            }
        };
        
        final WrappedLoader wrappedLoader = new WrappedLoader() {
            @Override
            public Scriptable readFile(String name) throws IOException {
                return Context.toObject(aggregatedLoader.readFile(name, resource, dependencies), scope);
            }
        };
        return wrappedLoader;
    }
    
    public void printFromParser(String message) {
        log.warn("parser printed: {}", message);
    }
}
