/**
 * Created by lixin on 2016/8/19.
 */
var express = require('express');
var router = express.Router();
var query = require('../my_lib/pQuery');

router.post('/dayUseTime', function(req, res, next) {
  var params = req.body;
  //var param={version:'1.0',dayTime:'2016-08-30'};
  query.queryUseTime(params,function(data){
    res.send(data);
  });
});

router.post('/pageCount', function(req, res, next) {
  var params = req.body;
  console.log(params);
  //var param={version:'1.0',endTime:'2016-08-30',interval:'1'};
  query.queryPageAccCount(params,function(data){
    console.log(data);
    res.send(data);
  });
});

module.exports = router;

