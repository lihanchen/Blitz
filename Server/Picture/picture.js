function listen(){
	var port=9071;
	var net=require('net');
	var server=net.createServer(function(socket) {
			var j = -1;
			var json;
			var picdata;
			var picid;
			console.log("enter createserver",j);
			socket.on('data', function(data) {
				console.log("socket on data");
				console.log(data.length);
				try{
					if(j == -1){
						json=JSON.parse(data.toString());
						j = 0;
					}
					//if we get the signal of picture
					if(j == 1){
						picdata += data;
					}
					if(j == 0){
						if(json.operation == "upload"){
							console.log(json);
							var createModule=require("./createnew");
							picid = createModule.createnew(data, socket);
							j = 1;
						}else if(json.operation == "getpic"){
							console.log("getpic");
							var getModule=require("./getpic");
							console.log(json.id);
							getModule.getpic(json, socket);
						}else{
							socket.write(JSON.stringify({error:"Unknown Operation"}));
							socket.destroy();
						}
					}
				}catch(e){
					console.error(e);
					socket.write(JSON.stringify({error:e.toString()}));
					socket.destroy();
				}
			});
			socket.on("end",function(){
				console.log("socket end");
				console.log("pic id: ",picid);
				console.log("global id: ",global.id);
				if(j == 0){
					j = -1;
					picdata = null;
					picid = null;
					return;
				}
				j = -1;
				if(picdata != null){
					var uploadModule=require("./upload");
					uploadModule.upload(picdata, socket,global.id);
				}
				picdata = null;
				picid = null;
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
