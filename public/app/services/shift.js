(function() {
    var speelsysteemApp = angular.module('speelsysteemShiftServices', []);

    speelsysteemApp.factory('allShifts', function($http) {
        return $http.get('/api/shift/all');
    });

})();
