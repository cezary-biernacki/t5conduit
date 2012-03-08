// executed before original less compiler, prepares necessary environment 
// which is browser like, because 'rhino' version is not actually usable :-(

location = {
		
};

window = {
   less: {
	   env: 'production'
   }
};

document = {
   getElementsByTagName: function() { 
	   return []; 
   }
};
