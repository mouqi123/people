$(document).ready(function(){
	$.ajax({
		type: "POST",
		url: "../area/v_initarea.do",
		data: {},
		dataType: "json",
		success: function(data){
			var provinceId = data.provinceId;
			var cityId = data.cityId;
			var countyId = data.countyId;
			$.each(data.provinceList, function(Index,province){
				if(province.areaId ==provinceId){
					$("#province").append("<option value='"+province.areaId+"' selected='selected'>"+province.areaName+"</option>");
				}else{
					$("#province").append("<option value='"+province.areaId+"'>"+province.areaName+"</option>");
				}
			});	
			
			$.each(data.cityList, function(Index,city){
				if(city.areaId ==cityId){
					$("#city").append("<option value='"+city.areaId+"' selected='selected'>"+city.areaName+"</option>");
				}else{
					$("#city").append("<option value='"+city.areaId+"'>"+city.areaName+"</option>");
				}
			});	
			
			$.each(data.countyList, function(Index,county){
				if(county.areaId ==countyId){
					$("#county").append("<option value='"+county.areaId+"' selected='selected'>"+county.areaName+"</option>");
				}else{
					$("#county").append("<option value='"+county.areaId+"'>"+county.areaName+"</option>");
				}
			});		
		}
	});
})

function provinceChange(){
	var provinceId = $("#province option:selected").val();
	$.ajax({
		type: "POST",
		url: "../area/v_city.do",
		data: {'provinceId':provinceId},
		dataType: "json",
		success: function(data){
			var $city = $("#city");
			$city.empty();
			$city.append("<option>----市----</option>");
			var $county = $("#county");			
			$county.empty();
			$county.append("<option>----县----</option>");
			$.each(data, function(Index,city){
				$city.append("<option value='"+city.areaId+"'>"+city.areaName+"</option>");
			});				
		  }
	 });		
}

function cityChange(){
	var cityId = $("#city option:selected").val();
	$.ajax({
		type: "POST",
		url: "../area/v_county.do",
		data: {'cityId':cityId},
		dataType: "json",
		success: function(data){
			var $county = $("#county");
			$county.empty();
			$county.append("<option>----县----</option>");
			$.each(data, function(Index,county){
				$county.append("<option value='"+county.areaId+"'>"+county.areaName+"</option>");
			});				
		}
	});		
}

function countyChange(){
	var countyId = $("#county option:selected").val();
	$.ajax({
		type: "POST",
		url: "../area/o_county.do",
		data: {'countyId':countyId},
		dataType: "json",
		success: function(data){
			if (data.result == "success") {
				parent.location.href = parent.location.href;
			}
		}
	});
}