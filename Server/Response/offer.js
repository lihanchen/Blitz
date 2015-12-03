exports.offer=function (receivedObj,socket){
	var ret={};
	try{
		var id=require("mongodb").ObjectID(receivedObj.postID);
		global.collection.findOne({"_id":id},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Post doesn't exist";
			}else if(item.TransactionCompleted==true){
				ret.success=false;
				ret.msg="Transaction is already completed";
			}else{
				global.collection.update({"_id":id},{$pull:{response:{username:receivedObj.username}}},function(err,item){
					global.collection.update({"_id":id},{$push:{response:{username:receivedObj.username,comment:receivedObj.comment,bounty:receivedObj.offeredPrice}}});
				});
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
