exports.resetPage=function(obj,response){
	var username=obj.query.username;
	var code=obj.query.code;
	if (username==null) response.end("ERROR");
	if (code==null) response.end("ERROR");
	global.collection.findOne({username:username},function(err,item){
		if (item==null){
			response.end("Username doesn't exist");
		}else if (item.forget==null){
			response.end("This account is not under process of resetting password.");
		}else if (item.forget!=code){
			response.end("Invalid link");
		}else{
			response.end('\
<head><title>Reset Password</title></head>\
<body><p>&nbsp;</p>\
<form name="resetForm" method="POST" action="resetHandler">\
<p align="center">\
Username:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label><input type="text" name="username" id="username" value="'+username+'" readonly>\
</p><p align="center">\
<label>VerificationCode:&nbsp;&nbsp;&nbsp;</label><input type="text" name="code" id="code" value="'+code+'"readonly>\
</p><p align="center">\
<label>New Password:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="password" name="newPassword" id="newPassword" ></label>\
</p><p align="center">\
<input type="submit" name="submit" id="Login" value="Login">&nbsp;&nbsp;\
</p></form></body></html>');
		}
	});
}
