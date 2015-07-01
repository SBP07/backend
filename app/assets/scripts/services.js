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
            return $resource('/api/animator/:id', null, {
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
        });

});