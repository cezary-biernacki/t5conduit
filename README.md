Module for Tapestry 5.2/5.3 that allows seamless integration of LessCSS and CoffeeScript.

Just add uk.org.cezary.t5conduit.T5ConduitModule as submodule to AppModule using @SubModule.
T5ConduitModule installs resource transformers, that compile '\*.coffee' and '\*.less' resources 
to JavaScript or CSS before sending to a user browser.


Notes:

  * Compilation is done only once per file (unless file changed) and results are cached in memory, 
    so don't worry about performance.
    
  * Only separate resource files are compiled, so if you want to include inline style or javasscript
    blocks directly in '.tml', you still need to use CSS or JavaScript.

  * You can use '@import' directive in .less. By default all paths are relative, but you can 
    establish additional rules of searching for imported files.
    See: DependencySourceLoader, RelativeDependencySourceLoader and ClassPathLoader. 
       
  * Actual compilers are written in JavaScript and executed using Mozilla's Rhino 1.7R3 (rhino-1.7R3.jar) library,
  	include that library and - in case of any problems check - for potential conflicts.  
  	
  	A JAR with different version of Rhino maybe already on classpath in some environments.
  	For example Tapestry 5.3 includes by default js-1.6R7.jar.
  	
  * In your '\*.less' files you can use CTX_PATH variable, that contains the path to the application context,
    so you can avoid using relative paths. For example you can write:
     
    	background-image: url("@{CTX_PATH}/image/img.jpg")
    	
  * You can customise the musing using Java's system properties or Tapestry's ApplicationDefaults with following variables
    (see T5ConduitConstants):
       * *t5conduit.coffee-suffix* - a suffix of CoffeeScript files, default is 'coffee'; an empty string disables CoffeeScript support.
       
       * *t5conduit.less-suffix* - a suffix of LessCSS files, default is 'less'; an empty string disables LessCSS support.
       
       * *t5conduit.less-ctx-path-var-name* - the name of variable with the path to the app context added to .less files, 
          default is 'CTX_PATH'; an empty string disables adding such variable. 

Usage example:

	@Import(library="foo.coffee", stylesheet="bar.less")
	public class MyPage
	
 

If you want to use t5conduit with Tapestry 5.2, you must also add t52transformers 
(https://github.com/cezary-biernacki/t52transformers) module to your project.

You can find precompiled binaries, [here](https://github.com/cezary-biernacki/t5conduit/downloads). 
	

---
Prepared by Cezary Biernacki.

Licensed under the Apache License, Version 2.0 (the "License").

You may not use this file except in compliance with the License.
You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

See also copyright and license information for included CoffeScript and LessCSS compilers.
