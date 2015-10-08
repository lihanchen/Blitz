var http = require('http');
var url = require('url');

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
					http.createServer(function (request, response) {
						response.writeHead(200, {'Content-Type': 'text/html'});
						obj=url.parse(request.url,true);
						if (obj.pathname=="/verify"){
							var msg="";
							var username=obj.query.username;
							var code=obj.query.code;
							collection.findOne({username:username},function(err,item){
								if (item==null){
									msg="Username doesn't exist";
								}else if (item.hold==null){
									msg="Account has already been activated";
								}else if (item.hold.reason!="unverified"){
									msg="This account cannot be activated";
								}else if (item.hold.unlockCode!=code){
									msg="Invalid activation link";
								}else{
									collection.update({username:username},{$unset:{"hold":1}})
									msg="Successfully activate your account";
								}
								response.end("<html><head><title>Activation</title></head><body><h1><font face=arial>"+msg+"</font></h1></body></html>");
							});
						}
					}).listen(8000)
				}
			});
		}
});

