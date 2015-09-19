function listen(){
	var port=5000;
	var net = require('net');
	var server = net.createServer(function(c) { //'connection' listener
			console.log('client connected');
			c.on('close', function() {
				console.log('client disconnected');
			});
			c.on('data', function(data) {
				var json=JSON.parse(data.toString());
				c.write('copy that\r\n');
				check(json.username,json.password);
			});
		});
		server.listen(port, function() { //'listening' listener
				console.log('server start');
		});
}

function check(username,password){
	var  mongodb = require('mongodb');
	var  server  = new mongodb.Server('localhost', 27017, {auto_reconnect:true});
	var  db = new mongodb.Db('490', server);
	db.open(function(err, db){
			if(!err){
				console.log('connect db');
				db.collection('Account', function(err, collection){
					if(err){
						console.log(err);
					}else{
						collection.findOne({username:username,password:password},function(err,item){
							if (item!=null){
								console.log("Success");
							}else{
								console.log("Fail");
							}
							db.close();
						}); 
					}
				});
			}
	});
}


listen();
//check("lhc1","111");
