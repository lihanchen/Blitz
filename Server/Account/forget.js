exports.forget=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else if (item.email!=receivedObj.email){
				ret.success=false;
				ret.msg="Username and Email don't match";
			}else{
				var unlockCode=Math.round(Math.random()*1000000);
				global.collection.update({username:receivedObj.username},{$set:{forget:unlockCode}}); 
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

