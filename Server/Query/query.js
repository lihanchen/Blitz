function listen(){
	var port=9067;
	var net=require('net');
	var server=net.createServer(function(socket) {
			socket.on('data', function(data) {
				try{
					var json=JSON.parse(data.toString());
					if (json.operation=="Query"){
						query(json, socket);
					}else{
						socket.write(JSON.stringify({error:"Unknown Operation"}));
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


function query(filters, socket) {
	delete filters['operation'];
	global.collection.find(filters,{_id:1,title:1,isRequest:1,postTime:1,TransactionCompleted:1,category:1,position:1}).toArray(function(err,doc){
		socket.write(JSON.stringify(doc));
		socket.destroy();
	});
}


var mongodb=require('mongodb');
var server=new mongodb.Server('localhost', 27017, {auto_reconnect:true});
var db=new mongodb.Db('490', server);
db.open(function(err, db){
	if(err){
		console.log(err);
	}else{
		db.collection('Post', function(err, collection){
			if(err){
				console.log(err);
			}else{
				global.collection=collection;
				listen();
			}
		});
	}
});

