function listen(){
	var port=5000;
	var net=require('net');
	var server=net.createServer(function(socket) {
			socket.on('data', function(data) {
				try{
					var json=JSON.parse(data.toString());
					if (json.operation=="Login"){
						login(json, socket);
					}
					socket.write(JSON.stringify({error:"Unknown Operation"}));
					socket.destroy();
				}catch(e){
					console.error(e);
					socket.write(JSON.stringify({error:e.toString()}));
					socket.destroy();
				}
			});
		});
		server.listen(port);
}

function login(receivedObj,socket){
	var ret={};
	try{
		table.findOne({username:receivedObj.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				if (item.password==receivedObj.password){
					ret.success=true;
				}else{
					ret.success=false;
					ret.msg="Password doesn't match";
				}
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


var mongodb=require('mongodb');
var server=new mongodb.Server('localhost', 27017, {auto_reconnect:true});
var db=new mongodb.Db('490', server);
var table;
db.open(function(err, db){
	if(err){
		console.log(err);
	}else{
		db.collection('Account', function(err, collection){
			if(err){
				console.log(err);
			}else{
				table=collection;
				listen();
			}
		});
	}
});
