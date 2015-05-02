(function() {
    var speelsysteemApp = angular.module('speelsysteemAnimatorServices', []);

    speelsysteemApp.factory('allAnimators', function($http) {
        return $http.get('/api/animator/all');
    });

})();
