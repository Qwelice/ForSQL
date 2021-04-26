package dbworking

import java.sql.ResultSet

interface ServerHandler {
    fun connect() : Boolean
    fun closeConnection() : Boolean
    fun execute(sql: String)
    fun executeQuery(sql: String) : ResultSet?
}