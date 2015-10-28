function listen(){
	var port=9069;
	var net=require('net');
	var server=net.createServer(function(socket) {
			socket.on('data', function(data) {
				try{
					var json=JSON.parse(data.toString());
					if (json.operation=="GetPostDetail"){
						var getModule=require("./get");
						getModule.get(json, socket);
					}else if(json.operation=="OfferPrice"){
						var offerModule=require("./offer");
						offerModule.offer(json, socket);
					}else if(json.operation=="DeleteOffer"){
						var deleteModule=require("./delete");
						deleteModule.delete(json, socket);
					}else if(json.operation=="AcceptOffer"){
						var acceptModule=require("./accept");
						acceptModule.accept(json, socket);
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
