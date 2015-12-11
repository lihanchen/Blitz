exports.getpic=function (receivedObj,socket){
	var ret={};
	try{
		ObjectID = require('mongodb').ObjectID;
		var objectid = new ObjectID(receivedObj.id);
		global.collection.findOne({"_id":objectid},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Picture doesn't exist";
			}else{
				ret.success=true;
				ret.data=item.data;
			}
			socket.write(JSON.stringify(ret));
			//socket.destroy();
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
			var picjsonstring = JSON.stringify(ret);
			if(picjsonstring.length > 20000){
				var i = 0;
				var maxi = Math.floor(picjsonstring.length/2000);
				for(i = 0;i<picjsonstring.length/2000;i++){
					if (i == maxi){
						var succ = socket.write(picjsonstring.substring(i*2000,picjsonstring.length));
						//console.log(succ+i)
						if(succ){
							continue;
						}
						else{
							console.log(succ);
							console.log(i);
							break;
						}

					}
					else{
						var succ = socket.write(picjsonstring.substring(i*2000,i*2000+2000));
						//console.log(succ+i);
						if(succ){
							continue;
						}
						else{
							console.log(succ);
							console.log(i);
							break;
						}

					}
				}

			}else{
				var succ = socket.write(JSON.stringify(ret),function(){
					//console.log("finally finished");
				});
				//console.log(succ);
			}
			console.log(picjsonstring.length);
*/
/*
var fs = require('fs');
var picstr = item.data.substring(9);
var decodedata = new Buffer(picstr, 'base64');
fs.writeFile('testpic.jpg', decodedata, function(err){
  if (err) throw err;
  console.log('Sucessfully saved!');
});
*/

/*
			socket.end();
			socket.destroy();
		}); 
	}catch(e){
		console.error(e);
		ret.error=e.toString();
		socket.write(JSON.stringify(ret));
		socket.destroy();
	}
}*/



