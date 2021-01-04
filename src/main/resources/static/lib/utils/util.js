StringBuffer = function() {
	var array			=	[];
	this.append			=	function(str)		{array.push(str);};
	this.toString		=	function()			{return array.join('');};
};

ArrayList = function() {
	var array			=	[];
	this.add			=	function(o)			{array.push(o);};
	this.remove			=	function(o)			{var temp=[]; for(var i=0;i<array.length;i++){if(o!=array[i]){temp.push(array[i]);}}array=temp;};
	this.get			=	function(i)			{if(i<0||i+1>array.length){return null;}return array[i];};
	this.size			=	function()			{return array.length;};
};

HashMap = function() {
	var size			=	0;
	var entry			=	{};
	this.keys			=	function()			{var keys=[];for(var prop in entry){keys.push(prop);}return keys;};
	this.values			=	function()			{var values=[];for(var prop in entry){values.push(entry[prop]);}return values;};
	this.containsKey	=	function(k)			{return (k in entry);};
	this.containsValue	=	function(v)			{for(var prop in entry){if(entry[prop]==v){return true;}}return false;};
	this.get			=	function(k)			{return this.containsKey(k)?entry[k]:null;};
	this.put			=	function(k, v)		{if(!this.containsKey(k)){size++;}entry[k]=v;};
	this.remove			=	function(key)		{if(this.containsKey(key)&&(delete entry[key])){size--;}};
	this.clear			=	function()			{entry={};size=0;};
};

RandomChar			=	function(len) {
	var x = "0123456789qwertyuioplkjhgfdsazxcvbnm";
	var tmp = '';
	for(var i=0;i<len;i++){tmp += x.charAt(Math.ceil(Math.random()*100000000)%x.length);}
	return tmp;
};

String.prototype.startWith = function(str) {  
    if(str==null||str==""||this.length==0||str.length>this.length)  
      return false;  
    if(this.substr(0,str.length)==str)  
      return true;  
    else  
      return false;  
    return true;  
};