/**
 * made by mackie
 */
Date.prototype.format = function (fmt) { //author: mackie 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
function submit() {
	var data = {};
	var table = document.getElementsByTagName("table")[0];
	for (var i = 1; i < table.rows.length; i++) {
		var row = table.rows[i];
		var name = row.cells[0].children[0].value;
		var value = row.cells[1].children[0].value;
		if ((name != null && value != null) || (name !="" && value != ""))
			data[name] = value;
	}
	console.log(JSON.stringify(data));
	var xhr = new XMLHttpRequest();
	var url = document.getElementById("select").value;
	xhr.open("POST", url, true);
	xhr.setRequestHeader('content-type', 'application/json');
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4) {
			console.log(xhr.responseText);
			var responseArea = document.getElementById("responseArea");
			responseArea.innerHTML = xhr.responseText;
			document.body.appendChild(responseArea);
		}
	}
	xhr.send(JSON.stringify(data));
}

function login() {
	var xhr = new XMLHttpRequest();
	data = {
		phoneNum : "13121220659",
		service : "login",
		password : "111111",
		devinfo : "{address:\"beijing\", loginTime:\""+new Date().format("yyyy-MM-dd hh:mm:ss")+"\", phoneType:\"浏览器\"}"
	}
	xhr.open("POST", "face/userauth.do", true);
	xhr.setRequestHeader('content-type', 'application/json');
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4) {
			console.log(xhr.responseText);
			var responseArea = document.getElementById("responseArea");
			responseArea.innerHTML = xhr.responseText;
			document.body.appendChild(responseArea);
		}
	}
	xhr.send(JSON.stringify(data));
}