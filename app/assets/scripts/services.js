/*global define */

define(['angular'], function (angular) {
    angular.module('speelApp.services', ['ngResource'])
        .factory('Child', function ($resource) {
            return $resource('/api/child/:id', null, {
                'update': {
                    method: 'PUT'
                }
            });
        })
        .factory('Volunteer', function ($resource) {
            return $resource('/api/volunteer/:id', null, {
                'update': {
                    method: 'PUT'
                }
            });
        })
        .factory('Shift', function ($resource) {
            return $resource('/api/shift/:id', null, {
                'update': {
                    method: 'PUT'
                }
            });
        })
        .factory('ChildPresence', function($http) {
            return {
                getById: function(childId) {
                    return $http.get('/api/attendance/child/' + Number(childId));
                },
                registerPresence: function(childId, shiftId) {
                    return $http.post('/api/attendance/child/register', {
                        shiftId: shiftId,
                        childId: childId
                    });
                },
                unregisterPresence: function(childId, shiftId) {
                    return $http.post('/api/attendance/child/unregister', {
                        shiftId: shiftId,
                        childId: childId
                    });
                }
            };
        });

});
