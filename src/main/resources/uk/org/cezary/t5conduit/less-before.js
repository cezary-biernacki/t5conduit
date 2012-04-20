// executed before original less compiler, prepares necessary environment 
// which is browser like, because 'rhino' version is not actually usable :-(

location = {
		protocol: '',
		hostname: '',
		port: ''
};

window = {};

document = {
   getElementsByTagName: function() { 
	   return []; 
   }
};
