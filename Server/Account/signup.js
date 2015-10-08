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
				//Send verifi email
				ret.success=true;
				ret.unlockCode=unlockCode; //temporary
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

//temporary
exports.verify=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				if (item.hold==null)
					ret.success=true;
				else if (item.hold.reason!="unverified"){
						ret.success=false;
						ret.msg="This account cannot be activated";
				}else if (item.hold.unlockCode!=receivedObj.unlockCode){
							ret.success=false;
							ret.msg="Invalid activation link";
				}else{
					global.collection.update({username:"lhc4"},{$unset:{"hold":1}});
					ret.success=true;
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
