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

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.TapestryModule;
import org.apache.tapestry5.services.assets.ContentTypeAnalyzer;
import org.apache.tapestry5.services.assets.ResourceTransformer;

/**
 * Module configuration for Tapestry-IOC. Installs support for Less and CoffeeScript. <p>
 * Created: 14 Sep 2011
 * </p>
 * 
 * @author Cezary Biernacki
 */

@SubModule( { TapestryModule.class } )
public class T5ConduitModule {
    public static void bind(ServiceBinder binder) {
        binder.bind(LessToCssTransformer.class);
        binder.bind(CoffeeToJsTransformer.class);
        binder.bind(ClassPathLoader.class);
    }
    
    @Contribute(SymbolProvider.class)
    @FactoryDefaults
    public static void setupDefaultSymbols(MappedConfiguration<String, String> configuration) {
    	configuration.add(T5ConduitConstants.COFFEE_SUFFIX, "coffee");
    	configuration.add(T5ConduitConstants.COFFEE_COMPILER, "coffee-script-1.3.1.js");
    	
    	configuration.add(T5ConduitConstants.LESS_SUFFIX, "less");
    	configuration.add(T5ConduitConstants.LESS_CTX_PATH_VAR_NAME, "CTX_PATH");
    	configuration.add(T5ConduitConstants.LESS_COMPILER, "less-1.3.0.js");
    	configuration.add(T5ConduitConstants.LESS_BEFORE_COMPILER, "less-before.js");
    	configuration.add(T5ConduitConstants.LESS_AFTER_COMPILER, "less-after.js");
    }
    
    @Contribute(ContentTypeAnalyzer.class)
    public static void setupContentTypeMappings(MappedConfiguration<String, String> configuration, 
    		@Inject @Symbol(T5ConduitConstants.COFFEE_SUFFIX) String coffeeSuffix,
    		@Inject @Symbol(T5ConduitConstants.LESS_SUFFIX) String lessSuffix
    		) {

    	if (!coffeeSuffix.isEmpty()) {
    		configuration.add(coffeeSuffix, "text/javascript");
    	}
    	
    	if (!lessSuffix.isEmpty()) {
    		configuration.add(lessSuffix, "text/css");
    	}
    }


    public static void contributeStreamableResourceSource(MappedConfiguration<String, ResourceTransformer> configuration,
    		CoffeeToJsTransformer coffeeTransformer,
    		LessToCssTransformer lessTransformer,
    		@Inject @Symbol(T5ConduitConstants.COFFEE_SUFFIX) String coffeeSuffix,
    		@Inject @Symbol(T5ConduitConstants.LESS_SUFFIX) String lessSuffix
    		)
    {
    	if (!coffeeSuffix.isEmpty()) {
    		configuration.add(coffeeSuffix, coffeeTransformer);
    	}
    	if (!lessSuffix.isEmpty()) {
    		configuration.add(lessSuffix, lessTransformer);
    	}
    }
}

