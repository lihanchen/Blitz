exports.getpic=function (receivedObj,socket){
	var ret={};
	try{
		ObjectID = require('mongodb').ObjectID;
		var id = "";
		id = receivedObj.toString();
		var objectid = new ObjectID(id);
		global.collection.findOne({"_id":objectid},function(err,item){
			/*if (item==null){
				ret.success=false;
				ret.msg="Picture doesn't exist";
			}else{
				ret.success=true;
				ret.data=item.data;
			}*/
console.log(item.data);
			//socket.write(item.data.buffer);

var fs = require('fs');
fs.writeFile('testpic.jpg', item.data.buffer, function(err){
  if (err) throw err;
  console.log('Sucessfully saved!');
});


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
exports.getpic=function (receivedObj,socket){
	var ret={};
	try{
		ObjectID = require('mongodb').ObjectID;
		var objectid = new ObjectID(receivedObj.pictureId);
		global.collection.findOne({"_id":objectid},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Picture doesn't exist";
			}else{
				ret.success=true;
				ret.data=item.data;
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
