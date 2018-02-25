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
                    onload="setIFrameHeight(this)"
                    src="${docPath}"></iframe>
        </div>

        <div class="doc-end"></div>
    </div>
</div>
</body>
<script>
    function setIFrameHeight(iframe) {
        if (iframe) {
            var iFrameWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
            if (iFrameWin.document.body) {
                iframe.height = iFrameWin.document.documentElement.scrollHeight
                        || iFrameWin.document.body.scrollHeight;
            }
        }
    };

    // 如果网速不给力导致加载太慢时，会让页面先出现一个
    // “拼命加载中...”
    // 的提示，加载完成后再显示文档内容。
    new Vue({
        el: '#app',
        data: function () {
            return {
                loading: true
            }
        },
        methods: {},
        created: function () {
            var _self = this;
            _self.loading = false;
        }
    });

</script>

</html>