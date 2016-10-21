var mysql = require('./mysqlPool');
var userParticipate = {};

userParticipate.queryUseTime = (data, callback) => {
  var day_use_base_column = " sum(1to3) as one, sum(4to10) as four, sum(10to30) as ten, sum(30to60) as thirty, sum(1mto3m) as one_m," +
      "sum(3mto10m) as three_m, sum(10mto30m) as ten_m, sum(30more) as thirty_m ";

  var day_use_sql = "select " + day_use_base_column + " from u_p_day_use_time ";
  var arr=[];
  //flag is used to mark the privious attribute , if it's true, you should add 'and' before next attibute. ^_^
  var flag = false;

  if (data.version && data.version != '') {
    day_use_sql += " where version_name = ? ";
    flag = true; 
    arr.push(data.version);
  }

  if (data.channel && data.channel != '') {
    if(flag) {
      day_use_sql += " and channel_name = ? ";
    }else {
      day_use_sql += " where channel_name = ? ";
      flag = true;
    }
    arr.push(data.channel);
  }

  if (data.dayTime && data.dayTime != '') {
    if(flag) {
      day_use_sql += " and operationtime = ? ";
    }else {
      day_use_sql += " where operationtime = ? ";
      flag = true;
    }
    arr.push(data.dayTime);
  }
  //console.log(day_use_sql);

  mysql.preQuery(day_use_sql, arr, callback);
}

userParticipate.queryPageAccCount = (data, callback) => {
  var page_count_base_column = " sum(1to2) as one, sum(3to5) as three, sum(6to9) as six, sum(10to29) as ten, sum(30to99) as thirty, sum(100more) as more ";

  var page_count_sql = " select " + page_count_base_column + " from u_p_page_count ";
  var arr=[];
  //flag is used to mark the privious attribute , if it's true, you should add 'and' before next attibute. ^_^
  var flag = false;

  if (data.version && data.version != '') {
    page_count_sql += " where version_name = ? ";
    flag = true;
    arr.push(data.version);
  }
  if (data.channel && data.channel != '') {
    if(flag) {
      page_count_sql += " and channel_name = ? ";
    }else {
      page_count_sql += " where channel_name = ? ";
      flag = true;
    }
    arr.push(data.channel);
  }
  if(flag){
    page_count_sql += " and ";
  }
  page_count_sql += " where date(date_time) >= date_sub(?, interval ? day)";
  arr.push(data.endTime);
  arr.push(data.interval);
  mysql.preQuery(page_count_sql, arr, callback);
}

userParticipate.newAddUser = (data, callback) => {
  userAnalyze(data, "u_a_new_add_user", callback);
}

userParticipate.activeUser = (data, callback) => {
  userAnalyze(data,"u_a_activity_user", callback);
}

var userAnalyze = (data, tableName, callback) => {
  var base_column = " date_time ,sum(count_value) as count_value ";
  var new_add_user_sql = " select " + base_column + " from " + tableName;

  var arr=[];
  //flag is used to mark the privious attribute , if it's true, you should add 'and' before next attibute. ^_^
  var flag = false;

  if (data.version && data.version != '') {
    new_add_user_sql += "  where version_name = ? ";
    flag = true;
    arr.push(data.version);
  }
  if (data.channel && data.channel != '') {
    if(flag) {
      new_add_user_sql += " and channel_name = ? ";
    }else {
      new_add_user_sql += " where channel_name = ? ";
      flag = true;
    }
    arr.push(data.channel);
  }
  if(flag){
    new_add_user_sql += " and ";
  }else{
    new_add_user_sql += " where ";
  }
  new_add_user_sql += " date(date_time) >= date_sub(?, interval ? day) group by date_time ";
  arr.push(data.endTime);
  arr.push(data.interval);
  mysql.preQuery(new_add_user_sql, arr, callback);
}

userParticipate.weekActivity = (data, callback) => {
  indicationAction(data, 'indication_week', callback);
}

userParticipate.monthActivity = (data, callback) => {
  indicationAction(data, 'indication_week', callback);
}

var indicationAction = (data, tableName, callback) => {
    var base_column = " sum(count_value) as count_value ";
    var sql = " select " + base_column + " from " + tableName + " where ";

    var arr=[];
    if (data.version && data.version != '') {
      sql += " version_name = ? ";
      flag = true;
      arr.push(data.version);
    }
    if (data.channel && data.channel != '') {
      if(flag) {
        sql += " and channel_name = ? ";
      }else {
        sql += " channel_name = ? ";
        flag = true;
      }
      arr.push(data.channel);
    }
    if (data.indicationType && data.indicationType != '') {
      if(flag) {
        sql += " and indicationType = ? ";
      }else {
        sql += " indicationType = ? ";
        flag = true;
      }
      arr.push(data.indicationType);
    }
    if(flag){
      sql += " and ";
    }
      arr.push(data.date);
      arr.push(data.interval);
      if (tableName == 'indication_week') {
        sql += " date(week_seg_date) >= date_sub(?, interval ? day) group by week_seg_date ";
      }else if(tableName == 'indication_month') {
        sql += " date(month_date) >= date_sub(?, interval ? day) group by month_seg_date ";
      }
      mysql.preQuery(sql, arr, callback);
}

userParticipate.versionErr = (data, callback) => {
  errorAnalyze = (data, 'version_count_value', callback);
}

userParticipate.countUser = (data, callback) => {
  errorAnalyze = (data, 'count_user', callback);
}

userParticipate.dayCount = (data, callback) => {
  errorAnalyze = (data, 'start_count_value', callback);
}

var errorAnalyze = (data, columnName, callback) => {
  var sql = " select " + columnName + " from e_a_error_analysis where ";

  var arr=[];
  //flag is used to mark the privious attribute , if it's true, you should add 'and' before next attibute. ^_^
  var flag = false;

  if (data.version && data.version != '') {
    new_add_user_sql += " version_name = ? ";
    flag = true;
    arr.push(data.version);
  }
  if(flag){
    sql += " and ";
  }
  arr.push(data.date);
  arr.push(data.interval);
  sql += " date(date_time) >= date_sub(?, interval ? day) group by date_time ";
    mysql.preQuery(sql, arr, callback);
}



module.exports = userParticipate;