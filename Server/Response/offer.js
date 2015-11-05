exports.offer=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({_id:new ObjectID(receivedObj.postID)},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Post doesn't exist";
			}else if(item.TranscationCompleted==true){
				ret.success=false;
				ret.msg="Transcation is already completed";
			}else{
				global.collection.update({postID:receivedObj.postID},{$pull:{response:{username:receivedObj.username}}},function(err,item){
					global.collection.update({postID:receivedObj.postID},{$push:{response:{username:receivedObj.username,comment:receivedObj.comment,bounty:receivedObj.offeredPrice}}});
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
