exports.upload=function (receivedObj,socket){
	ObjectID = require('mongodb').ObjectID;
	BSON = require('mongodb').BSONPure;
	var ret = {};
console.log("1");
	try{
		if (receivedObj == null) {
			ret.success = false;
			ret.pictureId = -1;
			ret.msg = "no data provided"
			socket.write(JSON.stringify(ret));
			socket.destroy();
		}
		//var binarydata = new BSON.Binary(receivedObj);
		global.collection.insert({data:receivedObj},
									function(err,docsInserted){
										if(err){
											ret.success=false;
											ret.msg="error in insert";
										}
										else{
											ret.success=true;
											ret.id=docsInserted.ops[0]._id;
											
											console.log(docsInserted);
										}
										socket.write(JSON.stringify(ret));
										socket.destroy();
									});
/*		global.collection.findOne({data:receivedObj},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Error in insert";
			}else{
				ret.success=true;
				ret.pictureId=item._id;
			}
			socket.write(JSON.stringify(ret));
			socket.destroy();
		});		*/

		
		
	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}

/*
exports.upload=function (receivedObj,socket){
	ObjectID = require('mongodb').ObjectID;
	var ret = {};
	try{
		if (receivedObj.data == null) {
			ret.success = false;
			ret.pictureId = -1;
			ret.msg = "no data provided"
			socket.write(JSON.stringify(ret));
			socket.destroy();
		}

		global.collection.insert({data:receivedObj.data});
		global.collection.findOne({data:receivedObj.data},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Error in insert";
			}else{
				ret.success=true;
				ret.pictureId=item._id;
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
}*/
