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
import org.apache.tapestry5.services.assets.ResourceDependencies;

/**
 * Reads a source code of dependency as relative to base resource.<p>
 * Created: 12 Oct 2011
 * </p>
 * 
 * @author Cezary Biernacki
 */

public class RelativeDependencySourceLoader implements DependencySourceLoader {

    @Override
    public String readFile(String name, Resource baseResource, ResourceDependencies dependencies) throws IOException {
        final Resource resource = baseResource.forFile(name);
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
