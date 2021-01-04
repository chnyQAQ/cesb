(function($) {
	
	
	var resetSelect = function(select) {
		var starterOption = true;
		if ($(select).attr('data-starter-option') == 'false') {
			starterOption = false;
		}
		if (starterOption) {
			$('option[value!=""]', select).remove();
			if ($('option[value=""]', select).length == 0) {
				$(select).append('<option value="">-</option>');
			}
		} else {
			$(select).empty();
		}
	};
	
	var methods = {
			
			init		:	function() {
				
				var items = [];
				var _options = {};
				var action = $.noop;
				
				$.each(arguments, function(i, arg) {
					if ($.isArray(arg)) {
						items = arg;
					} else if ($.isPlainObject(arg)) {
						_options = arg;
					} else if ($.isFunction(arg)) {
						action = arg;
					}
				});
				var options = $.extend({}, $.fn.selection.defaults, _options);
				
				return this.each(function() {
					
					resetSelect(this);
					
					var sb = new StringBuffer();
					$.each(items, function(i, obj) {
						// value
						var value = obj[options.value];
						if (typeof(value) == 'undefined') {
							value = eval('obj.' + options.value);
						}
						// text
						var text = obj[options.text];
						if (typeof(text) == 'undefined') {
							text = eval('obj.' + options.text);
						}
						sb.append('<option value=' + value + '>' + text + '</option>');
					});
					$(this).append(sb.toString());
					
					if (typeof($(this).attr('data-value')) != 'undefined') {
						if ($('option[value="'+ $(this).attr('data-value') +'"]', this).length > 0) {
							$(this).val($(this).attr('data-value'));
							$(this).trigger('change');
						} else {
							$(this).trigger('change');
						}
					} else {
						$(this).trigger('change');
					}
					
					action.call(this);
				
				});
				
			}
	
	};
	
	$.fn.selection = function() {
		if (arguments.length > 0) {
			if (typeof(arguments[0]) == 'string') {
				var args = arguments;
				$.ajax({type: 'get', url: arguments[0]}).done($.proxy(function(items) {
					args[0] = items;
					methods.init.apply(this, args);
				}, this));
				return this;
			} else {
				return methods.init.apply(this, arguments);
			}
		} else {
			$.error('no arguments for jQuery.selection');
		}
	};
	
	$.fn.selection.defaults = {
			value			:	'id',
			text			:	'name'
    };
	
})(jQuery);


