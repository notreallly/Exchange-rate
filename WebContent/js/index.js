$(document).ready(function() {
	// 選單更變
	$('#Select').change(function() {
		var seletedCurrency = $("#Select option:selected");
		currencyName = seletedCurrency.text();
		var val = seletedCurrency.val();
		
		getJsonChart(val);
	});
});