exports.signup=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item!=null){
				ret.success=false;
				ret.msg="Username already exists";
			}else{
				var unlockCode=Math.round(Math.random()*100000000);
				global.collection.insert({username:receivedObj.username,password:receivedObj.password,email:receivedObj.email,rating:0,ratingCount:0,hold:{reason:"unverified",unlockCode:unlockCode}});
				var emailModule=require("./sendEmail");
				var link='http://127.0.0.1:5006/verify?username='+receivedObj.username+'&code='+unlockCode;
				var email = {
				    from: 'Blitz <lhcmaiche@gmail.com.',
						to: receivedObj.email,
						subject: 'Activate Your Account',
						html: '\
Hello,<br/>\
You have just created your Blitz account. Please click the following link to activate it.<br/>\
<a href="'+link+'">'+link+' </a><br/>\
<br/>\
Yours Sincerely,<br/>\
Blitz'
				};
				emailModule.send(email);
				ret.success=true;
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
