Module for Tapestry 5.3 that seemlessly integrate LessCSS and CoffeeScript.

Just add uk.org.cezary.t5conduit.T5ConduitModule as submodule to AppModule using @SubModule.
After that all your '*.coffee' and '*.less' resources will be automatically compiled to 
JavaScript or CSS before sending to a user browser. 

If you want to use t5conduit with Tapestry 5.2, add t52transformers (https://github.com/cezary-biernacki/t52transformers) module to your project.

You can find already compiled binaries here
    https://github.com/cezary-biernacki/t5conduit-bin

---
Written by Cezary Biernacki.

Licensed under the Apache License, Version 2.0 (the "License").

You may not use this file except in compliance with the License.
You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
