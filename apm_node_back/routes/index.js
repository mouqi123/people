var express = require('express');
var router = express.Router();
var ms = require('../my_lib/mysql');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'apm可视化系统' });
});

// router.get('/test', function(req, res, next) {
//   res.send([1,2,3,4]);
// });

// router.post('/getVerionList', function(req, res, next) {
//   var objdemo = [
//     { "version" : "1.0" , "lastName" : "Doe" },
//     { "version" : "2.0" , "lastName" : "Smith" },
//     { "version" : "3.0" , "lastName" : "Jones" },
//     { "version" : "4.0" , "lastName" : "Jones" }];
//   res.send(objdemo);
// });

module.exports = router;
