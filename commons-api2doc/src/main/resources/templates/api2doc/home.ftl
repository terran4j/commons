<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/api2doc/css/index.css">
<script src="/api2doc/js/vue.min.js"></script>
<script src="/api2doc/js/index.js"></script>
<#--<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>-->
<script type="text/javascript">
</script>
<link rel="stylesheet" href="/api2doc/css/home.css">
<title>${title}</title>
</head>
<body>
	<div id="app" class="doc-app" v-loading.fullscreen.lock="loading"
		element-loading-text="拼命加载中...">

		<div class="doc-top">
		    <#if icon??>
			<div class="doc-icon">
				<img class="doc-icon-img" src="${icon}">
			</div>
			</#if>
			<div class="doc-title">
                <a href="/api2doc/home.html">${title}</a>
            </div>
			<div class="doc-end"></div>
		</div>

		<div class="doc-body">

			<!-- 左侧栏菜单 -->
			<div class="doc-left">
			    <el-menu class="el-menu-vertical-demo" theme="light"
			    		default-active="${p}">
			      <#list menus as folder>
				  <el-submenu index="${folder.index}">
				    <template slot="title">${folder.name}</template>
				    <#list folder.children as doc>
				    <el-menu-item index="${doc.index}">
				      <a href="${doc.url}" target="doc-iframe-name">${doc.name}</a>
				    </el-menu-item>
				    </#list>
				  </el-submenu>
				  </#list>
			    </el-menu>
			</div>

			<!-- 页面内容 -->
			<div class="doc-middle">
				<iframe id="doc-iframe-id" name="doc-iframe-name" class="doc-iframe"
						scrolling="no" frameborder="0" seamless="seamless"
						onload="setIframeHeight(this)"
						src="${docPath}"></iframe>
			</div>

			<div class="doc-end"></div>
		</div>
	</div>
</body>
<script>
	function setIframeHeight (iframe) {
		if (iframe) {
			var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
			if (iframeWin.document.body) {
				iframe.height = iframeWin.document.documentElement.scrollHeight
						|| iframeWin.document.body.scrollHeight;
			}
		}
	};

	var doInit = function(initData, ajaxPath, ajaxParams) {
		new Vue({
			el : '#app',
			data : function() {
				return {
					loading : true,
					data : initData
				}
			},
		    methods: {
		    },
			created : function() {
				var _self = this;
                _self.loading = false;

//				if (ajaxPath == null) {
//					_self.loading = false;
//				} else {
//					jQuery.get(ajaxPath, ajaxParams, function(data) {
//						_self.data = data;
//						_self.loading = false;
//					});
//				}
			}
		});
	}

	var initData = {
		myInput : ''
	};
	var ajaxPath = null;
	var ajaxParams = {
		loadSecond : 1
	};
	doInit(initData, ajaxPath, ajaxParams);
</script>

</html>