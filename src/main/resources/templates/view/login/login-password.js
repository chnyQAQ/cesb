$(document).ready(function() {
	
	if ($('#username').val() == '') {
		$('#username').focus();
	} else {
		$('#password').focus();
	}
	
	$('form').on('submit', function() {
		$('input', this).trigger('blur').prop('readonly', true);
		$('button', this).prop('disabled', true);
	});
	
});