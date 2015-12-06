exports.upload=function (receivedObj,socket,picid){
	ObjectID = require('mongodb').ObjectID;
	BSON = require('mongodb').BSONPure;
	var ret = {};
console.log("upload module");
	try{
		if (receivedObj == null) {
			ret.success = false;
			ret.pictureId = -1;
			ret.msg = "no data provided"
			socket.write(JSON.stringify(ret));
			socket.destroy();
		}

		var id = "";
		id = picid;
		var objectid = new ObjectID(id);
		global.collection.update({"_id":objectid},{"_id":objectid,"data":receivedObj},
												function (err, result) {
													if (err){
														ret.success = false;
														ret.msg="error in update";
														throw err;
													}
													else{
														ret.success = true;
														ret.msg="success";
													}
													console.log(result);
													//socket.write(JSON.stringify(ret));
													socket.destroy();
												});

	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}
