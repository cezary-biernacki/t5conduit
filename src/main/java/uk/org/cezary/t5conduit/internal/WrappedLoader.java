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

package uk.org.cezary.t5conduit.internal;

import java.io.IOException;

import org.mozilla.javascript.Scriptable;

import uk.org.cezary.t5conduit.LessToCssTransformer;


/**
 * Internally used by {@link LessToCssTransformer}. <p>
 * Created: 5 Oct 2011
 * </p>
 * 
 * @author Cezary Biernacki
 */

public interface WrappedLoader {
    
    Scriptable readFile(String name) throws IOException;

}
