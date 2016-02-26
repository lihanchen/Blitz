exports.rate=function (receivedObj,socket){
	var ret={};
	try{
		global.collection.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				var score=item.rating * item.ratingCount + receivedObj.score;
				global.collection.update({"username":receivedObj.username},{"$inc":{"ratingCount":1},"$set":{"rating":score/(item.ratingCount+1)}});
				var mongodb=require('mongodb');
				var server=new mongodb.Server('localhost', 27017, {auto_reconnect:true});
				var db2=new mongodb.Db('490', server);
				db2.open(function(err, db){
					if(err){
						console.log(err);
					}else{
						db2.collection('Post', function(err, collection2){
							if(err){
								console.log(err);
							}else{
								var id=require("mongodb").ObjectID(receivedObj.postID);
								collection2.update({"_id":id},{$set:{rated:true}});
							}
						});
					}
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
