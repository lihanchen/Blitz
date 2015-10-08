exports.send=function(mail){
	var nodemailer = require('nodemailer');
	var transporter = nodemailer.createTransport({
		service: 'Gmail',
		auth: {
				user: 'lhcmaiche@gmail.com',
				pass: 'Pass1018'
		}
	});
	transporter.sendMail(mail, function(error, info){
		if(error){
				console.log(error);
		}	
	});
}
