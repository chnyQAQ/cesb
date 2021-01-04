(function($) {
	
	var methods = {
			
			init		:	function() {
				
				var url = '';
				var obj = null;
				var success = null;
				var fail = null;
				
				$.each(arguments, function(i, arg) {
					if (i == 0) {
						url = arg;
					} else {
						if ($.isPlainObject(arg) && obj == null) {
							obj = arg;
						} else if ($.isFunction(arg)) {
							if (success == null) {
								success = arg;
							} else if (fail == null) {
								fail = arg;
							}
						}
					}
				});
				
				return this.each(function() {
					
					$(this).off('submit').on('submit', function(e) {
						
						e.preventDefault();
						
						if ($(this).prop('busy')) {
							return;
						} else {
							$('button[type="submit"]', this).prop('disabled', true);
							$(this).prop('busy', true);
						}
						
						$(this).formValidate('clean');
						$(this).fadeTo('fast', 0.35);
						
						$.ajax({
							type		:	'post',
							contentType	:	'application/json;charset=UTF-8',
							url			:	$.isFunction(url) ? url() : url,
							data		:	$(this).serializeJSON({useIntKeysAsArrayIndex: true}),
							processData	:	false
						}).done($.proxy(function() {
							$(this).parents('.modal').modal('hide');
							if ($.isFunction(success)) {
								success.apply(this, arguments);
							}
						}, this)).fail($.proxy(function(jqXHR, status, error) {
							if (jqXHR.responseJSON && jqXHR.responseJSON.error && jqXHR.responseJSON.error == 'ValidationException') {
								$(this).formValidate('show', JSON.parse(jqXHR.responseJSON.message));
							}
							if ($.isFunction(fail)) {
								fail.apply(this, arguments);
							}
						}, this)).always($.proxy(function() {
							$(this).stop(true, true).fadeTo('fast', 1);
							$('button[type="submit"]', this).prop('disabled', false);
							$(this).prop('busy', false);
						}, this));
						
					});
					
					$(this).formValidate('clean');
					$(this).trigger('reset');
					$(this).deserializeJSON(obj);
					$('.modal-title', this).html('<span><i class="far fa-edit"></i> 新建</span>');
					$(this).parents('.modal').modal('show').off('shown.bs.modal').on('shown.bs.modal', $.proxy(function() {
						$('[type="submit"]', this).trigger('focus');
					}, this));
					
				});
				
			}
	
	};
	
	$.fn.modalPost = function() {
		return methods.init.apply(this, arguments);
	};
	
})(jQuery);


