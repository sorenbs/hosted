angular.module('builder', [], function ($provide, $resource) {
    $provide.factory('PubSub', function () {
        var subscribers = {};
        return {
            pub: function (name, data) {
                if (subscribers[name]) {
                    angular.forEach(subscribers[name], function(subscriber, key) {
                        subscriber.call(data);
                    });
                }
            },
            sub: function (name, callback) {
                if (subscribers[name]) {
                    subscribers[name].push(callback);
                } else {
                    subscribers[name] = [callback];
                }
            }
        };
    });

}).
    value('appContext', {
        id: "7",
        version: "6",
        init: function (id) {
            this.id = id;
        }
    });



function AssetsCtrl($scope, appContext, codeResource) {
    //    console.log(aaa);

    codeResource.$save();

    $scope.images = [
    {
        "name": "Nexus S",
        "snippet": "Fast just got faster with Nexus S."
    },
    {
        "name": "Motorola ",
        "snippet": "The Next, Next Generation tablet."
    },
    {
        "name": "MOTOROLA XOOM™",
        "snippet": "The Next, Next Generation tablet."
    }
    ];

    function populateModel(json) {

    }

    function initFromServer() {

    }


    function addDroppedFiles(files) {
        for (var i = 0; i < files.length; i++) {
            var f = files[i];
            console.log(f.name);
            $scope.images.push({ name: f.name, snippet: "Cool!" });

        }
        $scope.$apply();
        console.log(files);
    }

    var dropbox = document.getElementById("assetsContainer");
    dropbox.addEventListener("dragenter", function (e) {
        e.stopPropagation();
        e.preventDefault();
        dropbox.style.backgroundColor = "pink";
    }, false);
    dropbox.addEventListener("dragover", function (e) {
        e.stopPropagation();
        e.preventDefault();
    }, false);
    dropbox.addEventListener("drop", function (e) {
        e.stopPropagation();
        e.preventDefault();
        var dt = e.dataTransfer;
        var files = dt.files;
        //this.style.backgroundColor = "lime";

        addDroppedFiles(files);
    }, false);
}

function CodeController() {
    function fetchCodeAndHistory() {
        $.ajax({
            url: "/api/craft/" + id + "/" + version,
            success: function (data) {
                if (data.error) {
                    log("AWK!, " + data.error)
                } else {
                    if (data && data.code != "") {
                        cm.setValue(data.code);
                    }
                    if (data.history) {
                        var historyMenu = $("#history-menu"), historyData = data.history;
                        historyMenu.children().remove();
                        historyMenu.append("<li><a>Remember to save!</a></li>");
                        historyMenu.append("<li class='divider'></li>");

                        for (var c in historyData) {
                            var date = new Date(Date.parse(historyData[c].created));
                            var date_str = ('0' + date.getHours()).substr(-2, 2) + ':' + ('0' + date.getMinutes()).substr(-2, 2) + " " + ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"][date.getMonth()] + " " + date.getDate();
                            historyMenu.append("<li><a href='" + historyData[c].version + "' title='" + historyData[c].comment + "'> [" + historyData[c].version + "] " + date_str + " " + historyData[c].comment.substring(0, Math.min(50, historyData[c].comment.length)) + "</a></li>");
                        }

                    }
                }
            },
            dataType: 'json'
        });
    }
}