var mysql = require('./mysqlPool');
var userParticipate = {};

var day_use_base_column = " sum(1to3) as 1to3, sum(4to10) as 4to10, sum(10to30) as 10to30, sum(30to60) as 30to60, sum(1mto3m) as 1mto3m," +
  "sum(3mto10m) as 3mto10m, sum(10mto30m) as 10mto30m, sum(30more) as 30more ";

var day_use_sql = "select " + day_use_base_column + " from u_p_day_use_time where ";

var page_count_base_column = " sum(1to2) as 1to2, sum(3to5) as 3to5, sum(6to9) as 6to9, sum(10to29) as 10to29, sum(30to99) as 30to99, sum(100more) as 100more ";

var page_count_sql = " select " + page_count_base_column + " from u_p_page_count where ";

userParticipate.queryUseTime = (data, callback) => {

  var arr=[];
  //flag is used to mark the privious attribute , if it's true, you should add 'and' before next attibute. ^_^
  var flag = false;

  if (data.version && data.version != '') {
    day_use_sql += " version_name = ? ";
    flag = true; 
    arr[0] = data.version;
  }

  if (data.channel && data.channel != '') {
    if(flag) {
      day_use_sql += " and channel_name = ? ";
    }else {
      day_use_sql += " channel_name = ? ";
      flag = true;
    }
    arr[1] = data.channel;
  }

  if (data.dayTime && data.dayTime != '') {
    if(flag) {
      day_use_sql += " and operationtime = ? ";
    }else {
      day_use_sql += " operationtime = ? ";
      flag = true;
    }
    arr[2] = data.dayTime;
  }
  //console.log(day_use_sql);

  mysql.preQuery(day_use_sql, arr, callback);
}

userParticipate.queryPageAccCount = (data, callback) => {
  console.log(1111111111111111111);
  var arr=[];
  //flag is used to mark the privious attribute , if it's true, you should add 'and' before next attibute. ^_^
  var flag = false;

  if (data.version && data.version != '') {
    page_count_sql += " version_name = ? ";
    flag = true;
    arr.push(data.version);
  }
  console.log(2222222222222222222222);
  if (data.channel && data.channel != '') {
    if(flag) {
      page_count_sql += " and channel_name = ? ";
    }else {
      page_count_sql += " channel_name = ? ";
      flag = true;
    }
    arr.push(data.channel);
  }
  console.log(33333);
  page_count_sql += " and date_time >= ? and date_time <= ?";
  arr.push(data.endTime-data.interval+1);
  arr.push(data.endTime);
  console.log("@@@"+arr[2]+arr[3]);
  	mysql.preQuery(page_count_sql, arr, callback);
}
module.exports = userParticipate;