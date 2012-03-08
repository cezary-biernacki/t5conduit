// executed after original less compiler
// 'support' and 'loader' objects set by LessToCssTransformer  
less = window.less;

function printError(name, e) {
  var out = '';
  
  for (var p in e) {
    out += p + ': "' + e[p] + '", ';
  }
  
  out = 'problem parsing: ' + name + ' (top level) info: {' + out + '}';
  support.printFromParser(out);
  return out;
}

function loadStyleSheet(sheet, callback, reload, remaining) {
    var sheetName = sheet.href;
    var input = loader.readFile(sheetName);
    var parser = new less.Parser();
    parser.parse(input, function (e, root) {
        if (e) {
            printError(sheetName, e);
        }
        
        callback(null, root, sheet, { local: false, lastModified: 0, remaining: remaining });
    });

}

less.Parser.importer = function (path, paths, callback, env) {
    if (path.charAt(0) !== '/' && paths.length > 0) {
        path = paths[0] + path;
    }
    loadStyleSheet({ href: path, title: path, type: env.mime }, callback, true);
};


function doParse(input, compress, filename) {
    
    var parser = new less.Parser({'filename': filename});
    var result = new Object();
    try {
        parser.parse(input, function (e, root) {
            if (e) {
                var out = printError(filename, e);
                result.v = "/* error: " + out + "*/\n";
            } else {
                result.v = root.toCSS( { compress : compress } );
            }
        });
    } catch(e) {
        var out = printError(filename, e);  
        return "/* exception: " + out + "*/\n";
    }
    
    return result.v;
}
