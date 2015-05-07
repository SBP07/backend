(function () {
    var speelsysteemApp = angular.module('speelsysteemChildServices', []);

    speelsysteemApp.factory('children', function ($http, $log) {
        return {
            all: function () {
                return $http.get('/api/child/all');
            },
            byId: function (id) {
                return $http.get('/api/child/' + id)
                        .then(function(child) {
                            child.data.birthDate = new XDate(child.data.birthDate).toDate();
                            return child.data;
                        });
            },
            update: function (child) {
                child.birthDate = new XDate(child.birthDate).toString("yyyy-MM-dd");
                return $http.put('/api/child/' + child.id, child);
            }
        }
    });

})();
