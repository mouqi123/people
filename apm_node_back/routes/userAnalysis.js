/**
 * Created by lixin on 2016/8/19.
 */
var express = require('express');
var router = express.Router();

router.post('/getList', function(req, res, next) {

    var data = [
        { "version" : "1.0" , "channel" : "Doe" , "date" : "8-19"},
        { "version" : "2.0" , "channel" : "Smith", "date" : "6-12" },
        { "version" : "3.0" , "channel" : "Jok", "date" : "7-11" },
        { "version" : "4.0" , "channel" : "Jones", "date" : "9-16" }
    ];
    // var data ={"one_three":"33","three_ten":"44"};
    res.send(data);
});

router.post('/newAddUser', function(req, res, next) {
    var userdata = [120, 132, 101, 134, 90, 230, 210];
    res.send(userdata);
});

router.post('/activeUser', function(req, res, next) {
    var data = [20, 132, 101, 134, 90, 230, 210];
    res.send(data);
});

module.exports = router;
