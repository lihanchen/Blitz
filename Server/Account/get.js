exports.get=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				ret=item;
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
