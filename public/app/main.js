var app = angular.module('speelsysteemApp', ['ngRoute', 'speelsysteemControllers']);

app.config(function($routeProvider){
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
    }).when('/kinderen/nieuw', {
        templateUrl: 'assets/app/templates/child/form.html',
        controller: 'ChildFormController'
    }).otherwise({
        redirectTo: '/'
    });
});
