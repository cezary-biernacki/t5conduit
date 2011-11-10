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
import org.apache.tapestry5.ioc.annotations.SubModule;
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
        binder.bind(ClassPathLoader.class);
    }
    
    @Contribute(ContentTypeAnalyzer.class)
    public static void setupContentTypeMappings(MappedConfiguration<String, String> configuration) {
        configuration.add("coffee", "text/javascript");
        configuration.add("less", "text/css");
    }


    public static void contributeStreamableResourceSource(MappedConfiguration<String, ResourceTransformer> configuration,
            LessToCssTransformer lessTransformer) {
        configuration.addInstance("coffee", CoffeeToJsTransformer.class);
        configuration.add("less", lessTransformer);
    }
    

}

