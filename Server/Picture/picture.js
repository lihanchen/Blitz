function listen(){
	var port=5005;
	var net=require('net');
	var server=net.createServer(function(socket) {
			socket.on('data', function(data) {
				try{
					if(data.length > 30){
						console.log("asdf"+ data.length);
						var uploadModule=require("./upload");
						uploadModule.upload(data, socket);
					}else if(data.length <= 30){
						console.log(data);
						var getModule=require("./getpic");
						getModule.getpic(data, socket);
					}else{
						socket.write(JSON.stringify({error:"Unknown Operation"}));
						socket.destroy();
					}
				}catch(e){
					console.error(e);
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
		db.collection('Picture', function(err, collection){
			if(err){
				console.log(err);
			}else{
				global.collection=collection;
				listen();
			}
		});
	}
});
