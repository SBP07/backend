(function() {
    var speelsysteemApp = angular.module('speelsysteemAnimatorServices', []);

    speelsysteemApp.factory('animators', function($http) {
        return {
            all: function() { return $http.get('/api/animator/all'); },
            byId: function(id) { return $http.get('/api/animator/' + id); }
        };
    });

})();
