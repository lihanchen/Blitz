exports.deletePost=function (receivedObj,socket){
	var ret={};
	try{
		

		ObjectID = require('mongodb').ObjectID;
		var id = "";
		id = receivedObj.id;
		var objectid = new ObjectID(id);
		global.collection.remove({"_id":objectid});

		ret.success = true;
		ret.id = objectid;
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}
