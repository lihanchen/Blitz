fs = require('fs');
http = require('http');
url = require('url');

var mongodb=require('mongodb');
var server=new mongodb.Server('localhost', 27017, {auto_reconnect:true});
var db=new mongodb.Db('490', server);
ObjectID = require('mongodb').ObjectID;
try{
	db.open(function(err, db){
			if(err){
				console.log(err);
			}else{
				db.collection('Picture', function(err, collection){
					if(err){
						console.log(err);
					}else{
						global.collection=collection;

                        http.createServer(function(req, res){
                            var request = url.parse(req.url, true);
                            var action = request.pathname;

                            action = action.substring(1);
                            console.log(action);

                            //get the picture id
                            var objectid = new ObjectID(action);


                            var ret="";
                            global.collection.findOne({"_id":objectid},function(err,item){
                                if (item==null){
                                    ret="Picture doesn't exist";
                                }else{
                                    var picstr = "found";
                                    ret=picstr;
                                }

                                //store the picture file to local
                                var picstr = item.data.substring(9);
                                var decodedata = new Buffer(picstr, 'base64');
                                fs.writeFile('testpic.jpg', decodedata, function(err){
                                    if (err) throw err;
                                    console.log('Sucessfully saved!');


                                    //after saved!
                                    if (item != null) {
                                        var img = fs.readFileSync('./testpic.jpg');
                                        res.writeHead(200, {'Content-Type': 'image/gif' });
                                        res.end(img, 'binary');
                                    } else {
                                        res.writeHead(200, {'Content-Type': 'text/plain' });
                                        res.end(ret);
                                    }
                                });

                            });

                        }).listen(9073);
                    }
                });
            }
    });
} catch (e){
        console.error(e);
}










/*

exports.getThePicture=function(obj,response){
    var picid = "";
    console.log("get to the picture");
    console.log(obj);

    response.end('\
<head><title>Reset Password</title></head>\
<body>\
<img src="http://localhost:9065/123.jpg" alt="Smiley face" height="42" width="42"> \
</body></html>');

}


*/
