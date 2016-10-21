/**
 * Created by lixin on 2016/8/19.
 */
var express = require('express');
var router = express.Router();
var query = require('../my_lib/pQuery');

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
    //var userdata = [120, 132, 101, 134, 90, 230, 210];
    var params = req.body;
    console.log(params);
    var param={version:'1.0',endTime:'2016-08-30',interval:'7'};
    if(req.body.timeFrame=='day'){
        query.newAddUser(params,function(data){
            console.log(data);
            res.send(data);
        });
    }else if(req.body.timeFrame=='week'){
        query.weekActivity(params,function(data){
            console.log(data);
            res.send(data);
        });
    }else if(req.body.timeFrame=='month'){
        query.monthActivity(params,function(data){
            console.log(data);
            res.send(data);
        });
    }
});

router.post('/activeUser', function(req, res, next) {
    //var data = [20, 132, 101, 134, 90, 230, 210];
    var params = req.body;
    console.log(params);
    var param={version:'1.0',endTime:'2016-08-30',interval:'7'};
    if(req.body.timeFrame=='day'){
        query.activeUser(params,function(data){
            console.log(data);
            res.send(data);
        });
    }else if(req.body.timeFrame=='week'){
        query.weekActivity(params,function(data){
            console.log(data);
            res.send(data);
        });
    }else if(req.body.timeFrame=='month'){
        query.monthActivity(params,function(data){
            console.log(data);
            res.send(data);
        });
    }

});

module.exports = router;
