/*function do_submit(id){
	
	document.getElementById(id).submit();
	
}*/

function goUrl(url){
	window.location.href=url;
}

function setmessage(index,message){
	if($("#"+index+"\\.errors").length==0){
		$("#"+index).after("<span class=\"validate-error\" id=\""+index+".errors\">"+message+"</span>");
	}else{
		$("#"+index+"\\.errors").html(message);
	}
}

function seterrormessage(message){
	if($("#system_message .alert-error").length==0){
		$("#system_message").html("<div class=\"alert alert-error\" style=\"width:70%;margin:0 auto;\">"+message+"</div>");
	}else{
		$("#system_message .alert-error").html(message);
	}
}

function setSuccessMessage(message){
	if($("#system_message .alert-success").length==0){
		$("#system_message").html("<div class=\"alert alert-success\" id=\"tt_mm_9\" style=\"width:70%;margin:0 auto;\">"+message+"</div>");
	}else{
		$("#system_message .alert-success").html(message);
		$("#system_message .alert-success").show();
	}
	setTimeout("hidden()", 1500);
}

function hidden(){
	$("#tt_mm_9").slideUp("1000");
}

function removemessage(index){
	$("#"+index+"\\.errors").remove();
}
var patterns = new Object();
patterns.key = /^[a-zA-Z_][\w_]+$/;
patterns.loginName=/^[A-Za-z0-9!#$%&'*+\/=?^_`{|}~-]+(?:\.[A-Za-z0-9!#$%&'*+\/=?^_`{|}~-]+)*$/;
patterns.name  = /^[a-zA-Z]\w{5,17}$/;
//patterns.email = /^[A-Za-z0-9!#$%&'*+\/=?^_`{|}~-]+(?:\.[A-Za-z0-9!#$%&'*+\/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[a-z0-9])?\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?$/;
patterns.email = /^([a-zA-Z0-9]+[-_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[-_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
patterns.mobile = /^0?(13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$/;
patterns.realname =  /^[\u4E00-\u9FA5]{2,4}$/;

$().ready(function(){
	$(".table-bordered>thead>tr>th:first>input[type='checkbox']").bind('click', function() {
		  if($(this).attr("checked")){
			  $(".table-bordered tbody input[type='checkbox']").attr("checked",true);
		  }else{
			  $(".table-bordered tbody input[type='checkbox']").attr("checked",false);
		  }
	});
	$("#t_clear").bind('click',function(){
		$(this).siblings("input[type='text']").val("");
	});
	setTimeout("hideMessage()", 1500);
});

function hideMessage(){
	$("#tt_mm_1,#tt_mm_2,#tt_mm_3,#tt_mm_4,#tt_mm_5,#tt_mm_6,#tt_mm_7,#tt_mm_8").slideUp("1000");
}

