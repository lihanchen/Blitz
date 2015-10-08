exports.verifyPage=function(obj,response){
	var msg="";
	var username=obj.query.username;
	var code=obj.query.code;
	if (username==null) response.end("ERROR");
	if (code==null) response.end("ERROR");
	global.collection.findOne({username:username},function(err,item){
			if (item==null){
				msg="Username doesn't exist";
			}else if (item.hold==null){
				msg="Account has already been activated";
			}else if (item.hold.reason!="unverified"){
				msg="This account cannot be activated";
			}else if (item.hold.unlockCode!=code){
				msg="Invalid activation link";
			}else{
				collection.update({username:username},{$unset:{"hold":1}})
				msg="Successfully activate your account";
			}
		response.writeHead(200, {'Content-Type': 'text/html'});
		response.end('<html><head><title>Activation</title></head><body><h1><font face=arial>'+msg+'</font></h1></body></html>');
	});
}
