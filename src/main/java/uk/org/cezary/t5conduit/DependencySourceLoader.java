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

import java.io.IOException;

import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.assets.ResourceDependencies;

/**
 * Interface for loading files requested from transformed files. 
 * Used to configure {@link LessToCssTransformer}, which calls it 
 * for resolving <code>@import</code> clauses in <code>.less</code> file.<p> 
 * <p>
 * Created: 5 Oct 2011
 * </p>
 * 
 * @author Cezary Biernacki
 */

public interface DependencySourceLoader {
    
    /**
     * Should read a given source file if it exits and return its content.   
     * @param name a file name with path, as specified in a parent source file.
     *  
     * @return a content of file or <code>null</code> if the file can not be found.
     */
    String readFile(String name, Resource baseResource, ResourceDependencies dependencies) throws IOException;
}


