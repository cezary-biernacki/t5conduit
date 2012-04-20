// executed after original less compiler
// 'support' and 'loader' objects set by LessToCssTransformer
(function(exports, window) { 

var less = window.less;

function printError(mainfile, filename, e) {
  var out = '';
  
  try {
	  for (var p in e) {
	    out += p + ': "' + e[p] + '", ';
	  }
  } catch(e2) {
	  out = '[double failure]';
  } 
  
  out = 'problem parsing: ' + filename + (mainfile != filename ? ' (top file: ' + mainfile + ')' : '')+ ' info: {' + out + '}';
  support.printFromParser(out);
  return out;
}

function loadStyleSheet(sheet, callback, mainParser) {
	
    var sheetName = sheet.href;
    var input = loader.readFile(sheetName);
    var paths = [];
    var filename = sheetName;
    var pos = sheetName.lastIndexOf('/');
    if (pos > 0) {
    	paths = [sheetName.substr(0,pos+1)];
    	filename = sheetName.substr(pos+1);
    }
    
    mainParser.imports.contents[filename] = input;
    
    var parser = new less.Parser({ paths: paths, filename: sheetName, mainParser: mainParser } );
    
    parser.parse(input, function (e, root) {
        if (e) {
        	mainParser.mainErrorReport(sheetName, e);
        } else {
        	try {
        		callback(null, root, input, sheet, { local: false, lastModified: 0, remaining: []});
        	} catch(e) {
        		mainParser.mainErrorReport(sheetName, e);
        	}
        }
    });

}

less.Parser.importer = function (path, paths, callback, env) {
	var title = path;
	if (!/^([a-z]+:)?\//.test(path) && paths.length > 0) {
        path = paths[0] + path;
    }
    
    loadStyleSheet({ href: path, title: title, type: env.mime }, callback, env.mainParser);
};


function doParse(input, compress, filename) {
    
	var env = {filename: filename};
    var parser = new less.Parser(env);
    env.mainParser = parser;
    
    var result = {v: '/* compiling failed, see logs */'};
    parser.mainErrorReport = function(subfile, e) {
    	var out = printError(filename, subfile, e);
    	result.v = "/* " + out + "*/\n";
    };
    
    try {
        parser.parse(input, function (e, root) {
            if (e) {
            	parser.mainErrorReport(filename, e);
                result.v = "/* error: " + out + "*/\n";
            } else {
                result.v = root.toCSS( { compress : compress } );
            }
        });
    } catch(e) {
        parser.mainErrorReport(filename, e);  
    }
    
    return result.v;
}

var oldTreeURL = less.tree.URL;
less.tree.URL = function (val, paths) {
    if (val.data) {
        this.attrs = val;
    } else {
    	if (!/^([a-z]+:)?(\/|@)/.test(val.value) && paths.length > 0) {
    		val.value = 'expanded:/' + paths[0] + val.value;
    	}
        this.value = val;
        this.paths = paths;
    }
};

less.tree.URL.prototype = {
	    toCSS: function () {
	    	var css;
	    	if (this.attrs) {
	    		css = 'data:' + this.attrs.mime + this.attrs.charset + this.attrs.base64 + this.attrs.data +")";
	    	} else {
	    		css = this.value.toCSS();
	    		css = css.replace(/^(\s*"|'|)expanded:\//, '$1');
	    	}
	    	
	    	return "url(" + css + ")";
	    	
	    },
	    
	    eval: function (ctx) {
	        return this.attrs ? this : new(less.tree.URL)(this.value.eval(ctx), this.paths);
	    }
	};

exports.doParse = doParse;

})(this, window);
