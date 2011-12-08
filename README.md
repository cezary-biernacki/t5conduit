Module for Tapestry 5.2/5.3 that allows seamless integration of LessCSS and CoffeeScript.

Just add uk.org.cezary.t5conduit.T5ConduitModule as submodule to AppModule using @SubModule.
T5ConduitModule installs resource transformers, that compile '\*.coffee' and '\*.less' resources 
to JavaScript or CSS before sending to a user browser.


Notes:

  * compilation is done only once per file (unless file changed) and results are cached in memory, 
    so don't worry about performance;
    
  * only separate resource files are compiled, so if you want to include inline style or javasscript
    blocks directly in '.tml', you still need to use CSS or JavaScript;

  * you can use '@import' directive in .less, by default all paths are relative, but you can 
    establish additional rules of searching for imported files, see 
    DependencySourceLoader, RelativeDependencySourceLoader and ClassPathLoader. 
       
  * actual compilers are written in JavaScript and executed using Mozilla's Rhino 1.7 (js-rhino-1.7.jar) library,
  	include that library and in case of problems check for potential conflicts.  
  	A jar with different version of Rhino maybe already on classpath in some environments, for example 
  	Tapestry 5.3 includes by default js-1.6R7.jar.   

Usage example:

	@Import(library="foo.coffee", stylesheet="bar.less")
	class MyPage
	
 

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
