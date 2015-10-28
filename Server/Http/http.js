var http = require('http');
var url = require('url');

var mongodb=require('mongodb');
var server=new mongodb.Server('localhost', 27017, {auto_reconnect:true});
var db=new mongodb.Db('490', server);
try{
	db.open(function(err, db){
			if(err){
				console.log(err);
			}else{
				db.collection('Account', function(err, collection){
					if(err){
						console.log(err);
					}else{
						global.collection=collection;
						http.createServer(function (request, response) {
							try{
								if (request.method=="POST"){
									var post='';      
									var qs=require('querystring');
									request.on('data',function(chunk){  
										post += chunk;  
										if (post.length > 500) request.connection.destroy();
									});   
									request.on('end',function(){  
										var handlerModule=require("./resetHandler");
										handlerModule.handle(qs.parse(post),response);  
									});
								}else{
									response.writeHead(200, {'Content-Type': 'text/html'});
									obj=url.parse(request.url,true);
									if (obj.pathname=="/verify"){
										var verifyPage=require("./verifyPage");
										verifyPage.verifyPage(obj,response);
									}else if (obj.pathname=="/reset"){
										var resetPage=require("./resetPage");
										resetPage.resetPage(obj,response);
									}else{
										response.end("ERROR");
									}
								}
							}catch(e1){
								request.connection.destroy();
								console.log(e1);
							}
						}).listen(9065)
					}
				});
			}
	});
}catch(e){
	console.error(e);
}


