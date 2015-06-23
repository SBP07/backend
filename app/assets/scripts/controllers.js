/*global define */

define(function () {
    var controllers = {};

    controllers.MainCtrl = function ($scope, $mdToast, Child) {
        $scope.children = Child.query();
        //Child.save({ firstName: 'Thomas', lastName: 'Toye', city: 'Beverly' });
        //Child.save({ firstName: 'Robbe', lastName: 'Toye', city: 'Beverly' });
        //Child.save({ firstName: 'Sander', lastName: 'Verkaemer', city: 'Beverly' });
        //Child.save({ firstName: 'Marva', lastName: 'De Kip', city: 'Tomberg 21A' });

        //$mdToast.show($mdToast.simple().content("test"));

        //Child.save($scope.child);
    };
    controllers.MainCtrl.$inject = ['$scope', '$mdToast', 'Child'];

    controllers.ChildDetailsCtrl = function ($scope, $stateParams, $mdToast, Child) {
        $scope.actionName = "Opslaan";

        $scope.save = function () {
            Child.update($scope.selectedChild, function () {
                $mdToast.show($mdToast.simple().content("Kind opgeslagen"));
            }, function () {
                $mdToast.show($mdToast.simple().content("Kon kind niet opslaan"));
            });
        };
        $scope.selectedChild = Child.get({id: $stateParams.id});
    };

    //controllers.ChildDetailsCtrl.$inject = ['$scope', '$stateParams', '$mdToast', 'Child'];

    controllers.ChildListCtrl = function ($scope, Child) {
        $scope.children = Child.query();
    };

    controllers.NewChildCtrl = function ($scope, $mdToast, $state, Child) {
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
    };

    controllers.HomeCtrl = function ($scope) {

    };

    return controllers;
});
