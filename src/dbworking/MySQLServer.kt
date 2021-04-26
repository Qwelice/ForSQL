package dbworking

import Communicator
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

class MySQLServer(private var host: String = "localhost", private var port: Int = 3306,
    private var username: String = "root", private var password: String = "root") : ServerHandler {
    val sHost: String
        get() = host
    val sPort: Int
        get() = port
    private var connection: Connection? = null
    private val urlData: String
        get() = "jdbc:mysql://$host:$port/?serverTimezone=UTC"
    val isConnected: Boolean
        get() = if(connection != null) connection!!.isValid(1000) else false

    init {
        try {
            Communicator.writeLine("Подключение к: $host: $port")
            connection = DriverManager.getConnection(urlData, username, password)
            Communicator.writeLine("Успешно!")
            Communicator.writeLine("Вы используете MySQL сервер")
        }catch (ex: SQLException){
            Communicator.writeLine("Не удалось подключиться :(")
        }
    }

    fun resetUsername(newUsername: String){
        username = newUsername
    }

    fun resetPassword(newPassword: String){
        password = newPassword
    }

    fun resetHost(newHost: String){
        host = newHost
    }

    fun resetPort(newPort: Int){
        port = newPort
    }

    override fun connect(): Boolean {
        if(isConnected){
            try {
                connection = DriverManager.getConnection(urlData, username, password)
            }catch (ex: SQLException){
                return false
            }
        }
        return true
    }

    override fun execute(sql: String){
        try {
            connection!!.createStatement().execute(sql)
        }catch (ex: SQLException){
            Communicator.writeLine("Ошибка в формировании запроса:\n\'$sql\'")
        }
    }

    override fun executeQuery(sql: String): ResultSet? {
        return try {
            connection!!.createStatement().executeQuery(sql)
        }catch (ex: SQLException){
            Communicator.writeLine("Ошибка в формировании запроса:\n\'$sql\'")
            null
        }
    }

    override fun closeConnection() : Boolean {
        return try {
            if(isConnected){
                connection!!.close()
            }
            true
        }catch (ex: SQLException){
            false
        }
    }
}