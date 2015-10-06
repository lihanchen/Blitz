function listen() {
    var port = 5001;
    var net = require('net');
    var server = net.createServer(function (socket) { //'connection' listener
            console.log('client connected');
            socket.on('data', function (data) {
                try{
                    console.log('trying');
                    var json = JSON.parse(data.toString());
                    query(json, socket);
                } catch (e) {
                    console.log('wrong');
                    console.error(e);
                    socket.write(JSON.stringify({error:e.toString()}));
                    socket.destroy();
                }
            });
    });
    server.listen(port);
}

function query(filters, socket) {
    var mongodb = require('mongodb');
    
    var server  = new mongodb.Server('localhost', 27017, {auto_reconnect:true});
    
    var db = new mongodb.Db('490', server);
    var res = null;
    console.log('start querying');
    db.open(function(err, db){
       if(!err){
            console.log('connect db'); 
            db.collection('posts', function(err, collection) {
                if(err) {
                    socket.write(JSON.stringify({error:e.toString()}));
                    socket.destroy();
                }
                else {
                    res = collection.find(filters);
                    socket.write(JSON.stringify(ret));
                    socket.destroy();
                }
            });
       }
       else {
        console.log('error processing');
       }     
    });
}


listen();
