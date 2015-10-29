exports.createPost=function (receivedObj,socket){
	var ret={};
	var data={};
	try{
		var pictures = [];
		var response = [];	
		var i = 0;
		for(i = 0;i<receivedObj.photo.length;i++){
			pictures.push(receivedObj.photo[i]);
		}
		//get server time
		var now = new Date();

		data.postTime = now.toISOString();
		data.username = receivedObj.username;
		data.position = receivedObj.position;
		data.description = receivedObj.description;
		data.quantity = receivedObj.quantity;
		data.title = receivedObj.title;
		data.bounty = receivedObj.bounty;
		data.contact = receivedObj.contact;
		data.TranscationCompleted = false;
		data.photo = pictures;
		data.response = response;
		data.isRequest = receivedObj.isRequest;
		data.category = receivedObj.category;

		global.collection.insert(data,
								function(err,docsInserted){
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
/*
{"operation":"CreatePost","username":"qwe","position":{},"description":"test post function","quantity":"1","title":"testtesttest","bounty":"100","contact":"","TransactionCompleted":"false","photo":[],"response":[],"isRequest":"true","category":"car"}
*/
