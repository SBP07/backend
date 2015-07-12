/*global define */

define(['angular'], function (angular) {
    angular.module('speelApp.filters', [])
        .filter('personFilter', function () {
            return function (items, input) {
                var test = function(item) {
                    var normalize = function(it) {
                        it = it || '';
                        return String(it).replace(/ /g, '').replace(/'/g, '').toLowerCase();
                    };

                    var firstName = normalize(item.firstName);
                    var lastName = normalize(item.lastName);
                    input = normalize(input);

                    return (firstName + lastName).indexOf(input) != -1 || (lastName + firstName).indexOf(input) != -1;
                };

                return items.filter(test);
            };
        });
});
