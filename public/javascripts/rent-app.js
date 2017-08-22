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
});

app.controller('ContractList', function($http, $scope) {
     $http.get("/contracts")
     .then(function(response) {
         $scope.contractList = response.data;
     });
});