exports.getpic=function (receivedObj,socket){
	var ret={};
	try{
		ObjectID = require('mongodb').ObjectID;
		var id = "";
		id = receivedObj.id;
		var objectid = new ObjectID(id);
		global.collection.findOne({"_id":objectid},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Picture doesn't exist";
			}else{
				ret.success=true;
				var picstr  = item.data.substring(9);
				ret.data=picstr;

			}
			socket.write(JSON.stringify(ret));

/*
var fs = require('fs');
var picstr = item.data.substring(9);
var decodedata = new Buffer(picstr, 'base64');
fs.writeFile('testpic.jpg', decodedata, function(err){
  if (err) throw err;
  console.log('Sucessfully saved!');
});
*/



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
