requirejs.config
  paths:
    'angular': [ '/assets/lib/angular/angular' ]
    'angular-ui-router': [ '/assets/lib/angular-ui-router/release/angular-ui-router' ]
  shim:
    'angular': exports: 'angular'
    'angular-ui-router':
      deps: [ 'angular' ]
      exports: 'angular'

require [
  'angular'
  './controllers'
#  './directives'
  './filters'
  './services'
  'angular-ui-router'
], (angular, controllers) ->

  angular.module('speelApp', [
    'speelApp.filters'
    'speelApp.services'
#    'speelApp.directives'
#    'ngRoute'
  ])
  angular.bootstrap document, [ 'myApp' ]

console.log("test")
