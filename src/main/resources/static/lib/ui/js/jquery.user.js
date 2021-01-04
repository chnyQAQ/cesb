(function($) {
	
	var methods = {
			
			init		:	function() {
				return this.each(function() {
					
					
					var typeCode = $(this).attr('data-user');
					var value = $(this).attr('data-value');
					var tagName = $(this).get(0).tagName;
					
					// input
					if (tagName == 'INPUT' || tagName == 'input') {
						
						$.ajax({url: ('/authenticated/users/one-by-x?x=' + value)}).done($.proxy(function(user) {
							if (user) {
								$(this).val(user[typeCode]);
							}
						}, this));
						
					}
					
					// 文本
					else {
						
						$.ajax({url: ('/authenticated/users/one-by-x?x=' + value)}).done($.proxy(function(user) {
							if (user) {
								$(this).text(user[typeCode]);
							}
						}, this));
						
					}
				
				});
			}
	
	};
	
	$.fn.user = function(method) {
		return methods.init.apply(this, arguments);
	};
	
})(jQuery);


