// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
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

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.annotations.UsesConfiguration;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final String LESS_JS = "less-rhino-1.1.3-hacked.js";
    private final Scriptable globalScope;
    private final boolean productionMode; 
    
    private final List<DependencySourceLoader> loaders;
    
    private final Object compileLock = new Object();

    public LessToCssTransformer(
                final List<DependencySourceLoader> loaders, 
                @Inject @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) 
    
    throws IOException {
        this.productionMode = productionMode;
        
        final ArrayList<DependencySourceLoader> ownLoaders = new ArrayList<DependencySourceLoader>(loaders);
        ownLoaders.add(new RelativeDependencySourceLoader());
        this.loaders = ownLoaders;
        
        final Reader reader = new InputStreamReader(getClass().getResourceAsStream(LESS_JS), "UTF-8");

        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1); // Without this, Rhino hits a 64K bytecode limit and
                                              // fails
            
            try {
                globalScope = context.initStandardObjects();
                globalScope.put("support", globalScope, this);
                context.evaluateString(globalScope, "function print(txt) { return support.printFromParser(txt); };", "init", 2, null);
                
                context.evaluateReader(globalScope, reader, LESS_JS, 0, null);
            } finally {
                Context.exit();
            }
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

    private String compile(String source, final Resource resource, final ResourceDependencies dependencies) {
        
        final Context context = Context.enter();
        try {
            
            final WrappedLoader wrappedLoader = prepareLoader(resource, dependencies);
            
            Scriptable scope = context.newObject(globalScope);
            scope.setParentScope(globalScope);
            scope.put("source", scope, source);

            synchronized (compileLock) {
                final Scriptable jsLoader = Context.toObject(wrappedLoader, globalScope);
                globalScope.put("loader", globalScope, jsLoader);
                context.evaluateString(globalScope, "function readFile(name) { return loader.readFile(name); };", "init", 1, null);
                
                
                final String shouldCompress = this.productionMode ? "true" : "false";
                final String cmd = String.format("doParse(source, %s);", shouldCompress);
                final String result = (String) context.evaluateString(scope, cmd, "LessToCssTransformer", 0, null);
                globalScope.delete("readFile");
                globalScope.delete("loader");
                return result;
            }
        } finally {
            Context.exit();
        }
    }

    private WrappedLoader prepareLoader(final Resource resource, final ResourceDependencies dependencies) {
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
                return Context.toObject(aggregatedLoader.readFile(name, resource, dependencies), globalScope);
            }
        };
        return wrappedLoader;
    }
    
    public void printFromParser(String message) {
        log.warn("parser printed: {}", message);
    }
}
