package DBSQL

import java.sql.DriverManager

class DBSQLTG(private val user: String, private val pass: String) {

    private val jdbcUrl = "jdbc:sqlserver://localhost:1433;databaseName=DBforTG;trustServerCertificate=true"
    private val connection = DriverManager.getConnection(jdbcUrl, user, pass) // get the connection

//    fun selectUser(chatId: Int, name:String): User{
//        val query = connection.prepareStatement(
//            "SELECT * FROM user"
//        )
//        val result = query.executeQuery().apply { next() }
//        return User(chatId,name)
//
//    }
//


}