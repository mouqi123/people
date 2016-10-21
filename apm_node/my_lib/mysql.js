/**
 * Created by Cold on 2016/8/16 0016.
 * //mysql查询封装
 */
var mysql = require('mysql'),
    conf = require('../config/config.js');

var ms = {};

ms.query = function (theSql, callback) {
    var connection = mysql.createConnection({
        host: conf.mysql.host,
        user: conf.mysql.user,
        password: conf.mysql.password,
        database: conf.mysql.database
    });

    //连接数据库
    connection.connect();

    //访问数据库
    connection.query(theSql, function(err, rows, fields) {
        callback(err, rows);
    });

    //断开数据库
    connection.end();
};

module.exports = ms;