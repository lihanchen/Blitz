exports.createPost=function (receivedObj,socket){
	var ret={};
	var data={};
	try{
		var pictures = [];
		var response = [];	
		var i = 0;
		for(i = 0;i<receivedObj.Photo.length;i++){
			pictures.push(receivedObj.photo[i]);
		}
		

		data.postTime = receivedObj.postTime;
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
		//{"operation":"CreatePost","Type_flag":"1","Post_time":"1","User_id":"1","Location":"12","Description":"1","Quantity":"1","Title":"1","Bounty":"1","Contact":"1","Photo":[{"data":"1"},{"data":"2"}]}

/*
{Type_flag:receivedObj.Type_flag,
								Post_time:receivedObj.Post_time,
								User_id:receivedObj.User_id,
								Location:receivedObj.Location,
								Description:receivedObj.Description,
								Quantity:receivedObj.Quantity,
								Title:receivedObj.Title,
								Bounty:receivedObj.Bounty,
								Contact:receivedObj.Contact,
								TransactionCompleted:false}
*/


	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}
