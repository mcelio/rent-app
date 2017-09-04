var app = angular.module('contractApp', []);

app.controller('ContractList', function($http, $scope) {
     $http.get("/contracts")
     .then(function(response) {
         $scope.contractList = response.data;
     });

     $scope.success = false;
     $scope.fail = false;
     $scope.selectedPerson;
     $scope.beginDate;
     $scope.endDate;
     $scope.numberAdvances;
     $scope.rentAmount;
     $scope.numberDeposits;
     $scope.depositAmount;
     $scope.numberAdvances;
     $scope.notes;

     $http.get("/persons").then(function(response) {
          $scope.selectPersonTable = response.data;
      });

      $http.get("/properties").then(function(response) {
                $scope.selectPropertyTable = response.data;
            });

      $scope.setPerson = function(person){
         $scope.selectedPerson = person;
      }

      $scope.setProperty = function(property){
         $scope.selectedProperty = property;
      }

    $scope.clearForm = function(){
        $scope.selectedPerson.name = '';
        $scope.selectedPerson.id = '';
        $scope.selectedProperty.id = '';
        $scope.selectedProperty.code = '';
        $scope.beginDate = '';
        $scope.endDate = '';
        $scope.numberAdvances = '';
        $scope.rentAmount = '';
        $scope.numberDeposits = '';
        $scope.depositAmount = '';
        $scope.numberAdvances = '';
        $scope.notes = '';
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

     $scope.fetchPerson = function(id){
        $http.get("/person/" + id).then(function(response) {
                  return response.data;
              });
     }

     $scope.removeContract = function(contract){
        $scope.clearMessages();
        $http.delete('/contract/' + contract.id).then(
                function(response){
                  // success callback
                  $http.get("/contracts").then(function(response) {
                         $scope.contractList = response.data;
                     });
                },
                function(response){
                  // failure call back
                }
             );
        $scope.success = true;
     }

     $scope.insertContract = function(){
        $scope.clearMessages();
        var jsonData = JSON.stringify({'propertyId':$scope.selectedProperty.id, 'personId':$scope.selectedPerson.id,
        'beginDate':$scope.beginDate,
            'endDate':$scope.endDate,'numberAdvances':$scope.numberAdvances,'rentAmount':$scope.rentAmount,
            'numberDeposits':$scope.numberDeposits,'depositAmount':$scope.depositAmount,'notes':$scope.notes});
        $http.post('/createContract', jsonData).then(function success(response) {
              var data = response.data;
              $http.get("/contracts")
                   .then(function(response) {
                       $scope.contractList = response.data;
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
