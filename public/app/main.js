var app = angular.module('speelsysteemApp', ['ngRoute', 'speelsysteemControllers']);

app.config(function ($routeProvider, $locationProvider) {
    $routeProvider.when('/', {
        templateUrl: 'assets/app/templates/home.html',
        controller: 'HomeController'
    }).when('/animatoren', {
        templateUrl: 'assets/app/templates/animators.html',
        controller: 'AnimatorsController'
    }).when('/kinderen', {
        templateUrl: 'assets/app/templates/children.html',
        controller: 'ChildrenController'
    }).when('/dagdelen', {
        templateUrl: 'assets/app/templates/shifts.html',
        controller: 'ShiftsController'
    }).when('/dagdelen/details/:shiftId', {
        templateUrl: 'assets/app/templates/shift/details.html',
        controller: 'ShiftDetailsController'
    }).when('/kinderen/nieuw', {
        templateUrl: 'assets/app/templates/child/form.html',
        controller: 'ChildFormController'
    }).when('/kinderen/bewerken/:id', {
        templateUrl: 'assets/app/templates/child/form.html',
        controller: 'ChildFormController'
    }).when('/kinderen/details/:id', {
        templateUrl: 'assets/app/templates/child/details.html',
        controller: 'ChildDetailsController'
    }).when('/animatoren/details/:id', {
        templateUrl: 'assets/app/templates/animator/details.html',
        controller: 'AnimatorDetailsController'
    }).otherwise({
        redirectTo: '/'
    });

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });

});
