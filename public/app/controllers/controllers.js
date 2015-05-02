var speelsysteemControllers = angular.module('speelsysteemControllers',
    ['ui.bootstrap', 'speelsysteemShiftServices', 'speelsysteemAnimatorServices',
        'speelsysteemChildServices']
);

speelsysteemControllers.controller('AnimatorsController', function ($scope, $modal, allAnimators) {
    allAnimators.success(function (response) {
        $scope.animators = response;
    });

    $scope.details = function (animator) {
        var modalInstance = $modal.open({
            templateUrl: '/assets/app/templates/animator/details.html',
            controller: 'AnimatorDetailsController',
            size: "lg",
            scope: $scope,
            resolve: {
                animator: function () {
                    return animator;
                }
            }
        });
    };
});

speelsysteemControllers.controller('AnimatorDetailsController', function ($scope, $modalInstance, $modal, animator) {
    $scope.animator = animator;

    $scope.modalWrapper = {
        include: '/assets/app/templates/animator/form.html',
        title: 'Animator bewerken'
    };

    $scope.edit = function (animator) {
        var modalInstance = $modal.open({
            templateUrl: '/assets/app/templates/misc/modalWrapper.html',
            controller: 'AnimatorFormController',
            size: "lg",
            scope: $scope,
            resolve: {
                animator: function () {
                    return animator;
                }
            }
        });
    };

    $scope.ok = function () {
        $modalInstance.close();
    };
});

speelsysteemControllers.controller('ShiftsController', function ($scope, $modal, allShifts) {
    allShifts.success(function (response) {
        $scope.shifts = response;
    });

    $scope.modalWrapper = {
        include: '/assets/app/templates/shift/details.html',
        title: 'Details voor dagdeel'
    };

    $scope.details = function (shift) {
        var modalInstance = $modal.open({
            templateUrl: '/assets/app/templates/misc/modalWrapper.html',
            controller: 'ShiftDetailsController',
            size: "lg",
            scope: $scope,
            resolve: {
                shift: function () {
                    return shift;
                }
            }
        });
    };
});

speelsysteemControllers.controller('ChildrenController', function ($scope, $modal, allChildren) {
    allChildren.success(function (response) {
        $scope.children = response;
    });

    $scope.details = function (child) {
        var modalInstance = $modal.open({
            templateUrl: '/assets/app/templates/child/details.html',
            controller: 'ChildDetailsController',
            size: "lg",
            scope: $scope,
            resolve: {
                child: function () {
                    return child;
                }
            }
        });
    };
});

speelsysteemControllers.controller('ChildDetailsController', function ($scope, $modal, $modalInstance, child) {
    $scope.child = child;

    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.modalWrapper = {
        include: '/assets/app/templates/child/form.html',
        title: 'Kind bewerken'
    };

    $scope.edit = function (child) {
        var modalInstance = $modal.open({
            templateUrl: '/assets/app/templates/misc/modalWrapper.html',
            controller: 'ChildFormController',
            size: "lg",
            scope: $scope,
            resolve: {
                child: function () {
                    return angular.copy(child);
                }
            }
        });
    };
});

speelsysteemControllers.controller('HomeController', function () {

});

speelsysteemControllers.controller('NavbarController', function () {

});

speelsysteemControllers.controller('ChildFormController', function ($scope, $modalInstance, child) {
    $scope.child = child || {};

    $scope.format = "dd-MM-yyyy";

    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened = true;
    };

    $scope.saveChild = function () {
        console.log($scope.child);
    };
});

speelsysteemControllers.controller('ShiftDetailsController', function ($scope, $modal, $modalInstance, shift) {
    $scope.shift = shift;

    $scope.ok = function () {
        $modalInstance.close()
    };
});
