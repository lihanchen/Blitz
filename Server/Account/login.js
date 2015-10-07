exports.login=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				if (item.password==receivedObj.password){
					if (item.hold==null){
						ret.success=true;
					}else if (item.hold.reason=="unverified"){
						ret.success=false;
						ret.msg="Email address not verified";
					}else{
						ret.success=false;
						ret.msg="Account suspended";
					}
				}else{
					ret.success=false;
					ret.msg="Password doesn't match";
				}
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
