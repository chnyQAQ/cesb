(function($) {
	
	$.extend({
		alert : function() {
			
			var methods = {
					
					init	:	function() {
						
						var title = '消息';
						var content = '';
						var action = null;
						
						if (arguments.length == 0) {
							return;
						} else if (arguments.length == 1) {
							if (typeof(arguments[0]) == 'string') {
								content = arguments[0];
							}
						} else if (arguments.length == 2) {
							if (typeof(arguments[1]) == 'string') {
								if (typeof(arguments[0]) == 'string') {
									title = arguments[0];
								}
								content = arguments[1];
							} else if ($.isFunction(arguments[1])) {
								if (typeof(arguments[0]) == 'string') {
									content = arguments[0];
								}
								action = arguments[1];
							}
						} else if (arguments.length == 3) {
							if (typeof(arguments[0]) == 'string') {
								title = arguments[0];
							}
							if (typeof(arguments[1]) == 'string') {
								content = arguments[1];
							}
							if ($.isFunction(arguments[2])) {
								action = arguments[2];
							}
						}
						
						$('.globalExclusiveModal').modal('hide');
						
						var str = '<div class="modal fade globalExclusiveModal alertModal" tabindex="-1" data-backdrop="static">'
									+ '<div class="modal-dialog modal-dialog-centered" role="document" tabindex="-1" data-backdrop="static" style="max-width: 600px;">'
										+ '<div class="modal-content">'
											+ '<div class="modal-header">'
												+ '<h5 class="modal-title text-muted"><i class="fas fa-info-circle"></i> '+ title +'</h5>'
												+ '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
											+ '</div>'
											+ '<div class="modal-body">'
												+ '<h4 class="text-center" style="word-wrap: break-word;">'+ content +'</h4>'
											+ '</div>'
											+ '<div class="modal-footer">'
												+ '<button type="button" class="btn btn-success btn-confirm">确定</button>'
											+ '</div>'
										+ '</div>'
									+ '</div>'
							+ '</div>';
						
						var $confirm = $(str).appendTo('body').modal('show').on('shown.bs.modal', function() {
							$('.btn-confirm', this).trigger('focus');
						}).on('hidden.bs.modal', function() {
							$confirm.modal('dispose').remove();
						});
						
						var $confirmButton = $('.btn-confirm', $confirm);
						
						$confirmButton.on('click', function() {
							$confirmButton.prop('disabled', true);
							if (action == null) {
								$confirm.modal('hide');
							} else {
								action();
								$confirm.modal('hide');
							}
						});
						
					},
					
					close	:	function() {
						$('.alertModal').modal('hide');
					}
					
			};
			
			if (arguments.length == 1 && arguments[0] == 'close') {
				methods.close();
			} else {
				methods.init.apply(window, arguments);
			}
			
			
	    }
	});
	
})(jQuery);


