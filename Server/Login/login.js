function listen(){
	var port=5000;
	var net = require('net');
	var server = net.createServer(function(client) {
			client.on('data', function(data) {
				check(data, client);
			});
		});
		server.listen(port);
}

function check(receivedData,socket){
	var ret={};
	try{
		var json=JSON.parse(receivedData.toString());
		table.findOne({username:json.username},function(err,item){
			if (item==null){
				ret.success=false;
				ret.msg="Username doesn't exist";
			}else{
				if (item.password==json.password){
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
			table=collection;
			listen();
		});
	}
});
