/**
 * Created by MouQi on 2016/8/29.
 * //mysql查询封装
 */
var mysql = require('mysql');
var fs=require('fs');
var MYSQL = {};
    
var dbconfig = JSON.parse(fs.readFileSync('../config/mysql.json'));

var pool = mysql.createPool(dbconfig);

MYSQL.query = (sql, callback) => {
    pool.getConnection( (err, connection) =>{
        if (err) {
            console.error("query error: ", err.stack);
            return callback(err);
        }else {
            connection.query(sql, (err, rows) => {

                if (err) {
                console.error("query error: ", err.stack);
                return callback(err);
                }

                connection.release();
                return callback(rows);
            });
        }
    });
};

//sql is preparing query statement, arr is the parameters.
MYSQL.preQuery = (sql, arr, callback) => {
    sql = mysql.format(sql, arr);
    console.log(sql);
    MYSQL.query(sql, callback);
};

module.exports = MYSQL;
