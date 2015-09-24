function listen() {
    var port = 5001;
    var net = require('net');
    var server = net.createServer(function (c) { //'connection' listener
        console.log('client connected');
        c.on('close', function () {
            console.log('client disconnected');
        });
        c.on('data', function (data) {
            var json = JSON.parse(data.toString());
            check(json.username, json.password);
        });
    });
    server.listen(port, function () { //'listening' listener
        console.log('server start');
    });
}

function dosomething() {
}


listen();
//check("lhc1","111");
