var speelsysteemControllers = angular.module('speelsysteemControllers',
    ['ui.bootstrap', 'speelsysteemShiftServices', 'speelsysteemAnimatorServices',
        'speelsysteemChildServices']
);

speelsysteemControllers.controller('AnimatorsController', function ($scope, $location, animators) {
    animators.all().success(function (response) {
        $scope.animators = response;
    });

    $scope.details = function (animator) {
        $location.path('/animatoren/details/' + animator.id);
    };
});

speelsysteemControllers.controller('AnimatorDetailsController', function ($scope, $location, $routeParams, animators) {
    $scope.animator = {};

    animators.byId($routeParams.id).success(function (response) {
        $scope.animator = response;
    });

    $scope.edit = function (animator) {
        $location.path('/animatoren/bewerken/' + animator.id);
    };

    $scope.ok = function () {
        $modalInstance.close();
    };
});

speelsysteemControllers.controller('ShiftsController', function ($scope, $modal, $location, shifts) {
    shifts.all().success(function (response) {
        $scope.shifts = response;
    });
    $scope.details = function (shift) {
        $location.path('dagdelen/details/' + shift.shiftId)
    };
});

speelsysteemControllers.controller('ChildrenController', function ($scope, $location, children) {
    children.all().success(function (response) {
        $scope.children = response;
    });

    $scope.newChild = function () {
        $location.path('/kinderen/nieuw');
    };

    $scope.details = function (child) {
        $location.path('/kinderen/details/' + child.id);
    };
});

speelsysteemControllers.controller('ChildDetailsController', function ($scope, $location, $routeParams, children) {
    $scope.child = {};

    children.byId($routeParams.id).success(function (response) {
        $scope.child = response;
    });

    $scope.edit = function (child) {
        $location.path('/kinderen/bewerken/' + child.id);
    };
});

speelsysteemControllers.controller('HomeController', function () {

});

speelsysteemControllers.controller('NavbarController', function ($scope, $location) {
    $scope.home = function () {
        $location.path('/kinderen')
    };

    $scope.newChild = function () {
        $location.path('/kinderen/nieuw')
    };
    $scope.listChildren = function () {
        $location.path('/kinderen')
    };

    $scope.newAnimator = function () {
        $location.path('/animatoren/nieuw')
    };
    $scope.listAnimators = function () {
        $location.path('/animatoren')
    };

    $scope.newShift = function () {
        $location.path('/dagdelen/nieuw')
    };
    $scope.listShifts = function () {
        $location.path('/dagdelen')
    };

    $scope.presences = function () {
        $location.path('/presences')
    };
});

speelsysteemControllers.controller('ChildFormController', function ($scope, $routeParams, $log, children) {
    $scope.child = {};

    if ($routeParams.id) {
        children.byId($routeParams.id).success(function (response) {
            $scope.child = response;
        });
    }

    $scope.format = "dd-MM-yyyy";

    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened = true;
    };

    $scope.saveChild = function () {
        var child = $scope.child;
        if (child.hasOwnProperty('id')) {
            // existing child (because it has an id)
            children.update(child)
                .success(function (res) {
                    $log.debug('updated child with id ' + child.id);
                })
                .error(function (res) {
                    $log.debug('could not update child with id ' + child.id);
                });
        } else {
            $log.debug('should now create new child child: ' + $scope.child.firstName);
        }
    };
});

speelsysteemControllers.controller('ShiftDetailsController', function ($scope, $routeParams, $location, $modal, shifts) {
    shifts.byId($routeParams.shiftId).success(function (response) {
        $scope.shift = response;
    });

    $scope.childDetails = function (child) {
        $location.path('/kinderen/details/' + child.id)
    };

    $scope.delete = function () {
        var modalInstance = $modal.open({
            templateUrl: '/assets/app/templates/shift/delete.html',
            controller: 'DeleteShiftController',
            scope: $scope
        });
    }
});

speelsysteemControllers.controller('DeleteShiftController', function ($scope, $modalInstance, $location, shifts) {
    // $scope.shift through scope inheritance

    $scope.reallyDelete = function () {
        $scope.status = "Bezig met verwijderen...";
        shifts.deleteById($scope.shift.shiftId).success(function (res) {
            $location.path('/dagdelen');
            $modalInstance.close();
        }).error(function (err) {
            $scope.status = "Kon dagdeel niet verwijderen. Probeer later opnieuw."
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss();
    };
});
