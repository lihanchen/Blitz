exports.forget=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				var unlockCode=Math.round(Math.random()*1000000000);
				global.collection.update({username:receivedObj.username},{$set:{forget:unlockCode}}); 
				var emailModule=require("./sendEmail");
				var link='http://127.0.0.1:5006/reset?username='+receivedObj.username+'&code='+unlockCode;
				var email = {
				    from: 'Blitz <lhcmaiche@gmail.com.',
						to: item.email,
						subject: 'Reset Your Password',
						html: '\
Hello,<br/>\
You tried to reset your password. Click the following link to continue<br/>\
<a href="'+link+'">'+link+' </a><br/>\
<br/>\
Yours Sincerely,<br/>\
Blitz'
				};
				emailModule.send(email);
				ret.success=true;
				ret.email=item.email;
			}
			socket.write(JSON.stringify(ret));
			socket.destroy();
		}); 
	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}

