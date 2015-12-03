exports.deletePost=function (receivedObj,socket){
	var ret={};
	try{
		ObjectID = require('mongodb').ObjectID;
		var objectid = new ObjectID(receivedObj.postID);
		global.collection.remove({"_id":objectid});

		ret.success = true;
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}
