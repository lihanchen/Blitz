exports.createPost=function (receivedObj,socket){
	var ret={};
	try{
		delete receivedObj['operation'];
		var now = new Date();
		receivedObj.postTime = now.toISOString();
		receivedObj.TranscationCompleted = false;
		global.collection.insert(receivedObj,function(err,docsInserted){
			if(err){
				ret.success=false;
				ret.msg="error in insert";
			}
			else{
				ret.success=true;
				ret.id=docsInserted.ops[0]._id;
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
