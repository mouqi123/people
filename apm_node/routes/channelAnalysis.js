/**
 * Created by lixin on 2016/8/23.
 */
var express = require('express');
var router = express.Router();

router.post('/newAddUser', function(req, res, next) {
    var data = [100, 100, 100, 10, 10, 100, 100,100];
    res.send(data);
});

router.post('/activeUser', function(req, res, next) {
    var data = [10, 10, 10, 100, 10, 10,10];
    res.send(data);
});

module.exports = router;