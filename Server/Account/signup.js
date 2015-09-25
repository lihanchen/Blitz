exports.signup=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item!=null){
				ret.success=false;
				ret.msg="Username already exists";
			}else{
				global.collection.insert({username:receivedObj.username,password:receivedObj.password,email:receivedObj.email,rating:0,hold:"unverified"});
				//Send verifi email
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
