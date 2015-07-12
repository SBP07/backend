/*global define */

define(function () {
    var controllers = {};

    controllers.MainCtrl = function ($scope, $mdToast, Child) {
        $scope.children = Child.query();
    };

    controllers.ChildDetailsCtrl = function ($scope, $stateParams, $mdToast, $state, Child) {
        $scope.actionName = "Opslaan";

        $scope.save = function () {
            Child.update($scope.selectedChild, function () {
                $mdToast.show($mdToast.simple().content("Kind opgeslagen"));
                $state.go('child.details', {id: $scope.selectedChild.id});
            }, function () {
                $mdToast.show($mdToast.simple().content("Kon kind niet opslaan"));
            });
        };
        $scope.selectedChild = Child.get({id: $stateParams.id});
    };

    controllers.ChildListCtrl = function ($scope, $state, Child) {
        $scope.children = Child.query();

        $scope.create = function () {
            $state.go('child.new');
        };

        $scope.refresh = function () {
            $scope.children = Child.query();
        };

        $scope.stateIs = function (name) {
            return $state.is(name);
        };
    };

    controllers.NewChildCtrl = function ($scope, $mdToast, $state, Child) {
        $scope.actionName = "Aanmaken";
        $scope.selectedChild = {};
        $scope.save = function () {
            Child.save($scope.selectedChild, function () {
                $mdToast.show($mdToast.simple().content("Kind aangemaakt"));
                $scope.refresh();
                $state.go('child');
            }, function () {
                $mdToast.show($mdToast.simple().content("Kon kind niet aanmaken"));
            });
        };

    };

    controllers.VolunteerDetailsCtrl = function ($scope, $stateParams, $mdToast, $state, Volunteer) {
        $scope.actionName = "Opslaan";

        $scope.save = function () {
            Volunteer.update($scope.selectedVolunteer, function () {
                $mdToast.show($mdToast.simple().content("Animator opgeslagen"));
                $state.go('volunteer.details', {id: $scope.selectedVolunteer.id});
            }, function () {
                $mdToast.show($mdToast.simple().content("Kon animator niet opslaan"));
            });
        };

        $scope.$watch('selectedVolunteer.yearStartedVolunteering', function (val, old) {
            $scope.selectedVolunteer.yearStartedVolunteering = parseInt(val);
        });

        $scope.selectedVolunteer = Volunteer.get({id: $stateParams.id});
    };

    controllers.VolunteerListCtrl = function ($scope, $state, Volunteer) {
        $scope.volunteers = Volunteer.query();

        $scope.create = function () {
            $state.go('volunteer.new');
        };

        $scope.refresh = function () {
            $scope.volunteers = Volunteer.query();
        };

        $scope.stateIs = function (name) {
            return $state.is(name);
        };
    };

    controllers.NewVolunteerCtrl = function ($scope, $mdToast, $state, Volunteer) {
        $scope.actionName = "Aanmaken";
        $scope.selectedVolunteer = {};
        $scope.save = function () {
            Volunteer.save($scope.selectedVolunteer, function () {
                $mdToast.show($mdToast.simple().content("Animator aangemaakt"));
                $scope.refresh();
                $state.go('volunteer');
            }, function () {
                $mdToast.show($mdToast.simple().content("Kon animator niet aanmaken"));
            });
        };

        $scope.$watch('selectedVolunteer.yearStartedVolunteering', function (val, old) {
            $scope.selectedVolunteer.yearStartedVolunteering = parseInt(val);
        });
    };

    controllers.HomeCtrl = function ($scope) {

    };

    controllers.AttendanceHomeCtrl = function ($scope, Shift) {
        Shift
            .query()
            .$promise
            .then(function (shifts) {
                return shifts
                    .map(function (day) {
                        return day.date;
                    })
                    .reduce(function (prev, current, index, array) {
                        return (array.indexOf(current) == index) ? prev.concat(current) : prev;
                    }, []);
            })
            .then(function (days) {
                $scope.days = days;
            });
    };

    controllers.AttendanceDayDetailsCtrl = function ($scope, $stateParams, $http, Child) {
        $scope.day = $stateParams.date;

        $scope.shifts = {};

        $http.get('/api/shift/bydate/' + $scope.day).then(function (res) {
            $scope.shifts = res.data;
        });

        $scope.children = Child.query();

        $scope.childAttended = function (childId, shiftId) {
            if (!$scope.shifts || !$scope.shifts.map) return false;

            var length = $scope.shifts
                .filter(
                    function (shift) {
                        return shift.shiftId == shiftId;
                    }
                )
                .filter(
                function (shift) {
                    return shift.presentChildren.some(function (child) {
                        return childId == child.id;
                    });
                }
            ).length;

            return length !== 0;
        };

        $scope.addAttendance = function (child, shift) {
            // TODO check for duplicates
            shift.presentChildren.push(child);
        };

        $scope.removeAttendance = function (child, shift) {
            shift.presentChildren = shift.presentChildren.filter(function (el) {
                return el.id != child.id;
            });
        };
    };

    return controllers;
});
