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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.services.assets.ResourceDependencies;

/**
 * A helper service that allows loading files relative to a class loader. Useful for implementing {@link DependencySourceLoader},
 * used by {@link LessToCssTransformer}.  
 * Created: 5 Oct 2011
 * </p>
 * 
 * @author Cezary Biernacki
 */

public class ClassPathLoader {

    
    public String readFile(Class<?> clazz, String prefix, String name, Resource baseResource, ResourceDependencies dependencies) throws IOException {
        if (!name.startsWith(prefix)) {
            return null;
        }

        final String strippedName = name.substring(prefix.length());
        final String resolvedName;
        if (!strippedName.startsWith("/")) {
            resolvedName = String.format("/%s/%s", clazz.getPackage().getName().replace('.', '/'), strippedName);
        } else {
            resolvedName = strippedName;
        }
        
        final ClasspathResource resource = new ClasspathResource(clazz.getClassLoader(), resolvedName);
        if (!resource.exists()) {
            return null;
        }
        
        dependencies.addDependency(resource);

        final Reader reader = new BufferedReader(new InputStreamReader(resource.toURL().openStream(), "UTF-8"));
        try {
            final StringBuilder b = new StringBuilder();
            
            int c;
            while ((c = reader.read()) != -1) {
                b.append((char) c);
            }
            
            return b.toString();
        } finally {
            reader.close();
        }
    }


}
