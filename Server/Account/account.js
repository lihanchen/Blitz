function listen(){
	var port=9066;
	var net=require('net');
	var server=net.createServer(function(socket) {
			socket.on('data', function(data) {
				try{
					var json=JSON.parse(data.toString());
					if (json.operation=="Login"){
						var loginModule=require("./login");
						loginModule.login(json, socket);
					}else if(json.operation=="Signup"){
						var signupModule=require("./signup");
						signupModule.signup(json, socket);
					}else if(json.operation=="ForgetPassword"){
						var forgetModule=require("./forget");
						forgetModule.forget(json, socket);
					}else if(json.operation=="ModifyProfile"){
						var modifyModule=require("./modify");
						modifyModule.modify(json, socket);
					}else if(json.operation=="GetProfile"){
						var getModule=require("./get");
						getModule.get(json, socket);
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
		db.collection('Account', function(err, collection){
			if(err){
				console.log(err);
			}else{
				global.collection=collection;
				listen();
			}
		});
	}
});
