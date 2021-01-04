$(document).ready(function () {

    // 当前时间
    var date = new Date();

    $('input[name=dayBegin]').datetimepicker({
        format: 'Y-m-d',
        onShow: function (ct) {
            this.setOptions({
                maxDate: $('input[name=dayEnd]').val() ? $('input[name=dayEnd]').val() : false
            })
        },
        timepicker: false,
        allowBlank: true,
        maxDate: new Date(),
        value: new Date(date.getFullYear(), date.getMonth(), 1)
    });
    $('input[name=dayEnd]').datetimepicker({
        format: 'Y-m-d',
        onShow: function (ct) {
            this.setOptions({
                minDate: $('input[name=dayBegin]').val() ? $('input[name=dayBegin]').val() : false
            })
        },
        timepicker: false,
        allowBlank: true,
        maxDate: new Date(),
        value: date
    });

    // 扩展方法
    var extension = function (mychart) {
        var id = document.getElementById("extension");
        if(!id) {
            $('<div id = "extension" sytle="display:none"></div>').appendTo($('html'));
        }
        mychart.on('mouseover', function(params) {
            if(params.componentType == "xAxis") {
                $('#extension').css({
                    "position": "absolute",
                    "color": "black",
                    "font-family": "Arial",
                    "font-size": "15px",
                    "padding": "5px",
                    "display": "inline"
                }).text(params.value);

                $("html").mousemove(function(event) {
                    var xx = event.pageX - 15;
                    var yy = event.pageY + 15;
                    $('#extension').css('top', yy).css('left', xx);
                });
            }
        });
        mychart.on('mouseout', function(params) {
            if(params.componentType == "xAxis") {
                $('#extension').css('display', 'none');
            }
        });
    };

    // 路由总数
    var setRouteCount = function () {
        $.ajax({
            url: '/statistics-endpoint/count',
            type: 'get'
        }).done(function (data) {
            if (data) {
                $('#endpointCount').text(data);
            }
        });
    };

    // 路由执行情况
    var setRouteExecuteInfo = function () {
        $.ajax({
            url: '/statistics-endpoint/execute-info',
            type: 'get'
        }).done(function (data) {
            if (data) {
                $('#executeSuccessCount').text(data.executeSuccessCount);
                $('#executeFailCount').text(data.executeFailCount);
            }
        });
    };

    // top10
    var setRouteExecuteInfoTop10 = function () {
        $.ajax({
            url: '/statistics-endpoint/execute-info-top10',
            type: 'get'
        }).done(function (data) {
            if (data) {
                buildCountHistogram(data.executeTop10);
                buildDurationHistogram(data.executeTimeTop10);
            }
        });
    };

    // 服务质量
    var setRouteServiceInfo = function () {
        $.ajax({
            url: '/statistics-endpoint/service-duration',
            data: {
                dayBegin: $('input[name=dayBegin]').val(),
                dayEnd: $('input[name=dayEnd]').val()
            },
            type: 'get'
        }).done(function (data) {
            if (null != data) {
                buildService(data.xAxis, data.callCounts, data.successCounts);
            }
        });
    };

    // 执行次数top10
    var buildCountHistogram = function (data) {
        if (null == data) {
            return;
        }
        option = {
            color: ['#4285f4'],
            tooltip: {
                trigger: 'axis'
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    triggerEvent: true,
                    data: data.xAxis,
                    axisTick: {
                        alignWithLabel: true
                    },
                    axisLabel: {
                        interval: 0,
                        rotate: 40,
                        formatter: function (value) {
                            var res = value;
                            if (res.length > 5) {
                                res = res.substring(0, 4) + "..";
                            }
                            return res;
                        }

                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLabel: {
                        formatter: '{value} 次'
                    },
                    minInterval : 1
                }
            ],
            series: [
                {
                    name: '次数',
                    type: 'bar',
                    barWidth: '60%',
                    barMaxWidth: '20',
                    data: data.series
                }
            ]
        };
        var countHistogram = echarts.init(document.getElementById('countHistogram'));
        countHistogram.setOption(option);
        extension(countHistogram);
    };

    // 执行耗时top10
    var buildDurationHistogram = function (data) {
        if (null == data) {
            return;
        }
        option = {
            color: ['#4285f4'],
            tooltip: {
                trigger: 'axis'
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    triggerEvent: true,
                    data: data.xAxis,
                    axisTick: {
                        alignWithLabel: true
                    },
                    axisLabel: {
                        interval: 0,
                        rotate: 40,
                        formatter: function (value) {
                            var res = value;
                            if (res.length > 5) {
                                res = res.substring(0, 4) + "..";
                            }
                            return res;
                        }
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLabel: {
                        formatter: '{value} ms'
                    },
                    minInterval : 1
                }
            ],
            series: [
                {
                    name: '平均耗时',
                    type: 'bar',
                    barWidth: '60%',
                    barMaxWidth: '20',
                    data: data.series
                }
            ]
        };
        var durationHistogram = echarts.init(document.getElementById('durationHistogram'));
        durationHistogram.setOption(option);
        extension(durationHistogram);
    };

    var buildService = function (xAxis, callCounts, successCounts) {
        var option = {
            color: ['#4285f4', '#1e8e3e'],
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['调用频次', '成功次数'],
                orient: 'vertical',
                y: 'center',
                x: 'right'
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                triggerEvent: true,
                data: xAxis,
                axisLabel: {
                    interval: 0,
                    rotate: 40,
                    formatter: function (value) {
                        var res = value;
                        if (res.length > 10) {
                            res = res.substring(0, 9) + "..";
                        }
                        return res;
                    }
                }
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                    formatter: '{value} 次'
                },
                minInterval : 1
            },
            series: [
                {
                    name: '调用频次',
                    type: 'line',
                    data: callCounts
                },
                {
                    name: '成功次数',
                    type: 'line',
                    data: successCounts
                }
            ]
        };
        $('#service').remove();
        $('<div id="service" style="height: 250px; width: 100%"></div>').appendTo($('.service-body'));
        var service = echarts.init(document.getElementById('service'));
        service.setOption(option);
        extension(service);
    };

    $('#search').on('click', function () {
        setRouteServiceInfo();
    });
    setRouteCount();
    setRouteExecuteInfo();
    setRouteExecuteInfoTop10();
    setRouteServiceInfo();
});