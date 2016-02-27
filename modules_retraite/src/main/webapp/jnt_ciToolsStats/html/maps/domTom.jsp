<%--
	@author : lakreb
	@created : 25 janv. 2013
	@Id : idDeLaPage
	@description : graphique de repartition des membres dans les DOM-TOM

 --%>

<%@include file="../../../common/declarations.jspf"%>

<script type='text/javascript'>
	google.setOnLoadCallback(function() {
		var dt_opts = $.extend({}, options);
		dt_opts.region = '${param.code}';
		dt_opts.width = 137;
		dt_opts.legend = 'none';
		dt_opts.resolution = 'countries';
		drawRegionGeo('chart_Region_${param.code}_div', dt_opts);
	});
</script>
<li class="domRegion"><span id='chart_Region_${param.code}_div'></span>
	<h6>${param.label}</h6></li>