(function($) {
	
	$.extend({
		dropdownActions : function(op, context) {
	        
			var options = {
				aglin		:	'left',
				trigger		:	'<button type="button" class="btn btn-sm btn-secondary" data-toggle="dropdown"><i class="fas fa-ellipsis-h"></i></button>',
				menus		:	[]
			};
			
			$.extend(options, op);
			
			var dropdown = $('<div class="dropdown"></div>');
			var toggle = $(options.trigger).appendTo(dropdown);
			var dropdownMenu = $('<div class="dropdown-menu"></div>').appendTo(dropdown);
			
			$.each(options.menus, function(i, menu) {
				if (menu.text == '-') {
					$('<div class="dropdown-divider"></div>').appendTo(dropdownMenu);
				} else {
					if (menu.href) {
						$('<a class="dropdown-item">'+ menu.text +'</a>').attr('href', menu.href).appendTo(dropdownMenu);
					} else if (menu.click) {
						$('<button class="dropdown-item" type="button">'+ menu.text +'</button>').appendTo(dropdownMenu).on('click', function(e) {
							menu.click.call(context, context);
						});
					} else {
						$('<a class="dropdown-item">'+ menu.text +'</a>').attr('href', '#').appendTo(dropdownMenu);
					}
				}
			});
			
			toggle.dropdown();
			return dropdown;
	    }
	});
	
})(jQuery);


