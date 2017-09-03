var app = angular.module('loginApp', []);

app.controller('LoginHelper', function($http, $scope, $window) {
     $scope.fail = false;
     $scope.username;
     $scope.password;
     $scope.login = function(){
                   $scope.fail = false;
                   var jsonData = JSON.stringify({'username':$scope.username, 'password':$scope.password});
                   $http.post('/authenticate', jsonData).then(function success(response) {
                      var data = response.data;
                      if(data){
                         $scope.fail = false;
                         $window.location.href = '/person';
                      } else {
                         $scope.fail = true;
                      }
                    },
                    function error(response) {
                      var data = response.data;
                   });
         };
});
