package utils

class AuthorizationUtils {


  def authorize(username: String, password: String): Boolean = {
    if(username == "Fernanda" && password == "Rent@123"){
        return true;
    }
    return false;
  }

  def isAuth(): Boolean ={
       true
  }
}
