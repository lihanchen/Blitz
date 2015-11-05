exports.createnew=function (receivedObj,socket){
	ObjectID = require('mongodb').ObjectID;
	BSON = require('mongodb').BSONPure;
	var ret = {};
console.log("create new");
	try{
		if (receivedObj == null) {
			ret.success = false;
			ret.pictureId = -1;
			ret.msg = "no data json provided"
			socket.write(JSON.stringify(ret));
			socket.destroy();
		}
		//var binarydata = new BSON.Binary(receivedObj);
		var emptystr = "";
		var finish = 1;
		global.collection.insert({data:emptystr},
									function(err,docsInserted){
										if(err){
											ret.success=false;
											ret.msg="error in insert";
											finish = 0;
										}
										else{
											ret.success=true;
											ret.id=docsInserted.ops[0]._id;
											finish = 0;
											console.log(docsInserted);
										}
										var succsend = socket.write(JSON.stringify(ret));
										global.id = ret.id;
										socket.end();
									});

		return ret.id;
	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}