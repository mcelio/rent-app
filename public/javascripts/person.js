var app = angular.module('rentApp', []);
app.controller('SelectPerson', function($scope, $http) {
     $http.get("/persons")
     .then(function(response) {
         $scope.selectPersonTable = response.data;
     });
     $scope.selectedPerson;
     $scope.setPerson = function(person){
        $scope.selectedPerson = person;
     }
});


app.controller('PersonList', function($http, $scope) {
     $http.get("/persons")
     .then(function(response) {
         $scope.personList = response.data;
     });

     $scope.success = false;
     $scope.fail = false;
     $scope.name;
     $scope.lastname;
     $scope.age;
     $scope.email;
     $scope.passport;

    $scope.clearForm = function(){
        $scope.name = '';
        $scope.lastname = '';
        $scope.age = '';
        $scope.email = '';
        $scope.passport = '';
    }

    $scope.clearMessages = function(){
        $scope.success = false;
        $scope.fail = false;
    }

    $scope.clearForm = function(){
            $scope.name = '';
            $scope.lastname = '';
            $scope.age = '';
            $scope.email = '';
            $scope.passport = '';
        }

     $scope.closeFail = function(){
        $scope.fail = false;
     }

     $scope.closeSuccess = function(){
         $scope.success = false;
      }

     $scope.removePerson = function(person){
        $scope.clearMessages();
        $http.delete('/person/' + person.id).then(
                function(response){
                  // success callback
                  $http.get("/persons").then(function(response) {
                         $scope.personList = response.data;
                     });
                },
                function(response){
                  // failure call back
                }
             );
        $scope.success = true;
     }

     $scope.insertPerson = function(){
        $scope.clearMessages();
        var jsonData = JSON.stringify({'name':$scope.name, 'lastname':$scope.lastname,'age':$scope.age,'email':$scope.email,'passport':$scope.passport});
        $http.post('/createPerson', jsonData).then(function success(response) {
              var data = response.data;
              $http.get("/persons")
                   .then(function(response) {
                       $scope.personList = response.data;
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

app.controller('ContractList', function($http, $scope) {
     $http.get("/contracts")
     .then(function(response) {
         $scope.contractList = response.data;
     });
});


app.factory("flash", function($rootScope) {
  var queue = [];
  var currentMessage = "";

  $rootScope.$on("$routeChangeSuccess", function() {
    currentMessage = queue.shift() || "";
  });

  return {
    setMessage: function(message) {
      queue.push(message);
    },
    getMessage: function() {
      return currentMessage;
    }
  };
});