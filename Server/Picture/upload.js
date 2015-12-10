exports.upload=function (receivedObj,socket){
	ret = {};
	try{
		global.collection.insert({"data":receivedObj.data},function(err, docsInserted){
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
