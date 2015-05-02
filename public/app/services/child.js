(function() {
    var speelsysteemApp = angular.module('speelsysteemChildServices', []);

    speelsysteemApp.factory('allChildren', function($http) {
        return $http.get('/api/child/all');
    });

})();
