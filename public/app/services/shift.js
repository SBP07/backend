(function () {
    var speelsysteemShiftServices = angular.module('speelsysteemShiftServices', []);

    speelsysteemShiftServices.factory('shifts', function ($http) {
        return {
            all: function () {
                return $http.get('/api/shift/all')
            },
            byId: function (id) {
                return $http.get('/api/shift/' + id)
            },
            deleteById: function (id) {
                return $http.delete('/api/shift/' + id);
            }
        };
    });
})();
