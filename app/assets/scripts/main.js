/*global require, requirejs */

requirejs.config({
    //enforceDefine: true,
    paths: {
        'angular': ['/assets/lib/angular/angular'],
        'angular-ui-router': ['/assets/lib/angular-ui-router/release/angular-ui-router'],
        'angular-material': ['/assets/lib/angular-material/angular-material'],
        'angular-resource': ['/assets/lib/angular-resource/angular-resource'],
        'angular-animate': ['/assets/lib/angular-animate/angular-animate'],
        'angular-aria': ['/assets/lib/angular-aria/angular-aria'],
        'angular-material-icons': ['/assets/lib/angular-material-icons/angular-material-icons'],
        'angular-messages': ['/assets/lib/angular-messages/angular-messages']
    },
    shim: {
        'angular': {
            exports: 'angular'
        },
        'angular-ui-router': {
            deps: ['angular'],
            exports: 'angular'
        },
        'angular-material': {
            deps: ['angular', 'angular-animate', 'angular-messages']
        },
        'angular-resource': {
            deps: ['angular']
        },
        'angular-animate': {
            deps: ['angular', 'angular-aria']
        },
        'angular-aria': {
            deps: ['angular']
        },
        'angular-material-icons': {
            deps: ['angular-material']
        },
        'angular-messages': {
            deps: ['angular']
        }
    }
});

require(['angular', './controllers', './filters', './services', 'angular-ui-router', 'angular-material', 'angular-resource', 'angular-material-icons'],
    function (angular, controllers) {

        // Declare app level module which depends on filters, and services

        angular.module('speelApp', [/*'myApp.filters',*/ 'speelApp.services', 'ui.router', 'ngResource', 'ngMaterial', 'ngMdIcons', 'ngMessages'])
            .controller('MainCtrl', ['$scope', '$mdToast', 'Child', function ($scope, $mdToast, Child) {
                $scope.children = Child.query();
                //Child.save({ firstName: 'Thomas', lastName: 'Toye', city: 'Beverly' });
                //Child.save({ firstName: 'Robbe', lastName: 'Toye', city: 'Beverly' });
                //Child.save({ firstName: 'Sander', lastName: 'Verkaemer', city: 'Beverly' });
                //Child.save({ firstName: 'Marva', lastName: 'De Kip', city: 'Tomberg 21A' });

                //$mdToast.show($mdToast.simple().content("test"));

                //Child.save($scope.child);
            }])
            .controller('ChildDetailsCtrl', ['$scope', '$stateParams', '$mdToast', 'Child', function ($scope, $stateParams, $mdToast, Child) {
                $scope.actionName = "Opslaan";

                $scope.save = function () {
                    Child.update($scope.selectedChild, function () {
                        $mdToast.show($mdToast.simple().content("Kind opgeslagen"));
                    }, function () {
                        $mdToast.show($mdToast.simple().content("Kon kind niet opslaan"));
                    });
                };
                $scope.selectedChild = Child.get({id: $stateParams.id});
            }])
            .controller('ChildListCtrl', function ($scope, Child) {
                $scope.children = Child.query();
            })
            .controller('NewChildCtrl', function ($scope, $mdToast, $state, Child) {
                $scope.actionName = "Aanmaken";
                $scope.selectedChild = {};
                $scope.save = function () {
                    Child.save($scope.selectedChild, function () {
                        $mdToast.show($mdToast.simple().content("Kind aangemaakt"));
                        $state.go('child.details(' + $scope.selectedChild.id + ')');
                    }, function () {
                        $mdToast.show($mdToast.simple().content("Kon kind niet aanmaken"));
                    });
                };
            })
            .controller('HomeCtrl', function ($scope) {

            })
            .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
                $urlRouterProvider.otherwise('/');

                $stateProvider
                    .state('home', {
                        url: '/',
                        templateUrl: '/assets/templates/home.html',
                        controller: 'HomeCtrl'
                    })
                    .state('child', {
                        url: '/kind',
                        templateUrl: '/assets/templates/child/list.html',
                        controller: 'ChildListCtrl'
                    })
                    .state('child.details', {
                        url: '/details/:id',
                        templateUrl: '/assets/templates/child/details.html',
                        controller: 'ChildDetailsCtrl'
                    })
                    .state('child.edit', {
                        url: '/bewerken/:id',
                        templateUrl: '/assets/templates/child/form.html',
                        controller: 'ChildDetailsCtrl'
                    })
                    .state('child.new', {
                        url: '/nieuw',
                        templateUrl: '/assets/templates/child/form.html',
                        controller: 'NewChildCtrl'
                    });
            }]);

        angular.bootstrap(document, ['speelApp']);

    });
