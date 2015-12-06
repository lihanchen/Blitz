exports.get=function (receivedObj,socket){
	var ret={};
	try{
		var id=require("mongodb").ObjectID(receivedObj.postID);
		global.collection.findOne({"_id":id},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Post doesn't exist";
			}else{
				ret.success=true;
				ret.object=item;
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
