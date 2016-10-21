$(document).ready(function(){
	var schoolrollType = "";
	$.ajax({
		type: "POST",
		url: "../student/v_initschoolrolltype.do",
		data: {},
		dataType: "json",
		success: function(data) {
			if (data.schoolrollType == "") {
				schoolrollType = "student";
			} else {
				schoolrollType = data.schoolrollType;
			}
			
			window.parent.schoolrollType = schoolrollType;
			$("#schoolrollType").find("option[id='"+ schoolrollType + "']").attr("selected", true);
		 }
	});
})

function changeType(schoolId, gradeId, classId) {
	//var queryStatus = $("input[name=queryStatus]:checked").val();
	var schoolrollType = $("#schoolrollType option:selected").val();
	if (schoolrollType == "student") {
		location.href ="../student/v_list.do?schoolrollType="+schoolrollType+"&schoolId="+schoolId+"&gradeId="+gradeId+"&classId="+classId;
	} else if (schoolrollType == "classteacher") {
		location.href = "../classteacher/v_list.do?schoolrollType="+schoolrollType+"&schoolId="+schoolId+"&gradeId="+gradeId+"&classId="+classId;
	} else if (schoolrollType == "curriculum") {
		location.href = "../curriculum/v_list.do?schoolrollType="+schoolrollType+"&schoolId="+schoolId+"&gradeId="+gradeId+"&classId="+classId;
	} else if (schoolrollType == "exam") {
		//location.href = "../exam/v_list.do?schoolrollType="+schoolrollType+"&schoolId="+schoolId+"&gradeId="+gradeId+"&classId="+classId;
	} else if (schoolrollType == "score") {
		location.href = "../score/v_list.do?schoolrollType="+schoolrollType+"&schoolId="+schoolId+"&gradeId="+gradeId+"&classId="+classId;
	} 
}