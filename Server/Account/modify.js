exports.modify=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				var operations={};
				receivedObj.changes.forEach(function(change){
					for (var prop in change) {
					  operations[prop] = change[prop];
					}  
				});
				global.collection.update({username:receivedObj.username},{"$set":operations});
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
