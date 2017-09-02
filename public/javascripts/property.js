var app = angular.module('propertyApp', []);

app.controller('PropertyList', function($http, $scope) {
     $http.get("/properties")
     .then(function(response) {
         $scope.propertyList = response.data;
     });

     $scope.success = false;
     $scope.fail = false;
     $scope.code;
     $scope.line1;
     $scope.line2;
     $scope.line3;
     $scope.zipcode;
     $scope.rooms;

    $scope.clearForm = function(){
        $scope.code = '';
        $scope.line1 = '';
        $scope.line2 = '';
        $scope.line3 = '';
        $scope.zipcode = '';
        $scope.rooms = '';
    }

    $scope.clearMessages = function(){
        $scope.success = false;
        $scope.fail = false;
    }

     $scope.closeFail = function(){
        $scope.fail = false;
     }

     $scope.closeSuccess = function(){
         $scope.success = false;
      }

     $scope.removeProperty= function(property){
        $scope.clearMessages();
        $http.delete('/property/' + property.id).then(
                function(response){
                  // success callback
                  $http.get("/properties").then(function(response) {
                         $scope.propertyList = response.data;
                     });
                },
                function(response){
                  // failure call back
                }
             );
        $scope.success = true;
     }

     $scope.insertProperty = function(){
        $scope.clearMessages();
        var jsonData = JSON.stringify({'code':$scope.code, 'line1':$scope.line1, 'line2':$scope.line2, 'line3':$scope.line3, 'zipcode':$scope.zipcode, 'rooms':$scope.rooms});
        $http.post('/createProperty', jsonData).then(function success(response) {
              var data = response.data;
              $http.get("/properties")
                   .then(function(response) {
                       $scope.propertyList = response.data;
                   });
              $scope.success = true;
              $scope.clearForm();
            },
            function error(response) {
              var data = response.data;
              $scope.fail = true;
           });
     };
});
