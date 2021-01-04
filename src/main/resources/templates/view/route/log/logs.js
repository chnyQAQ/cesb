$(document).ready(function() {
	$('input[name=beginTime]').datetimepicker({
		format: 'Y-m-d H:i',
		onShow: function( ct ){
			this.setOptions({
				maxDate:$('input[name=endTime]').val() ? $('input[name=endTime]').val() : new Date()
			})
		},
		timepicker: true,
		allowBlank:true,
		maxDate: new Date(),
		step: 1
	});
	$('input[name=endTime]').datetimepicker({
		format: 'Y-m-d H:i',
		onShow: function( ct ){
			this.setOptions({
				minDate:$('input[name=beginTime]').val() ? $('input[name=beginTime]').val() : new Date()
			})
		},
		timepicker: true,
		allowBlank:true,
		maxDate: new Date(),
		step: 1
	});

	$('#searchForm').formSearchPageRows({
		url		:	'/routes/'+ route.id +'/logs/list-page',
		table			:	'.table-logs',
		pagination		:	'.pagination-logs',
		paginationInfo	:	'.pagination-info-logs',
		cm		:	[
						{col: 'invoke'},
						{col: function(routeLog){
							return new Date(routeLog.beginTime).format('yyyy-MM-dd hh:mm:ss.S') ;
						}},
						{col: function(routeLog){
							return (routeLog.endTime != null && routeLog.endTime != '') ? new Date(routeLog.endTime).format('yyyy-MM-dd hh:mm:ss.S') : "数据待更新";
						}},
						{col: function(routeLog){
							if (routeLog.endTime != null && routeLog.endTime != '') {
								var duration = new Date(routeLog.endTime).getTime() - new Date(routeLog.beginTime).getTime();
								return $('<center>' + (duration > 0 ? duration : "1") + '</center>');
							} else {
								return "数据待更新";
							}
						}},
						{col: function(routeLog){
							return $('<center>' + ((routeLog.endTime != null && routeLog.endTime != '') ? (routeLog.success ? "是" : "否") : "数据待更新") + '</center>');
						}},
						{col: function(routeLog) { return $('<a href="'+ (contextPath + '/routes/logs/'+ routeLog.id) +'">详情</a>'); }}
			 	 	]
	}).trigger('submit');
	
	var refreshRows = function() {
		$('#searchForm').formSearchPageRows('search');
	};

	$.each(endpointMap, function(id, endpoint){
		var $tr = $('<tr><td>'+ endpoint.code +'</td><td>'+ endpoint.name +'</td><td>'+ endpoint.component +'</td><td>'+ endpoint.url +'</td></tr>');
		$tr.on('click', function() {
		    $(this).parent().find('tr').removeClass('selected');
			$(this).addClass("selected");
		});
		$('.table-endpoints tbody').append($tr);
	});

});