function listen(){
	var port=9072;
	var net=require('net');
	var server=net.createServer(function(socket) {
			socket.on('data', function(data) {
				try{
					var json=JSON.parse(data.toString());
					if (json.operation=="GetNotifications"){
						global.collection.find({username:json.username}).toArray(function(err,doc){
							socket.write(JSON.stringify(doc));
							socket.destroy();
						});
					}else if (json.operation=="PostNotifications"){
						delete json['operation'];
						global.collection.insert(json);
						socket.write(JSON.stringify({success:true}));
						socket.destroy();
					}else{
						socket.write(JSON.stringify({success:false,msg:"Unknown operation"}));
						socket.destroy();
					}

				}catch(e){
					socket.write(JSON.stringify({error:e.toString()}));
					socket.destroy();
				}
			});
	});
	server.listen(port);
}

var mongodb=require('mongodb');
var server=new mongodb.Server('localhost', 27017, {auto_reconnect:true});
var db=new mongodb.Db('490', server);
db.open(function(err, db){
	if(err){
		console.log(err);
	}else{
		db.collection('Notifications', function(err, collection){
			if(err){
				console.log(err);
			}else{
				global.collection=collection;
				listen();
			}
		});
	}
});

