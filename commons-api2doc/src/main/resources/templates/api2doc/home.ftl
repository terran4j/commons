<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
    <!--
        使用 rem 布局，使 H5 页面能适配不同设备屏幕尺寸
        flexible-lite-1.0.js 用于计算 html 根元素的 font-size 大小
        然后 css 或 less 中所有的尺寸值一定要用 rem 单位，而不是 px 或其它单位。
    -->
    <meta charset="UTF-8" name="viewport"
          content="width=device-width,initial-scale=1,user-scalable=0"/>
    <script src="/website/flexible-lite/flexible-lite-1.0.js"></script>
    <script type="text/javascript">
        flex(1000);
    </script>

    <!-- 左侧菜单是用 element-ui 写的，element-ui 又用了 vue，因此都要引用 -->
    <link href="/website/element-ui/element-ui.min.css" rel="stylesheet">
    <script src="/website/vue/vue-2.5.10.min.js"></script>
    <script src="/website/element-ui/element-ui.min.js"></script>

    <!-- 使用 jQuery 操作，让菜单栏始终悬浮出现在屏幕上。 -->
    <script src="/website/jquery/jquery-3.3.1.min.js"></script>

    <!--
            务必确保在 less.js 之前加载你的样式表。
            如果加载多个 .less 样式表文件，每个文件都会被单独编译。
            因此，一个文件中所定义的任何变量、mixin 或命名空间都无法在其它文件中访问。
    -->
    <link href="/api2doc/css/home.less?v=${v}" rel="stylesheet/less" type="text/css">
    <script src="/website/less/less-3.0.0.min.js" type="text/javascript"></script>
</head>
<body>
<div id="app" class="doc-app" v-loading.fullscreen.lock="loading"
     element-loading-text="拼命加载中...">

    <div class="doc-top">
    <#if icon??>
        <div class="doc-icon">
            <img class="doc-icon-img" src="${icon}"
                 onclick="location.href='/api2doc/home.html';">
        </div>
    </#if>
        <div class="doc-title">
            <span>${title}</span>
        </div>
        <div class="doc-end"></div>
    </div>

    <div class="doc-body">

        <!-- 左侧栏菜单 -->
        <div class="doc-left">
            <div id="doc-menus">
                <el-menu class="el-menu-vertical-demo" theme="light"
                         default-active="${p}">
                <#list menus as folder>
                    <el-submenu index="${folder.index}">
                        <template slot="title">${folder.name}</template>
                        <#list folder.children as doc>
                            <el-menu-item index="${doc.index}">
                                <a href="${doc.url}" target="_self">${doc.name}</a>
                            </el-menu-item>
                        </#list>
                    </el-submenu>
                </#list>
                </el-menu>
            </div>
        </div>

        <!-- 左侧菜单栏与右侧内容区域的分隔 -->
        <div class="doc-split"></div>

        <!-- 页面内容区域 -->
        <div class="doc-content">
            <iframe id="doc-frame-id" name="doc-frame-name" class="doc-frame"
                    scrolling="yes" frameborder="0" seamless="seamless"
                    src="${docPath}"></iframe>
        </div>

        <div class="doc-end"></div>
    </div>
</div>
<script type="text/javascript">

    // 如果网速不给力导致加载太慢时，
    // 会让页面先出现一个“拼命加载中...”的提示，
    // 加载完成后再显示文档内容。
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
</body>
</html>