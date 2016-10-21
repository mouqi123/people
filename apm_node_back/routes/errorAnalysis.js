/**
 * Created by lixin on 2016/8/24.
 */
var express = require('express');
var router = express.Router();

router.post('/getList', function(req, res, next) {
    var data = req.body;
    var objdemo = [
        { "version" : "1.0" , "channel" : "Doe" , "date" : "8-19"},
        { "version" : "2.0" , "channel" : "Smith", "date" : "6-12" },
        { "version" : "3.0" , "channel" : "Jok", "date" : "7-11" },
        { "version" : "4.0" , "channel" : "Jones", "date" : "9-16" }
    ];
    res.send(objdemo);
});

router.post('/errorList', function(req, res, next) {
    var data = [10, 100, 10, 100, 10, 100, 10];
    res.send(data);
});
module.exports = router;
