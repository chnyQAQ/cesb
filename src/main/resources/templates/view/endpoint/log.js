$(document).ready(function() {

	$('input[name=beginTime]').datetimepicker({
		format: 'Y-m-d H:i',
		maxDate: new Date(),
		onShow: function( ct ){
			this.setOptions({
				maxDate:$('input[name=endTime]').val() ? $('input[name=endTime]').val() : new Date()
			})
		},
		timepicker: true,
		allowBlank:true,
		step: 1
	});
	$('input[name=endTime]').datetimepicker({
		format: 'Y-m-d H:i',
		maxDate: new Date(),
		onShow: function( ct ){
			this.setOptions({
				minDate:$('input[name=beginTime]').val() ? $('input[name=beginTime]').val() : new Date()
			})
		},
		timepicker: true,
		allowBlank:true,
		step: 1
	});

	$('#searchForm').formSearchPageRows({
		url		:	'/endpoint/' + endpointId + '/logs/list-page',
		cm		:	[
						{col: function(routeEndpointLog){
								return $('<a href="'+ (contextPath + '/routes/'+ routeEndpointLog.routeId) +'/logs">'+ routeEndpointLog.routeName +'</a>');
						}},
						{col: function(routeEndpointLog){
							return new Date(routeEndpointLog.beginTime).format('yyyy-MM-dd hh:mm:ss.S') ;
						}},
						{col: function(routeEndpointLog){
							return new Date(routeEndpointLog.endTime).format('yyyy-MM-dd hh:mm:ss.S') ;
						}},
						{col: function(routeEndpointLog){
							var duration = new Date(routeEndpointLog.endTime).getTime() - new Date(routeEndpointLog.beginTime).getTime();
							return $('<center>' + (duration > 0 ? duration : "1") + '</center>');
						}},
						{col: function(routeEndpointLog){
							return $('<center>' + (routeEndpointLog.hasException ? "是" : "否") + '</center>');
						}},
						{col: function(routeEndpointLog){
							return $('<div title="'+routeEndpointLog.exceptionType+'">'+(routeEndpointLog.exceptionType ? routeEndpointLog.exceptionType : '<center>-</center>')+'</div>');
						}},
						{col: function(routeEndpointLog){
							return $('<div title="'+routeEndpointLog.exceptionMessage+'">'+(routeEndpointLog.exceptionMessage ? routeEndpointLog.exceptionMessage : '<center>-</center>')+'</div>');
						}}
			 	 	]
	}).trigger('submit');
	
	var refreshRows = function() {
		$('#searchForm').formSearchPageRows('search');
	};

});