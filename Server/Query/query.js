function listen() {
    var port = 5001;
    var net = require('net');
    var server = net.createServer(
        function (c) { //'connection' listener
            console.log('client connected');
            c.on(
                'close', 
                function () {
                    console.log('client disconnected');
                }
            );
            c.on(
                'data', 
                function (data) {
                    var filters = JSON.parse(data.toString());
                    return query(filters);
                }
            );
        }
    );
    server.listen(
        port, 
        function () { //'listening' listener
            console.log('server start');
        }
    );
}

function query(filters) {
    var  mongodb = require('mongodb');
    
    var  server  = new mongodb.Server(
        'localhost', 
        27017, 
        {auto_reconnect:true}
    );
    
    var  db = new mongodb.Db('490', server);
    
    db.open(
        function(err, db){
           if(!err){
                console.log('connect db'); 
                db.collection('Post',
                    function(err, collection) {
                        if(err) {console.log(err);}
                        else {
                            collection.find
                        }
                    }
                );
           }     
        }
    );
}


listen();
