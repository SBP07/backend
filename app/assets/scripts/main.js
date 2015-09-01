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
        'angular-messages': ['/assets/lib/angular-messages/angular-messages'],
        'angular-i18n-nl': ['/assets/webjars/angular-i18n/1.4.0/angular-locale_nl-be']
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
        },
        'angular-i18n-nl': {
            deps: ['angular']
        }
    }
});

require(['angular', './controllers', './filters', './services', 'angular-ui-router', 'angular-material', 'angular-resource', 'angular-material-icons', 'angular-i18n-nl'],
    function (angular, controllers) {

        angular.module('speelApp', ['speelApp.filters', 'speelApp.services', 'ui.router', 'ngResource', 'ngMaterial', 'ngMdIcons', 'ngMessages', 'ngAnimate'])
            .config(function ($stateProvider, $urlRouterProvider) {
                $urlRouterProvider.otherwise('/');

                $stateProvider
                    .state('home', {
                        url: '/',
                        templateUrl: '/assets/templates/home.html',
                        controller: controllers.HomeCtrl
                    })
                    // Child routes
                    .state('child', {
                        url: '/kind',
                        templateUrl: '/assets/templates/child/list.html',
                        controller: controllers.ChildListCtrl
                    })
                    .state('child.details', {
                        url: '/details/:id',
                        templateUrl: '/assets/templates/child/details.html',
                        controller: controllers.ChildDetailsCtrl
                    })
                    .state('child.edit', {
                        url: '/bewerken/:id',
                        templateUrl: '/assets/templates/child/form.html',
                        controller: controllers.ChildDetailsCtrl
                    })
                    .state('child.new', {
                        url: '/nieuw',
                        templateUrl: '/assets/templates/child/form.html',
                        controller: controllers.NewChildCtrl
                    })
                    .state('child.attendances', {
                        url: '/aanwezigheden/:id',
                        templateUrl: '/assets/templates/child/attendances.html',
                        controller: controllers.ChildAttendancesCtrl
                    })
                    // Volunteer routes
                    .state('volunteer', {
                        url: '/animator',
                        templateUrl: '/assets/templates/volunteer/list.html',
                        controller: controllers.VolunteerListCtrl
                    })
                    .state('volunteer.details', {
                        url: '/details/:id',
                        templateUrl: '/assets/templates/volunteer/details.html',
                        controller: controllers.VolunteerDetailsCtrl
                    })
                    .state('volunteer.edit', {
                        url: '/bewerken/:id',
                        templateUrl: '/assets/templates/volunteer/form.html',
                        controller: controllers.VolunteerDetailsCtrl
                    })
                    .state('volunteer.new', {
                        url: '/nieuw',
                        templateUrl: '/assets/templates/volunteer/form.html',
                        controller: controllers.NewVolunteerCtrl
                    })
                    // Attendance routes
                    .state('attendanceChild', {
                        url: '/aanwezigheden',
                        templateUrl: '/assets/templates/attendance/home.html',
                        controller: controllers.AttendanceHomeCtrl
                    })
                    .state('attendanceChild.dayDetails', {
                        url: '/dag/:date',
                        templateUrl: '/assets/templates/attendance/dayDetails.html',
                        controller: controllers.AttendanceDayDetailsCtrl
                    });
            });

        angular.bootstrap(document, ['speelApp']);

    });
