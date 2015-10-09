exports.handle=function(obj,response){
	response.writeHead(200, {'Content-Type': 'text/html'});
	var username=obj.username;
	var newPassword=obj.newPassword;
	var code=obj.code;
	if (username==null) response.end("ERROR");
	if (newPassword==null) response.end("ERROR");
	if (code==null) response.end("ERROR");
	global.collection.update({username:username,forget:parseInt(code)},{$set:{password:newPassword},$unset:{forget:1}});
	response.end('<html><head><title>Activation</title></head><body><h1><font face=arial>Successfully changed password</font></h1></body></html>');
}
